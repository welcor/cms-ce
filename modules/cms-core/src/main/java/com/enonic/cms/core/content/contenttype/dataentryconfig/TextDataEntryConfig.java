/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype.dataentryconfig;

public class TextDataEntryConfig
    extends AbstractBaseDataEntryConfig
{
    private String defaultValue;

    private Integer maxLength;

    public TextDataEntryConfig( String name, boolean required, String displayName, String xpath )
    {
        super( name, required, DataEntryConfigType.TEXT, displayName, xpath );
    }

    public TextDataEntryConfig setMaxLength( Integer value )
    {
        this.maxLength = value;
        return this;
    }

    public Integer getMaxLength()
    {
        return maxLength;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public TextDataEntryConfig setDefaultValue( final String defaultValue )
    {
        this.defaultValue = defaultValue;
        return this;
    }
}