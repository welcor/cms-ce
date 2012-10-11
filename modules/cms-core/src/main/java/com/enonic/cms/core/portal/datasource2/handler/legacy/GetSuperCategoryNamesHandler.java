package com.enonic.cms.core.portal.datasource2.handler.legacy;

import org.jdom.Document;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;

public final class GetSuperCategoryNamesHandler
    extends DataSourceHandler
{
    public GetSuperCategoryNamesHandler()
    {
        super( "getSuperCategoryNames" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        // TODO: Implement based on DataSourceServiceImpl.getSuperCategoryNames(..)
        return null;
    }
}
