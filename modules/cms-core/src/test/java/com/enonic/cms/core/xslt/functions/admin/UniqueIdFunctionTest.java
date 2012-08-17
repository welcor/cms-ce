package com.enonic.cms.core.xslt.functions.admin;

import org.junit.Test;
import org.mockito.Mockito;

public class UniqueIdFunctionTest
    extends AbstractAdminFunctionTest
{
    @Override
    protected AdminXsltFunctionLibrary newFunctionLibrary()
    {
        final UniqueIdGenerator generator = Mockito.mock( UniqueIdGenerator.class );
        Mockito.when( generator.generateUniqueId() ).thenReturn( "123" );
        return new AdminXsltFunctionLibrary( generator );
    }

    @Test
    public void testFunction()
        throws Exception
    {
        processTemplate( "uniqueId" );
    }
}
