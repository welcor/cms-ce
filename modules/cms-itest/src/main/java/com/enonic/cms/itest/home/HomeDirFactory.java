package com.enonic.cms.itest.home;

import java.io.File;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.home.HomeDir;

@Component
@Profile("itest")
public final class HomeDirFactory
    implements FactoryBean<HomeDir>
{

    public HomeDir getObject()
    {
        File homeDir = new File( "./src/test/homeDir" );
        if ( !homeDir.exists() )
        {
            homeDir = new File( "./modules/cms-itest/src/test/homeDir" );
        }

        return new HomeDir( homeDir );
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
