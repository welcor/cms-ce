/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.datasource.el;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.enonic.cms.core.RequestParameters;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.portal.VerticalSession;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import com.enonic.cms.core.structure.menuitem.MenuItemType;

public final class ExpressionFunctionsExecutor
{
    private static final Logger LOG = LoggerFactory.getLogger( ExpressionFunctionsExecutor.class );

    private static final ExpressionParser EXPR_FACTORY = new SpelExpressionParser();

    private static final TemplateParserContext TEMPLATE_PARSER_CONTEXT = new TemplateParserContext();

    private RequestParameters requestParameters;

    private VerticalSession verticalSession;

    private HttpServletRequest httpRequest;

    private ExpressionContext expressionContext;

    public void setRequestParameters( RequestParameters requestParameters )
    {
        this.requestParameters = requestParameters;
    }

    public void setExpressionContext( ExpressionContext expressionContext )
    {
        this.expressionContext = expressionContext;
    }

    public void setVerticalSession( VerticalSession verticalSession )
    {
        this.verticalSession = verticalSession;
    }

    public void setHttpRequest( HttpServletRequest httpRequest )
    {
        this.httpRequest = httpRequest;
    }

    public String evaluate( String expression )
    {
        ExpressionRootObject rootObject = new ExpressionRootObject();

        rootObject.setParam( createParameterMap() );
        rootObject.setSession( createSessionMap() );
        rootObject.setCookie( createCookieMap() );
        rootObject.setUser( createUserMap() );
        rootObject.setPortal( createPortalMap() );

        StandardEvaluationContext context = new StandardEvaluationContext( rootObject );

        ExpressionFunctionsFactory.get().setContext( expressionContext );

        context.addPropertyAccessor( new SafeMapAccessor( expression ) );

        Expression exp = EXPR_FACTORY.parseExpression( expression, TEMPLATE_PARSER_CONTEXT );

        String evaluatedString = "";

        try
        {
            Object result = exp.getValue( context );

            if ( result instanceof String[] )
            {
                evaluatedString = StringUtils.join( (String[]) result, ',' );
            }
            else
            {
                evaluatedString = result.toString();
            }
        }
        finally
        {
            ExpressionFunctionsFactory.get().removeContext();
        }

        return evaluatedString;
    }

    private Map<String, Object> createPortalMap()
    {
        Map<String, Object> portalMap = new HashMap<String, Object>();

        portalMap.put( "deviceClass", expressionContext.getDeviceClass() );
        portalMap.put( "locale", createLocale() );
        portalMap.put( "instanceKey", createPortalInstanceKey() );
        portalMap.put( "pageKey", createPageKey() );
        portalMap.put( "siteKey", createSiteKey() );
        portalMap.put( "contentKey", createContentKey() );
        portalMap.put( "windowKey", createWindowKey() );
        portalMap.put( "isWindowInline", createIsWindowInline() );

        return portalMap;

    }

    private boolean createIsWindowInline()
    {
        if ( expressionContext.isPortletWindowRenderedInline() != null )
        {
            return expressionContext.isPortletWindowRenderedInline();
        }

        return false;
    }

    private String createPortalInstanceKey()
    {
        if ( expressionContext.getPortalInstanceKey() != null )
        {
            return expressionContext.getPortalInstanceKey().toString();
        }
        return null;
    }

    private String createLocale()
    {
        if ( expressionContext.getLocale() != null )
        {
            return expressionContext.getLocale().toString();
        }
        return null;

    }


    private String createWindowKey()
    {
        if ( expressionContext.getPortalInstanceKey() != null && expressionContext.getPortalInstanceKey().isWindow() )
        {
            return expressionContext.getPortalInstanceKey().getWindowKey().asString();
        }
        return null;
    }

    private String createPageKey()
    {
        if ( expressionContext.getPortalInstanceKey() != null )
        {
            MenuItemKey menuItemKey = expressionContext.getPortalInstanceKey().getMenuItemKey();
            if ( menuItemKey != null )
            {
                return menuItemKey.toString();
            }
        }
        return null;
    }

    private String createSiteKey()
    {
        return expressionContext.getSite().getKey().toString();
    }

    private String createContentKey()
    {
        if ( expressionContext.getContentFromRequest() != null )
        {
            return expressionContext.getContentFromRequest().getKey().toString();
        }

        MenuItemEntity menuItem = expressionContext.getMenuItem();

        if ( menuItem == null || menuItem.getType() != MenuItemType.CONTENT )
        {
            return null;
        }

        ContentEntity content = menuItem.getContent();

        if ( content == null )
        {
            return null;
        }

        return content.getKey().toString();
    }

    private Map<String, String> createUserMap()
    {
        UserEntity user = expressionContext.getUser();
        Map<String, String> userMap = new HashMap<String, String>();
        UserStoreEntity userStore = user.getUserStore();
        String userStoreName = "";
        if ( userStore != null )
        {
            userStoreName = userStore.getName();
        }
        String uid = user.getName();
        if ( userStoreName.length() > 0 )
        {
            userMap.put( "qualifiedName", userStoreName + "\\" + uid );
        }
        else
        {
            userMap.put( "qualifiedName", uid );
        }
        userMap.put( "key", user.getKey().toString() );
        userMap.put( "userStore", userStoreName );
        userMap.put( "uid", uid );
        userMap.put( "fullName", user.getDisplayName() );
        userMap.put( "email", user.getEmail() );
        return userMap;
    }

    private Map<String, String[]> createParameterMap()
    {
        HashMap<String, String[]> map = new HashMap<String, String[]>();
        if ( this.requestParameters != null )
        {
            for ( RequestParameters.Param param : this.requestParameters.getParameters() )
            {
                String name = param.getName();
                String[] value = param.getValues();

                if ( value != null )
                {
                    map.put( name, value );
                }
            }
        }

        return map;
    }

    private Map<String, String> createSessionMap()
    {
        HashMap<String, String> map = new HashMap<String, String>();
        if ( this.verticalSession != null )
        {
            for ( String name : this.verticalSession.getAttributeNames() )
            {
                Object value = this.verticalSession.getAttribute( name );
                if ( value != null )
                {
                    map.put( name, value.toString() );
                }
            }
        }

        return map;
    }

    private Map<String, String> createCookieMap()
    {
        HashMap<String, String> map = new HashMap<String, String>();
        if ( this.httpRequest != null )
        {
            Cookie[] cookies = httpRequest.getCookies();
            if ( cookies != null )
            {
                for ( Cookie cookie : cookies )
                {
                    if ( cookie != null )
                    {
                        map.put( cookie.getName(), cookie.getValue() );
                    }
                }
            }
        }
        return map;
    }

    /**
     * does not throw Exception if map does not contain key
     */
    private static class SafeMapAccessor
        extends MapAccessor
    {
        private String expression;

        public SafeMapAccessor( final String expression )
        {

            this.expression = expression;
        }

        @Override
        public TypedValue read( EvaluationContext context, Object target, String name )
            throws AccessException
        {
            if ( target instanceof ExpressionRootObject )
            {
                //TODO: Do we really need this?
                //LOG.warn( "Accessing map {} as root object as the Map by parameter {}. Expression is {}", name,
                //          expression ); // must be accessed directly !

                ExpressionRootObject rootObject = (ExpressionRootObject) target;

                if ( "user".equals( name ) )
                {
                    return new TypedValue( rootObject.getUser() );
                }
                if ( "param".equals( name ) )
                {
                    return new TypedValue( rootObject.getParam() );
                }
                if ( "session".equals( name ) )
                {
                    return new TypedValue( rootObject.getSession() );
                }
                if ( "portal".equals( name ) )
                {
                    return new TypedValue( rootObject.getPortal() );
                }
                if ( "cookie".equals( name ) )
                {
                    return new TypedValue( rootObject.getCookie() );
                }

                return TypedValue.NULL;
            }
            else if ( target instanceof Map )
            {
                Map map = (Map) target;
                Object value = map.get( name );

                if ( value == null && !map.containsKey( name ) )
                {
                    return TypedValue.NULL;
                }

                return new TypedValue( value );
            }

            LOG.error( "Accessing root object as {}. Expression is {}", target.getClass(), expression );

            return TypedValue.NULL;
        }

        @Override
        public boolean canRead( EvaluationContext context, Object target, String name )
            throws AccessException
        {
            return true;
        }
    }
}

