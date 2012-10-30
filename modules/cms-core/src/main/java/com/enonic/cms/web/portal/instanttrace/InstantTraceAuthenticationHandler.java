package com.enonic.cms.web.portal.instanttrace;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.Path;
import com.enonic.cms.core.SiteURLResolver;
import com.enonic.cms.core.security.InvalidCredentialsException;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.QualifiedUsername;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.MemberOfResolver;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.UserStoreDao;
import com.enonic.cms.web.portal.PortalWebContext;
import com.enonic.cms.web.portal.handler.WebHandlerBase;
import com.enonic.cms.web.portal.template.TemplateProcessor;

@Component
public class InstantTraceAuthenticationHandler
    extends WebHandlerBase
{
    @Autowired
    protected ResourceLoader resourceLoader;

    @Autowired
    protected SecurityService securityService;

    @Autowired
    protected UserStoreService userStoreService;

    @Autowired
    protected MemberOfResolver memberOfResolver;

    @Autowired
    private UserStoreDao userStoreDao;

    @Autowired
    private TemplateProcessor templateProcessor;

    @Autowired
    private SiteURLResolver siteURLResolver;

    @Override
    protected boolean canHandle( final Path localPath )
    {
        return InstantTracePathInspector.isAuthenticationPagePath( localPath );
    }

    @Override
    protected void doHandle( final PortalWebContext context )
        throws Exception
    {
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put( "authenticationFailed", false );

        if ( InstantTraceRequestInspector.isAuthenticationSubmitted( context.getRequest() ) )
        {
            try
            {
                authenticateUser( context.getRequest() );
                HttpSession httpSession = context.getRequest().getSession( true );
                InstantTraceSessionInspector.markAuthenticated( httpSession ); // TODO: why?

                String localPathToRedirectTo = InstantTraceRequestInspector.getParameterOriginalUrl( context.getRequest() );
                String urlToRedirectTo =
                    siteURLResolver.createFullPathForRedirect( context.getRequest(), context.getSitePath().getSiteKey(),
                                                               localPathToRedirectTo );
                context.getResponse().sendRedirect( urlToRedirectTo );
                return;
            }
            catch ( InvalidCredentialsException ice )
            {
                model.put( "authenticationFailed", true );
            }
        }

        model.put( "userStores", createUserStoreMap() );
        final String originalURL = InstantTraceRequestInspector.getOriginalUrl( context.getRequest() );

        if ( !StringUtils.isBlank( originalURL ) )
        {
            model.put( "originalURL", originalURL );
        }
        else
        {
            model.put( "originalURL", "/" );
        }

        String html = templateProcessor.process( "instantTraceAuthenticationPage.ftl", model );
        context.getResponse().getWriter().println( html );

    }

    private void authenticateUser( HttpServletRequest request )
        throws InvalidCredentialsException
    {
        final String userName = InstantTraceRequestInspector.getParameterUsername( request );
        final String password = InstantTraceRequestInspector.getParameterPassword( request );
        final UserStoreKey userStoreKey;

        final QualifiedUsername qualifiedUsername;
        if ( UserEntity.isBuiltInUser( userName ) )
        {
            qualifiedUsername = new QualifiedUsername( userName );
        }
        else
        {
            userStoreKey = new UserStoreKey( InstantTraceRequestInspector.getParameterUserstore( request ) );
            qualifiedUsername = new QualifiedUsername( userStoreKey, userName );
        }

        securityService.loginInstantTraceUser( qualifiedUsername, password );

        final UserEntity user = userDao.findByQualifiedUsername( qualifiedUsername );
        if ( user == null )
        {
            throw new InvalidCredentialsException( qualifiedUsername );
        }
        if ( !memberOfResolver.hasDeveloperPowers( user ) )
        {
            throw new InvalidCredentialsException( user.getKey().toString() );
        }

    }

    private HashMap<String, UserStoreEntity> createUserStoreMap()
    {
        final HashMap<String, UserStoreEntity> userStoreMap = new HashMap<String, UserStoreEntity>();

        final List<UserStoreEntity> userStoreList = userStoreDao.findAll();
        for ( UserStoreEntity userStore : userStoreList )
        {
            userStoreMap.put( userStore.getKey().toString(), userStore );
        }

        return userStoreMap;
    }
}
