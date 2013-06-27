/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.query;

import org.apache.commons.lang.StringUtils;

public class QueryFieldAndValue
    extends AbstractQueryFieldAndValue
{
    private final QueryValue queryValue;

    public QueryFieldAndValue( final String path, final Object value )
    {
        super( path );
        this.queryValue = new QueryValue( value );
    }

    public QueryFieldAndValue( final QueryField queryField, final QueryValue queryValue )
    {
        super( queryField );
        this.queryValue = queryValue;
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

    @Override
    protected boolean isQueryValueNumeric()
    {
        return queryValue.isNumeric();
    }

    public boolean doBuildAsEmptyDateFieldQuery()
    {
        return queryPathIsDateAndValueEmpty();
    }

    @Override
    protected boolean queryPathIsDateAndValueEmpty()
    {
        return isDateField() && queryValue.isEmpty();
    }

    @Override
    protected boolean isQueryOnDateValue()
    {
        return queryValue.isDateTime();
    }

}
