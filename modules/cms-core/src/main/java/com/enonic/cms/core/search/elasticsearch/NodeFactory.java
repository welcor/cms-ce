package com.enonic.cms.core.search.elasticsearch;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


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
        final Settings settings = ImmutableSettings.settingsBuilder()
            .put( "path.logs", new File( this.storageDir, "log" ).getAbsolutePath() )
            .put( "path.data", new File( this.storageDir, "data" ).getAbsolutePath() )
            .build();

        this.node = NodeBuilder.nodeBuilder().client( false ).local( true ).data( true ).settings( settings ).build();

        this.node.start();
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
