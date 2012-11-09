package com.enonic.cms.framework.cache.xml;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

public final class CacheInfoXmlBuilder
{
    private final CacheManager cacheManager;

    public CacheInfoXmlBuilder( final CacheManager cacheManager )
    {
        this.cacheManager = cacheManager;
    }

    public XMLDocument build()
    {
        final Element root = new Element( "caches" );
        for ( final CacheFacade cache : this.cacheManager.getAll() )
        {
            append( root, cache );
        }

        return XMLDocumentFactory.create( new Document( root ) );
    }

    public void append( final Element parent, final CacheFacade cache )
    {
        final Element root = new Element( "cache" );
        parent.addContent( root );

        root.setAttribute( "name", cache.getName() );
        root.setAttribute( "implementationName", "Standard Cache" );
        root.setAttribute( "memoryCapacity", String.valueOf( cache.getMemoryCapacity() ) );
        root.setAttribute( "timeToLive", String.valueOf( cache.getTimeToLive() ) );

        final Element statsElem = new Element( "statistics" );
        statsElem.setAttribute( "objectCount", String.valueOf( cache.getCount() ) );
        statsElem.setAttribute( "memoryCapacityUsage", String.valueOf( cache.getMemoryCapacityUsage() ) );
        statsElem.setAttribute( "cacheHits", String.valueOf( cache.getHitCount() ) );
        statsElem.setAttribute( "cacheMisses", String.valueOf( cache.getMissCount() ) );
        statsElem.setAttribute( "cacheEffectiveness", String.valueOf( cache.getEffectiveness() ) );
        statsElem.setAttribute( "cacheClears", String.valueOf( cache.getRemoveAllCount() ) );
        root.addContent( statsElem );
    }
}
