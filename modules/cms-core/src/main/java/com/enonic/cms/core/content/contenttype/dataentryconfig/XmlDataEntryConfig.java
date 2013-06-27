/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype.dataentryconfig;


public class XmlDataEntryConfig
    extends AbstractBaseDataEntryConfig
{
    private String defaultValue;

    public XmlDataEntryConfig( String name, boolean required, String displayName, String xpath )
    {
        super( name, required, DataEntryConfigType.XML, displayName, xpath );
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public XmlDataEntryConfig setDefaultValue( final String defaultValue )
    {
        this.defaultValue = defaultValue;
        return this;
    }
}
