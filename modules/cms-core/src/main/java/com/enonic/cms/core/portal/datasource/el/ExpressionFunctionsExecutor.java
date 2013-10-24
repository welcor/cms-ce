/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.portal.datasource.el;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.enonic.cms.core.RequestParameters;
import com.enonic.cms.core.portal.VerticalSession;
import com.enonic.cms.core.portal.datasource.el.accessors.CmsAndSitePropertiesAccessor;
import com.enonic.cms.core.portal.datasource.el.accessors.CookieAccessor;
import com.enonic.cms.core.portal.datasource.el.accessors.ParamAccessor;
import com.enonic.cms.core.portal.datasource.el.accessors.ParamsAccessor;
import com.enonic.cms.core.portal.datasource.el.accessors.PortalAccessor;
import com.enonic.cms.core.portal.datasource.el.accessors.SessionAccessor;
import com.enonic.cms.core.portal.datasource.el.accessors.UserAccessor;
import com.enonic.cms.core.structure.SiteProperties;

public final class ExpressionFunctionsExecutor
{
    private static final Logger LOG = LoggerFactory.getLogger( ExpressionFunctionsExecutor.class );

    private static final ExpressionParser EXPR_FACTORY = new SpelExpressionParser();

    private static final TemplateParserContext TEMPLATE_PARSER_CONTEXT = new TemplateParserContext();

    private static final List<PropertyAccessor> PROPERTY_ACCESSORS = Arrays.<PropertyAccessor>asList( new PropertyAccessorImpl() );

    private RequestParameters requestParameters;

    private VerticalSession verticalSession;

    private HttpServletRequest httpRequest;

    private ExpressionContext expressionContext;

    private SiteProperties siteProperties; // site-xx.properties

    private Properties rootProperties; // cms.properties


    public String evaluate( final String expression )
    {
        final ExpressionRootObject rootObject = new ExpressionRootObject();

        rootObject.setSession( new SessionAccessor( verticalSession ) );
        rootObject.setCookie( new CookieAccessor( httpRequest ) );
        rootObject.setProperties( new CmsAndSitePropertiesAccessor( rootProperties, siteProperties ) );
        rootObject.setPortal( new PortalAccessor( expressionContext ) );
        rootObject.setUser( new UserAccessor( expressionContext.getUser() ) );
        rootObject.setParam( new ParamAccessor( requestParameters ) );
        rootObject.setParams( new ParamsAccessor( requestParameters ) );

        final String evaluatedString;

        try
        {
            final StandardEvaluationContext context = new StandardEvaluationContext( rootObject );

            context.setPropertyAccessors( PROPERTY_ACCESSORS );

            ExpressionFunctionsFactory.get().setContext( expressionContext );

            Object result;

            try
            {
                final Expression exp = EXPR_FACTORY.parseExpression( expression, TEMPLATE_PARSER_CONTEXT );

                result = exp.getValue( context );
            }
            catch ( SpelEvaluationException e )
            {
                result = null;
            }
            catch ( Exception e )
            {
                LOG.error( e.getMessage() );

                result = "ERROR: " + e.getMessage();
            }

            // must be converted here, because param.x[0] will not work
            if ( result instanceof String[] )
            {
                evaluatedString = StringUtils.join( (String[]) result, ',' );
            }
            else
            {
                evaluatedString = result != null ? result.toString() : null;
            }
        }
        finally
        {
            ExpressionFunctionsFactory.get().removeContext();
        }

        return evaluatedString;
    }

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

    public void setSiteProperties( final SiteProperties siteProperties )
    {
        this.siteProperties = siteProperties;
    }

    public void setRootProperties( final Properties rootProperties )
    {
        this.rootProperties = rootProperties;
    }
}

