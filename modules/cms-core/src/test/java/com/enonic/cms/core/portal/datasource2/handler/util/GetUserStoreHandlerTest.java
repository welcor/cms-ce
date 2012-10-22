package com.enonic.cms.core.portal.datasource2.handler.util;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.http.HTTPService;
import com.enonic.cms.core.portal.datasource2.DataSourceException;
import com.enonic.cms.core.portal.datasource2.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.UserStoreService;
import com.enonic.cms.store.dao.UserStoreDao;

public class GetUserStoreHandlerTest
    extends AbstractDataSourceHandlerTest<GetUserStoreHandler>
{
    private UserStoreDao userStoreDao;

    private UserStoreService userStoreService;

    public GetUserStoreHandlerTest()
    {
        super( GetUserStoreHandler.class );
    }

    @Override
    protected void initTest()
        throws Exception
    {
        this.userStoreDao = Mockito.mock( UserStoreDao.class );
        this.userStoreService = Mockito.mock( UserStoreService.class );
        this.handler.setUserStoreDao( this.userStoreDao );
        this.handler.setUserStoreService( this.userStoreService );
    }

    @Test
    public void testDefault()
        throws Exception
    {
        final UserStoreEntity entity = new UserStoreEntity();
        entity.setKey( new UserStoreKey( 0 ) );
        entity.setName( "default" );
        entity.setConnectorName( null );
        entity.setDefaultStore( true );
        Mockito.when( this.userStoreService.getDefaultUserStore() ).thenReturn( entity );

        testHandle( "getUserStore_default" );
    }

    @Test
    public void testDummy()
        throws Exception
    {
        final UserStoreEntity entity = new UserStoreEntity();
        entity.setKey( new UserStoreKey( 1 ) );
        entity.setName( "dummy" );
        entity.setConnectorName( null );
        entity.setDefaultStore( false );
        Mockito.when( this.userStoreDao.findByName( "dummy" )).thenReturn( entity );

        this.request.addParam( "userStore", "dummy" );
        testHandle( "getUserStore_dummy" );
    }

    @Test
    public void testNotFound()
        throws Exception
    {
        this.request.addParam( "userStore", "other" );
        testHandle( "getUserStore_notFound" );
    }
}
