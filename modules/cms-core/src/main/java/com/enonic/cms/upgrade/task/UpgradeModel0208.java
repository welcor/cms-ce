package com.enonic.cms.upgrade.task;

import com.enonic.cms.upgrade.UpgradeContext;

final class UpgradeModel0208
    extends AbstractUpgradeTask
{
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

        context.logInfo( "Add new column into table 'tUser'" );
        context.getJdbcTemplate().execute( "ALTER TABLE tUser ADD usr_sSyncValue2 @varchar(255)@ NOT NULL DEFAULT ''" );

        context.reorganizeTablesForDb2( "tUser" );
    }
}
