/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.web.portal.services;

import java.io.IOException;
import java.text.ParseException;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.core.mail.MailRecipientType;
import com.enonic.cms.core.mail.SendMailService;
import com.enonic.cms.core.mail.SimpleMailTemplate;
import com.enonic.cms.core.service.UserServicesService;
import com.enonic.cms.core.structure.SiteKey;

public abstract class SendMailServicesBase
    extends ContentServicesBase
{
    public final static int ERR_RECIPIENT_HAS_NO_EMAIL_ADDRESS = 100;

    public final static int ERR_RECIPIENT_HAS_WRONG_ADDRESS_NO_ALPHA = 101;

    public final static int ERR_RECIPIENT_HAS_WRONG_ADDRESS_MISSING_DOT = 102;

    public final static int ERR_MISSING_FROM_FIELDS = 103;

    public final static int ERR_MISSING_TO_FIELD = 104;

    public final static int ERR_MISSING_SUBJECT_FIELD = 105;

    public SendMailServicesBase( final String handlerName )
    {
        super( handlerName );
    }

    protected void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, ExtendedMap formItems,
                                  UserServicesService userServices, SiteKey siteKey, String operation )
        throws VerticalUserServicesException, VerticalEngineException, IOException, ClassNotFoundException, IllegalAccessException,
        InstantiationException, ParseException
    {
        if ( operation.equals( "send" ) )
        {
            try
            {

                final SimpleMailTemplate mail = new SimpleMailTemplate();

                // from field
                String fromName = formItems.getString( "from_name", "" );
                String fromEmail = formItems.getString( "from_email", "" );
                if ( StringUtils.isEmpty( fromName ) && StringUtils.isEmpty( fromEmail ) )
                {
                    String message = "No \"from\" fields given. " + "At least one of \"from_name\" and \"from_email\" is required.";
                    VerticalUserServicesLogger.warn( message );
                    redirectToErrorPage( request, response, formItems, ERR_MISSING_FROM_FIELDS );
                    return;
                }
                mail.setFrom( fromName, fromEmail );

                // to field
                String[] recipients = formItems.getStringArray( "to" );
                if ( recipients.length == 0 )
                {
                    String message = "No \"to\" fields given. At least one is required.";
                    VerticalUserServicesLogger.warn( message );
                    redirectToErrorPage( request, response, formItems, ERR_MISSING_TO_FIELD );
                    return;
                }
                else
                {
                    int error = addRecipients( mail, recipients, MailRecipientType.TO_RECIPIENT );
                    if ( error >= 0 )
                    {
                        redirectToErrorPage( request, response, formItems, error );
                        return;
                    }
                }

                // bcc field
                recipients = formItems.getStringArray( "bcc" );
                if ( recipients.length > 0 )
                {
                    int error = addRecipients( mail, recipients, MailRecipientType.BCC_RECIPIENT );
                    if ( error >= 0 )
                    {
                        redirectToErrorPage( request, response, formItems, error );
                        return;
                    }
                }

                // cc field
                recipients = formItems.getStringArray( "cc" );
                if ( recipients.length > 0 )
                {
                    int error = addRecipients( mail, recipients, MailRecipientType.CC_RECIPIENT );
                    if ( error >= 0 )
                    {
                        redirectToErrorPage( request, response, formItems, error );
                        return;
                    }
                }

                // subject
                String subject = formItems.getString( "subject" );
                if ( subject == null || subject.length() == 0 )
                {
                    String message = "No \"subject\" field given. A subject field is required.";
                    VerticalUserServicesLogger.warn( message );
                    redirectToErrorPage( request, response, formItems, ERR_MISSING_SUBJECT_FIELD );
                    return;
                }
                else
                {
                    mail.setSubject( subject );
                }

                // body
                StringBuffer body = new StringBuffer( 40 * formItems.size() );
                String sortOrder = formItems.getString( "sort_order" );
                if ( sortOrder != null && sortOrder.length() > 0 )
                {
                    StringTokenizer st = new StringTokenizer( sortOrder, "," );
                    while ( st.hasMoreTokens() )
                    {
                        String key = st.nextToken();
                        if ( formItems.containsKey( key ) )
                        {
                            Object value = formItems.get( key );
                            if ( value instanceof String )
                            {
                                body.append( key );
                                body.append( ": " );
                                if ( value.toString().indexOf( '\n' ) >= 0 )
                                {
                                    body.append( '\n' );
                                }
                                body.append( value );
                                body.append( '\n' );
                            }
                            else if ( value instanceof String[] )
                            {
                                String[] values = (String[]) value;
                                for ( int i = 0; i < values.length; i++ )
                                {
                                    body.append( key );
                                    body.append( ": " );
                                    if ( values[i].indexOf( '\n' ) >= 0 )
                                    {
                                        body.append( '\n' );
                                    }
                                    body.append( values[i] );
                                    body.append( '\n' );
                                }
                            }
                            else if ( value instanceof Boolean )
                            {
                                body.append( key );
                                body.append( ": " );
                                if ( value.toString().indexOf( '\n' ) >= 0 )
                                {
                                    body.append( '\n' );
                                }
                                body.append( value );
                                body.append( '\n' );
                            }
                        }
                    }
                }
                mail.setMessage( body.toString() );

                // attachments?
                boolean includeAttachment = formItems.getBoolean( "include_attachment", true );

                if ( includeAttachment && formItems.hasFileItems() )
                {
                    FileItem[] fileItems = formItems.getFileItems();
                    for ( int i = 0; i < fileItems.length; i++ )
                    {
                        mail.addAttachment( fileItems[i].getName(), fileItems[i].getInputStream() );
                    }
                }

                sendMailService.sendMail( mail );

                redirectToPage( request, response, formItems );
            }
            catch ( Exception esle )
            {
                String message = "Failed to send email: %t";
                VerticalUserServicesLogger.error( message, esle );
                redirectToErrorPage( request, response, formItems, ERR_EMAIL_SEND_FAILED );
            }
        }
        else
        {
            super.handlerCustom( request, response, session, formItems, userServices, siteKey, operation );
        }
    }

    private int addRecipients( SimpleMailTemplate mail, String[] recipients, MailRecipientType type )
    {

        for ( int i = 0; i < recipients.length; i++ )
        {

            // skip empty recipients when recipient type is bcc or cc
            if ( ( type == MailRecipientType.BCC_RECIPIENT || type == MailRecipientType.CC_RECIPIENT ) && recipients[i].trim().length() == 0 )
            {
                continue;
            }

            String name, email;

            int scIdx = recipients[i].indexOf( ';' );
            if ( scIdx >= 0 )
            {
                name = recipients[i].substring( 0, scIdx );
                email = recipients[i].substring( scIdx + 1 );
            }
            else
            {
                name = null;
                email = recipients[i];
            }

            // simple validation of email address:
            // 1. cannot be null
            // 2. must include an '@' and at least one '.' after the '@'
            if ( email == null || email.trim().length() == 0 )
            {
                String message = "{0} email address not given.";
                VerticalUserServicesLogger.warn( message, type.getShortName(), null );
                return ERR_RECIPIENT_HAS_NO_EMAIL_ADDRESS;
            }
            else
            {
                int idx = email.indexOf( '@' );
                if ( idx <= 0 )
                {
                    String message = "{0} email address is in wrong format. Include at least one '@': {1}";
                    Object[] objects = new Object[]{type.getShortName(), email};
                    VerticalUserServicesLogger.warn( message, objects );
                    return ERR_RECIPIENT_HAS_WRONG_ADDRESS_NO_ALPHA;
                }
                else if ( email.indexOf( '.', idx ) < 0 )
                {
                    String message = "{0} email address is in wrong format. Include at least one '.': {1}";
                    Object[] objects = new Object[]{type.getShortName(), email};
                    VerticalUserServicesLogger.warn( message, objects );
                    return ERR_RECIPIENT_HAS_WRONG_ADDRESS_MISSING_DOT;
                }

                mail.addRecipient( name, email, type );
            }
        }

        return -1;
    }

    @Autowired
    public void setSendMailService( final SendMailService sendMailService )
    {
        this.sendMailService = sendMailService;
    }
}
