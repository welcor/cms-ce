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
import com.enonic.cms.store.dao.RememberedLoginDao;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

@Component
public class UserStorerFactory
{
    @Autowired
    private UserStoreDao userStoreDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TimeService timeService;

    @Autowired
    private MenuItemAccessDao menuItemAccessDao;

    @Autowired
    private CategoryAccessDao categoryAccessDao;

    @Autowired
    private ContentAccessDao contentAccessDao;

    @Autowired
    private DefaultSiteAccessDao defaultSiteAccessDao;

    @Autowired
    private RememberedLoginDao rememberedLoginDao;

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private UserStoreConnectorManager userStoreConnectorManager;

    public UserStorer create( final UserStoreKey userStoreKey )
    {
        Preconditions.checkArgument( userStoreKey != null, "userStoreKey not given: " + userStoreKey );
        final UserStoreEntity userStore = userStoreDao.findByKey( userStoreKey );
        Preconditions.checkArgument( userStore != null, "user store not found: " + userStoreKey );

        final UserStorer userStorer = new UserStorer();
        userStorer.setGroupDao( groupDao );
        userStorer.setUserDao( userDao );
        userStorer.setTimeService( timeService );
        userStorer.setMenuItemAccessDao( menuItemAccessDao );
        userStorer.setCategoryAccessDao( categoryAccessDao );
        userStorer.setContentAccessDao( contentAccessDao );
        userStorer.setDefaultSiteAccessDao( defaultSiteAccessDao );
        userStorer.setRememberedLoginDao( rememberedLoginDao );
        userStorer.setSiteDao( siteDao );
        userStorer.setUserStore( userStore );

        UserStoreConnector userStoreConnector = userStoreConnectorManager.getUserStoreConnector( userStoreKey );
        if ( userStoreConnector instanceof RemoteUserStoreConnector )
        {
            UserStoreConnectorConfig userStoreConnectorConfig = userStoreConnectorManager.getUserStoreConnectorConfig( userStoreKey );
            userStorer.setResurrectDeletedUsers( userStoreConnectorConfig.resurrectDeletedUsers() );
        }
        else
        {
            userStorer.setResurrectDeletedUsers( false );
        }

        return userStorer;
    }
}
