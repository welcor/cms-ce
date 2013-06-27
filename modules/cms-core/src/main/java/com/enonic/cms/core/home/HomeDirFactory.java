/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.home;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("default")
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
