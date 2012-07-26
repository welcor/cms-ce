package com.enonic.cms.core.search;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.logging.slf4j.Slf4jESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

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

    private NodeSettingsBuilder nodeSettingsBuilder;

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

        final Settings settings = nodeSettingsBuilder.buildNodeSettings();
        this.node = NodeBuilder.nodeBuilder().client( client ).local( local ).data( data ).settings( settings ).build();
        this.node.start();
    }

    private void setLogger()
    {
        ESLoggerFactory.setDefaultFactory( new Slf4jESLoggerFactory() );
    }

    @Autowired
    public void setNodeSettingsBuilder( final NodeSettingsBuilder nodeSettingsBuilder )
    {
        this.nodeSettingsBuilder = nodeSettingsBuilder;
    }

    @PreDestroy
    public void stop()
    {
        this.node.stop();
        this.node.close();
    }

}
