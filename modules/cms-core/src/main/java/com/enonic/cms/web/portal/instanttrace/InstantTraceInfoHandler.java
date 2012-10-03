package com.enonic.cms.web.portal.instanttrace;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import com.enonic.cms.core.Path;
import com.enonic.cms.core.portal.instanttrace.JsonSerializer;
import com.enonic.cms.core.portal.livetrace.PortalRequestTrace;
import com.enonic.cms.web.portal.PortalWebContext;
import com.enonic.cms.web.portal.handler.WebHandlerBase;

@Component
public class InstantTraceInfoHandler
    extends WebHandlerBase
{

    @Override
    protected boolean canHandle( final Path localPath )
    {
        return localPath.containsSubPath( "_itrace", "info" );
    }

    @Override
    protected void doHandle( final PortalWebContext context )
        throws Exception
    {
        final HttpServletRequest httpRequest = context.getRequest();
        final InstantTraceId instantTraceId = InstantTraceRequestInspector.getInstantTraceId( httpRequest );
        final HttpSession httpSession = httpRequest.getSession( false );
        if ( httpSession == null )
        {
            return;
        }

        final InstantTraceSessionObject instantTraceSessionObject =
            InstantTraceSessionInspector.getInstantTraceSessionObject( httpSession );
        final PortalRequestTrace trace = instantTraceSessionObject.getTrace( instantTraceId );

        final String traceInfo = new JsonSerializer( trace ).serialize();
        final byte[] responseAsBytes = traceInfo.getBytes( "UTF-8" );

        final HttpServletResponse httpResponse = context.getResponse();
        httpResponse.setContentLength( responseAsBytes.length );
        httpResponse.setContentType( "application/json; charset=utf-8" );
        httpResponse.getOutputStream().write( responseAsBytes );
    }
}
