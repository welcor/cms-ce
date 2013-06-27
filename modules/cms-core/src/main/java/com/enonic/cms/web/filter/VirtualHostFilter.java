/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.enonic.cms.core.vhost.VirtualHost;
import com.enonic.cms.core.vhost.VirtualHostHelper;
import com.enonic.cms.core.vhost.VirtualHostResolver;
import com.enonic.cms.web.urlrewrite.UrlRewriterBean;
import com.enonic.cms.web.urlrewrite.UrlRewriterHttpServletRequestWrapper;

@Component
public final class VirtualHostFilter
    extends OncePerRequestFilter
{
    private final static Logger LOG = LoggerFactory.getLogger( VirtualHostFilter.class );

    @Autowired
    private VirtualHostResolver virtualHostResolver;

    @Autowired
    private UrlRewriterBean urlRewriterBean;

    @Value("${cms.security.vhost.require}")
    private boolean requireVirtualHost;

    protected void doFilterInternal( HttpServletRequest req, HttpServletResponse res, FilterChain chain )
            throws IOException, ServletException
    {
        try
        {
            doFilter( req, res, chain );
        }
        catch ( IOException e )
        {
            throw e;
        }
        catch ( ServletException e )
        {
            throw e;
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
            throw new ServletException( e );
        }
    }

    private void doFilter( HttpServletRequest req, HttpServletResponse res, FilterChain chain )
            throws Exception
    {

        String fullTargetPath = null;

        VirtualHost virtualHost = this.virtualHostResolver != null ? this.virtualHostResolver.resolve( req ) : null;
        if ( virtualHost != null )
        {
            fullTargetPath = virtualHost.getFullTargetPath( req );
            String fullSourcePath = virtualHost.getFullSourcePath( req );
            VirtualHostHelper.setBasePath( req, fullSourcePath );
        }
        else if ( requireVirtualHost )
        {
            res.setStatus( HttpServletResponse.SC_NOT_FOUND );
            return;
        }

        if ( urlRewriterBean.isEnabled() )
        {
            UrlRewriterHttpServletRequestWrapper wrappedReq = new UrlRewriterHttpServletRequestWrapper( req, fullTargetPath );
            boolean responseCommited = urlRewriterBean.doRewriteURL( wrappedReq, res, chain );
            if ( responseCommited )
            {
                return;
            }
        }

        if ( fullTargetPath != null )
        {
            RequestDispatcher dispatcher = req.getRequestDispatcher( fullTargetPath );
            dispatcher.forward( req, res );
        }
        else
        {
            chain.doFilter( req, res );
        }
    }
}
