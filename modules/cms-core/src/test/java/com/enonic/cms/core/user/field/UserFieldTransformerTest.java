/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.user.field;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.enonic.esl.containers.ExtendedMap;

import com.enonic.cms.api.client.model.user.Gender;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class UserFieldTransformerTest
{
    @Test
    public void testToUserFields()
    {
        ExtendedMap form = new ExtendedMap( true );
        form.put( "first_name", "Ola" );
        form.put( "last_name", "Normann" );
        form.put( "gender", "male" );
        form.put( "birthday", "2009-11-10" );

        UserFieldTransformer transformer = new UserFieldTransformer();
        UserFields fields = transformer.toUserFields( form );
        assertEquals( 4, fields.getSize() );

        assertNotNull( fields.getField( UserFieldType.FIRST_NAME ) );
        assertEquals( "Ola", fields.getField( UserFieldType.FIRST_NAME ).getValue() );

        assertNotNull( fields.getField( UserFieldType.LAST_NAME ) );
        assertEquals( "Normann", fields.getField( UserFieldType.LAST_NAME ).getValue() );

        assertNotNull( fields.getField( UserFieldType.GENDER ) );
        assertEquals( Gender.MALE, fields.getField( UserFieldType.GENDER ).getValue() );

        assertNotNull( fields.getField( UserFieldType.BIRTHDAY ) );

        Object birthday = fields.getField( UserFieldType.BIRTHDAY ).getValue();
        assertEquals( birthday.getClass(), Date.class );

        Date date = Date.class.cast( birthday );
        DateFormat dateFormat = DateFormat.getDateInstance( DateFormat.MEDIUM, Locale.ROOT );

        // the year param is year minus 1900. the month is between 0-11.
        assertEquals( dateFormat.format( new Date( 109, 10, 10 ) ), dateFormat.format( date ) );
    }

    @Test
    public void toUserFields_given_empty_strings_then_non_textual_fields_becomes_null()
    {
        // setup
        UserFieldTransformer transformer = new UserFieldTransformer();
        ExtendedMap submittedValues = new ExtendedMap( true );
        submittedValues.put( UserFieldType.BIRTHDAY.getName(), "" );
        submittedValues.put( UserFieldType.GENDER.getName(), "" );
        submittedValues.put( UserFieldType.HTML_EMAIL.getName(), "" );
        submittedValues.put( UserFieldType.LOCALE.getName(), "" );
        submittedValues.put( UserFieldType.TIME_ZONE.getName(), "" );

        // exercise
        UserFields userFields = transformer.toUserFields( submittedValues );

        // verify
        assertEquals( null, userFields.getField( UserFieldType.BIRTHDAY ).getValue() );
        assertEquals( null, userFields.getField( UserFieldType.GENDER ).getValue() );
        assertEquals( null, userFields.getField( UserFieldType.HTML_EMAIL ).getValue() );
        assertEquals( null, userFields.getField( UserFieldType.LOCALE ).getValue() );
        assertEquals( null, userFields.getField( UserFieldType.TIME_ZONE ).getValue() );
    }

    @Test
    public void toUserFields_given_empty_strings_then_textual_fields_becomes_empty()
    {
        // setup
        UserFieldTransformer transformer = new UserFieldTransformer();
        ExtendedMap submittedValues = new ExtendedMap( true );
        submittedValues.put( UserFieldType.FIRST_NAME.getName(), "" );
        submittedValues.put( UserFieldType.LAST_NAME.getName(), "" );
        submittedValues.put( UserFieldType.NICK_NAME.getName(), "" );
        submittedValues.put( UserFieldType.COUNTRY.getName(), "" );
        submittedValues.put( UserFieldType.GLOBAL_POSITION.getName(), "" );
        submittedValues.put( UserFieldType.HOME_PAGE.getName(), "" );
        submittedValues.put( UserFieldType.INITIALS.getName(), "" );
        submittedValues.put( UserFieldType.MEMBER_ID.getName(), "" );
        submittedValues.put( UserFieldType.MIDDLE_NAME.getName(), "" );
        submittedValues.put( UserFieldType.ORGANIZATION.getName(), "" );

        // exercise
        UserFields userFields = transformer.toUserFields( submittedValues );

        // verify
        assertEquals( "", userFields.getField( UserFieldType.FIRST_NAME ).getValue() );
        assertEquals( "", userFields.getField( UserFieldType.LAST_NAME ).getValue() );
        assertEquals( "", userFields.getField( UserFieldType.NICK_NAME ).getValue() );
        assertEquals( "", userFields.getField( UserFieldType.COUNTRY ).getValue() );
        assertEquals( "", userFields.getField( UserFieldType.GLOBAL_POSITION ).getValue() );
        assertEquals( "", userFields.getField( UserFieldType.HOME_PAGE ).getValue() );
        assertEquals( "", userFields.getField( UserFieldType.INITIALS ).getValue() );
        assertEquals( "", userFields.getField( UserFieldType.MEMBER_ID ).getValue() );
        assertEquals( "", userFields.getField( UserFieldType.MIDDLE_NAME ).getValue() );
        assertEquals( "", userFields.getField( UserFieldType.ORGANIZATION ).getValue() );
    }

}
