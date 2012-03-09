package com.enonic.vertical.work.quartz;

import java.util.Properties;
import java.util.logging.Logger;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.vertical.work.WorkRunner;

import com.enonic.cms.framework.jdbc.dialect.Dialect;
import com.enonic.cms.framework.jdbc.dialect.SqlServerDialect;

import com.enonic.cms.store.support.ConnectionFactory;
import com.enonic.cms.upgrade.UpgradeService;

/**
 * This class is responsible for configuring the scheduling service.
 */
public final class QuartzSchedulerManager
    implements InitializingBean, DisposableBean
{

    private final static Logger LOG = Logger.getLogger( QuartzSchedulerManager.class.getName() );

    /**
     * Instance.
     */
    private static QuartzSchedulerManager INSTANCE;

    /**
     * Data source.
     */
    private ConnectionFactory connectionFactory;

    /**
     * Enabled?
     */
    private boolean enabled = false;

    /**
     * Scheduler.
     */
    private Scheduler scheduler;

    /**
     * Clustered?
     */
    private boolean clustered;

    private WorkRunner workRunner;

    private UpgradeService upgradeService;

    private Dialect dialect;

    public QuartzSchedulerManager()
    {
        INSTANCE = this;
    }

    @Autowired
    public void setConnectionFactory( ConnectionFactory connectionFactory )
    {
        this.connectionFactory = connectionFactory;
    }

    @Autowired
    public void setUpgradeService( UpgradeService upgradeService )
    {
        this.upgradeService = upgradeService;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }

    public void setClustered( boolean clustered )
    {
        this.clustered = clustered;
    }

    @Autowired
    public void setWorkRunner( WorkRunner workRunner )
    {
        this.workRunner = workRunner;
    }

    public void afterPropertiesSet()
        throws Exception
    {
        if ( this.enabled && !this.upgradeService.needsUpgrade() )
        {
            QuartzHelper.setConnectionFactory( this.connectionFactory );
            QuartzHelper.setWorkRunner( this.workRunner );
            StdSchedulerFactory factory = new StdSchedulerFactory( createProperties() );

            this.scheduler = factory.getScheduler();
            LOG.info( "Starting Quartz Scheduler (version: " + this.scheduler.getMetaData().getVersion() + ")" );
            this.scheduler.start();
        }
    }

    public void destroy()
        throws Exception
    {
        if ( this.scheduler != null )
        {
            LOG.info( "Stopping Quartz Scheduler (version: " + this.scheduler.getMetaData().getVersion() + ")" );
            this.scheduler.shutdown();
            this.scheduler = null;
        }
    }

    private Properties createProperties()
    {
        Properties props = new Properties();
        props.setProperty( "org.quartz.scheduler.instanceId", "auto" );
        props.setProperty( "org.quartz.scheduler.instanceName", "default" );
        props.setProperty( "org.quartz.threadPool.class", org.quartz.simpl.SimpleThreadPool.class.getName() );
        props.setProperty( "org.quartz.threadPool.threadCount", "15" );
        props.setProperty( "org.quartz.threadPool.threadPriority", "5" );
        props.setProperty( "org.quartz.jobStore.class", QuartzJobStore.class.getName() );
        props.setProperty( "org.quartz.jobStore.driverDelegateClass", org.quartz.impl.jdbcjobstore.StdJDBCDelegate.class.getName() );
        props.setProperty( "org.quartz.jobStore.tablePrefix", "QRTZ_" );
        props.setProperty( "org.quartz.jobStore.isClustered", String.valueOf( this.clustered ) );

        // for MS SQL Server
        if ( dialect instanceof SqlServerDialect )
        {
            props.setProperty( "org.quartz.jobStore.selectWithLockSQL",
                               "SELECT * FROM {0}LOCKS WITH (UPDLOCK ROWLOCK) WHERE LOCK_NAME = ?" );
        }

        return props;
    }

    public Scheduler getScheduler()
    {
        return this.scheduler;
    }

    public static QuartzSchedulerManager getInstance()
    {
        return INSTANCE;
    }

    @Autowired
    public void setDialect( Dialect dialect )
    {
        this.dialect = dialect;
    }

    public Dialect getDialect()
    {
        return dialect;
    }
}
