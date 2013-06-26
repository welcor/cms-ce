package com.enonic.cms.framework.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;

import com.google.common.net.HttpHeaders;

public class HttpServletRangeUtil
{
    // Format: "bytes=n-n,n-n,n-n..."
    private static final String PATTERN_RANGE = "^bytes=\\d*-\\d*(,\\d*-\\d*)*$";

    // = 10KB
    private static final int DEFAULT_BUFFER_SIZE = 10240;
    private static final String SEPARATOR = "THIS_STRING_SEPARATES";

    /**
     * Process the range request.
     *
     * @param request request to be processed
     * @param response response to be created
     * @param contentType request contentType
     * @param file file to be proceed
     * @throws java.io.IOException
     */
    public static void processRequest( final HttpServletRequest request, final HttpServletResponse response, String contentType, final File file )
        throws IOException
    {
        // I. File validation
        int errorCode = checkRequestedFile( request );
        if ( errorCode != 0 )
        {
            response.sendError( errorCode );
            return;
        }

        errorCode = checkFileExists( file );
        if ( errorCode != 0 )
        {
            response.sendError( errorCode );
            return;
        }

        final String eTag = resolveETag( file );


        // II. Header validation
        errorCode = checkIfNoneMatch( request, eTag );
        if ( errorCode != 0 )
        {
            response.setHeader( HttpHeaders.ETAG, eTag );
            response.sendError( errorCode );
            return;
        }

        errorCode = checkIfModifiedSince( request, file );
        if ( errorCode != 0 )
        {
            response.setHeader( HttpHeaders.ETAG, eTag );
            response.sendError( errorCode );
            return;
        }

        errorCode = checkIfMatch( request, eTag );
        if ( errorCode != 0 )
        {
            response.sendError( errorCode );
            return;
        }

        errorCode = checkIfUnmodifiedSince( request, file );
        if ( errorCode != 0 )
        {
            response.sendError( errorCode );
            return;
        }


        // III. Process ranges
        final List<Range> ranges = new ArrayList<Range>();

        errorCode = processRanges( request, ranges, file, eTag );
        if ( errorCode != 0 )
        {
            response.setHeader( HttpHeaders.CONTENT_RANGE, String.format( "bytes */%d", file.length() ) );
            response.sendError( errorCode );
            return;
        }


        // IV. Process gzip
        boolean acceptGzip = processGZip( request, response, contentType, file, eTag );


        // V. Process download
        processDownload( response, contentType, file, ranges, acceptGzip );
    }

    private static int checkRequestedFile( final HttpServletRequest request )
    {
        final String requestedFile = request.getPathInfo();
        return ( requestedFile == null ) ? HttpServletResponse.SC_NOT_FOUND : 0;
    }

    private static int checkFileExists( final File file )
    {
        return ( !file.exists() ) ? HttpServletResponse.SC_NOT_FOUND : 0;
    }

    private static String resolveETag( final File file )
    {
        String fileName = file.getName();
        long length = file.length();
        long lastModified = file.lastModified();

        return String.format( "%s_%d_%d", fileName, length, lastModified );
    }

    /**
     * Check <code>If-None-Match</code> header contains "*" or ETag.
     * @param request HttpServletRequest
     * @param eTag ETag issue
     * @return success code (0), otherwise <code>304</code> indicating that a conditional GET operation found that the resource was available and not modified
     */
    private static int checkIfNoneMatch( final HttpServletRequest request, final String eTag )
    {
        final String ifNoneMatch = request.getHeader( HttpHeaders.IF_NONE_MATCH );

        return ( ifNoneMatch != null && HttpServletUtil.checkHeaderContainsETag( ifNoneMatch, eTag ) ) ? HttpServletResponse.SC_NOT_MODIFIED : 0;
    }

    /**
     * Check <code>If-Modified-Since</code> > LastModified.
     * @param request HttpServletRequest
     * @param file File proceed
     * @return success code (0), otherwise <code>304</code> indicating that a conditional GET operation found that the resource was available and not modified
     */
    private static int checkIfModifiedSince( final HttpServletRequest request, final File file )
    {
        final String ifNoneMatch = request.getHeader( HttpHeaders.IF_NONE_MATCH );

        // In case <code>If-None-Match</code> header is set, than skip checking
        if ( ifNoneMatch == null )
        {
            long lastModified = file.lastModified();
            long ifModifiedSince = request.getDateHeader( HttpHeaders.IF_MODIFIED_SINCE );

            if ( ifModifiedSince != -1 && ( ifModifiedSince + 1000 > lastModified ) )
            {
                return HttpServletResponse.SC_NOT_MODIFIED;
            }
        }

        return 0;
    }

    /**
     * Check <code>If-Match</code> header contains "*" or ETag.
     * @param request HttpServletRequest
     * @param eTag ETag issue
     * @return success code (0), otherwise <code>412</code> indicating that the precondition given in one or more of the request-header fields evaluated to false when it was tested on the server
     */
    private static int checkIfMatch( final HttpServletRequest request, final String eTag )
    {
        final String ifMatch = request.getHeader( HttpHeaders.IF_MATCH );

        return ( ifMatch != null && !HttpServletUtil.checkHeaderContainsETag( ifMatch, eTag ) ) ? HttpServletResponse.SC_PRECONDITION_FAILED : 0;
    }

    /**
     * Check <code>If-Unmodified-Since</code> > LastModified.
     * @param request HttpServletRequest
     * @param file File proceed
     * @return success code (0), otherwise <code>412</code> indicating that the precondition given in one or more of the request-header fields evaluated to false when it was tested on the server
     */
    private static int checkIfUnmodifiedSince( final HttpServletRequest request, final File file )
    {
        long lastModified = file.lastModified();
        long ifUnmodifiedSince = request.getDateHeader( HttpHeaders.IF_UNMODIFIED_SINCE );

        return ( ifUnmodifiedSince != -1 && ( ifUnmodifiedSince + 1000 <= lastModified ) ) ? HttpServletResponse.SC_PRECONDITION_FAILED : 0;
    }

    /**
     * Process ranges.
     * @param request HttpServletRequest
     * @param ranges list of Ranges
     * @param file File proceed
     * @param eTag <code>ETag</code> header
     * @return success code (0), otherwise code <code>416</code> indicating that the server cannot serve the requested byte range
     * @throws IOException
     */
    private static int processRanges( final HttpServletRequest request, final List<Range> ranges, final File file, final String eTag )
        throws IOException
    {
        long length = file.length();
        Range root = new Range( 0, length - 1, length );

        final String range = request.getHeader( HttpHeaders.RANGE );
        if ( range != null )
        {
            if ( !range.matches( PATTERN_RANGE ) )
            {
                return HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE;
            }

            final String ifRange = request.getHeader( HttpHeaders.IF_RANGE );
            if ( ifRange != null && !ifRange.equals( eTag ) )
            {
                long ifRangeValue = request.getDateHeader( HttpHeaders.IF_RANGE );
                if ( ifRangeValue != -1 && ( ifRangeValue + 1000 < file.lastModified() ) )
                {
                    ranges.add( root );
                }
            }

            if ( ranges.isEmpty() )
            {
                for ( String part : range.substring( 6 ).split( "," ) )
                {
                    int index = part.indexOf( "-" );
                    long start = splitLong( part, 0, index );
                    long end = splitLong( part, index + 1, part.length() );

                    if ( start == -1 )
                    {
                        start = length - end;
                        end = length - 1;
                    }
                    else if ( end == -1 || end > length - 1 )
                    {
                        end = length - 1;
                    }

                    if ( start > end )
                    {
                        return HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE;
                    }

                    ranges.add( new Range( start, end, length ) );
                }
            }
        }

        return 0;
    }

    private static boolean processGZip( final HttpServletRequest request, final HttpServletResponse response, String contentType, final File file, final String eTag )
    {
        contentType = ( contentType != null ) ? contentType : "application/octet-stream";

        boolean acceptsGzip = false;
        String disposition = "inline";

        if ( contentType.startsWith( "text" ) )
        {
            String acceptEncoding = request.getHeader( HttpHeaders.ACCEPT_ENCODING );
            acceptsGzip = acceptEncoding != null && HttpServletUtil.checkHeaderContainsValue( acceptEncoding, "gzip" );
            contentType += ";charset=UTF-8";
            disposition = "inline";
        }
        else if ( !contentType.startsWith( "image" ) )
        {
            String accept = request.getHeader( HttpHeaders.ACCEPT );
            disposition = accept != null && HttpServletUtil.checkHeaderContainsValue( accept, contentType ) ? "inline" : "attachment";
        }

        response.setHeader( HttpHeaders.ACCEPT_RANGES, "bytes" );
        response.setBufferSize( DEFAULT_BUFFER_SIZE );

        if ( !response.containsHeader( HttpHeaders.PRAGMA ) )
        {
            response.setHeader( HttpHeaders.ETAG, eTag );
            response.setDateHeader( HttpHeaders.LAST_MODIFIED, file.lastModified() );

            if ( !response.containsHeader( HttpHeaders.EXPIRES ) )
            {
                setExpiresHeader( response );
            }
        }

        if ( !response.containsHeader( HttpHeaders.CONTENT_DISPOSITION ) )
        {
            String header = String.format( "%s;filename=\"%s\"", disposition, file.getName() );
            response.setHeader( HttpHeaders.CONTENT_DISPOSITION, header );
        }

        return acceptsGzip;
    }

    private static void processDownload( final HttpServletResponse response, final String contentType, final File file, final List<Range> ranges, final boolean acceptGzip )
        throws IOException
    {
        RandomAccessFile input = null;
        OutputStream output = null;

        try
        {
            input = new RandomAccessFile( file, "r" );
            output = response.getOutputStream();

            if ( ranges.isEmpty() || ranges.get( 0 ).isRoot( file.length() ) )
            {

                Range root = new Range( 0, file.length() - 1, file.length() );
                response.setContentType( contentType );
                String header = String.format( "bytes %d-%d/%d", root.getStart(), root.getEnd(), root.getTotal() );
                response.setHeader( HttpHeaders.CONTENT_RANGE, header );

                if ( acceptGzip )
                {
                    response.setHeader( HttpHeaders.CONTENT_ENCODING, "gzip" );
                    output = new GZIPOutputStream( output, DEFAULT_BUFFER_SIZE );
                }
                else
                {
                    response.setHeader( HttpHeaders.CONTENT_LENGTH, String.valueOf( root.getLength() ) );
                }

                // Copy complete file
                copy( input, output, root );
            }
            else if ( ranges.size() == 1 )
            {

                Range section = ranges.get( 0 );

                response.setContentType( contentType );
                response.setStatus( HttpServletResponse.SC_PARTIAL_CONTENT );
                String header = String.format( "bytes %d-%d/%d", section.getStart(), section.getEnd(), section.getTotal() );
                response.setHeader( HttpHeaders.CONTENT_RANGE, header );
                response.setHeader( HttpHeaders.CONTENT_LENGTH, String.valueOf( section.getLength() ) );

                // Copy single section
                copy( input, output, section );
            }
            else
            {
                response.setStatus( HttpServletResponse.SC_PARTIAL_CONTENT );
                response.setContentType( "multipart/byteranges; boundary=" + SEPARATOR );

                for ( Range section : ranges )
                {
                    // Copy multiple sections
                    copy( input, output, section );
                }
            }
        }
        finally
        {
            if ( input != null )
            {
                input.close();
                input = null;
            }

            if ( output != null )
            {
                output.close();
                output = null;
            }
        }
    }

    private static void setExpiresHeader( final HttpServletResponse response )
    {
        DateTime now = new DateTime();
        DateTime expirationDate = now.plusWeeks( 1 );

        HttpServletUtil.setExpiresHeader( response, expirationDate.toDate() );
    }

    private static long splitLong( final String value, final int start, final int finish )
    {
        String substring = value.substring( start, finish );
        return ( substring.length() > 0 ) ? Long.parseLong( substring ) : -1;
    }

    private static void copy( final RandomAccessFile input, final OutputStream output, final Range range )
        throws IOException
    {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int read;

        // all
        if ( input.length() == range.getLength() )
        {
            while ( ( read = input.read( buffer ) ) > 0 )
            {
                output.write( buffer, 0, read );
            }
        }
        // partition
        else
        {
            input.seek( range.getStart() );
            long length = range.getLength();

            while ( ( read = input.read( buffer ) ) > 0 )
            {
                if ( ( length -= read ) > 0 )
                {
                    output.write( buffer, 0, read );
                }
                else
                {
                    output.write( buffer, 0, (int) length + read );
                    break;
                }
            }
        }
    }
}
