package com.enonic.cms.core.portal.datasource2.handler.content;

import org.apache.commons.lang.ArrayUtils;
import org.jdom.Document;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;

public final class GetContentVersionHandler
    extends DataSourceHandler
{
    public GetContentVersionHandler()
    {
        super( "getContentVersion" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final Integer[] keys = req.param( "versionKeys" ).required().asIntegerArray();
        int[] versionKeys = ArrayUtils.toPrimitive( keys );
        final int childrenLevel = req.param( "childrenLevel" ).asInteger( 1 );

        XMLDocument document = dataSourceService.getContentVersion( req, versionKeys, childrenLevel );
        return document.getAsJDOMDocument();
    }
}
