/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype.dataentryconfig;


public class TextAreaDataEntryConfig
    extends AbstractBaseDataEntryConfig
{
    private String defaultValue;

    public TextAreaDataEntryConfig( String name, boolean required, String displayName, String xpath )
    {
        super( name, required, DataEntryConfigType.TEXT_AREA, displayName, xpath );
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public TextAreaDataEntryConfig setDefaultValue( final String defaultValue )
    {
        this.defaultValue = defaultValue;
        return this;
    }
}