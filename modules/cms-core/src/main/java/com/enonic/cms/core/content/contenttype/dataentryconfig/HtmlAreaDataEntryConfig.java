/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype.dataentryconfig;

public class HtmlAreaDataEntryConfig
    extends AbstractBaseDataEntryConfig
{
    private String defaultValue;

    public HtmlAreaDataEntryConfig( String name, boolean required, String displayName, String xpath )
    {
        super( name, required, DataEntryConfigType.HTMLAREA, displayName, xpath );
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public HtmlAreaDataEntryConfig setDefaultValue( final String defaultValue )
    {
        this.defaultValue = defaultValue;
        return this;
    }
}