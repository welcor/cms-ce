package com.enonic.cms.core.boot;

import java.io.File;
import java.util.Properties;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;

public final class BootEnvironment
{
    public static void configure(final ConfigurableEnvironment env)
    {
        final File homeDir = new HomeResolver(env).resolve();
        configure(env, homeDir);
    }

    public static void configure(final ConfigurableEnvironment env, final File homeDir)
    {
        final MutablePropertySources sources = env.getPropertySources();

        final ConfigProperties props = new ConfigLoader(homeDir, env).load();
        sources.addFirst( new ConfigPropertySource( props ) );

        ConfigPropertiesAccessor.set( props );
    }

    public static File getHomeDir(final Environment env)
    {
        return env.getRequiredProperty("cms.home", File.class);
    }
}
