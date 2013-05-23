package com.enonic.cms.web.portal.services;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.Path;
import com.enonic.cms.core.portal.InvalidParameterValueException;
import com.enonic.cms.core.portal.InvalidTicketException;
import com.enonic.cms.core.portal.ticket.TicketValidator;
import com.enonic.cms.core.structure.SitePath;
import com.enonic.cms.core.structure.SitePropertyNames;
import com.enonic.cms.web.portal.PortalWebContext;
import com.enonic.cms.web.portal.handler.WebHandlerBase;

@Component
public final class ServicesHandler
    extends WebHandlerBase
{
    private final Map<String, ServicesProcessor> processorMap;

    public ServicesHandler()
    {
        this.processorMap = Maps.newHashMap();
    }

    @Override
    protected boolean canHandle( final Path localPath )
    {
        return localPath.containsSubPath( "_services" );
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

        if ( sitePropertiesService.getSiteProperties( sitePath.getSiteKey() ).getPropertyAsBoolean(
            SitePropertyNames.AUTOLOGIN_HTTP_REMOTE_USER_ENABLED ) )
        {
            autoLoginService.autologinWithRemoteUser( request );
        }

        final String handlerName = UserServicesParameterResolver.resolveHandlerFromSitePath( sitePath );
        final ServicesProcessor processor = this.processorMap.get( handlerName );

        if ( processor == null )
        {
            throw new InvalidParameterValueException( "handler", handlerName );
        }

        processor.handle( context );
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

    private void addProcessor( final ServicesProcessor processor )
    {
        this.processorMap.put( processor.getHandlerName(), processor );
    }

    @Autowired
    public void setContentSendMailServicesProcessor( final ContentSendMailServicesProcessor processor )
    {
        addProcessor( processor );
    }

    @Autowired
    public void setContentServicesProcessor( final ContentServicesProcessor processor )
    {
        addProcessor( processor );
    }

    @Autowired
    public void setFormServicesProcessor( final FormServicesProcessor processor )
    {
        addProcessor( processor );
    }

    @Autowired
    public void setPollServicesProcessor( final PollServicesProcessor processor )
    {
        addProcessor( processor );
    }

    @Autowired
    public void setPortalServicesProcessor( final PortalServicesProcessor processor )
    {
        addProcessor( processor );
    }

    @Autowired
    public void setSendMailServicesProcessor( final SendMailServicesProcessor processor )
    {
        addProcessor( processor );
    }

    @Autowired
    public void setSessionServicesProcessor( final SessionServicesProcessor processor )
    {
        addProcessor( processor );
    }

    @Autowired
    public void setUserServicesProcessor( final UserServicesProcessor processor )
    {
        addProcessor( processor );
    }
}
