package com.enonic.cms.upgrade.standalone;

import com.enonic.cms.upgrade.UpgradeService;
import com.enonic.cms.upgrade.log.UpgradeLog;

final class StandaloneUpgradeImpl
    implements StandaloneUpgrade
{
    private final UpgradeLog logger;

    private final UpgradeService upgradeService;

    public StandaloneUpgradeImpl( final UpgradeService upgradeService )
    {
        this.logger = new UpgradeLog();
        this.upgradeService = upgradeService;
    }

    @Override
    public int getCurrentModel()
    {
        return this.upgradeService.getCurrentModelNumber();
    }

    @Override
    public int getTargetModel()
    {
        return this.upgradeService.getTargetModelNumber();
    }

    @Override
    public boolean upgradeAll()
        throws Exception
    {
        return this.upgradeService.upgrade( this.logger );
    }

    @Override
    public boolean upgradeStep()
        throws Exception
    {
        return this.upgradeService.upgradeStep( this.logger );
    }

    @Override
    public boolean upgradeCheck()
        throws Exception
    {
        return this.upgradeService.canUpgrade( this.logger );
    }
}
