/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.security.userstore;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.security.userstore.connector.UserStoreConnector;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.cms.core.security.userstore.connector.remote.RemoteUserStoreConnector;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.store.dao.CategoryAccessDao;
import com.enonic.cms.store.dao.ContentAccessDao;
import com.enonic.cms.store.dao.DefaultSiteAccessDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.MenuItemAccessDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

@Component
public class GroupStorerFactory
{
    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserStoreDao userStoreDao;

    @Autowired
    private MenuItemAccessDao menuItemAccessDao;

    @Autowired
    private CategoryAccessDao categoryAccessDao;

    @Autowired
    private ContentAccessDao contentAccessDao;

    @Autowired
    private DefaultSiteAccessDao defaultSiteAccessDao;

    @Autowired
    private UserStoreConnectorManager userStoreConnectorManager;

    @Autowired
    private TimeService timeService;

    public GroupStorer createForGlobalGroups()
    {
        GroupStorer groupStorer = new GroupStorer();
        groupStorer.setGroupDao( groupDao );
        groupStorer.setUserDao( userDao );
        groupStorer.setMenuItemAccessDao( menuItemAccessDao );
        groupStorer.setCategoryAccessDao( categoryAccessDao );
        groupStorer.setContentAccessDao( contentAccessDao );
        groupStorer.setDefaultSiteAccessDao( defaultSiteAccessDao );
        groupStorer.setTimeService( timeService );
        groupStorer.setResurrectDeletedGroups( false );
        groupStorer.setGroupsStoredLocally( true );

        return groupStorer;
    }

    public GroupStorer create( final UserStoreKey userStoreKey )
    {
        Preconditions.checkArgument( userStoreKey != null, "userStoreKey not given: " + userStoreKey );
        UserStoreEntity userStore = userStoreDao.findByKey( userStoreKey );
        Preconditions.checkArgument( userStore != null, "user store not found: " + userStoreKey );

        GroupStorer groupStorer = new GroupStorer();
        groupStorer.setGroupDao( groupDao );
        groupStorer.setUserDao( userDao );
        groupStorer.setMenuItemAccessDao( menuItemAccessDao );
        groupStorer.setCategoryAccessDao( categoryAccessDao );
        groupStorer.setContentAccessDao( contentAccessDao );
        groupStorer.setDefaultSiteAccessDao( defaultSiteAccessDao );
        groupStorer.setTimeService( timeService );
        groupStorer.setUserStore( userStore );
        UserStoreConnector userStoreConnector = userStoreConnectorManager.getUserStoreConnector( userStoreKey );
        if ( userStoreConnector instanceof RemoteUserStoreConnector )
        {
            UserStoreConnectorConfig userStoreConnectorConfig = userStoreConnectorManager.getUserStoreConnectorConfig( userStoreKey );
            groupStorer.setResurrectDeletedGroups( userStoreConnectorConfig.resurrectDeletedGroups() );
            groupStorer.setGroupsStoredLocally( userStoreConnectorConfig.groupsStoredLocal() );
        }
        else
        {
            groupStorer.setResurrectDeletedGroups( false );
            groupStorer.setGroupsStoredLocally( true );
        }

        return groupStorer;
    }
}
