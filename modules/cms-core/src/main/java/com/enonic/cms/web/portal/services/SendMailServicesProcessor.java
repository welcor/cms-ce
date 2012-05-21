package com.enonic.cms.web.portal.services;

import org.springframework.stereotype.Component;

@Component
public final class SendMailServicesProcessor
    extends SendMailServicesBase
{
    public SendMailServicesProcessor()
    {
        super( "sendmail" );
    }
}
