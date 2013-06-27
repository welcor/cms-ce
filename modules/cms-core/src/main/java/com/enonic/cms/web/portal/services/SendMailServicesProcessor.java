/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

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
