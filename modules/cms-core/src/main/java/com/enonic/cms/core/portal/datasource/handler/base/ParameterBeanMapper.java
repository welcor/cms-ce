package com.enonic.cms.core.portal.datasource.handler.base;

import java.lang.reflect.Field;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import com.enonic.cms.core.portal.datasource.DataSourceException;

final class ParameterBeanMapper<T>
{
    private final Class<T> type;

    private final String dsName;

    private final Map<String, Field> fields;

    private ParameterBeanMapper( final Class<T> type, final String dsName )
    {
        this.type = type;
        this.dsName = dsName;
        this.fields = Maps.newHashMap();
        initializeMappings();
    }

    private void initializeMappings()
    {
        for ( final Field field : this.type.getFields() )
        {
            this.fields.put( field.getName(), field );
        }
    }

    public T newBean( final Map<String, String> params )
    {
        final T bean = newParamsBean();
        applyParams( bean, params );
        checkRequired( bean );
        return bean;
    }

    private T newParamsBean()
    {
        try
        {
            return this.type.newInstance();
        }
        catch ( final Exception e )
        {
            throw new DataSourceException( "Failed to create params bean for data source [{0}]", this.dsName ).withCause( e );
        }
    }


    private void applyParams( final T bean, final Map<String, String> params )
    {
        for ( final Map.Entry<String, String> entry : params.entrySet() )
        {
            applyParam( bean, entry.getKey(), entry.getValue() );
        }
    }

    private void applyParam( final T bean, final String name, final String value )
    {
        final Field field = this.fields.get( name );
        if ( field == null )
        {
            throw new DataSourceException( "No such parameter [{0}] for data source [{1}]", name, this.dsName );
        }

        try
        {
            final Object convertedValue = ParameterConverter.getInstance().convert( value, field.getType() );
            field.set( bean, convertedValue );
        }
        catch ( final Exception e )
        {
            throw new DataSourceException( "Could not set parameter [{0}] for data source [{1}]", name, this.dsName ).withCause( e );
        }
    }

    private void checkRequired( final T bean )
    {
        for ( final Field field : this.fields.values() )
        {
            checkRequired( bean, field );
        }
    }

    private void checkRequired( final T bean, final Field field )
    {
        final boolean required = field.getAnnotation( Nonnull.class ) != null;

        if ( required && isNullValue( bean, field ) )
        {
            throw new DataSourceException( "Parameter [{0}] is required for data source [{1}]", field.getName(), this.dsName );
        }
    }

    private boolean isNullValue( final T bean, final Field field )
    {
        try
        {
            return field.get( bean ) == null;
        }
        catch ( final Exception e )
        {
            return false;
        }
    }

    public static <T> ParameterBeanMapper<T> create( final Class<T> type, final String dsName )
    {
        return new ParameterBeanMapper<T>( type, dsName );
    }
}
