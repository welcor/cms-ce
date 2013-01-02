package com.enonic.cms.upgrade.standalone;

/**
 * This interface is used by stand-alone upgrade managers and should be handled as API.
 */
@SuppressWarnings( "unused" )
public interface StandaloneUpgrade
{
    public int getCurrentModel();

    public int getTargetModel();

    public boolean upgradeAll()
        throws Exception;

    public boolean upgradeStep()
        throws Exception;

    public boolean upgradeCheck()
        throws Exception;
}
