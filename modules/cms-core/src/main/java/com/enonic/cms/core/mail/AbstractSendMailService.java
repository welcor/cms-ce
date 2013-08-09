/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.mail;

import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.UserDao;

public abstract class AbstractSendMailService
{
    protected final Logger log;

    private JavaMailSender mailSender;

    private UserDao userDao;

    private static final String MAIL_ENCODING = "UTF-8";

    public AbstractSendMailService()
    {
        this.log = LoggerFactory.getLogger( getClass() );
    }

    private String fromMail;

    public final void setFromMail( String value )
    {
        this.fromMail = value;
    }

    public final void sendMail( AbstractMailTemplate template )
    {
        try
        {
            MessageSettings settings = new MessageSettings();

            setFromSettings( template, settings );

            settings.setBody( template.getBody() );

            final MimeMessageHelper message = createMessage( settings, template.isHtml() || !template.getAttachments().isEmpty() );
            message.setSubject( template.getSubject() );

            final Map<String,InputStream> attachments = template.getAttachments();

            for ( final Map.Entry<String, InputStream> attachment : attachments.entrySet() )
            {
                final InputStreamSource inputStreamSource = new InputStreamResource( attachment.getValue() );
                message.addAttachment( attachment.getKey(), inputStreamSource );
            }

            if ( template.isHtml() )
            {
                message.setText( "[You need html supported mail client to read this email]", template.getBody() );
            }
            else
            {
                message.setText( template.getBody() );
            }

            if ( template.getMailRecipients().size() == 0 )
            {
                this.log.info( "No recipients specified, mail not sent." );
            }

            for ( MailRecipient recipient : template.getMailRecipients() )
            {
                if ( recipient.getEmail() != null )
                {
                    final MailRecipientType type = recipient.getType();

                    switch ( type )
                    {
                        case TO_RECIPIENT:
                            message.addTo( recipient.getEmail(), recipient.getName() );
                            break;
                        case BCC_RECIPIENT:
                            message.addBcc( recipient.getEmail(), recipient.getName() );
                            break;
                        case CC_RECIPIENT:
                            message.addCc( recipient.getEmail(), recipient.getName() );
                            break;
                        default:
                            throw new RuntimeException( "Unknown recipient type: " + type );
                    }
                }
            }

            sendMessage( message );
        }
        catch ( Exception e )
        {
            this.log.error( "Failed to send mail", e );
        }
    }

    private void setFromSettings( AbstractMailTemplate template, MessageSettings settings )
    {
        MailRecipient fromRecipient = template.getFrom();

        if ( fromRecipient != null && fromRecipient.getEmail() != null )
        {
            settings.setFromMail( fromRecipient.getEmail() );
            settings.setFromName( fromRecipient.getName() );
        }
        else
        {
            settings.setFromMail( this.fromMail );
            settings.setFromName( null );
        }
    }

    public final void sendChangePasswordMail( QualifiedUsername userName, String newPassword )
    {
        sendChangePasswordMail( userName, newPassword, null );
    }

    public final void sendChangePasswordMail( QualifiedUsername userName, String newPassword, MessageSettings settings )
    {
        UserEntity entity = this.userDao.findByQualifiedUsername( userName );
        if ( entity != null )
        {
            sendChangePasswordMail( entity, newPassword, settings );
        }
        else
        {
            this.log.warn( "Unknown user [" + userName + "]. Skipped sending mail." );
        }
    }

    private void sendChangePasswordMail( UserEntity user, String newPassword, MessageSettings settings )
    {
        try
        {
            settings = createSettingsIfNeeded( settings );
            MimeMessageHelper message = createMessage( settings, false );
            composeChangePasswordMail( message, user, newPassword, settings );
            sendMessage( message );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to send mail", e );
        }
    }

    private MessageSettings createSettingsIfNeeded( MessageSettings settings )
    {
        if ( settings == null )
        {
            settings = new MessageSettings();
        }

        if ( settings.getFromMail() == null )
        {
            settings.setFromMail( this.fromMail );
            settings.setFromName( null );
        }

        return settings;
    }

    private MimeMessageHelper createMessage( MessageSettings settings, boolean multipart )
        throws Exception
    {
        final MimeMessageHelper message = new MimeMessageHelper( this.mailSender.createMimeMessage(), multipart, MAIL_ENCODING );
        message.setFrom( settings.getFromMail(), settings.getFromName() );
        return message;
    }

    private void sendMessage( MimeMessageHelper message )
    {
        this.mailSender.send( message.getMimeMessage() );
    }


    protected abstract void composeChangePasswordMail( MimeMessageHelper message, UserEntity user, String newPassword,
                                                       MessageSettings settings )
        throws Exception;

    public void setMailSender( final JavaMailSender mailSender )
    {
        this.mailSender = mailSender;
    }

    public void setUserDao( final UserDao userDao )
    {
        this.userDao = userDao;
    }
}
