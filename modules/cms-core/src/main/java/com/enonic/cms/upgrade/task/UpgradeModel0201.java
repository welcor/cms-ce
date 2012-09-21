/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade.task;

import com.enonic.cms.upgrade.UpgradeContext;

public final class UpgradeModel0201
    extends AbstractUpgradeTask
{
    public UpgradeModel0201()
    {
        super( 201 );
    }

    public void upgrade( final UpgradeContext context )
        throws Exception
    {
        // Do not remove quartz tables anymore
    }
}
