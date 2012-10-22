package com.enonic.cms.core.portal.datasource2.cache;

import java.util.Map;

import org.elasticsearch.common.collect.Maps;
import org.jdom.Document;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import com.enonic.cms.core.portal.datasource2.xml.DataSourceElement;
import com.enonic.cms.core.portal.datasource2.xml.ParameterElement;

public final class InvocationCacheImpl
    implements InvocationCache
{
    private final Map<String, Document> map;

    public InvocationCacheImpl()
    {
        this.map = Maps.newHashMap();
    }

    @Override
    public Document get( final DataSourceElement elem )
    {
        final String key = buildCacheKey( elem );
        return this.map.get( key );
    }

    @Override
    public void put( final DataSourceElement elem, final Document doc )
    {
        final String key = buildCacheKey( elem );
        this.map.put( key, doc );
    }

    private String buildCacheKey( final DataSourceElement elem )
    {
        final Hasher hasher = Hashing.md5().newHasher();
        buildCacheKey( elem, hasher );
        return hasher.hash().toString();
    }

    public void buildCacheKey( final DataSourceElement elem, final Hasher hasher )
    {
        hasher.putString( elem.getName() );
        for ( final ParameterElement param : elem.getParameters() )
        {
            hasher.putString( "-" ).putString( param.getName() ).putString( "-" ).putString( param.getValue() );
        }
    }
}
