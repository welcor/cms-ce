package com.enonic.cms.core.boot;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.stereotype.Component;

@Component
public final class ConfigPropertiesFactory
    implements FactoryBean<ConfigProperties>
{
    public ConfigProperties getObject()
    {
        return ConfigPropertiesAccessor.get();
    }

    public Class<?> getObjectType()
    {
        return ConfigProperties.class;
    }

    public boolean isSingleton()
    {
        return true;
    }
}
