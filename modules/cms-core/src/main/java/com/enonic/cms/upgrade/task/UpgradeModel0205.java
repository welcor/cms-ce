/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.upgrade.task;

import com.enonic.cms.upgrade.UpgradeContext;

final class UpgradeModel0205
    extends AbstractUpgradeTask
{
    public UpgradeModel0205()
    {
        super( 205 );
    }

    @Override
    public void upgrade( final UpgradeContext context )
        throws Exception
    {
        context.logInfo( "Dropping tVirtualFile table" );
        context.dropTable( "tVirtualFile" );
    }
}
