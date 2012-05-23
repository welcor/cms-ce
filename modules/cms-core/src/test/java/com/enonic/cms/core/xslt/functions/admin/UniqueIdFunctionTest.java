package com.enonic.cms.core.xslt.functions.admin;

import org.junit.Test;
import static org.junit.Assert.*;

public class UniqueIdFunctionTest
    extends AbstractAdminFunctionTest
{
    @Override
    protected AbstractAdminFunction newFunction()
    {
        return new UniqueIdFunction()
        {
            @Override
            protected String generateId()
            {
                return "123";
            }
        };
    }

    @Test
    public void testFunction()
        throws Exception
    {
        processTemplate( "uniqueId" );
    }

    @Test
    public void testUniqueness()
    {
        final UniqueIdFunction function = new UniqueIdFunction();
        final String id1 = function.generateId();
        final String id2 = function.generateId();

        assertNotNull(id1);
        assertNotNull(id2);
        assertFalse(id1.equals( id2 ));
    }
}
