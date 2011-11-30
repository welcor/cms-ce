package com.enonic.cms.core.search.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 10/13/11
 * Time: 9:26 AM
 */
public abstract class BaseQueryBuilder
{


    protected static Double getNumericValue( Object value )
    {
        if ( value == null )
        {
            return null;
        }

        try
        {
            return Double.parseDouble( value.toString() );

        }
        catch ( NumberFormatException e )
        {
            return null;
        }
    }

    public static QueryBuilder wrapInHasChildQuery( QueryPath path, QueryBuilder query )
    {
        return QueryBuilders.hasChildQuery( path.getIndexType().toString(), query );
    }
}
