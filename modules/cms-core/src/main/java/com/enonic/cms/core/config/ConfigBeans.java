package com.enonic.cms.core.config;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import com.enonic.cms.core.boot.BootEnvironment;

@Configuration
public class ConfigBeans
{
    @Bean
    public GlobalConfig config(final ConfigProperties props)
    {
        return new GlobalConfigImpl(props);
    }

    @Bean
    public ConfigProperties configProperties(final ConfigurableEnvironment env)
    {
        final File homeDir = BootEnvironment.getHomeDir(env);
        final ConfigPropertiesImpl props = new ConfigPropertiesImpl(env.getConversionService());
        props.putAll(new ConfigLoader(homeDir, env).load());
        return props;
    }
}
