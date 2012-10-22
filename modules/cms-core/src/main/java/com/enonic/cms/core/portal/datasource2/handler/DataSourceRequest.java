package com.enonic.cms.core.portal.datasource2.handler;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import com.enonic.cms.core.SiteKey;
import com.enonic.cms.core.portal.PortalInstanceKey;
import com.enonic.cms.core.preview.PreviewContext;
import com.enonic.cms.core.security.user.UserEntity;

public final class DataSourceRequest
{
    private String name;

    private UserEntity user;

    private SiteKey siteKey = null;

    private PortalInstanceKey portalInstanceKey;

    private PreviewContext previewContext;

    private final Map<String, String> paramMap;

    public DataSourceRequest()
    {
        this.paramMap = Maps.newHashMap();
    }

    public String getName()
    {
        return this.name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public DataSourceParam param( final String name )
    {
        final String value = this.paramMap.get( name );
        return new DataSourceParamImpl( this.name, name, value );
    }

    public void addParam( final String name, final String value )
    {
        this.paramMap.put( name, value );
    }

    public UserEntity getCurrentUser()
    {
        return this.user;
    }

    public void setCurrentUser( final UserEntity user )
    {
        this.user = user;
    }

    public SiteKey getSiteKey()
    {
        return siteKey;
    }

    public void setSiteKey( final SiteKey siteKey )
    {
        this.siteKey = siteKey;
    }

    public PortalInstanceKey getPortalInstanceKey()
    {
        return portalInstanceKey;
    }

    public void setPortalInstanceKey( final PortalInstanceKey portalInstanceKey )
    {
        this.portalInstanceKey = portalInstanceKey;
    }

    public PreviewContext getPreviewContext()
    {
        return previewContext;
    }

    public void setPreviewContext( final PreviewContext previewContext )
    {
        this.previewContext = previewContext;
    }

    public String getCacheKey()
    {
        final Hasher hasher = Hashing.md5().newHasher();
        buildCacheKey( hasher );
        return hasher.hash().toString();
    }

    public void buildCacheKey( final Hasher hasher )
    {
        hasher.putString( this.name );
        for ( final Map.Entry<String, String> param : this.paramMap.entrySet() )
        {
            hasher.putString( "-" ).putString( param.getKey() ).putString( "-" ).putString( param.getValue() );
        }
    }
}
