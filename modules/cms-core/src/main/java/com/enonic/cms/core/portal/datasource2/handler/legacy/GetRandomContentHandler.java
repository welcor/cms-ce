package com.enonic.cms.core.portal.datasource2.handler.legacy;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;

public final class GetRandomContentHandler
    extends DataSourceHandler
{
    public GetRandomContentHandler()
    {
        super( "getRandomContent" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int count = req.param( "count" ).asInteger( 10 );
        final Integer[] categoryKeys = req.param( "categoryKeys" ).required().asIntegerArray();
        final boolean recursive = req.param( "recursive" ).asBoolean( false );
        final int childrenLevel = req.param( "childrenLevel" ).asInteger( 1 );
        final int minPriority = req.param( "minPriority" ).asInteger( 0 );
        final int parentLevel = req.param( "parentLevel" ).asInteger( 0 );
        final int parentChildrenLevel = req.param( "parentChildrenLevel" ).asInteger( 0 );

        // TODO: Implement based on DataSourceServiceImpl.getRandomContent(..)
        return null;
    }
}
