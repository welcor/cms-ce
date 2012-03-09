package com.enonic.cms.upgrade.task.helper;

public final class StaticUpgradeState
{
    private final static StaticUpgradeState INSTANCE =
        new StaticUpgradeState();

    private boolean quartzTablesAvailable;

    private StaticUpgradeState()
    {
        this.quartzTablesAvailable = false;
    }

    public boolean isQuartzTablesAvailable()
    {
        return quartzTablesAvailable;
    }

    public void setQuartzTablesAvailable( final boolean quartzTablesAvailable )
    {
        this.quartzTablesAvailable = quartzTablesAvailable;
    }

    public static StaticUpgradeState getInstance()
    {
        return INSTANCE;
    }
}
