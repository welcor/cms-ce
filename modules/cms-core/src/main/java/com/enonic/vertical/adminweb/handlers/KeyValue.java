package com.enonic.vertical.adminweb.handlers;

class KeyValue
    implements Comparable<KeyValue>
{
    protected int key;

    protected String value;

    public KeyValue( int key, String value )
    {
        this.key = key;
        this.value = value;
    }

    public int compareTo( KeyValue o )
    {

        return value.compareTo( o.value );
    }

    @Override
    public String toString()
    {
        return "KeyValue [key=" + key + ", value=" + value + "]";
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + key;
        result = prime * result + ( ( value == null ) ? 0 : value.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        KeyValue other = (KeyValue) obj;
        if ( key != other.key )
        {
            return false;
        }
        if ( value == null )
        {
            if ( other.value != null )
            {
                return false;
            }
        }
        else if ( !value.equals( other.value ) )
        {
            return false;
        }
        return true;
    }


}
