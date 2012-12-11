/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.rendering.tracing;

import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.structure.portlet.PortletKey;

/**
 * This class manages the rendering trace.
 */
public final class RenderTrace
{
    /**
     * Context key.
     */
    private final static String CONTEXT_KEY = TraceContext.class.getName();

    /**
     * History size per user.
     */
    private final static int HISTORY_SIZE_PER_USER = 10;

    /**
     * Return the current session.
     */
    private static HttpSession getSession()
    {
        return getCurrentRequest().getSession();
    }

    /**
     * Return the current trace context.
     */
    public static TraceContext getCurrentTraceContext()
    {
        return (TraceContext) getCurrentRequest().getAttribute( CONTEXT_KEY );
    }

    /**
     * Return the current request.
     */
    private static HttpServletRequest getCurrentRequest()
    {
        return ServletRequestAccessor.getRequest();
    }

    /**
     * Add render context.
     */
    private synchronized static void addTraceContext( RenderTraceInfo info )
    {
        TraceContext context = new TraceContext( info );
        getCurrentRequest().setAttribute( CONTEXT_KEY, context );

        LinkedList<RenderTraceInfo> history = getHistory();
        history.addFirst( info );
    }

    private static LinkedList<RenderTraceInfo> getHistory()
    {
        LinkedList<RenderTraceInfo> history = getHistoryPerUser();

        if ( history == null )
        {
            history = new LinkedList<RenderTraceInfo>();
            setHistoryPerUser( history );
        }
        return history;
    }

    @SuppressWarnings("unchecked")
    private static LinkedList<RenderTraceInfo> getHistoryPerUser()
    {
        final UserKey userKey = PortalSecurityHolder.getLoggedInUser();
        final RenderTraceHistory history = RenderTraceHistory.getFromSession( getSession(), userKey );
        return history != null ? history.getHistory() : null;
    }

    private static void setHistoryPerUser( final LinkedList<RenderTraceInfo> history )
    {
        final UserKey userKey = PortalSecurityHolder.getLoggedInUser();
        final RenderTraceHistory obj = new RenderTraceHistory();
        obj.setHistory( history );
        obj.setInSession( getSession(), userKey );
    }

    /**
     * Start render trace.
     */
    public static RenderTraceInfo enter()
    {
        RenderTraceInfo info = new RenderTraceInfo();
        addTraceContext( info );
        info.enter();
        return info;
    }

    /**
     * Stop render trace.
     */
    public synchronized static RenderTraceInfo exit()
    {
        RenderTraceInfo info = getCurrentRenderTraceInfo();
        getCurrentRequest().removeAttribute( CONTEXT_KEY );

        RenderTraceInfo renderTraceInfo = getRenderTraceInfo( info.getKey() );

        LinkedList<RenderTraceInfo> history = getHistory();

        if ( renderTraceInfo != null && renderTraceInfo.getPageInfo() == null )
        {
            getHistory().remove( renderTraceInfo );
            history.remove( renderTraceInfo );
        }

        while ( history.size() > HISTORY_SIZE_PER_USER )
        {
            history.removeLast();
        }

        return info;
    }

    /**
     * Enter page trace.
     */
    public static PageTraceInfo enterPage( int key )
    {
        TraceContext context = getCurrentTraceContext();
        if ( context != null )
        {
            PageTraceInfo info = new PageTraceInfo( key );
            context.setPageTraceInfo( info );
            info.enter();
            return info;
        }
        else
        {
            return null;
        }
    }

    /**
     * Exit page trace.
     */
    public static PageTraceInfo exitPage()
    {
        TraceContext context = getCurrentTraceContext();
        if ( context != null )
        {
            PageTraceInfo info = context.getPageTraceInfo();
            info.exit();
            return info;
        }
        else
        {
            return null;
        }
    }

    /**
     * Enter page object trace.
     */
    public static PagePortletTraceInfo enterPageObject( PortletKey key )
    {
        TraceContext context = getCurrentTraceContext();
        if ( context != null )
        {
            PagePortletTraceInfo info = new PagePortletTraceInfo( key );
            context.pushPageObjectTraceInfo( info );
            info.enter();
            return info;
        }
        else
        {
            return null;
        }
    }

    /**
     * Exit page object trace.
     */
    public static PagePortletTraceInfo exitPageObject()
    {
        TraceContext context = getCurrentTraceContext();
        if ( context != null )
        {
            PagePortletTraceInfo info = context.popPageObjectTraceInfo();
            info.exit();
            return info;
        }
        else
        {
            return null;
        }
    }

    /**
     * Enter function trace.
     */
    public static FunctionTraceInfo enterFunction( String name )
    {
        TraceContext context = getCurrentTraceContext();
        if ( context != null )
        {
            FunctionTraceInfo info = new FunctionTraceInfo( name );
            context.pushFunctionTraceInfo( info );
            info.enter();
            return info;
        }
        else
        {
            return null;
        }
    }

    /**
     * Exit function trace.
     */
    public static FunctionTraceInfo exitFunction()
    {
        TraceContext context = getCurrentTraceContext();
        if ( context != null )
        {
            FunctionTraceInfo info = context.popFunctionTraceInfo();
            info.exit();
            return info;
        }
        else
        {
            return null;
        }
    }

    /**
     * Return true if it is inside render trace.
     */
    public static boolean isTraceOn()
    {
        return getCurrentTraceContext() != null;
    }

    public static boolean isTraceOff()
    {
        return !isTraceOn();
    }

    /**
     * Return the current render trace info.
     */
    public static RenderTraceInfo getCurrentRenderTraceInfo()
    {
        TraceContext context = getCurrentTraceContext();
        return context != null ? context.getRenderTraceInfo() : null;
    }

    /**
     * Return the current render trace info.
     */
    public static PagePortletTraceInfo getCurrentPageObjectTraceInfo()
    {
        TraceContext context = getCurrentTraceContext();
        return context != null ? context.getCurrentPageObjectTraceInfo() : null;
    }

    /**
     * Return the current render trace info.
     */
    public static DataTraceInfo getCurrentDataTraceInfo()
    {
        TraceContext context = getCurrentTraceContext();
        return context != null ? context.getCurrentDataTraceInfo() : null;
    }

    /**
     * Return a render trace info by key.
     */
    public synchronized static RenderTraceInfo getRenderTraceInfo( String key )
    {
        for ( RenderTraceInfo info : getHistory() )
        {
            if ( info.getKey().equals( key ) )
            {
                return info;
            }
        }
        return null;
    }
}
