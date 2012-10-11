package com.enonic.cms.core.portal.datasource2.handler;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.support.DefaultConversionService;

final class ParameterConverter
{
    private final static ParameterConverter INSTANCE = new ParameterConverter();

    private final DefaultConversionService service;

    private ParameterConverter()
    {
        this.service = new DefaultConversionService();
    }

    public Integer toInteger( final String value )
        throws ConversionFailedException
    {
        return this.service.convert( value, Integer.class );
    }

    public static ParameterConverter getInstance()
    {
        return INSTANCE;
    }
}
