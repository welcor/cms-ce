package com.enonic.cms.core.security.userstore;


import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.cms.core.time.MockTimeService;
import com.enonic.cms.core.time.TimeService;

import static org.junit.Assert.*;

public class NonRepeatableSyncValueResolverTest
{
    @Test
    public void resolve()
    {
        DateTime timeNow = new DateTime( 2012, 12, 12, 12, 13, 14, 15 );
        TimeService timeService = new MockTimeService( timeNow );
        NonRepeatableSyncValueResolver resolver = new NonRepeatableSyncValueResolver( timeService );

        String nonRepeatableSyncValue = resolver.resolve( "MySyncKey" );
        assertTrue( nonRepeatableSyncValue.startsWith( "MySyncKey_nonRepeatable_2012-12-12 12:13:14_" ) );
    }
}
