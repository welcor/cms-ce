/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.web.webdav;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.io.OutputContext;

import com.enonic.cms.core.product.ProductVersion;

final class DavFolderIndexWriter
{
    private final DavResource resource;

    public DavFolderIndexWriter( final DavResource resource )
    {
        this.resource = resource;
    }

    public void write( final OutputContext out )
        throws IOException
    {
        out.setModificationTime( System.currentTimeMillis() );
        out.setContentType( "text/html" );

        if ( out.hasStream() )
        {
            write( out.getOutputStream() );
        }
    }

    private void write( final OutputStream out )
        throws IOException
    {
        final PrintWriter writer = new PrintWriter( out );
        write( writer );
        writer.flush();
        writer.close();
    }

    private void write( final PrintWriter out )
        throws IOException
    {
        out.print( "<html><head><title>" );
        out.print( this.resource.getResourcePath() );
        out.print( "</title></head>" );
        out.print( "<body><h2>" );
        out.print( "WebDav " + this.resource.getResourcePath() );
        out.print( "</h2><ul>" );

        if ( !this.resource.getResourcePath().equals( "/" ) )
        {
            out.print( "<li><a href=\"..\">..</a></li>" );
        }

        final DavResourceIterator it = this.resource.getMembers();
        while ( it.hasNext() )
        {
            final DavResource child = it.nextResource();
            final String label = Text.getName( child.getResourcePath() );
            out.print( "<li><a href=\"" );
            out.print( child.getHref() );
            out.print( "\">" );
            out.print( label );
            out.print( "</a></li>" );
        }

        out.print( "</ul><hr size=\"1\"><em>Powered by " );
        out.print( ProductVersion.getFullTitleAndVersion() );
        out.print( "</em></body></html>" );
    }
}
