package com.enonic.cms.core.elastic;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.search.NodeSettingsBuilder;

@Component
public final class ElasticNodeFactory
    implements FactoryBean<Node>
{
    private Node node;

    private File storageDir;

    @Autowired
    private NodeSettingsBuilder nodeSettingsBuilder;

    public Node getObject()
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
        ESLoggerFactory.setDefaultFactory( new Slf4jESLoggerFactory() );

        final Settings settings = nodeSettingsBuilder.createNodeSettings( this.storageDir );

        this.node = NodeBuilder.nodeBuilder().client( false ).local( true ).data( true ).settings( settings ).build();
        this.node.start();
    }

    @PreDestroy
    public void stop()
    {
        this.node.close();
    }

    @Value("${cms.search.index.dir}")
    public void setStorageDir( final File storageDir )
    {
        this.storageDir = storageDir;
    }
}
