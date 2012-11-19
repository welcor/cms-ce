package com.enonic.cms.core.search.builder.contentindexdata;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.cms.core.search.builder.ContentIndexDateValueResolver;

import static org.junit.Assert.*;

public class ContentIndexDateValueResolverTest
{
    ContentIndexDateValueResolver resolver = new ContentIndexDateValueResolver();

    @Test
    public void testNull()
    {
        assertNull( ContentIndexDateValueResolver.resolveDateValue( null ) );

    }

    @Test
    public void testValidDateFormats()
    {
        Date date = ContentIndexDateValueResolver.resolveDateValue( "2010-08-01" );
        assertNotNull( date );
        assertEquals( new DateTime( 2010, 8, 01, 00, 00 ).toDate(), date );

        date = ContentIndexDateValueResolver.resolveDateValue( "2010-08-01t10:00" );
        assertNotNull( date );
        assertEquals( new DateTime( 2010, 8, 01, 00, 00 ).toDate(), date );

        date = ContentIndexDateValueResolver.resolveDateValue( "2010-08-01 10:00:00" );
        assertNotNull( date );
        assertEquals( new DateTime( 2010, 8, 01, 10, 00 ).toDate(), date );
    }

    @Test
    public void testInvalidDateFormats()
    {
        Date date = ContentIndexDateValueResolver.resolveDateValue( "2010/08/01" );
        assertNull( date );

        date = ContentIndexDateValueResolver.resolveDateValue( "01/08/1975 10:00" );
        assertNull( date );

        date = ContentIndexDateValueResolver.resolveDateValue( "2010.08.01 10:00:00" );
        assertNull( date );


    }


}
