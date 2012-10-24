package com.enonic.cms.core.portal.datasource.handler;

import org.springframework.core.convert.ConversionFailedException;

import com.google.common.base.Strings;

import com.enonic.cms.core.portal.datasource.DataSourceException;

final class DataSourceParamImpl
    implements DataSourceParam
{
    private final String dsName;

    private final String name;

    private final String value;

    public DataSourceParamImpl( final String dsName, final String name, final String value )
    {
        this.dsName = dsName;
        this.name = name;
        this.value = Strings.emptyToNull( value );
    }

    @Override
    public DataSourceParamImpl required()
    {
        if ( this.value == null )
        {
            throw newException( "Parameter is required", null );
        }

        return this;
    }

    @Override
    public String asString()
    {
        return this.value;
    }

    @Override
    public String asString( final String defValue )
    {
        return optional( asString(), defValue );
    }

    @Override
    public Integer asInteger()
    {
        if ( this.value == null )
        {
            return null;
        }

        try
        {
            return ParameterConverter.getInstance().toInteger( this.value );
        }
        catch ( final ConversionFailedException e )
        {
            throw newException( e );
        }
    }

    @Override
    public Integer asInteger( final Integer defValue )
    {
        return optional( asInteger(), defValue );
    }

    @Override
    public Boolean asBoolean()
    {
        if ( this.value == null )
        {
            return null;
        }

        try
        {
            return ParameterConverter.getInstance().toBoolean( this.value );
        }
        catch ( final ConversionFailedException e )
        {
            throw newException( e );
        }
    }

    @Override
    public Boolean asBoolean( final Boolean defValue )
    {
        return optional( asBoolean(), defValue );
    }

    @Override
    public String[] asStringArray()
    {
        if ( this.value == null )
        {
            return new String[0];
        }

        try
        {
            return ParameterConverter.getInstance().toStringArray( this.value );
        }
        catch ( final ConversionFailedException e )
        {
            throw newException( e );
        }
    }

    @Override
    public String[] asStringArray( final String... defValues )
    {
        return optional( asStringArray(), defValues );
    }

    @Override
    public Integer[] asIntegerArray()
    {
        if ( this.value == null )
        {
            return new Integer[0];
        }

        try
        {
            return ParameterConverter.getInstance().toIntegerArray( this.value );
        }
        catch ( final ConversionFailedException e )
        {
            throw newException( e );
        }
    }

    @Override
    public Integer[] asIntegerArray( final Integer... defValues )
    {
        return optional( asIntegerArray(), defValues );
    }

    private <T> T optional( final T value, final T defValue )
    {
        if ( value == null )
        {
            return defValue;
        }

        return value;
    }

    private <T> T[] optional( final T[] array, final T... defValues )
    {
        if ( array == null )
        {
            return defValues;
        }

        return array;
    }

    private DataSourceException newException( final String message, final Throwable cause )
    {
        return new DataSourceException( "[{0}.{1}] {2}", this.dsName, this.name, message ).withCause( cause );
    }

    private DataSourceException newException( final ConversionFailedException cause )
    {
        return newException( cause.getMessage(), null );
    }
}
