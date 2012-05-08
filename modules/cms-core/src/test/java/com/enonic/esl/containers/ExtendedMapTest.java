package com.enonic.esl.containers;

import org.junit.Assert;
import org.junit.Test;

public class ExtendedMapTest
{
    @Test
    public void testGetInt()
        throws Exception
    {
        ExtendedMap map = new ExtendedMap();

        map.put( "int", "       1        " );
        Assert.assertEquals( 1, map.getInt( "int" ) );

        map.put( "int2", "    \t \r \n   2  \t \r \n      " );
        Assert.assertEquals( 2, map.getInt( "int2" ) );

    }
}
