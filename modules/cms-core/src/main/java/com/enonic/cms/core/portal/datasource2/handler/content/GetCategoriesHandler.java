package com.enonic.cms.core.portal.datasource2.handler.content;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.vertical.engine.PresentationEngine;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.service.DataSourceServiceCompabilityKeeper;

public final class GetCategoriesHandler
    extends DataSourceHandler
{
    private PresentationEngine presentationEngine;

    public GetCategoriesHandler()
    {
        super( "getCategories" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int categoryKey = req.param( "categoryKey" ).required().asInteger();
        final int levels = req.param( "levels" ).asInteger( 0 );
        final boolean includeContentCount = req.param( "includeContentCount" ).asBoolean( false );
        final boolean includeTopCategory = req.param( "includeTopCategory" ).asBoolean( true );

        final UserEntity user = req.getCurrentUser();
        org.w3c.dom.Document doc =
            presentationEngine.getCategories( user, categoryKey, levels, includeTopCategory, true, true, includeContentCount );

        // TODO check if the compatibility changes are still needed
        DataSourceServiceCompabilityKeeper.fixCategoriesCompability( doc );

        return JDOMUtil.toDocument( doc );
    }

    @Autowired
    public void setPresentationEngine( final PresentationEngine presentationEngine )
    {
        this.presentationEngine = presentationEngine;
    }
}
