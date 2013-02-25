package com.enonic.cms.itest.search;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.enonic.cms.core.search.ElasticSearchIndexServiceImpl;

@Configuration
@Profile("itest")
public class ElasticsearchServiceConfiguration
{

    @Bean
    public ElasticSearchIndexServiceImpl elasticSearchIndexService()
    {
        final ElasticSearchIndexServiceImpl elasticSearchIndexService = new ElasticSearchIndexServiceImpl();
        elasticSearchIndexService.setStatusTimeout( 5 );
        return elasticSearchIndexService;
    }


}
