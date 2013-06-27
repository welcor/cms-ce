/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.user.field;


import org.junit.Test;

import com.enonic.cms.api.client.model.user.Address;
import com.enonic.cms.api.client.model.user.Gender;
import com.enonic.cms.core.AbstractEqualsTest;

public class UserFieldsEqualsTest
    extends AbstractEqualsTest
{
    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        UserFields userFieldsA1 = new UserFields();
        userFieldsA1.setFirstName( "First name" );
        userFieldsA1.setLastName( "Last name" );
        Address address0 = new Address();
        address0.setLabel( "Home" );
        address0.setStreet( "Street" );
        address0.setPostalCode( "0001" );
        address0.setPostalAddress( "My City" );
        address0.setRegion( "My Region" );
        address0.setCountry( "My Country" );
        address0.setIsoRegion( "My ISO Region" );
        address0.setIsoCountry( "My ISO Country" );
        userFieldsA1.setAddresses( address0 );
        return userFieldsA1;
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        // only missing last name
        UserFields userFieldsB1 = new UserFields();
        userFieldsB1.setFirstName( "First name" );
        Address addressA1 = new Address();
        addressA1.setLabel( "Home" );
        addressA1.setStreet( "Street" );
        addressA1.setPostalCode( "0001" );
        addressA1.setPostalAddress( "My City" );
        addressA1.setRegion( "My Region" );
        addressA1.setCountry( "My Country" );
        addressA1.setIsoRegion( "My ISO Region" );
        addressA1.setIsoCountry( "My ISO Country" );
        userFieldsB1.setAddresses( addressA1 );

        // only uequal first name
        UserFields userFieldsC1 = new UserFields();
        userFieldsC1.setFirstName( "Other first name" );
        userFieldsC1.setLastName( "Last name" );
        Address addressA2 = new Address();
        addressA2.setLabel( "Home" );
        addressA2.setStreet( "Street" );
        addressA2.setPostalCode( "0001" );
        addressA2.setPostalAddress( "My City" );
        addressA2.setRegion( "My Region" );
        addressA2.setCountry( "My Country" );
        addressA2.setIsoRegion( "My ISO Region" );
        addressA2.setIsoCountry( "My ISO Country" );
        userFieldsC1.setAddresses( addressA2 );

        // only unequal postal code
        UserFields userFieldsD1 = new UserFields();
        userFieldsD1.setFirstName( "First name" );
        userFieldsD1.setLastName( "Last name" );
        Address addressB1 = new Address();
        addressB1.setLabel( "Home" );
        addressB1.setStreet( "Street" );
        addressB1.setPostalCode( "0002" );
        addressB1.setPostalAddress( "My City" );
        addressB1.setRegion( "My Region" );
        addressB1.setCountry( "My Country" );
        addressB1.setIsoRegion( "My ISO Region" );
        addressB1.setIsoCountry( "My ISO Country" );
        userFieldsD1.setAddresses( addressB1 );

        // only having extra field
        UserFields userFieldsE1 = new UserFields();
        userFieldsE1.setFirstName( "First name" );
        userFieldsE1.setLastName( "Last name" );
        userFieldsE1.setGender( Gender.FEMALE );
        Address addressA3 = new Address();
        addressA3.setLabel( "Home" );
        addressA3.setStreet( "Street" );
        addressA3.setPostalCode( "0001" );
        addressA3.setPostalAddress( "My City" );
        addressA3.setRegion( "My Region" );
        addressA3.setCountry( "My Country" );
        addressA3.setIsoRegion( "My ISO Region" );
        addressA3.setIsoCountry( "My ISO Country" );
        userFieldsB1.setAddresses( addressA3 );

        return new Object[]{userFieldsB1, userFieldsC1, userFieldsD1};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        UserFields userFieldsA2 = new UserFields();
        userFieldsA2.setFirstName( "First name" );
        userFieldsA2.setLastName( "Last name" );
        Address addressA1 = new Address();
        addressA1.setLabel( "Home" );
        addressA1.setStreet( "Street" );
        addressA1.setPostalCode( "0001" );
        addressA1.setPostalAddress( "My City" );
        addressA1.setRegion( "My Region" );
        addressA1.setCountry( "My Country" );
        addressA1.setIsoRegion( "My ISO Region" );
        addressA1.setIsoCountry( "My ISO Country" );
        userFieldsA2.setAddresses( addressA1 );
        return userFieldsA2;
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        UserFields userFieldsA3 = new UserFields();
        userFieldsA3.setFirstName( "First name" );
        userFieldsA3.setLastName( "Last name" );
        Address addressA1 = new Address();
        addressA1.setLabel( "Home" );
        addressA1.setStreet( "Street" );
        addressA1.setPostalCode( "0001" );
        addressA1.setPostalAddress( "My City" );
        addressA1.setRegion( "My Region" );
        addressA1.setCountry( "My Country" );
        addressA1.setIsoRegion( "My ISO Region" );
        addressA1.setIsoCountry( "My ISO Country" );
        userFieldsA3.setAddresses( addressA1 );
        return userFieldsA3;
    }

}

