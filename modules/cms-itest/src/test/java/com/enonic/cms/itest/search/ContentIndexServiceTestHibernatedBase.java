package com.enonic.cms.itest.search;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.access.ContentAccessEntity;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/2/12
 * Time: 3:09 PM
 */
@TransactionConfiguration(defaultRollback = true)
@DirtiesContext
@Transactional
public class ContentIndexServiceTestHibernatedBase
    extends ContentIndexServiceTestBase
{

    protected DomainFactory factory;

    @Autowired
    protected DomainFixture fixture;

    @Test
    public void dummy()
    {

    }

    protected ContentAccessEntity createContentAccess( final String userName, boolean read, boolean update )
    {
        return createContentAccess( fixture.findUserByName( userName ).getUserGroup(), read, update );
    }

    protected ContentAccessEntity createContentAccess( GroupEntity group, boolean read, boolean update )
    {
        ContentAccessEntity contentAccess = new ContentAccessEntity();
        contentAccess.setGroup( group );
        contentAccess.setReadAccess( read );
        contentAccess.setUpdateAccess( update );
        return contentAccess;
    }

    protected CreateContentCommand createCreateContentCommand( String categoryName, String creatorUid, ContentStatus contentStatus )
    {
        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCategory( fixture.findCategoryByName( categoryName ) );
        createContentCommand.setCreator( fixture.findUserByName( creatorUid ).getKey() );
        createContentCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        createContentCommand.setStatus( contentStatus );
        createContentCommand.setPriority( 0 );
        createContentCommand.setContentName( "name_" + categoryName + "_" + contentStatus );

        ContentTypeConfig contentTypeConfig = fixture.findContentTypeByName( "Person" ).getContentTypeConfig();
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Initial" ) );
        createContentCommand.setContentData( contentData );
        return createContentCommand;
    }


    protected GroupEntity createAndSaveGroup( String groupId, String userstoreName, GroupType groupType )
    {
        GroupEntity group = factory.createGroupInUserstore( groupId, groupType, userstoreName );

        fixture.save( group );

        return group;
    }

    protected void createAndSaveNormalUser( String uid, String userstoreName )
    {
        GroupEntity userGroup = factory.createGroupInUserstore( uid + "_group", GroupType.USERSTORE_GROUP, userstoreName );

        fixture.save( userGroup );

        UserEntity user = factory.createUser( uid, uid, UserType.NORMAL, userstoreName, userGroup );

        fixture.save( user );

        fixture.flushAndClearHibernateSesssion();
    }

    protected void createAndStoreCategory( String categoryName )
    {
        createAndStoreCategory( categoryName, false );
    }

    protected void createAndStoreCategory( String categoryName, boolean autoApprove )
    {
        fixture.save(
            factory.createCategory( categoryName, null, "Person", "UnitForPerson", User.ANONYMOUS_UID, User.ANONYMOUS_UID, autoApprove ) );

        fixture.flushAndClearHibernateSesssion();
    }


    protected void createAndSaveContentAccess( ContentKey contentKey, String userUid, String accesses )
    {
        final UserEntity user = fixture.findUserByName( userUid );
        fixture.save( factory.createContentAccess( contentKey, user, accesses ) );
        fixture.flushAndClearHibernateSesssion();
    }

    protected void createAndSaveCategoryAccess( String categoryName, String userUid, String accesses )
    {
        final UserEntity user = fixture.findUserByName( userUid );

        //final CategoryEntity category = fixture.findCategoryByName( categoryName );
        //category.addAccessRight( factory.createCategoryAccess( categoryName, user, accesses ) );

        fixture.save( factory.createCategoryAccess( categoryName, user, accesses ) );
        fixture.flushAndClearHibernateSesssion();
    }

    protected void createAndSaveCategoryAccessForGroup( String categoryName, String groupName, String accesses )
    {
        final GroupEntity group = fixture.findGroupByName( groupName );
        fixture.save( factory.createCategoryAccess( categoryName, group, accesses ) );
        fixture.flushAndClearHibernateSesssion();
    }

}
