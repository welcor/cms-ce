package com.enonic.cms.web.portal.userservices;

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
