package com.enonic.cms.upgrade.standalone;

import com.enonic.cms.upgrade.UpgradeService;
import com.enonic.cms.upgrade.log.UpgradeLog;

/**
 * This interface is used by stand-alone upgrade managers and should be handled as API.
 */
@SuppressWarnings( "unused" )
public final class StandaloneUpgrade
{
    private final UpgradeLog logger;

    private final UpgradeService upgradeService;

    public StandaloneUpgrade( final UpgradeService upgradeService )
    {
        this.logger = new UpgradeLog();
        this.upgradeService = upgradeService;
    }

    public int getCurrentModel()
    {
        return this.upgradeService.getCurrentModelNumber();
    }

    public int getTargetModel()
    {
        return this.upgradeService.getTargetModelNumber();
    }

    public boolean upgradeAll()
        throws Exception
    {
        return this.upgradeService.upgrade( this.logger );
    }

    public boolean upgradeStep()
        throws Exception
    {
        return this.upgradeService.upgradeStep( this.logger );
    }

    public boolean upgradeCheck()
        throws Exception
    {
        return this.upgradeService.canUpgrade( this.logger );
    }
}
