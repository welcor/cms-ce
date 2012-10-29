package com.enonic.cms.core.portal.datasource.handler.content;

import org.apache.commons.lang.ArrayUtils;
import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource.handler.base.SimpleDataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;

@Component("ds.GetContentVersionHandler")
public final class GetContentVersionHandler
    extends SimpleDataSourceHandler
{
    public GetContentVersionHandler()
    {
        super( "getContentVersion" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final Integer[] keys = param( req, "versionKeys" ).required().asIntegerArray();
        int[] versionKeys = ArrayUtils.toPrimitive( keys );
        final int childrenLevel = param( req, "childrenLevel" ).asInteger( 1 );

        XMLDocument document = dataSourceService.getContentVersion( req, versionKeys, childrenLevel );
        return document.getAsJDOMDocument();
    }
}
