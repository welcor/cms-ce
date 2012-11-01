/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.datasource.executor;

import org.jdom.Document;
import org.jdom.Element;

import com.google.common.base.Strings;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.portal.datasource.DataSourceException;
import com.enonic.cms.core.portal.datasource.context.DataSourcesContextXmlCreator;
import com.enonic.cms.core.portal.datasource.el.ExpressionContext;
import com.enonic.cms.core.portal.datasource.el.ExpressionFunctionsExecutor;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.xml.DataSourceElement;
import com.enonic.cms.core.portal.datasource.xml.DataSourcesElement;
import com.enonic.cms.core.portal.livetrace.DatasourceExecutionTrace;
import com.enonic.cms.core.portal.livetrace.DatasourceExecutionTracer;
import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.core.portal.rendering.tracing.DataTraceInfo;
import com.enonic.cms.core.portal.rendering.tracing.RenderTrace;

final class DataSourceExecutorImpl
    implements DataSourceExecutor
{
    private final DataSourceExecutorContext context;

    private DataSourcesContextXmlCreator datasourcesContextXmlCreator;

    private LivePortalTraceService livePortalTraceService;

    private DatasourceExecutionTrace trace;

    private String defaultResultRootElementName;

    private final ExpressionFunctionsExecutor expressionFunctionsExecutor;

    private DataSourceInvoker invoker;

    public DataSourceExecutorImpl( final DataSourceExecutorContext context )
    {
        this.context = context;

        final ExpressionContext expressionFunctionsContext = new ExpressionContext();
        expressionFunctionsContext.setSite( context.getSite() );
        expressionFunctionsContext.setMenuItem( context.getMenuItem() );
        expressionFunctionsContext.setContentFromRequest( context.getContentFromRequest() );
        expressionFunctionsContext.setUser( context.getUser() );
        expressionFunctionsContext.setPortalInstanceKey( context.getPortalInstanceKey() );
        expressionFunctionsContext.setLocale( context.getLocale() );
        expressionFunctionsContext.setDeviceClass( context.getDeviceClass() );
        expressionFunctionsContext.setPortletWindowRenderedInline( context.isPortletWindowRenderedInline() );

        this.expressionFunctionsExecutor = new ExpressionFunctionsExecutor();
        this.expressionFunctionsExecutor.setExpressionContext( expressionFunctionsContext );
        this.expressionFunctionsExecutor.setHttpRequest( context.getHttpRequest() );
        this.expressionFunctionsExecutor.setRequestParameters( context.getRequestParameters() );
        this.expressionFunctionsExecutor.setVerticalSession( context.getVerticalSession() );
    }

    public XMLDocument execute( final DataSourcesElement element )
    {
        final String rootName = resolveResultRootElementName( element );
        final DataSourceResultBuilder result = new DataSourceResultBuilder( rootName );

        executeContext( result );

        for ( final DataSourceElement ds : element.getList() )
        {
            executeDataSource( result, ds );
        }

        final Document resultDoc = new Document( result.getRootElement() );
        setTraceDataSourceResult( resultDoc );
        return XMLDocumentFactory.create( resultDoc );
    }

    private void executeContext( final DataSourceResultBuilder result )
    {
        final Element contextElem = this.datasourcesContextXmlCreator.createContextElement( this.context );
        result.addElement( contextElem );
    }

    private void executeDataSource( final DataSourceResultBuilder result, final DataSourceElement ds )
    {
        this.trace = DatasourceExecutionTracer.startTracing( this.context.getDataSourceType(), ds.getName(), this.livePortalTraceService );

        try
        {
            DatasourceExecutionTracer.traceRunnableCondition( this.trace, ds.getCondition() );
            boolean runnableByCondition = isRunnableByCondition( ds );
            DatasourceExecutionTracer.traceIsExecuted( this.trace, runnableByCondition );

            if ( runnableByCondition )
            {
                doExecuteDataSource( result, ds );
            }
        }
        finally
        {
            DatasourceExecutionTracer.stopTracing( trace, livePortalTraceService );
        }

    }

    protected boolean isRunnableByCondition( final DataSourceElement dataSource )
    {
        final String condition = dataSource.getCondition();

        if ( Strings.isNullOrEmpty( condition ) )
        {
            return true;
        }

        try
        {
            final String result = this.expressionFunctionsExecutor.evaluate( condition );
            return "true".equals( result );
        }
        catch ( final Exception e )
        {
            throw new DataSourceException( "Failed to evaluate expression for [{0}]", dataSource.getName() ).withCause( e );
        }
    }

    private String resolveResultRootElementName( final DataSourcesElement dataSources )
    {
        final String name = dataSources.getResultElement();
        if ( !Strings.isNullOrEmpty( name ) )
        {
            return name;
        }

        return this.defaultResultRootElementName;
    }

    private void setTraceDataSourceResult( final Document doc )
    {
        final DataTraceInfo info = RenderTrace.getCurrentDataTraceInfo();
        if ( info != null )
        {
            info.setDataSourceResult( XMLDocumentFactory.create( (Document) doc.clone() ) );
        }
    }

    public void setDataSourcesContextXmlCreator( final DataSourcesContextXmlCreator datasourcesContextXmlCreator )
    {
        this.datasourcesContextXmlCreator = datasourcesContextXmlCreator;
    }

    public void setLivePortalTraceService( final LivePortalTraceService livePortalTraceService )
    {
        this.livePortalTraceService = livePortalTraceService;
    }

    public void setDefaultResultRootElementName( final String value )
    {
        this.defaultResultRootElementName = value;
    }

    public void setInvoker( final DataSourceInvoker invoker )
    {
        this.invoker = invoker;
    }

    private void doExecuteDataSource( final DataSourceResultBuilder result, final DataSourceElement element )
    {
        final DataSourceRequestFactory factory = new DataSourceRequestFactory( this.expressionFunctionsExecutor, this.context );
        final DataSourceRequest request = factory.createRequest( element );
        final Document doc = doExecuteDataSource( request );

        final String groupName = Strings.emptyToNull( element.getResultElement() );
        final Element resultElement = (Element) doc.getRootElement().clone();

        result.addElementToGroup( groupName, resultElement );
    }

    private Document doExecuteDataSource( final DataSourceRequest request )
    {
        DatasourceExecutionTracer.traceMethodCall( request, trace );
        RenderTrace.enterFunction( request.getName() );

        try
        {
            return doExecuteDataSource( request, this.context.getInvocationCache() );
        }
        finally
        {
            RenderTrace.exitFunction();
        }
    }

    private Document doExecuteDataSource( final DataSourceRequest request, final DataSourceInvocationCache cache )
    {
        Document result = null;

        if ( request.isCache() )
        {
            result = cache.get( request );
        }

        if ( result != null )
        {
            DatasourceExecutionTracer.traceIsCacheUsed( true, this.livePortalTraceService );
            return result;
        }

        result = this.invoker.execute( request );

        if ( request.isCache() )
        {
            cache.put( request, result );
        }

        return result;
    }
}
