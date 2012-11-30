package com.enonic.cms.core.plugin.deploy;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.enonic.cms.api.util.LogFacade;
import com.enonic.cms.core.plugin.PluginManager;

@Component
public final class HotDeployTask
{
    private final static LogFacade LOG = LogFacade.get( HotDeployTask.class );

    private File deployDir;

    private long scanPeriod;

    private FileAlterationMonitor monitor;

    private PluginManager pluginManager;

    @Value("${cms.plugin.deployDir}")
    public void setDeployDir( final File deployDir )
    {
        this.deployDir = deployDir;
    }

    @Value("${cms.plugin.scanPeriod}")
    public void setScanPeriod( final long scanPeriod )
    {
        this.scanPeriod = scanPeriod;
    }

    @Autowired
    public void setPluginManager( final PluginManager pluginManager )
    {
        this.pluginManager = pluginManager;
    }

    @PostConstruct
    public void start()
        throws Exception
    {
        final JarFileFilter filter = new JarFileFilter();

        final FileAlterationObserver observer = new FileAlterationObserver(this.deployDir, filter);
        observer.addListener(new HotDeployListener( this.pluginManager ));
        observer.checkAndNotify();

        this.monitor = new FileAlterationMonitor(this.scanPeriod, observer);
        this.monitor.start();

        LOG.info("Hot deploying plugins from [{0}]. Scanning every [{1}] ms.", this.deployDir.getAbsolutePath(), this.scanPeriod);
    }

    @PreDestroy
    public void stop()
        throws Exception
    {
        this.monitor.stop();
    }
}
