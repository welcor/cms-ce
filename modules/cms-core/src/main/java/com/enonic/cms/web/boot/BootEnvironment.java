package com.enonic.cms.web.boot;

import java.io.File;

import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;

import com.enonic.cms.core.home.HomeDir;
import com.enonic.cms.core.home.HomeResolver;
import com.enonic.cms.core.product.ProductVersion;

final class BootEnvironment
{
    private final static Logger LOG = LoggerFactory.getLogger( BootEnvironment.class );

    private final static String BANNER = "\n" +
        " _______ _______ _______ _______ _______ ______   ______ _______ _______ \n" +
        "|    ___|    |  |       |    |  |_     _|      | |      |   |   |     __|\n" +
        "|    ___|       |   -   |       |_|   |_|   ---| |   ---|       |__     |\n" +
        "|_______|__|____|_______|__|____|_______|______| |______|__|_|__|_______|\n\n";

    private HomeDir homeDir;

    public void initialize()
    {
        try {
            doInitialize();
        } catch (final Exception e) {
            LOG.error( "Error occurred starting system", e );

            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            } else {
                throw new RuntimeException( e );
            }
        }
    }

    private void doInitialize()
        throws Exception
    {
        initializeLogging();
        logBanner();
        resolveHomeDir();
        configureLogBack();
    }

    public void destroy()
    {
        destroyLogging();
    }

    private void initializeLogging()
    {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    private void destroyLogging()
    {
        SLF4JBridgeHandler.uninstall();
    }

    private void resolveHomeDir()
    {
        final HomeResolver resolver = new HomeResolver();
        this.homeDir = resolver.resolve();
    }

    private void logBanner()
    {
        final StringBuilder str = new StringBuilder( BANNER );
        str.append( "  # " ).append( ProductVersion.getFullTitleAndVersion() ).append( "\n" );
        str.append( "  # " ).append( getFormattedJvmInfo() ).append( "\n" );
        str.append( "  # " ).append( getFormattedOsInfo() ).append( "\n" );

        LOG.info( str.toString() );
    }

    private String getFormattedJvmInfo()
    {
        final StringBuilder str = new StringBuilder();
        str.append( SystemUtils.JAVA_RUNTIME_NAME ).append( " " ).append( SystemUtils.JAVA_RUNTIME_VERSION ).append( " (" ).append(
            SystemUtils.JAVA_VENDOR ).append( ")" );
        return str.toString();
    }

    private String getFormattedOsInfo()
    {
        final StringBuilder str = new StringBuilder();
        str.append( SystemUtils.OS_NAME ).append( " " ).append( SystemUtils.OS_VERSION ).append( " (" ).append(
            SystemUtils.OS_ARCH ).append( ")" );
        return str.toString();
    }

    private void configureLogBack()
        throws Exception
    {
        final File configFile = new File( this.homeDir.toFile(), "config/logback.xml" );
        if ( !configFile.exists() )
        {
            LOG.info( "Using default logging configuration. Create [{}] file to override default configuration.", configFile );
            return;
        }

        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        final JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext( context );
        context.reset();
        configurator.doConfigure( configFile );

        StatusPrinter.printInCaseOfErrorsOrWarnings( context );
    }
}
