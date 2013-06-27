/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.query;

import java.util.Set;

import com.google.common.collect.Sets;

public class QueryFieldAndMultipleValues
    extends AbstractQueryFieldAndValue
{
    private final Set<QueryValue> queryValues = Sets.newHashSet();

    private Boolean isEmpty;

    public QueryFieldAndMultipleValues( final String fieldName, final Set<QueryValue> queryValues )
    {
        super( new QueryField( fieldName ) );

        this.queryValues.addAll( queryValues );
    }

    public Object[] getValues()
    {
        Set<Object> values = Sets.newHashSet();

        if ( isQueryValueNumeric() )
        {
            for ( QueryValue value : queryValues )
            {
                if ( !value.isEmpty() )
                {
                    values.add( value.getNumericValue() );
                }
            }

        }
        else if ( isQueryOnDateValue() )
        {
            for ( QueryValue value : queryValues )
            {
                if ( !value.isEmpty() )
                {
                    values.add( value.getDateTime() );
                }
            }
        }
        else
        {
            for ( QueryValue value : queryValues )
            {
                if ( !value.isEmpty() )
                {
                    values.add( value.getStringValueNormalized() );
                }
            }
        }
        return values.toArray();
    }

    @Override
    protected boolean isQueryOnDateValue()
    {
        if ( isEmpty() )
        {
            return false;
        }

        for ( QueryValue queryValue : queryValues )
        {
            if ( !queryValue.isEmpty() && !queryValue.isDateTime() )
            {
                return false;
            }
        }

        return true;
    }

    @Override
    protected boolean isQueryValueNumeric()
    {
        if ( isEmpty() )
        {
            return false;
        }

        for ( QueryValue queryValue : queryValues )
        {
            if ( !queryValue.isEmpty() && !queryValue.isNumeric() )
            {
                return false;
            }
        }

        return true;
    }

    boolean isEmpty()
    {
        if ( isEmpty == null )
        {
            isEmpty = getIsEmptyValue();
        }

        return isEmpty;
    }

    private boolean getIsEmptyValue()
    {
        for ( QueryValue queryValue : queryValues )
        {
            if ( queryValue == null || !queryValue.isEmpty() )
            {
                return false;
            }
        }

        return true;
    }

    @Override
    protected boolean queryPathIsDateAndValueEmpty()
    {
        return isDateField() && isEmpty();
    }

}
