/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype.dataentryconfig;


import java.util.LinkedHashMap;

public class DropdownDataEntryConfig
    extends SelectorDataEntryConfig
{
    private String defaultValue;

    public DropdownDataEntryConfig( String name, boolean required, String displayName, String xpath,
                                    LinkedHashMap<String, String> optionValuesWithDescriptions )
    {
        super( name, required, DataEntryConfigType.DROPDOWN, displayName, xpath, optionValuesWithDescriptions );
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public DropdownDataEntryConfig setDefaultValue( final String defaultValue )
    {
        this.defaultValue = defaultValue;
        return this;
    }
}