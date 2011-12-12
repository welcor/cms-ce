/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.mock;

import com.enonic.cms.api.client.Client;
import com.enonic.cms.api.client.ClientException;
import com.enonic.cms.api.client.model.*;
import com.enonic.cms.api.client.model.preference.Preference;
import org.jdom.Document;

import java.util.List;

/**
 * Mock implementation of the Client interface.
 */
public class MockClient
    implements Client
{

    public String getUser()
        throws ClientException
    {
        return null;
    }

    public String getUserName()
        throws ClientException
    {
        return null;
    }

    public String getRunAsUser()
        throws ClientException
    {
        return null;
    }

    public String getRunAsUserName()
        throws ClientException
    {
        return null;
    }

    public Document getUserContext()
        throws ClientException
    {
        return null;
    }

    public Document getRunAsUserContext()
        throws ClientException
    {
        return null;
    }

    public String login( String user, String password )
        throws ClientException
    {
        return null;
    }

    public String impersonate( String user )
        throws ClientException
    {
        return null;
    }

    public String logout()
        throws ClientException
    {
        return null;
    }

    public String logout( boolean invalidateSession )
        throws ClientException
    {
        return null;
    }

    public Document getUser( GetUserParams params )
        throws ClientException
    {
        return null;
    }

    public Document getUsers( GetUsersParams params )
        throws ClientException
    {
        return null;
    }

    public Document getGroup( GetGroupParams params )
        throws ClientException
    {
        return null;
    }

    public Document getGroups( GetGroupsParams params )
        throws ClientException
    {
        return null;
    }

    public Document joinGroups( JoinGroupsParams params )
        throws ClientException
    {
        return null;
    }

    public Document leaveGroups( LeaveGroupsParams params )
        throws ClientException
    {
        return null;
    }

    public Document createGroup( CreateGroupParams params )
        throws ClientException
    {
        return null;
    }

    public void deleteGroup( DeleteGroupParams params )
    {

    }

    public int createContent( CreateContentParams params )
    {
        return 0;
    }

    public String createUser( CreateUserParams params )
    {
        return null;
    }

    public void deleteUser( DeleteUserParams params )
        throws ClientException
    {

    }

    public int createCategory( CreateCategoryParams params )
    {
        return 0;
    }

    public int updateContent( UpdateContentParams params )
    {
        return 0;
    }

    public void deleteContent( DeleteContentParams params )
    {

    }

    public int createFileContent( CreateFileContentParams params )
    {
        return 0;
    }

    public int updateFileContent( UpdateFileContentParams params )
    {
        return 0;
    }

    public int createImageContent( CreateImageContentParams params )
    {
        return 0;
    }

    public void assignContent( AssignContentParams params )
        throws ClientException
    {
    }

    public void unassignContent( UnassignContentParams params )
        throws ClientException
    {
    }

    public void snapshotContent( SnapshotContentParams params )
        throws ClientException
    {
    }

    public Document getContent( GetContentParams params )
        throws ClientException
    {
        return null;
    }

    public Document getContentVersions( GetContentVersionsParams params )
        throws ClientException
    {
        return null;
    }

    public Document getCategories( GetCategoriesParams params )
        throws ClientException
    {
        return null;
    }

    public Document getContentByQuery( GetContentByQueryParams params )
        throws ClientException
    {
        return null;
    }

    public Document getContentByCategory( GetContentByCategoryParams params )
        throws ClientException
    {
        return null;
    }

    public Document getRandomContentByCategory( GetRandomContentByCategoryParams params )
        throws ClientException
    {
        return null;
    }

    public Document getContentBySection( GetContentBySectionParams params )
        throws ClientException
    {
        return null;
    }

    public Document getRandomContentBySection( GetRandomContentBySectionParams params )
        throws ClientException
    {
        return null;
    }

    public Document getMenu( GetMenuParams params )
        throws ClientException
    {
        return null;
    }

    public Document getMenuBranch( GetMenuBranchParams params )
        throws ClientException
    {
        return null;
    }

    public Document getMenuData( GetMenuDataParams params )
        throws ClientException
    {
        return null;
    }

    public Document getMenuItem( GetMenuItemParams params )
        throws ClientException
    {
        return null;
    }

    public Document getSubMenu( GetSubMenuParams params )
        throws ClientException
    {
        return null;
    }

    public Document getRelatedContent( GetRelatedContentsParams params )
        throws ClientException
    {
        return null;
    }

    public Document renderContent( RenderContentParams params )
        throws ClientException
    {
        return null;
    }

    public Document renderPage( RenderPageParams params )
        throws ClientException
    {
        return null;
    }

    public Document getBinary( GetBinaryParams params )
        throws ClientException
    {
        return null;
    }

    public Document getContentBinary( GetContentBinaryParams params )
        throws ClientException
    {
        return null;
    }

    public Document getResource( GetResourceParams params )
        throws ClientException
    {
        return null;
    }

    public Document importContents( ImportContentsParams params )
        throws ClientException
    {
        return null;
    }

    public Preference getPreference( GetPreferenceParams params )
        throws ClientException
    {
        return null;
    }

    public List<Preference> getPreferences()
        throws ClientException
    {
        return null;
    }

    public void setPreference( SetPreferenceParams params )
        throws ClientException
    {

    }

    public void deletePreference( DeletePreferenceParams params )
        throws ClientException
    {

    }

    public void clearPageCacheForSite( Integer siteKey )
        throws ClientException
    {

    }

    public void clearPageCacheForPage( Integer siteKey, Integer[] menuItemKeys )
    {

    }

    public void clearPageCacheForContent( Integer[] contentKeys )
        throws ClientException
    {

    }

    public Document getContentTypeConfigXML( GetContentTypeConfigXMLParams params )
        throws ClientException
    {
        return null;
    }

    public void deleteCategory( DeleteCategoryParams params )
        throws ClientException
    {

    }
}
