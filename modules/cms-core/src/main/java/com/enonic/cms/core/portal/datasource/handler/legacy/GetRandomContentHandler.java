package com.enonic.cms.core.portal.datasource.handler.legacy;

import org.jdom.Document;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.SimpleDataSourceHandler;

@Component("ds.GetRandomContentHandler")
public final class GetRandomContentHandler
    extends SimpleDataSourceHandler
{
    public GetRandomContentHandler()
    {
        super( "getRandomContent" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int count = param( req, "count" ).asInteger( 10 );
        final Integer[] categoryKeys = param( req, "categoryKeys" ).required().asIntegerArray();
        final boolean recursive = param( req, "recursive" ).asBoolean( false );
        final int childrenLevel = param( req, "childrenLevel" ).asInteger( 1 );
        final int minPriority = param( req, "minPriority" ).asInteger( 0 );
        final int parentLevel = param( req, "parentLevel" ).asInteger( 0 );
        final int parentChildrenLevel = param( req, "parentChildrenLevel" ).asInteger( 0 );

        // TODO: Implement based on DataSourceServiceImpl.getRandomContent(..)
        return null;
    }
}
