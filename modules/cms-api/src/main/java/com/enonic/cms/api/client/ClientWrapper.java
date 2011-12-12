/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client;

import com.enonic.cms.api.client.model.*;
import com.enonic.cms.api.client.model.preference.Preference;
import org.jdom.Document;

import java.util.List;

/**
 * This class wraps another client. It can be extended to do useful proxy functionality.
 */
public abstract class ClientWrapper
    implements Client
{

    /**
     * Client delegate.
     */
    private final Client delegate;

    public ClientWrapper( Client delegate )
    {
        this.delegate = delegate;
    }

    public String getUser()
        throws ClientException
    {
        return this.delegate.getUserName();
    }

    public String getUserName()
        throws ClientException
    {
        return this.delegate.getUserName();
    }

    /**
     * @inheritDoc
     * @deprecated Use getRunAsUserName() instead
     */
    public String getRunAsUser()
        throws ClientException
    {
        return this.delegate.getRunAsUserName();
    }

    /**
     * Return the run as user name.
     */
    public String getRunAsUserName()
        throws ClientException
    {
        return this.delegate.getRunAsUserName();
    }

    /**
     * Return the user context.
     */
    public Document getUserContext()
        throws ClientException
    {
        return this.delegate.getUserContext();
    }

    /**
     * Return the user context.
     */
    public Document getRunAsUserContext()
        throws ClientException
    {
        return this.delegate.getRunAsUserContext();
    }

    /**
     * Login a user.
     */
    public String login( String user, String password )
        throws ClientException
    {
        return this.delegate.login( user, password );
    }

    /**
     * Impersonate the user. This means that the runAsUser is different than the logged in user.
     */
    public String impersonate( String user )
        throws ClientException
    {
        return this.delegate.impersonate( user );
    }

    /**
     * Logout the current user.
     */
    public String logout()
        throws ClientException
    {
        return this.delegate.logout();
    }

    public String logout( boolean invalidateSession )
        throws ClientException
    {
        return this.delegate.logout( invalidateSession );
    }

    public Document getUser( GetUserParams params )
        throws ClientException
    {

        return this.delegate.getUser( params );
    }

    public Document getUsers( GetUsersParams params )
        throws ClientException
    {

        return this.delegate.getUsers( params );
    }

    public Document getGroup( GetGroupParams params )
        throws ClientException
    {

        return this.delegate.getGroup( params );
    }

    public Document getGroups( GetGroupsParams params )
        throws ClientException
    {

        return this.delegate.getGroups( params );
    }

    public Document joinGroups( JoinGroupsParams params )
        throws ClientException
    {

        return this.delegate.joinGroups( params );
    }

    public Document leaveGroups( LeaveGroupsParams params )
        throws ClientException
    {
        return delegate.leaveGroups( params );
    }

    public Document createGroup( CreateGroupParams params )
        throws ClientException
    {

        return this.delegate.createGroup( params );
    }

    public void deleteGroup( DeleteGroupParams params )
    {

        this.delegate.deleteGroup( params );
    }

    public int createContent( CreateContentParams params )
    {
        return this.delegate.createContent( params );
    }

    public String createUser( CreateUserParams params )
        throws ClientException
    {
        return delegate.createUser( params );
    }

    public void deleteUser( DeleteUserParams params )
        throws ClientException
    {
        delegate.deleteUser( params );
    }

    public int createCategory( CreateCategoryParams params )
    {
        return delegate.createCategory( params );
    }

    public int updateContent( UpdateContentParams params )
    {

        return this.delegate.updateContent( params );
    }

    public void deleteContent( DeleteContentParams params )
    {

        this.delegate.deleteContent( params );
    }

    public void deleteCategory( DeleteCategoryParams params )
    {

        this.delegate.deleteCategory( params );
    }

    public int createFileContent( CreateFileContentParams params )
    {

        return this.delegate.createFileContent( params );
    }

    public int updateFileContent( UpdateFileContentParams params )
    {

        return this.delegate.updateFileContent( params );
    }

    public int createImageContent( CreateImageContentParams params )
        throws ClientException
    {
        return this.delegate.createImageContent( params );
    }

    public void assignContent( AssignContentParams params )
        throws ClientException
    {
        this.delegate.assignContent( params );
    }

    public void unassignContent( UnassignContentParams params )
        throws ClientException
    {
        this.delegate.unassignContent( params );
    }

    public void snapshotContent( SnapshotContentParams params )
        throws ClientException
    {
        this.delegate.snapshotContent( params );
    }

    /**
     * @inheritDoc
     */
    public Document getContent( GetContentParams params )
        throws ClientException
    {
        return this.delegate.getContent( params );
    }

    public Document getContentVersions( GetContentVersionsParams params )
        throws ClientException
    {
        return this.delegate.getContentVersions( params );
    }

    /**
     * Get categories.
     */
    public Document getCategories( GetCategoriesParams params )
        throws ClientException
    {
        return this.delegate.getCategories( params );
    }

    public Document getContentByQuery( GetContentByQueryParams params )
        throws ClientException
    {
        return this.delegate.getContentByQuery( params );
    }

    public Document getContentByCategory( GetContentByCategoryParams params )
        throws ClientException
    {
        return this.delegate.getContentByCategory( params );
    }

    public Document getRandomContentByCategory( GetRandomContentByCategoryParams params )
        throws ClientException
    {
        return this.delegate.getRandomContentByCategory( params );
    }

    public Document getContentBySection( GetContentBySectionParams params )
        throws ClientException
    {
        return this.delegate.getContentBySection( params );
    }

    public Document getRandomContentBySection( GetRandomContentBySectionParams params )
        throws ClientException
    {
        return this.delegate.getRandomContentBySection( params );
    }

    /**
     * Get menu.
     */
    public Document getMenu( GetMenuParams params )
        throws ClientException
    {
        return this.delegate.getMenu( params );
    }

    /**
     * Get menu branch.
     */
    public Document getMenuBranch( GetMenuBranchParams params )
        throws ClientException
    {
        return this.delegate.getMenuBranch( params );
    }

    /**
     * Get menu data.
     */
    public Document getMenuData( GetMenuDataParams params )
        throws ClientException
    {
        return this.delegate.getMenuData( params );
    }

    /**
     * Get menu item.
     */
    public Document getMenuItem( GetMenuItemParams params )
        throws ClientException
    {
        return this.delegate.getMenuItem( params );
    }

    /**
     * Get sub menu.
     */
    public Document getSubMenu( GetSubMenuParams params )
        throws ClientException
    {
        return this.delegate.getSubMenu( params );
    }

    public Document getRelatedContent( GetRelatedContentsParams params )
        throws ClientException
    {
        return this.delegate.getRelatedContent( params );
    }

    /**
     * Render content.
     */
    public Document renderContent( RenderContentParams params )
        throws ClientException
    {
        return this.delegate.renderContent( params );
    }

    /**
     * Render page.
     */
    public Document renderPage( RenderPageParams params )
        throws ClientException
    {
        return this.delegate.renderPage( params );
    }

    /**
     * Get binary data for a binary key.
     */
    public Document getBinary( GetBinaryParams params )
        throws ClientException
    {
        return this.delegate.getBinary( params );
    }

    /**
     * Get binary data for a content
     */
    public Document getContentBinary( GetContentBinaryParams params )
        throws ClientException
    {
        return this.delegate.getContentBinary( params );
    }

    /**
     * Get a resource
     */
    public Document getResource( GetResourceParams params )
        throws ClientException
    {
        return this.delegate.getResource( params );
    }

    /**
     * Import contents.
     */
    public Document importContents( ImportContentsParams params )
        throws ClientException
    {
        return this.delegate.importContents( params );
    }

    /**
     * @inheritDoc
     */
    public Preference getPreference( GetPreferenceParams params )
        throws ClientException
    {
        return this.delegate.getPreference( params );
    }

    /**
     * @inheritDoc
     */
    public List<Preference> getPreferences()
        throws ClientException
    {
        return this.delegate.getPreferences();
    }

    /**
     * @inheritDoc
     */
    public void setPreference( SetPreferenceParams params )
        throws ClientException
    {
        this.delegate.setPreference( params );
    }

    /**
     * @inheritDoc
     */
    public void deletePreference( DeletePreferenceParams params )
        throws ClientException
    {
        this.delegate.deletePreference( params );
    }

    public void clearPageCacheForSite( Integer siteKey )
        throws ClientException
    {
        this.delegate.clearPageCacheForSite( siteKey );
    }

    public void clearPageCacheForPage( Integer siteKey, Integer[] menuItemKeys )
    {
        this.delegate.clearPageCacheForPage( siteKey, menuItemKeys );
    }

    public void clearPageCacheForContent( Integer[] contentKeys )
        throws ClientException
    {
        this.delegate.clearPageCacheForContent( contentKeys );
    }

    public Document getContentTypeConfigXML( GetContentTypeConfigXMLParams params )
        throws ClientException
    {
        return this.delegate.getContentTypeConfigXML( params );
    }
}