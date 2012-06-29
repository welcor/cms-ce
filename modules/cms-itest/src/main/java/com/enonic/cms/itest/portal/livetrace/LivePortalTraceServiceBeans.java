package com.enonic.cms.itest.portal.livetrace;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.core.portal.livetrace.LivePortalTraceServiceImpl;

@Configuration
public class LivePortalTraceServiceBeans
{
    public
    @Bean
    LivePortalTraceService livePortalTraceService()
    {
        LivePortalTraceServiceImpl service = new LivePortalTraceServiceImpl();
        service.setEnabled( "false" );
        return service;
    }

}
