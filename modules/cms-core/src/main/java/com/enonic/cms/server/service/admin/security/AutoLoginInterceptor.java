/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.server.service.admin.security;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.enonic.esl.servlet.http.CookieUtil;

import com.enonic.cms.api.plugin.ext.http.HttpAutoLogin;
import com.enonic.cms.core.AdminConsoleTranslationService;
import com.enonic.cms.core.plugin.PluginManager;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.server.service.servlet.OriginalPathResolver;

/**
 * This interceptor executes any auto login plugins available.
 */
public final class AutoLoginInterceptor
    extends HandlerInterceptorAdapter
{
    private final static Logger LOG = LoggerFactory.getLogger( AutoLoginInterceptor.class );

    private PluginManager pluginManager;

    private SecurityService securityService;

    private OriginalPathResolver originalPathResolver = new OriginalPathResolver();

    @Autowired
    public void setPluginManager( PluginManager pluginManager )
    {
        this.pluginManager = pluginManager;
    }

    @Autowired
    public void setSecurityService( SecurityService securityService )
    {
        this.securityService = securityService;
    }

    /**
     * Execute the auto login, if an auto login plugin has been configured.
     */
    public boolean preHandle( HttpServletRequest req, HttpServletResponse res, Object o )
        throws Exception
    {

        String path = originalPathResolver.getRequestPathFromHttpRequest( req );
        HttpAutoLogin plugin = pluginManager.getExtensions().findMatchingHttpAutoLoginPlugin( path );

        if ( plugin != null )
        {
            doAutoLogin( req, plugin );
        }

        return super.preHandle( req, res, o );
    }


    private void doAutoLogin( HttpServletRequest req, HttpAutoLogin plugin )
    {
        UserEntity current = securityService.getLoggedInAdminConsoleUserAsEntity();

        if ( current != null && !current.isAnonymous() )
        {
            if ( current.isEnterpriseAdmin() )
            {
                LOG.debug( "Already logged in as Enterprise Admin. Skipping auto-login." );
                return;
            }

            LOG.debug( "Already logged in. Checking if current user equals SSO user." );
            boolean currentUserIsValid = plugin.validateCurrentUser( current.getName(), current.getUserStore().getName(), req );
            if ( currentUserIsValid )
            {
                LOG.debug( "Already logged in. Skipping auto-login." );
                return;
            }
            else
            {
                LOG.debug( "A new SSO user has arrived. Logging out current user before continuing" );
                securityService.logoutAdminUser();
            }
        }

        QualifiedUsername qualifiedUserName = getAuthenticatedUser( req, plugin );
        if ( qualifiedUserName == null )
        {
            return;
        }

        if ( securityService.autoLoginAdminUser( qualifiedUserName ) )
        {
            LOG.debug( "Auto-login logged in user [" + qualifiedUserName + "]" );

            // Setting the user selected language, so it's available for all admin XSLs.
            AdminConsoleTranslationService languageMap = AdminConsoleTranslationService.getInstance();
            String languageCode;
            Cookie cookie = CookieUtil.getCookie( req, "languageCode" );
            if ( cookie == null )
            {
                languageCode = languageMap.getDefaultLanguageCode();
            }
            else
            {
                languageCode = cookie.getValue();
            }
            req.getSession().setAttribute( "languageCode", languageCode );
        }
        else
        {
            LOG.error( "Auto-login user [" + qualifiedUserName + "] does not exist. Auto-login failed." );
        }
    }

    private QualifiedUsername getAuthenticatedUser( HttpServletRequest req, HttpAutoLogin plugin )
    {
        try
        {
            String qualifiedUsernameStr = plugin.getAuthenticatedUser( req );
            if ( qualifiedUsernameStr == null )
            {
                return null;
            }

            return QualifiedUsername.parse( qualifiedUsernameStr );
        }
        catch ( Exception e )
        {
            LOG.error( "Failed to get authenticated user from plugin", e );
            return null;
        }
    }
}
