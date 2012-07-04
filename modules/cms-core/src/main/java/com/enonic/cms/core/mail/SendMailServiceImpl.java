/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.mail;

import org.springframework.mail.javamail.MimeMessageHelper;

import com.enonic.cms.core.security.user.UserEntity;

public final class SendMailServiceImpl
    extends AbstractSendMailService
    implements SendMailService
{
    private String defaultSubjectForNewPasswordEmail;

    private String defaultBodyForNewPasswordEmail;


    protected void composeChangePasswordMail( MimeMessageHelper message, UserEntity user, String newPassword, MessageSettings settings )
        throws Exception
    {
        String subject = settings.getSubject();
        if ( subject == null )
        {
            subject = defaultSubjectForNewPasswordEmail;
        }

        String body = settings.getBody();
        if ( body == null )
        {
            body = defaultBodyForNewPasswordEmail;
        }

        message.addTo( user.getEmail(), user.getDisplayName() );
        message.setSubject( subject );

        body = body.replaceAll( "%password%", newPassword );
        body = body.replaceAll( "%uid%", user.getName() );

        message.setText( body );
    }

    public void setDefaultSubjectForNewPasswordEmail( final String defaultSubjectForNewPasswordEmail )
    {
        this.defaultSubjectForNewPasswordEmail = defaultSubjectForNewPasswordEmail;
    }

    public void setDefaultBodyForNewPasswordEmail( final String defaultMailBodyForNewPasswordEmail )
    {
        this.defaultBodyForNewPasswordEmail = defaultMailBodyForNewPasswordEmail;
    }
}
