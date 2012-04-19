package com.enonic.cms.core.search.query;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.search.builder.IndexFieldNameConstants;

public class QueryFieldAndValue
    extends IndexFieldNameConstants
{
    private final QueryField queryField;

    private final QueryValue queryValue;


    public QueryFieldAndValue( final String path, final Object value )
    {

        this.queryField = QueryPathResolver.resolveQueryPath( path );
        this.queryValue = new QueryValue( value );
    }

    public QueryFieldAndValue( final QueryField queryField, final QueryValue queryValue )
    {
        this.queryField = queryField;
        this.queryValue = queryValue;
    }

    public String getFieldName()
    {
        // TODO HOW TO HANDLE WILDCARDS? Should probably be more logic with numeric and datestuff? For _all_userdata.number?
        if ( queryField.isWildcardQueyField() )
        {
            return ALL_USERDATA_FIELDNAME;
        }

        if ( isQueryValueNumeric() )
        {
            return queryField.getFieldNameForNumericQueries();
        }

        if ( useDateFieldPath() )
        {
            return queryField.getFieldNameForDateQueries();
        }

        return queryField.getFieldName();
    }

    public Object getValue()
    {
        if ( isQueryValueNumeric() )
        {
            return queryValue.getNumericValue();
        }

        if ( isQueryOnDateValue() )
        {
            return queryValue.getDateTime();
        }

        return queryValue.getStringValueNormalized();
    }

    public String getValueForIdQuery()
    {
        if ( queryValue.isNumeric() )
        {
            return StringUtils.substringBefore( queryValue.getStringValueNormalized(), "." );
        }

        return queryValue.getStringValueNormalized();
    }

    private boolean isQueryValueNumeric()
    {
        return queryValue.isNumeric();
    }

    public boolean doRenderAsHasChildQuery()
    {
        return queryField.doRenderAsHasChildQuery();
    }

    public boolean doBuildAsIdQuery()
    {
        return queryField.doBuildAsIdQuery();
    }

    public boolean doBuildAsEmptyDateFieldQuery()
    {
        return queryPathIsDateAndValueEmpty();
    }

    private boolean useDateFieldPath()
    {
        return queryPathIsDateAndValueEmpty() || isQueryOnDateValue();
    }

    private boolean queryPathIsDateAndValueEmpty()
    {
        return queryField.isDateField() && queryValue.isEmpty();
    }

    private boolean isQueryOnDateValue()
    {
        return queryValue.isDateTime();
    }

    public String getIndexType()
    {
        return queryField.getIndexType().toString();
    }

}
