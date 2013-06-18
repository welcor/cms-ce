package com.enonic.cms.core.portal.datasource.executor;

import java.util.Map;

import com.google.common.base.Strings;

import com.enonic.cms.core.portal.datasource.DataSourceException;
import com.enonic.cms.core.portal.datasource.el.ExpressionFunctionsExecutor;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.xml.DataSourceElement;

final class DataSourceRequestFactory
{
    private final ExpressionFunctionsExecutor evaluator;

    private final DataSourceExecutorContext context;

    public DataSourceRequestFactory( final ExpressionFunctionsExecutor evaluator, final DataSourceExecutorContext context )
    {
        this.evaluator = evaluator;
        this.context = context;
    }

    public DataSourceRequest createRequest( final DataSourceElement element )
    {
        final DataSourceRequest request = new DataSourceRequest();
        request.setPreviewContext( context.getPreviewContext() );
        request.setName( element.getName() );
        request.setPortalInstanceKey( context.getPortalInstanceKey() );
        request.setSiteKey( context.getSite() != null ? context.getSite().getKey() : null );
        request.setUser( context.getUser() );
        request.setCache( element.isCache() );
        request.setVerticalSession( context.getVerticalSession() );
        request.setHttpRequest( context.getHttpRequest() );
        evaluateParameters( request, element );
        return request;
    }

    private void evaluateParameters( final DataSourceRequest req, final DataSourceElement element )
    {
        for ( final Map.Entry<String, String> param : element.getParameters().entrySet() )
        {
            final String name = param.getKey();
            final String value = param.getValue();

            try
            {
                req.addParam( name, evaluateParameter( value ) );
            }
            catch ( final Exception e )
            {
                throw new DataSourceException( "Failed to evaluate expression [{0}] for [{1}.{2}]", value, req.getName(), name ).withCause(
                    e );
            }
        }
    }

    private String evaluateParameter( final String value )
    {
        if ( !Strings.isNullOrEmpty( value ) && value.contains( "${" ) )
        {
            return this.evaluator.evaluate( value );
        }

        return value;
    }
}
