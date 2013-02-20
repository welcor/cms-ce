package com.enonic.cms.web.boot;

import javax.servlet.ServletContextEvent;
import org.springframework.web.context.ContextLoaderListener;

public final class BootContextListener
    extends ContextLoaderListener
{
    @Override
    public void contextInitialized( final ServletContextEvent event )
    {
        new BootEnvironment().initialize();
        super.contextInitialized( event );
    }
}
