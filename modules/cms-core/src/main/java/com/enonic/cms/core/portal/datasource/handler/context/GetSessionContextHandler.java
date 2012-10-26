package com.enonic.cms.core.portal.datasource.handler.context;

import org.jdom.Document;
import org.jdom.Element;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.util.JDOMUtil;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.portal.VerticalSession;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamDataSourceHandler;

@Component("ds.GetSessionContextHandler")
public final class GetSessionContextHandler
    extends ParamDataSourceHandler
{
    public GetSessionContextHandler()
    {
        super( "getSessionContext" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final Document document = new Document();
        final VerticalSession verticalSession = req.getVerticalSession();
        if ( verticalSession != null )
        {
            document.addContent( buildVerticalSessionXml( verticalSession ) );
        }
        return document;
    }

    private Element buildVerticalSessionXml( VerticalSession session )
    {
        Document doc = XMLDocumentFactory.create( session.toXML() ).getAsJDOMDocument();
        return (Element) JDOMUtil.getFirstElement( doc.getRootElement() ).detach();
    }
}
