/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.util.PropertiesUtil;

import com.enonic.cms.core.boot.ConfigProperties;

/**
 * This class is deprecated. Use spring injection instead with @Value("${..}").
 */
@Deprecated
public final class VerticalProperties
{
    private static VerticalProperties verticalProperties;

    private Properties properties;

    public static VerticalProperties getVerticalProperties()
    {
        return verticalProperties;
    }

    public VerticalProperties()
    {
        verticalProperties = this;
    }

    @Autowired
    public void setProperties( ConfigProperties properties )
    {
        this.properties = properties;
    }

    public String getProperty( final String key )
    {
        final String systemProperty = StringUtils.trimToNull( System.getProperty( key ) );
        if ( systemProperty != null )
        {
            return systemProperty;
        }
        return StringUtils.trimToNull( properties.getProperty( key ) );
    }

    public String getProperty( final String key, final String defaultValue )
    {
        final String systemProperty = StringUtils.trimToNull( System.getProperty( key ) );
        if ( systemProperty != null )
        {
            return systemProperty;
        }
        return StringUtils.trimToNull( properties.getProperty( key, defaultValue ) );
    }

    public Properties getSubSet( final String base )
    {
        return PropertiesUtil.getSubSet( properties, base );
    }

    public String getSMTPHost()
    {
        return getMailSmtpHost();
    }

    public int getAutologinTimeout()
    {
        return Integer.parseInt( getProperty( "com.enonic.vertical.presentation.autologinTimeout", "30" ) );
    }

    /**
     * Get property <code>com.enonic.vertical.adminweb.XSL_PREFIX</code>.
     *
     * @return The parameter converted to a Java long.
     */
    public long getMultiPartRequestMaxSize()
    {
        return Long.parseLong( getProperty( "cms.admin.binaryUploadMaxSize" ) );
    }

    /**
     * Get property <code>com.enonic.vertical.xml.XMLTool.storeXHTML</code>.
     *
     * @return The parameter converted to a Java boolean.
     */
    public boolean isStoreXHTMLOn()
    {
        return !"false".equals( getProperty( "cms.xml.storeXHTML", "true" ) );
    }

    public String getAdminPassword()
    {
        return getProperty( "cms.admin.password" );
    }

    public String getAdminEmail()
    {
        return getProperty( "cms.admin.email" );
    }

    public String getAdminNewPasswordMailSubject()
    {
        return getProperty( "cms.admin.newPasswordMailSubject" );
    }

    public String getAdminNewPasswordMailBody()
    {
        return getProperty( "cms.admin.newPasswordMailBody" );
    }

    public String getMailSmtpHost()
    {
        return getProperty( "cms.mail.smtpHost" );
    }

    public String getDataSourceUserAgent()
    {
        return getProperty( "cms.enonic.vertical.presentation.dataSource.getUrl.userAgent" );
    }

    public String getDatasourceDefaultResultRootElement()
    {
        return getProperty( "cms.datasource.defaultResultRootElement" );
    }
}
