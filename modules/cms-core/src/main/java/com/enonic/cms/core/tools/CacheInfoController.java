package com.enonic.cms.core.tools;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

public final class CacheInfoController
    extends AbstractToolController
{
    private CacheManager cacheManager;

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        final String op = req.getParameter( "op" );

        if ( "info".equals( op ) )
        {
            renderJson( res, createCacheInfo() );
        }
        else
        {
            final Map<String, Object> model = Maps.newHashMap();
            model.put( "baseUrl", getBaseUrl( req ) );
            renderView( req, res, model, "cacheInfoPage" );
        }
    }

    @Override
    protected void doPost( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        final String cache = req.getParameter( "cache" );
        final String op = req.getParameter( "op" );

        executeCommand( cache, op );
        res.setStatus( HttpServletResponse.SC_NO_CONTENT );
    }

    private void executeCommand( final String cache, final String command )
    {
        if ( Strings.isNullOrEmpty( cache ) )
        {
            return;
        }

        final CacheFacade cacheFacade = this.cacheManager.getCache( cache );
        if ( cacheFacade == null )
        {
            return;
        }

        if ( "clear-statistics".equalsIgnoreCase( command ) )
        {
            cacheFacade.clearStatistics();
        }
        else if ( "clear-cache".equalsIgnoreCase( command ) )
        {
            cacheFacade.removeAll();
        }
    }

    @Autowired
    public void setCacheManager( final CacheManager cacheManager )
    {
        this.cacheManager = cacheManager;
    }

    private ObjectNode createCacheInfo()
    {
        final ObjectNode node = JsonNodeFactory.instance.objectNode();
        for ( final CacheFacade cache : this.cacheManager.getAll() )
        {
            appendCacheInfo( node, cache );
        }

        return node;
    }

    private void appendCacheInfo( final ObjectNode parent, final CacheFacade cache )
    {
        final ObjectNode node = parent.putObject( cache.getName() );
        node.put( "memoryCapacity", cache.getMemoryCapacity() );
        node.put( "timeToLive", cache.getTimeToLive() );
        node.put( "objectCount", cache.getCount() );
        node.put( "memoryCapacityUsage", cache.getMemoryCapacityUsage() );
        node.put( "cacheHits", cache.getHitCount() );
        node.put( "cacheMisses", cache.getMissCount() );
        node.put( "cacheEffectiveness", cache.getEffectiveness() );
        node.put( "cacheClears", cache.getRemoveAllCount() );
    }
}
