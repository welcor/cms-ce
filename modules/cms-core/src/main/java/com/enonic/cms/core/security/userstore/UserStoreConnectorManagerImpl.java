/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.enonic.cms.api.plugin.userstore.RemoteUserStore;
import com.enonic.cms.core.security.userstore.connector.UserStoreConnector;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfigLoader;
import com.enonic.cms.core.security.userstore.connector.local.LocalUserStoreConnector;
import com.enonic.cms.core.security.userstore.connector.remote.RemoteUserStoreConnector;
import com.enonic.cms.core.security.userstore.connector.remote.RemoteUserStoreManager;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.store.dao.UserStoreDao;

@Component
public final class UserStoreConnectorManagerImpl
    implements UserStoreConnectorManager, InitializingBean
{
    @Autowired
    private UserStoreConnectorConfigLoader userStoreConnectorConfigLoader;

    @Autowired
    private RemoteUserStoreManager remoteUserStoreFactory;

    @Autowired
    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private GroupStorerFactory groupStorerFactory;

    @Autowired
    private UserStorerFactory userStorerFactory;

    @Autowired
    private TimeService timeService;

    @Autowired
    private UserStoreDao userStoreDao;

    private final Map<UserStoreKey, RemoteUserStoreConnector> remoteUSConnectorMap =
        Collections.synchronizedMap( new HashMap<UserStoreKey, RemoteUserStoreConnector>() );

    private final Map<UserStoreKey, LocalUserStoreConnector> localUSConnectorMap =
        Collections.synchronizedMap( new HashMap<UserStoreKey, LocalUserStoreConnector>() );


    public Map<String, UserStoreConnectorConfig> getUserStoreConnectorConfigs()
    {
        return userStoreConnectorConfigLoader.getAllUserStoreConnectorConfigs();
    }

    public UserStoreConnectorConfig getUserStoreConnectorConfig( final String configName )
    {
        return userStoreConnectorConfigLoader.getUserStoreConnectorConfig( configName );
    }

    public UserStoreConnectorConfig getUserStoreConnectorConfig( final UserStoreKey userStoreKey )
    {
        UserStoreEntity userStore = userStoreDao.findByKey( userStoreKey );
        if ( userStore.isLocal() )
        {
            throw new IllegalArgumentException( "Local user stores does not have a connector config" );
        }
        return userStoreConnectorConfigLoader.getUserStoreConnectorConfig( userStore.getConnectorName() );
    }

    @Override
    public void invalidateCachedConfig( UserStoreKey userStoreKey )
    {
        remoteUSConnectorMap.remove( userStoreKey );
        localUSConnectorMap.remove( userStoreKey );
    }

    public UserStoreConnector getUserStoreConnector( final UserStoreKey userStoreKey )
    {
        return doGetUSConnectorBykey( userStoreKey );
    }

    public RemoteUserStoreConnector getRemoteUserStoreConnector( final UserStoreKey userStoreKey )
    {
        UserStoreConnector comm = doGetUSConnectorBykey( userStoreKey );
        if ( comm instanceof RemoteUserStoreConnector )
        {
            return (RemoteUserStoreConnector) comm;
        }
        //throw new IllegalArgumentException( "Given userStoreKey does not reference a remote user store: " + userStoreKey );
        return null;
    }

    private UserStoreConnector doGetUSConnector( final UserStoreEntity userStore )
    {
        if ( userStore.isLocal() )
        {
            return doGetLocalUserStoreConnector( userStore );
        }
        return doGetRemoteUserStoreConnector( userStore );
    }

    private synchronized RemoteUserStoreConnector doGetRemoteUserStoreConnector( final UserStoreEntity userStore )
    {
        RemoteUserStoreConnector connector = remoteUSConnectorMap.get( userStore.getKey() );
        if ( connector != null )
        {
            boolean connectorNameSame = connector.getConnectorName().equals( userStore.getConnectorName() );
            boolean userStoreNameSame = connector.getUserStoreName().equals( userStore.getName() );
            if ( connectorNameSame && userStoreNameSame )
            {
                return connector;
            }
            //Userstore name or connector name has changed - remove obsolete connector for userstore
            remoteUSConnectorMap.remove( userStore.getKey() );
        }

        connector = createRemoteUserStoreConnector( userStore );

        remoteUSConnectorMap.put( userStore.getKey(), connector );

        return connector;
    }

    private synchronized LocalUserStoreConnector doGetLocalUserStoreConnector( final UserStoreEntity userStore )
    {
        LocalUserStoreConnector connector = localUSConnectorMap.get( userStore.getKey() );
        if ( connector != null )
        {
            boolean connectorNameSame = connector.getConnectorName().equals( userStore.getConnectorName() );
            boolean userStoreNameSame = connector.getUserStoreName().equals( userStore.getName() );
            if ( connectorNameSame && userStoreNameSame )
            {
                return connector;
            }
            //Userstore name or connector name has changed - remove obsolete connector for userstore
            localUSConnectorMap.remove( userStore.getKey() );
        }

        connector = createLocalUserStoreUserStoreConnector( userStore );

        localUSConnectorMap.put( userStore.getKey(), connector );

        return connector;
    }

    private RemoteUserStoreConnector createRemoteUserStoreConnector( final UserStoreEntity userStore )
    {
        final UserStoreConnectorConfig connectorConfig =
            userStoreConnectorConfigLoader.getUserStoreConnectorConfig( userStore.getConnectorName() );
        final RemoteUserStore remoteUserStorePlugin =
            remoteUserStoreFactory.create( connectorConfig.getPluginType(), connectorConfig.getPluginProperties() );

        final RemoteUserStoreConnector connector =
            new RemoteUserStoreConnector( userStore.getKey(), userStore.getName(), userStore.getConnectorName() );
        connector.setTimeService( timeService );
        connector.setRemoteUserStorePlugin( remoteUserStorePlugin );
        connector.setUserDao( userDao );
        connector.setGroupDao( groupDao );
        connector.setGroupStorerFactory( groupStorerFactory );
        connector.setUserStorerFactory( userStorerFactory );
        connector.setUserStoreDao( userStoreDao );
        connector.setConnectorConfig( connectorConfig );
        connector.setUserStoreConfig( userStore.getConfig() );
        return connector;
    }

    private LocalUserStoreConnector createLocalUserStoreUserStoreConnector( final UserStoreEntity userStore )
    {
        final LocalUserStoreConnector connector = new LocalUserStoreConnector( userStore.getKey(), userStore.getName() );
        connector.setUserDao( userDao );
        connector.setGroupDao( groupDao );
        connector.setGroupStorerFactory( groupStorerFactory );
        connector.setUserStorerFactory( userStorerFactory );
        connector.setUserStoreDao( userStoreDao );
        return connector;
    }

    private UserStoreConnector doGetUSConnectorBykey( final UserStoreKey userStoreKey )
    {
        Assert.notNull( userStoreKey, "userStoreKey cannot be null" );

        UserStoreEntity userStore = this.userStoreDao.findByKey( userStoreKey );
        if ( userStore != null )
        {
            return doGetUSConnector( userStore );
        }

        throw new IllegalArgumentException( "User store [" + userStoreKey + "] not found" );
    }

    @Override
    public void afterPropertiesSet()
        throws Exception
    {
        this.remoteUserStoreFactory.setRefreshCallback( new Runnable()
        {
            @Override
            public void run()
            {
                remoteUSConnectorMap.clear();
            }
        } );
    }
}
