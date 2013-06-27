/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype.dataentryconfig;


import java.util.LinkedHashMap;

public class RadioButtonDataEntryConfig
    extends SelectorDataEntryConfig
{
    private String defaultValue;

    public RadioButtonDataEntryConfig( String name, boolean required, String displayName, String xpath,
                                       LinkedHashMap<String, String> optionValuesWithDescriptions )
    {
        super( name, required, DataEntryConfigType.RADIOBUTTON, displayName, xpath, optionValuesWithDescriptions );
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public RadioButtonDataEntryConfig setDefaultValue( final String defaultValue )
    {
        this.defaultValue = defaultValue;
        return this;
    }
}