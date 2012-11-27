package com.enonic.cms.upgrade.task;

import com.enonic.cms.upgrade.UpgradeContext;

final class UpgradeModel0209
    extends AbstractUpgradeTask
{
    public UpgradeModel0209()
    {
        super( 209 );
    }

    public void upgrade( final UpgradeContext context )
        throws Exception
    {
        if ( !context.columnExist( "tUser", "usr_sSyncValue" ) )
        {
            context.logInfo( "Column 'tUser.usr_sSyncValue' was already dropped. Skipping." );
            return;
        }

        context.logInfo( "Insert values from column 'usr_sSyncValue' into column 'usr_sSyncValue2'" );
        context.getJdbcTemplate().execute( "UPDATE tUser SET usr_sSyncValue2 = usr_sSyncValue" );

        context.logInfo( "In Local UserStore 'usr_sSyncValue2' gets value from column 'usr_hKey' for uniqueness" );
        context.getJdbcTemplate().execute(
            "UPDATE tUser SET usr_sSyncValue2 = usr_hKey WHERE (usr_dom_lkey IN (SELECT dom_lkey FROM tDomain WHERE dom_sconfigname is null OR dom_sconfigname = '')) OR (USR_UT_LKEY IN ( 1, 2 ))" );
    }
}
