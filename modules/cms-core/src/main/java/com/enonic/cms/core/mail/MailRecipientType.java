/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.mail;

public enum MailRecipientType
{
    TO_RECIPIENT( "To" ),
    CC_RECIPIENT( "Cc" ),
    BCC_RECIPIENT( "Bcc" );

    private String address;

    MailRecipientType( final String address )
    {
        this.address = address;
    }

    public String getShortName()
    {
        return address;
    }
}
