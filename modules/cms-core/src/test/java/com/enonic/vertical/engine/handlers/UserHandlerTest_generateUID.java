package com.enonic.vertical.engine.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.store.dao.UserDao;

import static org.junit.Assert.*;

public class UserHandlerTest_generateUID
{
    private UserHandler userHandler;

    @Before
    public void setUp()
    {
        userHandler = new UserHandler();
        userHandler.userDao = Mockito.mock( UserDao.class );
    }

    @Test
    public void testGenerate_Usual()
    {
        final String username = userHandler.generateUID( "Piter", "Pen", new UserStoreKey( "0" ) );
        assertEquals( "piterp", username );
    }

    @Test
    public void testGenerateUID_Cyrillic()
    {
        // Василий Щукин
        final String username = userHandler.generateUID( "\u0412\u0430\u0441\u0438\u043b\u0438\u0439", "\u0429\u0443\u043a\u0438\u043d", new UserStoreKey( "0" ) );
        assertEquals( "vasilijs", username );
    }

    @Test
    public void testGenerateUID_Empty()
    {
        final String username = userHandler.generateUID( " ", " ", new UserStoreKey( "0" ) );
        assertTrue( username.matches( "user\\d\\d\\du" ) );
    }
}
