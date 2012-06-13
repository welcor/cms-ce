package com.enonic.cms.core.home;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public final class HomeDirFactory
    implements FactoryBean<HomeDir>
{
    public HomeDir getObject()
    {
        return HomeDir.get();
    }

    public Class<?> getObjectType()
    {
        return HomeDir.class;
    }

    public boolean isSingleton()
    {
        return true;
    }
}
