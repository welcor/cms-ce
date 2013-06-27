/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.itest.vertical.engine.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.enonic.vertical.engine.handlers.KeyHandler;

@Configuration
@Profile("itest")
public class KeyHandlerBean
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Bean
    public KeyHandler keyHandler()
    {
        KeyHandler keyHandler = new KeyHandler();
        keyHandler.setHibernateTemplate( hibernateTemplate );
        return keyHandler;
    }

}
