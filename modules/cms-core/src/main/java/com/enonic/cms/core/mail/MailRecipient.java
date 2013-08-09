/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.mail;

import com.enonic.cms.core.security.user.UserEntity;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: May 7, 2010
 * Time: 10:18:56 AM
 */
public class MailRecipient
{
    private String name;

    private String email;

    private MailRecipientType type;

    public MailRecipient( String name, String email )
    {
        this( name, email, MailRecipientType.TO_RECIPIENT );
    }

    public MailRecipient( UserEntity user )
    {
        this( user.getDisplayName(), user.getEmail() );
    }

    public MailRecipient( final String name, final String email, final MailRecipientType type )
    {
        this.name = name;
        this.email = email;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public MailRecipientType getType()
    {
        return type;
    }

    public void setType( final MailRecipientType type )
    {
        this.type = type;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        MailRecipient that = (MailRecipient) o;

        if ( email != null ? !email.equals( that.email ) : that.email != null )
        {
            return false;
        }
        if ( name != null ? !name.equals( that.name ) : that.name != null )
        {
            return false;
        }

        if ( type != that.type )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + ( email != null ? email.hashCode() : 0 );
        return result;
    }
}
