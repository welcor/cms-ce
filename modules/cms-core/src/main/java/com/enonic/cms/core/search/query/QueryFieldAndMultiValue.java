package com.enonic.cms.core.search.query;

import java.util.Arrays;
import java.util.Set;

import com.google.common.collect.Sets;

public class QueryFieldAndMultiValue
    extends AbstractQueryFieldAndValue
{
    private final Set<QueryValue> queryValues = Sets.newHashSet();

    public QueryFieldAndMultiValue( final QueryField queryField, final QueryValue[] queryValues )
    {
        super( queryField );

        this.queryValues.addAll( Arrays.asList( queryValues ) );
    }

    public QueryFieldAndMultiValue( final QueryField queryField, final Set<QueryValue> queryValues )
    {
        super( queryField );

        this.queryValues.addAll( queryValues );
    }

    public QueryFieldAndMultiValue( final String fieldName, final Set<QueryValue> queryValues )
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
                values.add( value.getNumericValue() );
            }

        }
        else if ( isQueryOnDateValue() )
        {
            for ( QueryValue value : queryValues )
            {
                values.add( value.getDateTime() );
            }
        }
        else
        {
            for ( QueryValue value : queryValues )
            {
                values.add( value.getStringValueNormalized() );
            }
        }
        return values.toArray();
    }

    @Override
    protected boolean isQueryOnDateValue()
    {
        final QueryValue firstValue = getFirstQueryValue();

        return firstValue != null && firstValue.isDateTime();

    }

    @Override
    protected boolean isQueryValueNumeric()
    {
        final QueryValue firstValue = getFirstQueryValue();

        return firstValue != null && firstValue.isNumeric();

    }

    private QueryValue getFirstQueryValue()
    {
        return queryValues.iterator().next();
    }

    @Override
    protected boolean useDateFieldPath()
    {
        return false;
    }
}
