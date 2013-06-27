/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.mockito.Mockito;

import com.enonic.cms.framework.jdbc.wrapper.ConnectionWrapper;
import com.enonic.cms.framework.jdbc.wrapper.PreparedStatementWrapper;
import com.enonic.cms.framework.jdbc.wrapper.StatementWrapper;

import static org.junit.Assert.*;

/**
 * Jan 19, 2010
 */
public abstract class AbstractConnectionDecoratorTest
{
    protected Statement realStatement;

    protected PreparedStatement realPreparedStatement;

    protected Connection realConnection;


    public void setupRealConnectionStatementAndResultSet()
        throws SQLException
    {
        realConnection = Mockito.mock( Connection.class );
        realStatement = Mockito.mock( Statement.class );
        realPreparedStatement = Mockito.mock( PreparedStatement.class );

        Mockito.when( realConnection.createStatement() ).thenReturn( realStatement );
        Mockito.when( realConnection.prepareStatement( Mockito.anyString() ) ).thenReturn( realPreparedStatement );
    }

    public void testDecoratingLevel( ConnectionDecorator connectionDecorator, int levels )
        throws SQLException
    {
        testDecoratingLevelWithStatement( connectionDecorator, levels );
        testDecoratingLevelWithPreparedStatement( connectionDecorator, levels );
    }

    public void testDecoratingLevelWithStatement( ConnectionDecorator connectionDecorator, int levels )
        throws SQLException
    {
        Connection decoratedConnnection = decorate( connectionDecorator, realConnection, levels );

        assertDecoratedConnection( realConnection, decoratedConnnection, levels );

        // verify statement
        Statement decoratedStatement = decoratedConnnection.createStatement();
        assertDecoratedStatement( realStatement, decoratedStatement, levels );
    }

    public void testDecoratingLevelWithPreparedStatement( ConnectionDecorator connectionDecorator, int levels )
        throws SQLException
    {
        Connection decoratedConnnection = decorate( connectionDecorator, realConnection, levels );

        assertDecoratedConnection( realConnection, decoratedConnnection, levels );

        // verify statement
        PreparedStatement decoratedPreparedStatement = decoratedConnnection.prepareStatement( "dummySQL" );
        assertDecoratedPreparedStatement( realPreparedStatement, decoratedPreparedStatement, levels );
    }

    Connection decorate( ConnectionDecorator connectionDecorator, Connection connectionToBeDecorated, int times )
        throws SQLException
    {
        if ( times <= 0 )
        {
            return connectionToBeDecorated;
        }

        Connection decorated = connectionDecorator.decorate( connectionToBeDecorated );
        return decorate( connectionDecorator, decorated, times - 1 );
    }

    void assertDecoratedConnection( Connection real, Connection decorated, int numberOfLevelsToReal )
    {
        assertNotSame( real, decorated );
        assertTrue( "decorated Connection not DelegatingConnection at level " + numberOfLevelsToReal,
                    decorated instanceof ConnectionWrapper );
    }

    void assertDecoratedStatement( Statement real, Statement decorated, int numberOfLevelsToReal )
    {
        assertNotSame( real, decorated );
        assertTrue( "decorated Statement not DelegatingStatement at level " + numberOfLevelsToReal,
                    decorated instanceof StatementWrapper );
    }

    void assertDecoratedPreparedStatement( PreparedStatement real, PreparedStatement decorated, int numberOfLevelsToReal )
    {
        assertNotSame( real, decorated );
        assertTrue( "decorated Statement not DelegatingStatement at level " + numberOfLevelsToReal,
                    decorated instanceof PreparedStatementWrapper );
    }
}
