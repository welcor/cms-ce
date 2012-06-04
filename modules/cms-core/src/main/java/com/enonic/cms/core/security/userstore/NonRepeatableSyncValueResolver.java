package com.enonic.cms.core.security.userstore;


import java.security.SecureRandom;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.enonic.cms.core.time.TimeService;

class NonRepeatableSyncValueResolver
{
    private TimeService timeService;

    private SecureRandom secureRandom = new SecureRandom();

    NonRepeatableSyncValueResolver( TimeService timeService )
    {
        this.timeService = timeService;
    }

    String resolve( String syncValue )
    {
        StringBuilder s = new StringBuilder();
        s.append( syncValue );
        s.append( "_nonRepeatable_" );

        DateTimeFormatter formatter = DateTimeFormat.forPattern( "YYYY-MM-dd HH:mm:ss" );
        s.append( timeService.getNowAsDateTime().toString( formatter ) );
        s.append( "_" );
        s.append( secureRandom.nextInt() );
        return s.toString();
    }
}
