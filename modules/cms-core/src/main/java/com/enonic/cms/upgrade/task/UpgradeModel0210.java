package com.enonic.cms.upgrade.task;

import com.enonic.cms.upgrade.UpgradeContext;

final class UpgradeModel0210
    extends AbstractUpgradeTask
{
    public UpgradeModel0210()
    {
        super( 210 );
    }

    public void upgrade( final UpgradeContext context )
        throws Exception
    {
        if ( !context.columnExist( "tUser", "usr_sSyncValue" ) )
        {
            context.logInfo( "Column 'tUser.usr_sSyncValue' was already dropped. Skipping." );
            return;
        }

        context.logInfo( "Drop all current constraints on table 'tUser'" );
        context.dropTableConstraints( "tUser", true );

        context.logInfo( "Drop column 'usr_sSyncValue' on table 'tUser'" );
        context.getJdbcTemplate().execute( "ALTER TABLE tUser DROP column usr_sSyncValue" );
    }
}
