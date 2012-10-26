/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.datasource;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.portal.datasource.context.DatasourcesContextXmlCreator;
import com.enonic.cms.core.portal.datasource.el.ExpressionContext;
import com.enonic.cms.core.portal.datasource.el.ExpressionFunctionsExecutor;
import com.enonic.cms.core.portal.datasource.methodcall.MethodCall;
import com.enonic.cms.core.portal.datasource.methodcall.MethodCallFactory;
import com.enonic.cms.core.portal.datasource.xml.DataSourceElement;
import com.enonic.cms.core.portal.datasource.xml.DataSourcesElement;
import com.enonic.cms.core.portal.livetrace.DatasourceExecutionTrace;
import com.enonic.cms.core.portal.livetrace.DatasourceExecutionTracer;
import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.core.portal.rendering.tracing.DataTraceInfo;
import com.enonic.cms.core.portal.rendering.tracing.RenderTrace;

public class DatasourceExecutor
{

    private DatasourceExecutorContext context;

    private DatasourcesContextXmlCreator datasourcesContextXmlCreator;

    private LivePortalTraceService livePortalTraceService;

    private DatasourceExecutionTrace trace;

    public DatasourceExecutor( DatasourceExecutorContext datasourceExecutorContext )
    {
        this.context = datasourceExecutorContext;
    }

    public XMLDocument getDataSourceResult( DataSourcesElement datasources )
    {
        Document resultDoc = new Document( new Element( resolveResultRootElementName( datasources ) ) );
        Element verticaldataEl = resultDoc.getRootElement();

        Element contextEl = datasourcesContextXmlCreator.createContextElement( datasources, context );
        verticaldataEl.addContent( contextEl );

        // execute data sources
        for ( DataSourceElement datasource : datasources.getList() )
        {
            trace =
                DatasourceExecutionTracer.startTracing( context.getDatasourcesType(), datasource.getName(), livePortalTraceService );
            try
            {
                DatasourceExecutionTracer.traceRunnableCondition( trace, datasource.getCondition() );

                boolean runnableByCondition = isRunnableByCondition( datasource );
                DatasourceExecutionTracer.traceIsExecuted( trace, runnableByCondition );
                if ( runnableByCondition )
                {
                    Document datasourceResultDocument = executeMethodCall( datasource );
                    verticaldataEl.addContent( datasourceResultDocument.getRootElement().detach() );
                }
            }
            finally
            {
                DatasourceExecutionTracer.stopTracing( trace, livePortalTraceService );
            }

        }

        setTraceDataSourceResult( resultDoc );
        return XMLDocumentFactory.create( resultDoc );
    }

    /**
     * Checks the condition attribute of the xml element, and evaluates it to decide if the datasource should be run. Returns true if
     * condition does not exists, is empty, contains 'true' or evaluates to true.
     */
    protected boolean isRunnableByCondition( final DataSourceElement datasource )
    {
        // Note: Made protected to enable testing. Should normally be tested through public methods

        if ( datasource.getCondition() == null || datasource.getCondition().equals( "" ) )
        {
            return true;
        }

        try
        {
            ExpressionContext expressionFunctionsContext = new ExpressionContext();
            expressionFunctionsContext.setSite( context.getSite() );
            expressionFunctionsContext.setMenuItem( context.getMenuItem() );
            expressionFunctionsContext.setContentFromRequest( context.getContentFromRequest() );
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

            String evaluatedExpression = expressionExecutor.evaluate( datasource.getCondition() );
            return evaluatedExpression.equals( "true" );
        }
        catch ( Exception e )
        {
            throw new DataSourceException( "Failed to evaluate expression" ).withCause( e );
        }

    }


    private String resolveResultRootElementName( DataSourcesElement datasources )
    {
        final String name = datasources.getResultElement();

        if ( name != null && name.length() > 0 )
        {
            return name;
        }
        return context.getDefaultResultRootElementName();
    }

    private Document executeMethodCall( DataSourceElement datasource )
    {
        MethodCall methodCall = MethodCallFactory.create( context, datasource );

        DatasourceExecutionTracer.traceMethodCall( methodCall, trace );

        return executeMethodCall( datasource, methodCall );
    }

    private Document executeMethodCall( final DataSourceElement datasource, final MethodCall methodCall )
    {
        XMLDocument xmlDocument = methodCall.invoke();
        Document jdomDocument = (Document) xmlDocument.getAsJDOMDocument().clone();

        if ( datasource.getResultElement() != null )
        {
            Element originalRootEl = jdomDocument.getRootElement();
            Element wrappingResultElement = new Element( datasource.getResultElement() );
            wrappingResultElement.addContent( originalRootEl.detach() );
            jdomDocument = new Document( wrappingResultElement );
        }

        return jdomDocument;
    }

    private void setTraceDataSourceResult( Document result )
    {
        DataTraceInfo info = RenderTrace.getCurrentDataTraceInfo();
        if ( info != null )
        {
            info.setDataSourceResult( XMLDocumentFactory.create( result ) );
        }
    }

    public void setContext( DatasourceExecutorContext value )
    {
        this.context = value;
    }

    public void setDatasourcesContextXmlCreator( DatasourcesContextXmlCreator datasourcesContextXmlCreator )
    {
        this.datasourcesContextXmlCreator = datasourcesContextXmlCreator;
    }

    public void setLivePortalTraceService( LivePortalTraceService livePortalTraceService )
    {
        this.livePortalTraceService = livePortalTraceService;
    }
}
