/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.api.plugin.ext.userstore;

import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.enonic.cms.api.client.model.user.Address;
import com.enonic.cms.api.client.model.user.Gender;

public final class UserField
{
    private final UserFieldType type;

    private Object value;

    public UserField( UserFieldType type )
    {
        this( type, null );
    }

    public UserField( UserFieldType type, Object value )
    {
        this.type = type;
        setValue( value );
    }

    public UserFieldType getType()
    {
        return this.type;
    }

    public boolean isOfType( UserFieldType type )
    {
        return this.type == type;
    }

    public boolean isBirthday()
    {
        return isOfType( UserFieldType.BIRTHDAY );
    }

    public boolean isAddress()
    {
        return isOfType( UserFieldType.ADDRESS );
    }

    public boolean isPhoto()
    {
        return isOfType( UserFieldType.PHOTO );
    }

    public Object getValue()
    {
        return this.value;
    }

    public String getValueAsString()
    {
        return (String) this.value;
    }

    public Date getValueAsDate()
    {
        return (Date) this.value;
    }

    public Locale getValueAsLocale()
    {
        return (Locale) this.value;
    }

    public Boolean getValueAsBoolean()
    {
        return (Boolean) this.value;
    }

    public Gender getValueAsGender()
    {
        return (Gender) this.value;
    }

    public TimeZone getValueAsTimeZone()
    {
        return (TimeZone) this.value;
    }

    public byte[] getValueAsBytes()
    {
        return (byte[]) this.value;
    }

    public Address getValueAsAddress()
    {
        return (Address) this.value;
    }

    public void setValue( Object value )
    {
        checkType( value );
        this.value = value;
    }

    private void checkType( Object value )
    {
        if ( value == null )
        {
            return;
        }

        Class<?> clz = value.getClass();
        if ( !this.type.isOfType( clz ) )
        {
            throw new IllegalArgumentException( "Value must be of type [" + this.type.getTypeClass() + "]" );
        }
    }


    public boolean equals( UserField compareField )
    {
        if ( compareField == null )
        {
            return false;
        }
        else if ( getType() != compareField.getType() )
        {
            return false;
        }
        else if ( getValue() == null && compareField.getValue() == null )
        {
            return true;
        }
        else if ( getValue() == null && compareField.getValue() != null )
        {
            return false;
        }
        else if ( getValue() != null && compareField.getValue() == null )
        {
            return false;
        }
        else
        {
            if ( isOfType( UserFieldType.PHOTO ) )
            {
                byte[] commandPhoto = (byte[]) getValue();
                byte[] remotePhoto = (byte[]) compareField.getValue();
                if ( !( Arrays.equals( commandPhoto, remotePhoto ) ) )
                {
                    return false;
                }
            }
            else
            {
                if ( bothAreBlankStrings( getValue(), compareField.getValue() ) )
                {
                    return true;
                }

                if ( !( getValue().equals( compareField.getValue() ) ) )
                {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean bothAreBlankStrings( Object a, Object b )
    {
        if ( a instanceof String )
        {
            if ( isBlank( (String) a ) && isBlank( (String) b ) )
            {
                return true;
            }
        }
        return false;
    }

    private boolean isBlank( final String string )
    {
        return string == null || "".equals( string );
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

        UserField userField = (UserField) o;

        if ( type != userField.type )
        {
            return false;
        }
        if ( value != null ? !value.equals( userField.value ) : userField.value != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return type.hashCode() + value.hashCode();
    }
}
