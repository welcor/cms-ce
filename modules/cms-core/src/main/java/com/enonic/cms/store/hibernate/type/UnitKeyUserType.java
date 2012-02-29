package com.enonic.cms.store.hibernate.type;


import com.enonic.cms.core.content.category.UnitKey;


public class UnitKeyUserType
    extends AbstractIntegerBasedUserType<UnitKey>
{
    public UnitKeyUserType()
    {
        super( UnitKey.class );
    }

    public boolean isMutable()
    {
        return false;
    }

    public UnitKey get( int value )
    {
        return new UnitKey( value );
    }

    public Integer getIntegerValue( UnitKey value )
    {
        return value.toInt();
    }
}