/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.user.field;

import org.junit.Test;

import junit.framework.Assert;

import com.enonic.cms.api.plugin.userstore.UserField;
import com.enonic.cms.api.plugin.userstore.UserFieldType;

public class UserFieldTest
{
    @Test
    public void testLegalType()
    {
        UserField field = new UserField( UserFieldType.FIRST_NAME );
        Assert.assertNull( field.getValue() );
        field.setValue( "Ola" );
        Assert.assertEquals( "Ola", field.getValue() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalType()
    {
        UserField field = new UserField( UserFieldType.FIRST_NAME );
        field.setValue( Boolean.TRUE );
    }
}
