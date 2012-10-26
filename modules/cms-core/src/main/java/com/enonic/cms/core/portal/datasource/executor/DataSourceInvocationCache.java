/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.datasource.executor;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.common.collect.Maps;
import org.jdom.Document;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;

public final class DataSourceInvocationCache
{
    private final HashMap<String, Document> cache;

    public DataSourceInvocationCache()
    {
        this.cache = Maps.newHashMap();
    }

    private String createCacheKey( final DataSourceRequest req )
    {
        final StringBuilder str = new StringBuilder();
        str.append( req.getName() );

        for ( final Map.Entry<String, String> param : req.getParams().entrySet() )
        {
            str.append( "-" ).append( param.getKey() ).append( ":" ).append( param.getValue() );
        }

        return str.toString();
    }

    public Document get( final DataSourceRequest req )
    {
        final String key = createCacheKey( req );
        return this.cache.get( key );
    }

    public void put( final DataSourceRequest req, final Document doc )
    {
        final String key = createCacheKey( req );
        this.cache.put( key, doc );
    }
}
