/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.net;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mail
{
    /**
     * Logger.
     */
    private final static Logger LOG = LoggerFactory.getLogger( Mail.class );

    final public static short TO_RECIPIENT = 0;

    final public static short CC_RECIPIENT = 1;

    final public static short BCC_RECIPIENT = 2;

    private String from_name;

    private String from_email;

    private String subject;

    private String message;

    private String htmlMessage;

    ArrayList<String[]> bcc = new ArrayList<String[]>();

    ArrayList<String[]> cc = new ArrayList<String[]>();

    ArrayList<String[]> to = new ArrayList<String[]>();

    private final static String DEFAULT_SMTPHOST = "localhost";

    private String smtpHost;

    private final static String ENCODING = "UTF-8";

    private ArrayList<FileItem> attachments = new ArrayList<FileItem>();

    /**
     * <p/> Send the mail. The SMTP host is contacted and the mail is sent according to the parameters set. </p> <p/> If it fails, it is
     * considered a runtime exception. Note that this doesn't make it very failsafe, so care should be taken when one wants fault tolerance.
     * One solution could be to catch the exception thrown. Another solution could be to use the JavaMail API directly. </p>
     */
    public void send()
    {
        // smtp server
        Properties smtpProperties = new Properties();
        if ( smtpHost != null )
        {
            smtpProperties.put( "mail.smtp.host", smtpHost );
            System.setProperty( "mail.smtp.host", smtpHost );
        }
        else
        {
            smtpProperties.put( "mail.smtp.host", DEFAULT_SMTPHOST );
            System.setProperty( "mail.smtp.host", DEFAULT_SMTPHOST );
        }
        Session session = Session.getDefaultInstance( smtpProperties, null );

        try
        {
            // create message
            Message msg = new MimeMessage( session );
            // set from address
            InternetAddress addressFrom = new InternetAddress();
            if ( from_email != null )
            {
                addressFrom.setAddress( from_email );
            }
            if ( from_name != null )
            {
                addressFrom.setPersonal( from_name, ENCODING );
            }
            msg.setFrom( addressFrom );

            if ( ( to.size() == 0 && bcc.size() == 0 ) || subject == null ||
                    ( message == null && htmlMessage == null ) )
            {
                LOG.error( "Missing data. Unable to send mail." );
                throw new IllegalArgumentException( "Missing data. Unable to send mail." );
            }

            for ( final String[] recipient : this.to )
            {
                InternetAddress addressTo = new InternetAddress( recipient[1] );
                if ( recipient[0] != null )
                {
                    addressTo.setPersonal( recipient[0], ENCODING );
                }
                msg.addRecipient( Message.RecipientType.TO, addressTo );
            }

            for ( final String[] recipient : this.bcc )
            {
                InternetAddress addressTo = null;
                try
                {
                    addressTo = new InternetAddress( recipient[1] );
                }
                catch ( Exception e )
                {
                    System.err.println( "exception on address: " + recipient[1] );
                    continue;
                }
                if ( recipient[0] != null )
                {
                    addressTo.setPersonal( recipient[0], ENCODING );
                }
                msg.addRecipient( Message.RecipientType.BCC, addressTo );
            }

            for ( final String[] recipient : this.cc )
            {
                InternetAddress addressTo = new InternetAddress( recipient[1] );
                if ( recipient[0] != null )
                {
                    addressTo.setPersonal( recipient[0], ENCODING );
                }
                msg.addRecipient( Message.RecipientType.CC, addressTo );
            }

            // Setting subject and content type
            ( (MimeMessage) msg ).setSubject( subject, ENCODING );

            if ( message != null )
            {
                message = message.replaceAll( "\\\\n", "\n" );
            }

            // if there are any attachments, treat this as a multipart message.
            if ( attachments.size() > 0 )
            {
                BodyPart messageBodyPart = new MimeBodyPart();
                if ( message != null )
                {
                    ( (MimeBodyPart) messageBodyPart ).setText( message, ENCODING );
                }
                else
                {
                    DataHandler dataHandler =
                            new DataHandler( new ByteArrayDataSource( htmlMessage, "text/html", ENCODING ) );
                    messageBodyPart.setDataHandler( dataHandler );
                }
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart( messageBodyPart );

                // add all attachments
                for ( final FileItem fileItem : this.attachments )
                {
                    messageBodyPart = new MimeBodyPart();
                    FileItemDataSource fds = new FileItemDataSource( fileItem );
                    messageBodyPart.setDataHandler( new DataHandler( fds ) );
                    messageBodyPart.setFileName( fds.getName() );
                    multipart.addBodyPart( messageBodyPart );
                }

                msg.setContent( multipart );
            }
            else
            {
                if ( message != null )
                {
                    ( (MimeMessage) msg ).setText( message, ENCODING );
                }
                else
                {
                    DataHandler dataHandler =
                            new DataHandler( new ByteArrayDataSource( htmlMessage, "text/html", ENCODING ) );
                    msg.setDataHandler( dataHandler );
                }
            }

            // send message
            Transport.send( msg );
        }
        catch ( AddressException e )
        {
            String MESSAGE_30 = "Error in email address: " + e.getMessage();
            LOG.warn( MESSAGE_30 );
            throw new IllegalArgumentException( MESSAGE_30, e );
        }
        catch ( UnsupportedEncodingException e )
        {
            String MESSAGE_40 = "Unsupported encoding: " + e.getMessage();
            LOG.error( MESSAGE_40, e );
            throw new IllegalArgumentException( MESSAGE_40, e );
        }
        catch ( SendFailedException sfe )
        {
            Throwable t = null;
            Exception e = sfe.getNextException();
            while ( e != null )
            {
                t = e;
                if ( t instanceof SendFailedException )
                {
                    e = ( (SendFailedException) e ).getNextException();
                }
                else
                {
                    e = null;
                }
            }
            if ( t != null )
            {
                String MESSAGE_50 = "Error sending mail: " + t.getMessage();
                throw new IllegalArgumentException( MESSAGE_50, t );
            }
            else
            {
                String MESSAGE_50 = "Error sending mail: " + sfe.getMessage();
                throw new IllegalArgumentException( MESSAGE_50, sfe );
            }
        }
        catch ( MessagingException e )
        {
            String MESSAGE_50 = "Error sending mail: " + e.getMessage();
            LOG.error( MESSAGE_50, e );
            throw new IllegalArgumentException( MESSAGE_50, e );
        }
    }

    /**
     * Set the sender name and email address.
     */
    public void setFrom( String name, String email )
    {
        from_name = name;
        from_email = email;
    }

    /**
     * Set the message body that will be used in the mail. The message is plain text.
     */
    public void setMessage( String message )
    {
        setMessage( message, false );
    }

    /**
     * Set the message body that will be used in the mail. If html is <strong>true</strong>, then message must be a html message.
     */
    public void setMessage( String message, boolean html )
    {
        if ( html )
        {
            this.message = null;
            this.htmlMessage = message;
        }
        else
        {
            this.message = message;
            this.htmlMessage = null;
        }
    }

    /**
     * Get the hostname used as the mail server. Outgoing mail is sent using the SMTP server at port 25 on this host.
     */
    public void setSMTPHost( String newHost )
    {
        smtpHost = newHost;
    }

    /**
     * Set the subject of the mail.
     */
    public void setSubject( String subject )
    {
        this.subject = subject;
    }

    /**
     * Add a recipient. Duh..
     */
    public void addRecipient( String name, String email, short type )
    {
        if ( type == TO_RECIPIENT )
        {
            to.add( new String[]{name, email} );
        }
        else if ( type == BCC_RECIPIENT )
        {
            bcc.add( new String[]{name, email} );
        }
        else if ( type == CC_RECIPIENT )
        {
            cc.add( new String[]{name, email} );
        }
    }

    public void clearRecipients()
    {
        to.clear();
        cc.clear();
        bcc.clear();
    }

    public void addAttachment( FileItem fi )
    {
        attachments.add( fi );
    }

    private final class ByteArrayDataSource
            implements DataSource
    {
        private byte[] data; // data

        private String type; // content-type

        /* Create a DataSource from a String */
        public ByteArrayDataSource( String data, String type, String encoding )
        {
            try
            {
                // Assumption that the string contains only ASCII
                // characters!  Otherwise just pass a charset into this
                // constructor and use it in getBytes()
                this.data = data.getBytes( encoding );
            }
            catch ( UnsupportedEncodingException uex )
            {
            }
            this.type = type + "; charset=" + encoding;
        }

        public String getContentType()
        {
            return type;
        }

        /**
         * Return an InputStream for the data. Note - a new stream must be returned each time.
         */
        public InputStream getInputStream()
                throws IOException
        {
            if ( data == null )
            {
                throw new IOException( "no data" );
            }
            return new ByteArrayInputStream( data );
        }

        public String getName()
        {
            return "dummy";
        }

        public OutputStream getOutputStream()
                throws IOException
        {
            throw new IOException( "cannot do this" );
        }
    }

    private final class FileItemDataSource
        implements DataSource
    {

        /**
         * File item.
         */
        private final FileItem item;

        /**
         * Construct the data source.
         */
        public FileItemDataSource( FileItem item )
        {
            this.item = item;
        }

        /**
         * Return the content type.
         */
        public String getContentType()
        {
            return this.item.getContentType();
        }

        /**
         * Return the input stream.
         */
        public InputStream getInputStream()
                throws IOException
        {
            return this.item.getInputStream();
        }

        /**
         * Return the name.
         */
        public String getName()
        {
            File file = new File( this.item.getName() );
            return file.getName();
        }

        /**
         * Return the output stream.
         */
        public OutputStream getOutputStream()
                throws IOException
        {
            return this.item.getOutputStream();
        }
    }
}
