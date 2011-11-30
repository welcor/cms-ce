package com.enonic.cms.core.search;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class AttachmentFilter
{

    String[] values;

    AttachmentFilterType filterType;

    public AttachmentFilter( AttachmentFilterType filterType, String value )
    {
        this.values = getValues( value );
        this.filterType = filterType;
    }

    private String[] getValues( String value )
    {
        return StringUtils.split( value, " ,;:" );
    }

    public List<String> getValueList()
    {
        return Arrays.asList( values );
    }

    public boolean hasValues()
    {
        return !getValueList().isEmpty();
    }

    public String[] getValues()
    {
        return values;
    }

    public void setValues( String[] values )
    {
        this.values = values;
    }

    public AttachmentFilterType getFilterType()
    {
        return filterType;
    }

    public void setFilterType( AttachmentFilterType filterType )
    {
        this.filterType = filterType;
    }

}