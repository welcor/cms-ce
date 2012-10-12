package com.enonic.cms.core.portal.datasource2.handler.util;

import java.util.Map;

import org.jdom.Document;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreNotFoundException;
import com.enonic.cms.core.security.userstore.UserStoreParser;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.core.security.userstore.UserStoreXmlCreator;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.cms.store.dao.UserStoreDao;

public final class GetUserStoreHandler
    extends DataSourceHandler
{
    private UserStoreService userStoreService;

    private UserStoreParser userStoreParser;

    public GetUserStoreHandler()
    {
        super( "getUserStore" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final String userStore = req.param( "userStore" ).asString();
        return getUserStore( userStore );
    }

    private Document getUserStore( final String userStore )
    {
        final Map<String, UserStoreConnectorConfig> configs = this.userStoreService.getUserStoreConnectorConfigs();
        final UserStoreXmlCreator xmlCreator = new UserStoreXmlCreator( configs );

        if ( userStore == null )
        {
            return xmlCreator.createUserStoresDocument( this.userStoreService.getDefaultUserStore() );
        }

        final UserStoreEntity entity = parseUserStore( userStore );
        if ( entity != null )
        {
            return xmlCreator.createUserStoresDocument( entity );
        }
        else
        {
            return xmlCreator.createUserStoreNotFoundDocument( userStore );
        }
    }

    private UserStoreEntity parseUserStore( final String key )
    {
        try
        {
            return this.userStoreParser.parseUserStore( key );
        }
        catch ( final UserStoreNotFoundException e )
        {
            return null;
        }
    }

    @Autowired
    public void setUserStoreDao( final UserStoreDao userStoreDao )
    {
        this.userStoreParser = new UserStoreParser( userStoreDao );
    }

    @Autowired
    public void setUserStoreService( final UserStoreService userStoreService )
    {
        this.userStoreService = userStoreService;
    }
}
