/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.user.field;

import org.joda.time.DateMidnight;
import org.junit.Test;

import com.enonic.cms.api.plugin.userstore.UserFieldType;

import static org.junit.Assert.*;

public class UserFieldHelperTest
{
    @Test
    public void fromString_given_birthday_on_form_yyyyMMdd()
    {
        assertEquals( new DateMidnight( 2005, 12, 5 ).toDate(), new UserFieldHelper().fromString( UserFieldType.BIRTHDAY, "20051205" ) );
    }
}
