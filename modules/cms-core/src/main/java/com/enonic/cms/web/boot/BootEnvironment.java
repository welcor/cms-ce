/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.web.boot;

import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public void initialize()
    {
        try
        {
            doInitialize();
        }
        catch ( final Exception e )
        {
            LOG.error( "Error occurred starting system", e );

            if ( e instanceof RuntimeException )
            {
                throw (RuntimeException) e;
            }
            else
            {
                throw new RuntimeException( e );
            }
        }
    }

    private void doInitialize()
        throws Exception
    {
        resolveHomeDir();
        logBanner();
    }

    private void resolveHomeDir()
    {
        final HomeResolver resolver = new HomeResolver();
        resolver.addSystemProperties( System.getenv() );
        resolver.addSystemProperties( System.getProperties() );
        resolver.resolve();
    }

    private void logBanner()
    {
        final StringBuilder str = new StringBuilder( BANNER );
        str.append( "  # " ).append( ProductVersion.getFullTitleAndVersion() ).append( "\n" );
        str.append( "  # " ).append( getFormattedJvmInfo() ).append( "\n" );
        str.append( "  # " ).append( getFormattedOsInfo() ).append( "\n" );
        str.append( "  # Home directory is " ).append( HomeDir.get() ).append( "\n" );

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
}
