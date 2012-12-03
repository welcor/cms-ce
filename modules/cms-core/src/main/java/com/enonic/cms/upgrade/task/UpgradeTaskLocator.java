/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade.task;

import java.util.List;

import com.google.common.collect.Lists;

public final class UpgradeTaskLocator
{
    private final List<UpgradeTask> tasks;

    public UpgradeTaskLocator()
    {
        this.tasks = Lists.newArrayList();
        this.tasks.add( new UpgradeModel0201() );
        this.tasks.add( new UpgradeModel0202() );
        this.tasks.add( new UpgradeModel0203() );
        this.tasks.add( new UpgradeModel0204() );
        this.tasks.add( new UpgradeModel0205() );
        this.tasks.add( new UpgradeModel0206() );
        this.tasks.add( new UpgradeModel0207() );
        this.tasks.add( new UpgradeModel0208() );
        this.tasks.add( new UpgradeModel0209() );
        this.tasks.add( new UpgradeModel0210() );
        this.tasks.add( new UpgradeModel0211() );
        this.tasks.add( new UpgradeModel0212() );
        this.tasks.add( new UpgradeModel0213() );
    }

    public List<UpgradeTask> getTasks()
    {
        return this.tasks;
    }
}
