package com.enonic.cms.core.security.user;


import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

import com.enonic.cms.api.client.model.user.Address;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.user.field.UserField;
import com.enonic.cms.core.user.field.UserFieldType;
import com.enonic.cms.core.user.field.UserFields;

import static org.junit.Assert.*;

public class UserEntityTest
{
    @Test
    public void testIsInRemoteUserStore()
        throws Exception
    {
        UserEntity user = new UserEntity();

        user.setType( UserType.ANONYMOUS );
        assertFalse( user.isInRemoteUserStore() );

        user.setType( UserType.ADMINISTRATOR );
        assertFalse( user.isInRemoteUserStore() );

        user.setType( UserType.NORMAL );
        assertFalse( user.isInRemoteUserStore() );

        UserStoreEntity localUserStore = new UserStoreEntity();
        user.setUserStore( localUserStore );
        assertFalse( user.isInRemoteUserStore() );

        UserStoreEntity remoteUserStore = new UserStoreEntity();
        remoteUserStore.setConnectorName( "myRemoteConnector" );
        user.setUserStore( remoteUserStore );
        assertTrue( user.isInRemoteUserStore() );
    }

    @Test
    public void setUserFields_given_empty()
    {
        UserEntity user = new UserEntity();

        // exercise
        boolean changed = user.setUserFields( new UserFields( true ) );

        // verify
        assertFalse( changed );
        assertEquals( 0, user.getFieldMap().size() );
    }

    @Test
    public void setUserFields_given_one_user_field()
    {
        UserEntity user = new UserEntity();

        // exercise
        boolean changed = user.setUserFields( new UserFields().setCountry( "Norway" ) );

        // verify
        assertTrue( changed );
        assertEquals( 1, user.getFieldMap().size() );
        assertEquals( "Norway", user.getFieldMap().get( UserFieldType.COUNTRY.getName() ) );
    }

    @Test
    public void setUserFields_given_addresses_user_field()
    {
        UserEntity user = new UserEntity();

        Address address1 = new Address();
        address1.setLabel( "Home" );
        address1.setCountry( "Norway" );
        Address address2 = new Address();
        address2.setLabel( "Second home" );
        address2.setCountry( "South Africa" );

        // exercise
        boolean changed = user.setUserFields( new UserFields().setAddresses( address1, address2 ) );

        // verify
        assertTrue( changed );
        assertEquals( 2, user.getUserFields().getSize() );
        assertEquals( 4, user.getFieldMap().size() );
    }

    @Test
    public void setUserFields_returns_true_when_given_is_same_as_existing()
    {
        UserEntity user = new UserEntity();
        Map<String, String> fieldMap = Maps.newHashMap();
        fieldMap.put( UserFieldType.FIRST_NAME.getName(), "First name" );
        fieldMap.put( UserFieldType.ADDRESS.getName() + "[0].label", "Home" );
        fieldMap.put( UserFieldType.ADDRESS.getName() + "[0].country", "Norway" );
        fieldMap.put( UserFieldType.ADDRESS.getName() + "[1].label", "Second home" );
        fieldMap.put( UserFieldType.ADDRESS.getName() + "[1].country", "South Africa" );
        user.setFieldMap( fieldMap );

        // exercise
        Address address1 = new Address();
        address1.setLabel( "Home" );
        address1.setCountry( "Norway" );
        Address address2 = new Address();
        address2.setLabel( "Second home" );
        address2.setCountry( "South Africa" );
        UserFields userFields = new UserFields().setFirstName( "First name" ).setAddresses( address1, address2 );
        boolean changed = user.setUserFields( userFields );

        // verify
        assertFalse( changed );
    }

    @Test
    public void setUserFields_returns_false_when_given_contains_fields_with_null_which_are_already_missing()
    {
        UserEntity user = new UserEntity();
        Map<String, String> fieldMap = Maps.newHashMap();
        fieldMap.put( UserFieldType.FIRST_NAME.getName(), "First name" );
        user.setFieldMap( fieldMap );

        // exercise
        UserFields userFields = new UserFields();
        userFields.setFirstName( "First name" );
        userFields.add( new UserField( UserFieldType.LAST_NAME, null ) );
        boolean changed = user.setUserFields( userFields );

        // verify
        assertFalse( changed );
    }
}
