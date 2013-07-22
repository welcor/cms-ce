/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.store.dao;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.api.client.model.user.Address;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.api.plugin.userstore.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreConfigParser;
import com.enonic.cms.api.plugin.userstore.UserFields;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

import static org.junit.Assert.*;

public class UserEntityDaoTest
    extends AbstractSpringTest
{
    @Autowired
    private UserDao userDao;

    @Autowired
    private UserStoreDao userStoreDao;

    @Autowired
    private GroupDao groupDao;


    @Test
    public void testFindBySpecWithUserGroupKey()
    {
        // Setup of prerequisites
        final UserEntity user = new UserEntity();
        user.setDeleted( false );
        user.setEmail( "email@example.com" );
        user.setDisplayName( "DisplayName" );
        user.setName( "uid" );
        user.setSyncValue( "syncValue" );
        user.setType( UserType.NORMAL );
        user.setTimestamp( new DateTime() );

        userDao.storeNew( user );

        final GroupEntity userGroup = new GroupEntity();
        userGroup.setDeleted( 0 );
        userGroup.setDescription( null );
        userGroup.setName( "userGroup" + user.getKey() );
        userGroup.setSyncValue( user.getSync() );
        userGroup.setUser( user );
        userGroup.setType( GroupType.USER );
        userGroup.setRestricted( 1 );
        groupDao.storeNew( userGroup );
        user.setUserGroup( userGroup );

        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        // Excercise
        final UserSpecification userSpecification = new UserSpecification();
        userSpecification.setUserGroupKey( userGroup.getGroupKey() );
        final UserEntity storedUser = userDao.findSingleBySpecification( userSpecification );

        // Verify
        assertEquals( user, storedUser );
    }

    @Test
    public void testStoreUserWithUserInfo()
    {
        // Setup of prerequisites
        final UserEntity user = createUser( "uid", "displayName", "email@example.com", "syncValue" );

        UserFields userFields = new UserFields();
        userFields.setBirthday( new DateMidnight( 1976, 4, 19 ).toDate() );
        userFields.setInitials( "JVS" );
        userFields.setCountry( "Norway" );
        userFields.setNickName( "Skriu" );
        user.setUserFields( userFields );
        userDao.storeNew( user );

        final UserKey userKey = user.getKey();

        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        // Excercise
        final UserEntity storedUser = userDao.findByKey( userKey );

        // Verify
        assertEquals( user, storedUser );
        userFields = storedUser.getUserFields();
        assertEquals( "JVS", userFields.getInitials() );
        assertEquals( "Norway", userFields.getCountry() );
        assertEquals( "Skriu", userFields.getNickName() );
        assertEquals( new DateMidnight( 1976, 4, 19 ).toDate(), userFields.getBirthday() );
    }

    @Test
    public void testStoreUserWithUserInfo_Address()
    {
        // Setup of prerequisites
        final UserEntity user = createUser( "uid", "displayName", "email@example.com", "syncValue" );

        UserFields userFields = new UserFields();
        userFields.setInitials( "JVS" );
        userFields.setCountry( "Norway" );

        final Address homeAddress = new Address();
        homeAddress.setLabel( "My Home address" );
        homeAddress.setStreet( "Street 9" );
        homeAddress.setPostalCode( "0123" );
        homeAddress.setPostalAddress( "MyCity" );
        homeAddress.setRegion( "MyRegion" );
        homeAddress.setCountry( "MyCountry" );

        final Address workAddress = new Address();
        workAddress.setLabel( "My Work address" );
        workAddress.setStreet( "Street 113" );
        workAddress.setPostalCode( "3210" );
        workAddress.setPostalAddress( "WorkCity" );
        workAddress.setRegion( "Work Region" );
        workAddress.setCountry( "MyCountry" );

        userFields.setAddresses( homeAddress, workAddress );

        user.setUserFields( userFields );
        userDao.storeNew( user );

        final UserKey userKey = user.getKey();

        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        // Excercise
        final UserEntity storedUser = userDao.findByKey( userKey );

        // Verify
        assertEquals( user, storedUser );

        userFields = storedUser.getUserFields();
        assertEquals( "JVS", userFields.getInitials() );
        assertEquals( "Norway", userFields.getCountry() );

        final Address storedHomeAddress = userFields.getAddresses().get( 0 );
        assertNotNull( storedHomeAddress );
        assertEquals( storedHomeAddress, homeAddress );
        assertEquals( "My Home address", storedHomeAddress.getLabel() );
        assertEquals( "Street 9", storedHomeAddress.getStreet() );
        assertEquals( "0123", storedHomeAddress.getPostalCode() );
        assertEquals( "MyCity", storedHomeAddress.getPostalAddress() );
        assertEquals( "MyRegion", storedHomeAddress.getRegion() );
        assertEquals( "MyCountry", storedHomeAddress.getCountry() );

        final Address storedWorkAddress = userFields.getAddresses().get( 1 );
        assertNotNull( storedWorkAddress );
        assertEquals( storedWorkAddress, workAddress );
        assertEquals( "My Work address", storedWorkAddress.getLabel() );
        assertEquals( "Street 113", storedWorkAddress.getStreet() );
        assertEquals( "3210", storedWorkAddress.getPostalCode() );
        assertEquals( "WorkCity", storedWorkAddress.getPostalAddress() );
        assertEquals( "Work Region", storedWorkAddress.getRegion() );
        assertEquals( "MyCountry", storedWorkAddress.getCountry() );
    }

    @Test
    public void testUpdateUserInfo()
    {
        // Setup of prerequisites
        final UserEntity newUser = createUser( "uid", "displayName", "email@example.com", "syncValue" );

        // Excercise
        newUser.setUserFields( new UserFields().setCountry( "Norway" ) );
        userDao.storeNew( newUser );
        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        final UserKey userKey = newUser.getKey();

        // Verify
        assertEquals( newUser, userDao.findByKey( userKey ) );
        assertEquals( "Norway", userDao.findByKey( userKey ).getUserFields().getCountry() );

        // Excercise
        UserEntity updateUser = userDao.findByKey( userKey );
        updateUser.setUserFields( new UserFields().setCountry( "South Africa" ) );
        userDao.updateExisting( updateUser );
        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        // Verify
        assertEquals( newUser, userDao.findByKey( userKey ) );
        assertEquals( "South Africa", userDao.findByKey( userKey ).getUserFields().getCountry() );
    }

    @Test
    public void testUpdateUserInfo_Address()
    {
        // TODO: Implement
    }


    @Test
    public void testDeleteUserInfo()
    {
        // Setup of prerequisites
        final UserEntity user = createUser( "uid", "displayName", "email@example.com", "syncValue" );

        UserFields userFields = new UserFields();
        userFields.setInitials( "JVS" );
        userFields.setCountry( "Norway" );
        user.setUserFields( userFields );
        userDao.storeNew( user );

        final UserKey userKey = user.getKey();

        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        // Excercise
        UserEntity storedUser = userDao.findByKey( userKey );

        // Verify
        assertEquals( user, storedUser );

        UserFields storedUserFields = storedUser.getUserFields();
        assertEquals( "JVS", storedUserFields.getInitials() );
        assertEquals( "Norway", storedUserFields.getCountry() );

        // Update
        userFields = new UserFields();
        userFields.setInitials( null );
        userFields.setCountry( "South Africa" );
        storedUser.setUserFields( userFields );

        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        // Excercise
        storedUser = userDao.findByKey( userKey );

        // Verify
        assertEquals( user, storedUser );

        storedUserFields = storedUser.getUserFields();
        Assert.assertNull( storedUserFields.getInitials() );
        assertEquals( "South Africa", storedUserFields.getCountry() );
    }

    @Test
    public void testDeleteUserInfo_Address()
    {

        // Setup of prerequisites
        final UserEntity user = createUser( "uid", "displayName", "email@example.com", "syncValue" );

        UserFields userFields = new UserFields();
        Address address = new Address();
        address.setLabel( "Home" );
        address.setStreet( "Street 1" );
        userFields.setAddresses( address );
        user.setUserFields( userFields );
        userDao.storeNew( user );

        final UserKey userKey = user.getKey();

        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        // Excercise
        UserEntity storedUser = userDao.findByKey( userKey );

        // Verify
        assertEquals( user, storedUser );

        UserFields storedUserFields = storedUser.getUserFields();
        assertEquals( 1, storedUserFields.getAddresses().size() );
        assertEquals( "Home", storedUserFields.getAddresses().get( 0 ).getLabel() );

        // Update
        userFields = new UserFields();
        storedUser.setUserFields( userFields );

        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        // Excercise
        storedUser = userDao.findByKey( userKey );

        // Verify
        assertEquals( user, storedUser );

        storedUserFields = storedUser.getUserFields();
        assertEquals( 0, storedUserFields.getAddresses().size() );
    }


    @Test
    public void findByEmailAndUserStore()
    {
        final UserStoreEntity userStore = createUserStore();

        userStoreDao.storeNew( userStore );

        final UserStoreKey userStoreKey = userStore.getKey();

        userStoreDao.getHibernateTemplate().flush();
        userStoreDao.getHibernateTemplate().clear();

        final UserEntity user = createUser( "uid", "displayName", "email@example.com", "syncValue" );
        user.setUserStore( userStore );
        userDao.storeNew( user );

        userDao.getHibernateTemplate().flush();
        userDao.getHibernateTemplate().clear();

        // Excercise
        final UserSpecification userSpecification = new UserSpecification();
        userSpecification.setUserStoreKey( userStoreKey );
        userSpecification.setEmail( "email@example.com" );
        final UserEntity storedUser = userDao.findSingleBySpecification( userSpecification );

        // Verify
        assertNotNull( "storedUser cannot be null", storedUser );
        assertEquals( user, storedUser );
    }

    private UserStoreEntity createUserStore()
    {
        final UserStoreEntity userStore = new UserStoreEntity();

        userStore.setDefaultStore( false );
        userStore.setDeleted( false );
        userStore.setName( "TestName" );
        userStore.setConnectorName( "TestConnectorName" );

        final String configAsString = "<config><user-fields><first-name required=\"true\"/></user-fields></config>";
        final XMLDocument configXmlDoc = XMLDocumentFactory.create( configAsString );
        final UserStoreConfig config = UserStoreConfigParser.parse( configXmlDoc.getAsJDOMDocument().getRootElement() );
        userStore.setConfig( config );
        return userStore;
    }

    private UserEntity createUser( String uid, String displayName, String email, String syncValue )
    {
        final UserEntity user = new UserEntity();
        user.setDeleted( false );
        user.setEmail( email );
        user.setDisplayName( displayName );
        user.setName( uid );
        user.setSyncValue( syncValue );
        user.setType( UserType.NORMAL );
        user.setTimestamp( new DateTime() );
        return user;
    }
}
