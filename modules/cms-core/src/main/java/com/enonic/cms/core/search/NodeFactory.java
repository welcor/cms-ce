package com.enonic.cms.core.search;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/21/11
 * Time: 2:13 PM
 */
public class NodeFactory
    implements FactoryBean<Node>
{
    private Node node;

    private File storageDir;

    private final static boolean local = true;

    private final static boolean client = false;

    private final static boolean data = true;

    public Node getObject()
        throws Exception
    {
        return this.node;
    }

    public Class<?> getObjectType()
    {
        return Node.class;
    }

    public boolean isSingleton()
    {
        return true;
    }

    @PostConstruct
    public void start()
    {
        setLogger();

        final Settings settings = createNodeSettings();

        this.node = NodeBuilder.nodeBuilder().client( client ).local( local ).data( data ).settings( settings ).build();

        this.node.start();
    }

    private void setLogger()
    {
        ESLoggerFactory.setDefaultFactory( new Slf4jESLoggerFactory() );
    }

    private Settings createNodeSettings()
    {
        return ImmutableSettings.settingsBuilder()
            .put( "path.logs", new File( this.storageDir, "log" ).getAbsolutePath() )
            .put( "path.data", new File( this.storageDir, "data" ).getAbsolutePath() )
                //.put(  "cluster.name", "cms-index-cluster" )
            .build();

    }

    @PreDestroy
    public void stop()
    {
        this.node.close();
    }

    public void setStorageDir( File storageDir )
    {
        this.storageDir = storageDir;
    }
}
