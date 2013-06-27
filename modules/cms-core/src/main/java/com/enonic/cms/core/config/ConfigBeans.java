/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.enonic.cms.core.home.HomeDir;

@Configuration
public class ConfigBeans
{
    @Bean
    public ConfigProperties config( final HomeDir homeDir )
    {
        final ConfigLoader loader = new ConfigLoader( homeDir );
        return loader.load();
    }
}
