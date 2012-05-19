package com.enonic.cms.web.portal.userservices;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enonic.cms.core.SitePath;
import com.enonic.cms.core.SitePropertyNames;
import com.enonic.cms.core.portal.InvalidTicketException;
import com.enonic.cms.core.portal.ticket.TicketValidator;
import com.enonic.cms.web.portal.handler.WebContext;
import com.enonic.cms.web.portal.handler.WebHandlerBase;

@Component
public final class UserServicesHandler
    extends WebHandlerBase
{
    private CustomContentHandlerController customContentHandler;

    @Override
    protected boolean canHandle( final String localPath )
    {
        return localPath.contains( "/_services/" );
    }

    @Override
    protected void doHandle( final WebContext context )
        throws Exception
    {
        final SitePath sitePath = context.getSitePath();
        final HttpServletRequest request = context.getRequest();

        final String handler = UserServicesParameterResolver.resolveHandlerFromSitePath( sitePath );
        final String operation = UserServicesParameterResolver.resolveOperationFromSitePath( sitePath );

        if ( ticketIsRequired( handler, operation ) && !ticketIsValid( request ) )
        {
            throw new InvalidTicketException();
        }

        if ( sitePropertiesService.getPropertyAsBoolean( SitePropertyNames.AUTOLOGIN_HTTP_REMOTE_USER_ENABLED, sitePath.getSiteKey() ) )
        {
            autoLoginService.autologinWithRemoteUser( request );
        }

    }

    private boolean ticketIsRequired( final String handler, final String operation )
    {
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
