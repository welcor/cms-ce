package com.enonic.vertical.engine.handlers;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("default")
public class KeyHandlerBean
{
    @Autowired
    private SessionFactory sessionFactory;

    @Bean
    public KeyHandler keyHandler()
    {
        KeyHandler keyHandler = new KeyHandler();
        keyHandler.setSessionFactory( sessionFactory );
        return keyHandler;
    }

}
