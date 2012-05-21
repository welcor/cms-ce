package com.enonic.cms.web.portal.services;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.SitePropertyNames;
import com.enonic.cms.core.portal.InvalidParameterValueException;
import com.enonic.cms.core.portal.InvalidTicketException;
import com.enonic.cms.core.portal.ticket.TicketValidator;
import com.enonic.cms.web.portal.PortalWebContext;
import com.enonic.cms.web.portal.handler.WebHandlerBase;

@Component
@Order(0)
public final class ServicesHandler
    extends WebHandlerBase
{
    private final Map<String, ServicesProcessor> processorMap;

    public ServicesHandler()
    {
        this.processorMap = Maps.newHashMap();
    }

    @Override
    protected boolean canHandle( final String localPath )
    {
        return localPath.contains( "/_services/" );
    }

    @Override
    protected void doHandle( final PortalWebContext context )
        throws Exception
    {
        final HttpServletRequest request = context.getRequest();
        final SitePath sitePath = context.getSitePath();

        if ( ticketIsRequired( sitePath ) && !ticketIsValid( request ) )
        {
            throw new InvalidTicketException();
        }

        if ( sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.AUTOLOGIN_HTTP_REMOTE_USER_ENABLED, sitePath.getSiteKey() ) )
        {
            autoLoginService.autologinWithRemoteUser( request );
        }

        final String handlerName = UserServicesParameterResolver.resolveHandlerFromSitePath( sitePath );
        final ServicesProcessor processor = this.processorMap.get( handlerName );

        if (processor == null) {
            throw new InvalidParameterValueException( "handler", handlerName );
        }

        processor.handle( context );
    }

    @Autowired
    public void setProcessors( final ServicesProcessor... processors )
    {
        for ( final ServicesProcessor processor : processors )
        {
            this.processorMap.put( processor.getHandlerName(), processor );
        }
    }

    private boolean ticketIsRequired( SitePath sitePath )
    {
        String handler = UserServicesParameterResolver.resolveHandlerFromSitePath( sitePath );
        String operation = UserServicesParameterResolver.resolveOperationFromSitePath( sitePath );

        if ( "user".equals( handler ) )
        {
            if ( "login".equals( operation ) || "logout".equals( operation ) )
            {
                return false;
            }
        }
        else if ( "portal".equals( handler ) && "forceDeviceClass".equals( operation ) )
        {
            return false;
        }
        else if ( "portal".equals( handler ) && "resetDeviceClass".equals( operation ) )
        {
            return false;
        }
        else if ( "portal".equals( handler ) && "forceLocale".equals( operation ) )
        {
            return false;
        }
        else if ( "portal".equals( handler ) && "resetLocale".equals( operation ) )
        {
            return false;
        }

        return true;
    }

    private boolean ticketIsValid( HttpServletRequest request )
    {
        return TicketValidator.isValid( request );
    }
}
