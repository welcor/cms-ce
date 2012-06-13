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
        final ConfigLoader loader = new ConfigLoader( homeDir.toFile() );
        return loader.load();
    }
}
