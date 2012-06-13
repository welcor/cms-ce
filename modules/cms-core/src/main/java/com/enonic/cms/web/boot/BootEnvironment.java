package com.enonic.cms.web.boot;

import com.enonic.cms.core.home.HomeDir;
import com.enonic.cms.core.home.HomeResolver;

final class BootEnvironment
{
    private HomeDir homeDir;

    public void initialize()
    {
        resolveHomeDir();
    }

    public void destroy()
    {
        // Do nothing for now
    }

    private void resolveHomeDir()
    {
        final HomeResolver resolver = new HomeResolver();
        this.homeDir = resolver.resolve();
    }
}
