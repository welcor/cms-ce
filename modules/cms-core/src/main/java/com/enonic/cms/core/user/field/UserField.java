/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.user.field;

import java.util.Arrays;

public final class UserField implements Comparable<UserField>
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

    public Object getValue()
    {
        return this.value;
    }

    public void setValue( Object value )
    {
        checkType( value );
        this.value = value;
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

    /**
     * Compares this userField with another.  String fields are considered equal if they are both blank in one way or the other.  I.e. if one
     * value is null and the other is a string with only whitespace, they are considered equal.
     *
     * @param compareField The map of fields to compare to.
     * @return 3 if the fields are not of the same type.
     *         2 if both fields have values, but they are not the same.
     *         1 if the compareField is zero.
     *         0 if both fields are equal.
     */
    public int compareTo( UserField compareField )
    {
        // TODO: Improve compareTo implementation of method, so that values that exist in both places do not return the value 2, but a comparison of the values.
        if ( compareField == null )
        {
            return 1;
        }
        else if ( getType() != compareField.getType() )
        {
            return 3;
        }
        else
        {
            if ( isOfType( UserFieldType.PHOTO ) )
            {
                byte[] commandPhoto = (byte[]) getValue();
                byte[] remotePhoto = (byte[]) compareField.getValue();
                if ( !( Arrays.equals( commandPhoto, remotePhoto ) ) )
                {
                    return 2;
                }
            }
            else
            {
                if ( !emptyValueEquals( getValue(), compareField.getValue() ) )
                {
                    if ( !( getValue().equals( compareField.getValue() ) ) )
                    {
                        return 2;
                    }
                }
            }
        }

        return 0;
    }

    public boolean emptyValueEquals( Object commandValue, Object remoteValue )
    {
        if ( commandValue == null && remoteValue == null )
        {
            return true;
        }
        if ( commandValue instanceof String )
        {
            if ( isBlank( (String) commandValue ) && isBlank( (String) remoteValue ) )
            {
                return true;
            }
        }
        return false;
    }

    public boolean isBlank( String s )
    {
        if ( s == null )
        {
            return true;
        }
        else if ( s.trim().equals( "" ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
