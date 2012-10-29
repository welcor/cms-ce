package com.enonic.cms.core.portal.datasource.handler.base;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.support.DefaultConversionService;

public final class ParameterConverter
{
    private final static ParameterConverter INSTANCE = new ParameterConverter();

    private final DefaultConversionService service;

    private ParameterConverter()
    {
        this.service = new DefaultConversionService();
    }

    public Object convert( final String value, final Class<?> targetType )
        throws ConversionFailedException
    {
        return this.service.convert( value, targetType );
    }

    public static ParameterConverter getInstance()
    {
        return INSTANCE;
    }
}
