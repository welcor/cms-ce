/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.upgrade.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.enonic.cms.upgrade.UpgradeContext;

final class UpgradeModel0208
    extends AbstractUpgradeTask
{
    @Override
    protected boolean canModelUpgrade( UpgradeContext context )
    {
        context.logInfo( "Looking for duplicate users in database..." );
        context.logInfo( "Duplicates user are found if more than one user resides in a user-store with same sync value" );

        List<Pair> duplicates;
        SyncValueColumn syncValueColumn;

        try
        {
            boolean isSyncValue2 = context.columnExist( "tUser", "usr_sSyncValue2" );
            syncValueColumn = isSyncValue2 ? SyncValueColumn.SYNCVALUE2 : SyncValueColumn.SYNCVALUE;

            duplicates =
                (List<Pair>) context.getJdbcTemplate().query( syncValueColumn.getQuery(), new UserPropertyExtractor( syncValueColumn ) );
        }
        catch ( Exception ex )
        {
            context.logError( ex.getMessage(), ex );
            return false;
        }

        if ( duplicates.isEmpty() )
        {
            context.logInfo( "Upgrade check ok." );
            return true;
        }

        context.logInfo( "Duplicate users found:" );
        for ( Pair duplicate : duplicates )
        {
            final String message =
                "\nusr_dom_lKey = " + duplicate.getDomKey() + ", " + syncValueColumn.getName() + " = '" + duplicate.getSyncValue() + "', " +
                    "SELECT SQL: SELECT * FROM tuser WHERE usr_dom_lKey = " + duplicate.getDomKey() + " AND " + syncValueColumn.getName() +
                    " = '" +
                    duplicate.getSyncValue() + "'";

            context.logInfo( message );
        }

        context.logInfo( "Please use the generated SELECT SQL to identify each duplicated user." );
        context.logInfo( "Then set the " + syncValueColumn.getName() + " of the user that is no longer needed to something unique." );

        return false;
    }

    public UpgradeModel0208()
    {
        super( 208 );
    }

    public void upgrade( final UpgradeContext context )
        throws Exception
    {
        if ( context.columnExist( "tUser", "usr_sSyncValue2" ) )
        {
            context.logInfo( "'tUser.usr_sSyncValue2' is already exists. Skipping." );
            return;
        }

        context.logInfo( "Adding new column 'usr_sSyncValue2' to table 'tUser'" );
        context.getJdbcTemplate().execute( "ALTER TABLE tUser ADD usr_sSyncValue2 @varchar(255)@ DEFAULT '-' NOT NULL" );

        context.reorganizeTablesForDb2( "tUser" );
    }

    private enum SyncValueColumn
    {
        SYNCVALUE
            {
                String getQuery()
                {
                    StringBuilder sb = new StringBuilder();
                    sb.append( "select usr_dom_lKey, usr_sSyncValue " );
                    sb.append( "from tUser " );
                    // to exclude 1) Admin user, 2) Anonymous user, 3) users in Local User Store
                    sb.append(
                        "WHERE NOT ( (usr_dom_lkey IN (SELECT dom_lkey FROM tDomain WHERE dom_sconfigname is null OR dom_sconfigname = '')) OR (USR_UT_LKEY IN ( 1, 2 )) ) " );
                    sb.append( "group by usr_dom_lKey, usr_sSyncValue HAVING (COUNT(usr_sSyncValue) > 1) " );

                    return sb.toString();
                }

                String getName()
                {
                    return "usr_sSyncValue";
                }
            },
        SYNCVALUE2
            {
                String getQuery()
                {
                    StringBuilder sb = new StringBuilder();
                    sb.append( "select usr_dom_lKey, usr_sSyncValue2 " );
                    sb.append( "from tUser " );
                    // to exclude 1) Admin user, 2) Anonymous user, 3) users in Local User Store
                    sb.append(
                        "WHERE NOT ( (usr_dom_lkey IN (SELECT dom_lkey FROM tDomain WHERE dom_sconfigname is null OR dom_sconfigname = '')) OR (USR_UT_LKEY IN ( 1, 2 )) ) " );
                    sb.append( "group by usr_dom_lKey, usr_sSyncValue2 HAVING (COUNT(usr_sSyncValue2) > 1) " );

                    return sb.toString();
                }

                String getName()
                {
                    return "usr_sSyncValue2";
                }
            };

        abstract String getQuery();

        abstract String getName();
    }

    private class UserPropertyExtractor
        implements ResultSetExtractor
    {
        private SyncValueColumn syncValueColumn;

        private UserPropertyExtractor( final SyncValueColumn syncValueColumn )
        {
            this.syncValueColumn = syncValueColumn;
        }

        public Object extractData( ResultSet rs )
            throws SQLException, DataAccessException
        {
            List<Pair> duplicates = new ArrayList<Pair>();

            while ( rs.next() )
            {
                String domKey = rs.getString( "usr_dom_lKey" );
                String syncValue = rs.getString( syncValueColumn.getName() );

                duplicates.add( new Pair( domKey, syncValue ) );
            }

            return duplicates;
        }
    }

    private class Pair
    {
        private String domKey;

        private String syncValue;

        private Pair( String domKey, String syncValue )
        {
            this.domKey = domKey;
            this.syncValue = syncValue;
        }

        public String getDomKey()
        {
            return domKey;
        }

        public String getSyncValue()
        {
            return syncValue;
        }
    }
}
