package com.enonic.cms.upgrade.task;

import com.enonic.cms.upgrade.UpgradeContext;

final class UpgradeModel0211
    extends AbstractUpgradeTask
{
    public UpgradeModel0211()
    {
        super( 211 );
    }

    public void upgrade( final UpgradeContext context )
        throws Exception
    {
        context.logInfo( "Drop all current constraints on table 'tUser'" );
        context.dropTableConstraints( "tUser", true );

        context.logInfo( "Re-create all constraints on table 'tUser'" );
        context.createTableConstraints( "tUser", true );
        context.reorganizeTablesForDb2( "tUser" );
    }
}
