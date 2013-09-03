/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;


import junit.framework.TestCase;

public class ContentIndexQueryTest
    extends TestCase
{

    public void testFullTextValidation()
    {
        // one char illegal
        ContentIndexQuery query1 = new ContentIndexQuery( "fulltext CONTAINS \"A\"" );
        assertNotNull( query1 );

        // two chars illegal
        ContentIndexQuery query2 = new ContentIndexQuery( "fulltext CONTAINS \"AB\"" );
        assertNotNull( query2 );

        // three chars are legal
        ContentIndexQuery query3 = new ContentIndexQuery( "fulltext CONTAINS \"ABC\"" );
        assertNotNull( query3 );

    }
}
