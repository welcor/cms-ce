package com.enonic.cms.core.search.elasticsearch;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/21/11
 * Time: 2:09 PM
 */
public class ClientFactory
    implements FactoryBean<Client>
{

    private Node node;

    private Client client;

    @Autowired
    public void setNode( final Node node )
    {
        this.node = node;
    }


    public Client getObject()
    {
        return this.client;
    }

    public Class<?> getObjectType()
    {
        return Client.class;
    }

    public boolean isSingleton()
    {
        return true;
    }

    @PostConstruct
    public void init()
        throws Exception
    {
        this.client = this.node.client();

        CreateIndexRequest indexRequest = new CreateIndexRequest( "cms" );

        CreateIndexResponse response = client.admin().indices().create( indexRequest ).actionGet();
        System.out.println("Response: " + response.toString());
    }
}
