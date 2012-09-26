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
public class InstantTraceHandler
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
        return localPath.containsSubPath( "_itrace", "authenticate" );
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
                InstantTraceSessionInspector.markAuthenticated( httpSession );

                String localPathToRedirectTo = context.getRequest().getParameter( "_itrace_original_url" );
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
        String originalURL = (String) context.getRequest().getAttribute( "itrace.originalUrl" );
        if ( StringUtils.isBlank( originalURL ) )
        {
            originalURL = context.getRequest().getParameter( "_itrace_original_url" );
        }

        if ( StringUtils.isBlank( originalURL ) )
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
        final String userName = request.getParameter( "_itrace_username" );
        final String password = request.getParameter( "_itrace_password" );
        final UserStoreKey userStoreKey;

        final QualifiedUsername qualifiedUsername;
        if ( UserEntity.isBuiltInUser( userName ) )
        {
            qualifiedUsername = new QualifiedUsername( userName );
        }
        else
        {
            userStoreKey = new UserStoreKey( request.getParameter( "_itrace_userstore" ) );
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

    private HashMap<String, String> createUserStoreMap()
    {
        final HashMap<String, String> userStoreMap = new HashMap<String, String>();

        final List<UserStoreEntity> userStoreList = userStoreDao.findAll();
        for ( UserStoreEntity userStore : userStoreList )
        {
            // TODO: Is this the way to get  userStore key? -> userStore.getKey().toString()
            userStoreMap.put( userStore.getKey().toString(), userStore.getName() );
        }

        return userStoreMap;
    }
}
