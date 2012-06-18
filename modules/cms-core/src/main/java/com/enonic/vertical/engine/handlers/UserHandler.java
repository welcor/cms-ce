/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.VerticalEngineLogger;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserNameXmlCreator;
import com.enonic.cms.core.security.user.UserSpecification;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.store.dao.UserDao;

public final class UserHandler
    extends BaseHandler
{
    @Autowired
    private UserDao userDao;

    public String generateUID( String fName, String sName, UserStoreKey userStoreKey )
    {
        final int uidLength = 8;

        if ( fName == null || sName == null )
        {
            return null;
        }

        if ( fName.length() == 0 || sName.length() == 0 )
        {
            return null;
        }

        fName = NameGenerator.simplifyString( fName );
        sName = NameGenerator.simplifyString( sName );

        String suffix = "";
        int counter = 0;
        boolean done = false;
        String newUID = null;

        while ( !done )
        {

            int iterations = sName.length() + fName.length() - 1;
            if ( ( iterations + 1 ) > ( uidLength - suffix.length() ) )
            {
                iterations -= iterations + 1 - ( uidLength - suffix.length() );
            }

            for ( int i = 1; i <= iterations; i++ )
            {
                int letters_from_sname = Math.min( Math.min( i, sName.length() ), uidLength - 1 - suffix.length() );
                int letters_from_fname =
                    Math.min( fName.length(), uidLength - letters_from_sname - suffix.length() ) - Math.max( 0, i - letters_from_sname );

                newUID = fName.substring( 0, letters_from_fname ) + sName.substring( 0, letters_from_sname ) + suffix;

                if ( !existsUser( newUID, userStoreKey ) )
                {
                    done = true;
                    break;
                }
                else
                {
                    newUID = null;
                }
            }
            counter++;
            suffix = Integer.toString( counter );

            // Not very likely to happen, exit to prevent infinite loop
            if ( counter == 100 )
            {
                newUID = null;
                break;
            }
        }

        if ( newUID == null )
        {
            VerticalEngineLogger.warn( "Unable to generate UID for user ({0}, {1}).", new Object[]{fName, sName} );
        }

        return newUID;
    }

    private boolean existsUser( String uid, UserStoreKey userStoreKey )
    {
        UserSpecification userSpec = new UserSpecification();
        userSpec.setName( uid );
        userSpec.setUserStoreKey( userStoreKey );
        userSpec.setDeletedStateNotDeleted();
        final List<UserEntity> users = userDao.findBySpecification( userSpec );
        return users != null && users.size() > 0;
    }


    public Document getUsersByGroupKeys( String[] groupKeys )
    {
        if ( groupKeys.length == 0 )
        {
            return XMLTool.createDocument( "usernames" );
        }
        List<User> userKeys = new ArrayList<User>();
        for ( String groupKey : groupKeys )
        {
            UserSpecification userSpec = new UserSpecification();
            userSpec.setUserGroupKey( new GroupKey( groupKey ) );
            userSpec.setDeletedStateNotDeleted();
            UserEntity user = userDao.findSingleBySpecification( userSpec );
            if ( user != null )
            {
                userKeys.add( user );
            }
        }

        UserNameXmlCreator userNameXmlCreator = new UserNameXmlCreator();
        return XMLDocumentFactory.create( userNameXmlCreator.createUserNamesDocument( userKeys ) ).getAsDOMDocument();
    }

    public User getAnonymousUser()
    {
        return userDao.findBuiltInAnonymousUser();
    }


}
