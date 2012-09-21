/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade.task;

import java.util.List;

import com.enonic.cms.upgrade.UpgradeContext;

public final class UpgradeModel0202
    extends AbstractUpgradeTask
{
    public UpgradeModel0202()
    {
        super( 202 );
    }

    public void upgrade( final UpgradeContext context )
        throws Exception
    {
        if (context.hasTable( "QRTZ_JOB_DETAILS" )) {
            context.logInfo( "Quartz table are available. Skipping." );
            return;
        }

        context.logInfo( "Creating Quartz Scheduler tables..." );
        final List<String> statements = context.getStatementsFromSchema( 202 );
        for ( String statement : statements )
        {
            context.executeStatement( statement );
        }
    }
}
