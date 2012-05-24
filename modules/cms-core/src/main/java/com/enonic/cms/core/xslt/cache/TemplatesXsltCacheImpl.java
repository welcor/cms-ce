package com.enonic.cms.core.xslt.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.sf.saxon.s9api.XsltExecutable;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

import com.enonic.cms.core.xslt.XsltResource;

@Component
public final class TemplatesXsltCacheImpl
    implements TemplatesXsltCache
{
    private CacheFacade cacheFacade;

    @Override
    public XsltExecutable get( final XsltResource xsl )
    {
        return (XsltExecutable) this.cacheFacade.get( null, xsl.getName() );
    }

    @Override
    public void put( final XsltResource xsl, final XsltExecutable templates )
    {
        this.cacheFacade.put( null, xsl.getName(), templates );
    }

    @Override
    public void clear()
    {
        this.cacheFacade.removeAll();
    }

    @Autowired
    public void setCacheManager( final CacheManager cacheManager )
    {
        this.cacheFacade = cacheManager.getOrCreateCache( "xslt" );
    }
}
