package com.enonic.cms.web.filter;

import java.util.List;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate3.support.OpenSessionInViewFilter;
import org.springframework.web.filter.CompositeFilter;
import org.springframework.web.filter.RequestContextFilter;

import com.google.common.collect.Lists;

@Configuration
public class ServletFilterBeans
{
    private CharacterEncodingFilter characterEncodingFilter;

    private UpgradeCheckFilter upgradeCheckFilter;

    private VirtualHostFilter virtualHostFilter;

    @Bean
    public CompositeFilter compositeFilter()
    {
        final List<Filter> filters = Lists.newArrayList();
        filters.add( requestContextFilter() );
        filters.add( this.characterEncodingFilter );
        filters.add( openSessionInViewFilter() );
        filters.add( this.upgradeCheckFilter );
        filters.add( this.virtualHostFilter );

        final CompositeFilter filter = new CompositeFilter();
        filter.setFilters( filters );
        return filter;
    }

    @Bean
    public RequestContextFilter requestContextFilter()
    {
        return new RequestContextFilter();
    }

    @Bean
    public OpenSessionInViewFilter openSessionInViewFilter()
    {
        final OpenSessionInViewFilter filter = new OpenSessionInViewFilter();
        filter.setSingleSession( true );
        return filter;
    }

    @Autowired
    public void setCharacterEncodingFilter( final CharacterEncodingFilter characterEncodingFilter )
    {
        this.characterEncodingFilter = characterEncodingFilter;
    }

    @Autowired
    public void setUpgradeCheckFilter( final UpgradeCheckFilter upgradeCheckFilter )
    {
        this.upgradeCheckFilter = upgradeCheckFilter;
    }

    @Autowired
    public void setVirtualHostFilter( final VirtualHostFilter virtualHostFilter )
    {
        this.virtualHostFilter = virtualHostFilter;
    }
}
