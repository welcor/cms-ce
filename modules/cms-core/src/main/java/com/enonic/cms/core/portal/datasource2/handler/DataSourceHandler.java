package com.enonic.cms.core.portal.datasource2.handler;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.portal.rendering.tracing.DataTraceInfo;
import com.enonic.cms.core.portal.rendering.tracing.RenderTrace;
import com.enonic.cms.core.service.DataSourceService;

public abstract class DataSourceHandler
{
    protected DataSourceService dataSourceService;

    private final String name;

    public DataSourceHandler( final String name )
    {
        this.name = name;
    }

    public final String getName()
    {
        return this.name;
    }

    public abstract Document handle( final DataSourceRequest req )
        throws Exception;

    /**
     * Adds traceInfo to a content document before it's returned.
     *
     * @param doc A JDom document with all information that needs to be traced.
     */
    @SuppressWarnings("unchecked")
    protected void addDataTraceInfo( Document doc )
    {
        DataTraceInfo traceInfo = RenderTrace.getCurrentDataTraceInfo();
        if ( traceInfo != null )
        {
            Element root = doc.getRootElement();
            List<Element> contentNodes = root.getChildren( "content" );
            for ( Element e : contentNodes )
            {
                Integer key = Integer.parseInt( e.getAttributeValue( "key" ) );
                Element firstChild = (Element) e.getChildren( "title" ).get( 0 );
                String title = firstChild.getText();
                traceInfo.addContentInfo( key, title );
            }
            Element relatedContentsNode = root.getChild( "relatedcontents" );

            if ( relatedContentsNode != null )
            {
                List<Element> relatedContentNodes = relatedContentsNode.getChildren( "content" );
                for ( Element e : relatedContentNodes )
                {
                    Integer key = Integer.parseInt( e.getAttributeValue( "key" ) );
                    Element firstChild = (Element) e.getChildren( "title" ).get( 0 );
                    String title = firstChild.getText();
                    traceInfo.addRelatedContentInfo( key, title );
                }
            }
        }
    }

    @Autowired
    public void setDataSourceService( final DataSourceService dataSourceService )
    {
        this.dataSourceService = dataSourceService;
    }
}
