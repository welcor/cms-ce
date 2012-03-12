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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class ElasticNodeFactory
    implements FactoryBean<Node>
{
    private Node node;

    private File storageDir;

    @Override
    public Node getObject()
    {
        return this.node;
    }

    @Override
    public Class<?> getObjectType()
    {
        return Node.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }

    @PostConstruct
    public void start()
    {
        ESLoggerFactory.setDefaultFactory( new Slf4jESLoggerFactory() );

        final Settings settings = createNodeSettings();
        this.node = NodeBuilder.nodeBuilder().client( false ).local( true ).data( true ).settings( settings ).build();
        this.node.start();
    }

    @PreDestroy
    public void stop()
    {
        this.node.close();
    }

    private Settings createNodeSettings()
    {
        final ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder();
        settings.put( "path.logs", new File( this.storageDir, "log" ).getAbsolutePath() );
        settings.put( "path.data", new File( this.storageDir, "data" ).getAbsolutePath() );
        settings.put( "path.config", new File( this.storageDir, "config" ).getAbsolutePath() );
        settings.put( "cluster.name", "enonic-cms-es-cluster" );
        return settings.build();
    }

    @Value("#{config.elasticStorageDir}")
    public void setStorageDir( final File storageDir )
    {
        this.storageDir = storageDir;
    }
}
