/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.mail;

public class SimpleMailTemplate
    extends AbstractMailTemplate
{
    private String body;

    private boolean isHtml;

    private String subject;

    public SimpleMailTemplate()
    {
        isHtml = false;
    }

    @Override
    public String getBody()
    {
        return this.body;
    }

    @Override
    public String getSubject()
    {
        return this.subject;
    }

    public void setSubject( final String subject )
    {
        this.subject = subject;
    }

    public void setMessage( final String message )
    {
        this.body = message;
    }

    public void addRecipient( final String displayName, final String email, final MailRecipientType type )
    {
        addRecipient( new MailRecipient( displayName, email, type) );
    }

    public void setFrom( final String displayName, final String email )
    {
        setFrom( new MailRecipient( displayName, email ) );

    }

    public void setMessage( final String body, final boolean isHtml )
    {
        this.body = body;
        this.isHtml = isHtml;
    }

    public boolean isHtml()
    {
        return isHtml;
    }
}
