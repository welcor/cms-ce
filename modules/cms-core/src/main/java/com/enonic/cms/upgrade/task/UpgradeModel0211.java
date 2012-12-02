package com.enonic.cms.upgrade.task;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.enonic.cms.upgrade.UpgradeContext;

final class UpgradeModel0211
    extends AbstractUpgradeTask
{
    private static final String GET_DUPLICATES =
        "select usr_dom_lKey, usr_sSyncValue2 from tUser group by usr_dom_lKey, usr_sSyncValue2 HAVING (COUNT(usr_sSyncValue2) > 1)";

    public UpgradeModel0211()
    {
        super( 211 );
    }

    @Override
    protected boolean canModelUpgrade( UpgradeContext context )
    {
        List<Pair> duplicates;
        try
        {
            duplicates = (List<Pair>) context.getJdbcTemplate().query( GET_DUPLICATES, new UserPropertyExtractor() );
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

        context.logInfo( "There are duplicates entries in table [tUser] present in DB:" );
        for ( Pair duplicate : duplicates )
        {
            context.logInfo( "\nDuplicate user[tUser]: [usr_dom_lKey] = " + duplicate.getDomKey() + ", [usr_sSyncValue2] = " +
                                 duplicate.getSyncValue() );
        }

        return false;
    }

    public void upgrade( final UpgradeContext context )
        throws Exception
    {
        context.logInfo( "Drop all current constraints on table 'tUser'" );
        context.dropTableConstraints( "tUser", true );

        context.logInfo( "Re-create all constraints on table 'tUser'" );
        context.createTableConstraints( "tUser", true );
    }

    private class UserPropertyExtractor
        implements ResultSetExtractor
    {
        public Object extractData( ResultSet rs )
            throws SQLException, DataAccessException
        {
            List<Pair> duplicates = new ArrayList<Pair>();

            while ( rs.next() )
            {
                String domKey = rs.getString( "usr_dom_lKey" );
                String syncValue = rs.getString( "usr_sSyncValue2" );

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
