package com.enonic.cms.core.portal.datasource2.executor;

import org.jdom.Document;
import org.jdom.Element;

import com.google.common.base.Strings;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.portal.datasource.DataSourceException;
import com.enonic.cms.core.portal.datasource2.DataSourceExecutor;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceInvoker;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource2.xml.DataSourceElement;
import com.enonic.cms.core.portal.datasource2.xml.DataSourceXmlParser;
import com.enonic.cms.core.portal.datasource2.xml.DataSourcesElement;
import com.enonic.cms.core.portal.datasource2.xml.ParameterElement;
import com.enonic.cms.core.portal.rendering.tracing.DataTraceInfo;
import com.enonic.cms.core.portal.rendering.tracing.RenderTrace;

final class DataSourceExecutorImpl
    implements DataSourceExecutor
{
    private Document inputDoc;

    private DataSourcesElement inputElem;

    private String defaultResultRoot;

    private DataSourceInvoker invoker;

    @Override
    public DataSourceExecutor input( final Document value )
    {
        this.inputDoc = value;
        return this;
    }

    public void setDefaultResultRoot( final String defaultResultRoot )
    {
        this.defaultResultRoot = defaultResultRoot;
    }

    public void setInvoker( final DataSourceInvoker invoker )
    {
        this.invoker = invoker;
    }

    private String resolveResultRootElementName()
    {
        final String name = this.inputElem.getResultElement();
        if ( Strings.isNullOrEmpty( name ) )
        {
            return this.defaultResultRoot;
        }
        else
        {
            return name;
        }
    }

    @Override
    public Document execute()
        throws DataSourceException
    {
        this.inputElem = new DataSourceXmlParser().parse( this.inputDoc );

        final Element root = new Element( resolveResultRootElementName() );
        root.addContent( buildContext() );

        for ( final DataSourceElement child : this.inputElem.getList() )
        {
            doExecute( root, child );
        }

        final Document resultDoc = new Document( root );
        setTraceDataSourceResult( resultDoc );
        return resultDoc;
    }

    private Element buildContext()
    {
        return new Element( "context" );
    }

    private void setTraceDataSourceResult( final Document result )
    {
        final DataTraceInfo info = RenderTrace.getCurrentDataTraceInfo();
        if ( info != null )
        {
            info.setDataSourceResult( XMLDocumentFactory.create( result ) );
        }
    }

    private void doExecute( final Element root, final DataSourceElement elem )
    {
        /*
        if ( elem.isSkipExecution() )
        {
            return;
        }*/

        final Document result = doExecute( elem );
        final Document cloned = (Document) result.clone();
        final Element resultElem = cloned.getRootElement();

        final String resultName = elem.getResultElement();
        if ( !Strings.isNullOrEmpty( resultName ) )
        {
            resultElem.setName( resultName );
        }

        root.addContent( resultElem.detach() );
    }

    private Document doExecute( final DataSourceElement elem )
    {
        Document result = null;

        final DataSourceRequest req = createRequest( elem );
        result = this.invoker.execute( req );

        return result;
    }

    private DataSourceRequest createRequest( final DataSourceElement elem )
    {
        final DataSourceRequest req = new DataSourceRequest();
        req.setName( elem.getName() );

        for ( final ParameterElement param : elem.getParameters() )
        {
            req.addParam( param.getName(), param.getValue() );
        }

        return req;
    }
}
