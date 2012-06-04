package com.enonic.cms.web.portal.interceptor;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.api.plugin.ext.http.HttpAutoLogin;
import com.enonic.cms.core.plugin.PluginManager;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.server.service.servlet.OriginalPathResolver;
import com.enonic.cms.web.portal.PortalWebContext;

@Component
public final class AutoLoginInterceptor
    implements RequestInterceptor
{
    private final static Logger LOG = Logger.getLogger( AutoLoginInterceptor.class.getName() );

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
    @Override
    public boolean preHandle( final PortalWebContext context )
        throws Exception
    {
        final HttpServletRequest req = context.getRequest();
        String path = originalPathResolver.getRequestPathFromHttpRequest( req );
        HttpAutoLogin plugin = pluginManager.getExtensions().findMatchingHttpAutoLoginPlugin( path );

        if ( plugin != null )
        {
            doAutoLogin( req, plugin );
        }

        return true;
    }

    @Override
    public void postHandle( final PortalWebContext context )
        throws Exception
    {
        // Do nothing
    }

    private void doAutoLogin( HttpServletRequest req, HttpAutoLogin plugin )
    {
        UserEntity current = securityService.getLoggedInPortalUserAsEntity();

        if ( !current.isAnonymous() )
        {
            if ( current.isEnterpriseAdmin() )
            {
                LOG.finest( "Already logged in as Enterprise Admin. Skipping auto-login." );
                return;
            }

            LOG.finest( "Already logged in. Checking if current user equals SSO user." );
            boolean currentUserIsValid = plugin.validateCurrentUser( current.getName(), current.getUserStore().getName(), req );
            if ( currentUserIsValid )
            {
                LOG.finest( "Already logged in. Skipping auto-login." );
                return;
            }
            else
            {
                LOG.finest( "A new SSO user has arrived. Logging out current user before continueing" );
                securityService.logoutPortalUser();
            }
        }

        QualifiedUsername qualifiedUserName = getAuthenticatedUser( req, plugin );
        if ( qualifiedUserName == null )
        {
            return;
        }

        if ( securityService.autoLoginPortalUser( qualifiedUserName ) )
        {
            LOG.finest( "Auto-login logged in user [" + qualifiedUserName + "]" );
        }
        else
        {
            LOG.severe( "Auto-login user [" + qualifiedUserName + "] does not exist. Auto-login failed." );
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
            LOG.log( Level.SEVERE, "Failed to get authenticated user from plugin", e );
            return null;
        }
    }
}
