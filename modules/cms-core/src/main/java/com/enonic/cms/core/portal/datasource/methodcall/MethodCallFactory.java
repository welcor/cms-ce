/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.datasource.methodcall;

import java.lang.reflect.Method;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import org.jdom.Element;

import com.enonic.esl.util.StringUtil;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.api.plugin.ext.FunctionLibrary;
import com.enonic.cms.core.plugin.ExtensionSet;
import com.enonic.cms.core.portal.datasource.DataSourceContext;
import com.enonic.cms.core.portal.datasource.DataSourceException;
import com.enonic.cms.core.portal.datasource.xml.DataSourceElement;
import com.enonic.cms.core.portal.datasource.DatasourceExecutorContext;
import com.enonic.cms.core.portal.datasource.el.ExpressionFunctionsExecutor;
import com.enonic.cms.core.portal.datasource.el.ExpressionContext;

/**
 * Jul 31, 2009
 */
public class MethodCallFactory
{

    public static MethodCall create( final DatasourceExecutorContext context, final DataSourceElement datasource )
    {
        String methodName = datasource.getName();
        if ( methodName == null )
        {
            return null;
        }

        String pluginName = resolvePluginName( methodName );

        ExtensionSet extensions = context.getPluginManager().getExtensions();
        FunctionLibrary pluginObject = pluginName != null ? getPluginObject( extensions, pluginName ) : null;

        Object targetObject = pluginObject != null ? pluginObject.getTarget() : context.getDataSourceService();
        Class targetClass = targetObject.getClass();
        boolean useContext = pluginObject == null;

        List parameterEl = null; // datasource.getParameterElements();
        int paramCount = parameterEl.size() + ( useContext ? 1 : 0 );

        Method method = resolveMethod( targetClass, methodName, paramCount, useContext );
        Class[] paramTypes = method.getParameterTypes();
        MethodCallParameter[] parameters = new MethodCallParameter[paramCount];

        if ( useContext )
        {
            DataSourceContext dataSourceContext = createDataSourceContext( context );
            parameters[0] = new MethodCallParameter( "__context__", dataSourceContext, "false", DataSourceContext.class );
        }

        int paramOffset = useContext ? 1 : 0;
        for ( int i = 0; i < parameterEl.size(); i++ )
        {

            Element paramEl = (Element) parameterEl.get( i );
            try
            {
                int paramterIndex = i + paramOffset;

                parameters[paramterIndex] = createParameter( paramEl, paramTypes[paramterIndex], context );
            }
            catch ( Exception e )
            {
                StringBuffer msg = new StringBuffer();
                msg.append( "Method [" ).append( methodName ).append( "]" );
                msg.append( " has correct number of parameters [" ).append( paramCount ).append( "]" );
                msg.append( ", but parameter number " ).append( i + 1 );
                msg.append( " is possibly wrong. Please check documentation." );
                throw new IllegalArgumentException( msg.toString(), e );
            }
        }

        boolean isCacheable = datasource.isCache();

        return new MethodCall( context.getInvocationCache(), targetObject, parameters, method, isCacheable );
    }

    private static MethodCallParameter createParameter( Element parmeterEl, Class paramType, DatasourceExecutorContext context )
    {
        String defValue = JDOMUtil.getElementText( parmeterEl );
        if ( ( defValue != null ) && defValue.contains( "${" ) )
        {
            defValue = evaluateExpression( defValue, context );
        }

        String value = defValue;

        Object argument = convertParameter( paramType, value );
        return new MethodCallParameter( "param", argument, null, paramType );
    }

    private static DataSourceContext createDataSourceContext( DatasourceExecutorContext context )
    {
        DataSourceContext dataSourceContext = new DataSourceContext( context.getPreviewContext() );
        dataSourceContext.setPortalInstanceKey( context.getPortalInstanceKey() );
        dataSourceContext.setSiteKey( context.getSite() != null ? context.getSite().getKey() : null );
        dataSourceContext.setUser( context.getUser() );
        return dataSourceContext;
    }

    private static String resolvePluginName( String methodName )
    {
        int pos = methodName.indexOf( '.' );
        if ( pos > 0 )
        {
            return methodName.substring( 0, pos );
        }
        else
        {
            return null;
        }
    }

    private static String resolveLocalMethodName( String methodName )
    {
        int pos = methodName.indexOf( '.' );
        if ( pos > 0 )
        {
            return methodName.substring( pos + 1 );
        }
        else
        {
            return methodName;
        }
    }

    private static FunctionLibrary getPluginObject( ExtensionSet extensions, String pluginName )
    {
        FunctionLibrary object = extensions.findFunctionLibrary( pluginName );
        if ( object == null )
        {
            throw new DataSourceException( "Plugin [{0}] is not registered", pluginName );
        }
        else
        {
            return object;
        }
    }

    private static Method resolveMethod( Class targetClass, String methodName, int numParams, boolean useContext )
    {
        final String localMethodName = resolveLocalMethodName( methodName );

        for ( Method method : targetClass.getMethods() )
        {
            if ( localMethodName.equals( method.getName() ) && ( method.getParameterTypes().length == numParams ) )
            {
                return method;
            }
        }
        throw new DataSourceException( "Method [{0}] with [{1}] parameters does not exist", localMethodName,
                                       ( useContext ? numParams - 1 : numParams ) );
    }

    private static Object convertParameter( Class type, String value )
    {

        if ( type == Integer.TYPE )
        {
            try
            {
                return new Integer( value );
            }
            catch ( NumberFormatException e )
            {
                throw new IllegalArgumentException( "Expected value of type Integer, got: " + value, e );
            }
        }
        else if ( type == String.class )
        {
            return value;
        }
        else if ( type == Boolean.TYPE )
        {
            return Boolean.valueOf( value );
        }
        else if ( type == String[].class )
        {
            if ( value == null )
            {
                return null;
            }

            return Iterables.toArray( Splitter.on( ',' ).trimResults().omitEmptyStrings().split( value ), String.class );
        }
        else if ( type == int[].class )
        {
            if ( value == null )
            {
                return null;
            }

            String[] tmpArray = StringUtil.splitString( value, ',' );
            int[] intArray = new int[tmpArray.length];
            int index = 0;
            for ( String tmp : tmpArray )
            {
                intArray[index++] = Integer.parseInt( tmp.trim() );
            }

            return intArray;
        }
        else
        {
            return null;
        }
    }

    private static String evaluateExpression( String expression, DatasourceExecutorContext context )
    {
        try
        {
            ExpressionContext expressionFunctionsContext = new ExpressionContext();
            expressionFunctionsContext.setContentFromRequest( context.getContentFromRequest() );
            expressionFunctionsContext.setSite( context.getSite() );
            expressionFunctionsContext.setMenuItem( context.getMenuItem() );
            expressionFunctionsContext.setUser( context.getUser() );
            expressionFunctionsContext.setPortalInstanceKey( context.getPortalInstanceKey() );
            expressionFunctionsContext.setLocale( context.getLocale() );
            expressionFunctionsContext.setDeviceClass( context.getDeviceClass() );
            expressionFunctionsContext.setPortletWindowRenderedInline( context.isPortletWindowRenderedInline() );

            ExpressionFunctionsExecutor expressionExecutor = new ExpressionFunctionsExecutor();
            expressionExecutor.setExpressionContext( expressionFunctionsContext );
            expressionExecutor.setHttpRequest( context.getHttpRequest() );
            expressionExecutor.setRequestParameters( context.getRequestParameters() );
            expressionExecutor.setVerticalSession( context.getVerticalSession() );

            return expressionExecutor.evaluate( expression );
        }
        catch ( Exception e )
        {
            throw new DataSourceException( "Failed to evaluate expression").withCause( e );
        }
    }
}
