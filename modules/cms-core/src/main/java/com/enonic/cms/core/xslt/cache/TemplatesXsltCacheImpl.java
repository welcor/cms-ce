package com.enonic.cms.core.xslt.cache;

import javax.annotation.PostConstruct;
import javax.xml.transform.Templates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

import com.enonic.cms.core.xslt.XsltResource;

@Component
public final class TemplatesXsltCacheImpl
    implements TemplatesXsltCache
{
    private CacheFacade cacheFacade;

    @Override
    public Templates get( final XsltResource xsl )
    {
        return (Templates) this.cacheFacade.get( null, xsl.getName() );
    }

    @Override
    public void put( final XsltResource xsl, final Templates templates )
    {
        this.cacheFacade.put( null, xsl.getName(), templates );
    }

    @Override
    public void clear()
    {
        this.cacheFacade.removeAll();
    }

    @Autowired
    @Qualifier("cacheFacadeManager")
    public void setCacheManager( final CacheManager cacheManager )
    {
        this.cacheFacade = cacheManager.getOrCreateCache( "xslt" );
    }
}
