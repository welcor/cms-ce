package com.enonic.cms.upgrade.standalone;

import java.io.File;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.enonic.cms.framework.jdbc.DialectConnectionDecorator;
import com.enonic.cms.framework.jdbc.dialect.Dialect;
import com.enonic.cms.framework.jdbc.dialect.DialectResolver;

import com.enonic.cms.store.support.ConnectionFactory;
import com.enonic.cms.upgrade.UpgradeService;
import com.enonic.cms.upgrade.service.StandardPropertyResolver;
import com.enonic.cms.upgrade.service.UpgradeServiceImpl;

/**
 * This class is used by stand-alone upgrade managers and should be handled as API.
 */
@SuppressWarnings( "unused" )
public final class StandaloneUpgradeFactory
{
    private File homeDir;

    private DataSource dataSource;

    public void setHomeDir( final File homeDir )
    {
        this.homeDir = homeDir;
    }

    public void setDataSource( final DataSource dataSource )
    {
        this.dataSource = dataSource;
    }

    public StandaloneUpgrade create()
        throws Exception
    {
        final UpgradeService upgradeService = createUpgradeService();
        return new StandaloneUpgradeImpl( upgradeService );
    }

    private UpgradeService createUpgradeService()
        throws Exception
    {
        final DialectResolver dialectResolver = new DialectResolver();
        dialectResolver.setDataSource( this.dataSource );
        final Dialect dialect = dialectResolver.resolveDialect();

        final ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setDataSource( this.dataSource );
        connectionFactory.setDecorator( new DialectConnectionDecorator( dialect ) );

        final DataSourceTransactionManager txManager = new DataSourceTransactionManager();
        txManager.setDataSource( this.dataSource );
        txManager.afterPropertiesSet();

        final TransactionTemplate txTemplate = new TransactionTemplate();
        txTemplate.setTransactionManager( txManager );
        txTemplate.afterPropertiesSet();

        final StandardPropertyResolver propertyResolver = new StandardPropertyResolver();
        propertyResolver.setHomeDir( this.homeDir );
        propertyResolver.afterPropertiesSet();

        final UpgradeServiceImpl upgradeService = new UpgradeServiceImpl();
        upgradeService.setDialect( dialect );
        upgradeService.setConnectionFactory( connectionFactory );
        upgradeService.setTransactionTemplate( txTemplate );
        upgradeService.setPropertyResolver( propertyResolver );
        upgradeService.afterPropertiesSet();

        return upgradeService;
    }
}
