package com.enonic.cms.core.portal.livetrace;

import org.joda.time.DateTime;

public class ContentIndexQueryTrace
    extends BaseTrace
    implements Trace
{
    private int index;

    private int count;

    private int matchCount;

    private MaxLengthedString query = new MaxLengthedString();

    private MaxLengthedString translatedQuery = new MaxLengthedString();

    private String contentFilter;

    private String sectionFilter;

    private String categoryFilter;

    private String contentTypeFilter;

    private String securityFilter;

    private String categoryAccessTypeFilter;

    private Duration durationInElasticSearch = new Duration();

    ContentIndexQueryTrace()
    {
    }

    @SuppressWarnings("UnusedDeclaration")
    public int getIndex()
    {
        return index;
    }

    void setIndex( int index )
    {
        this.index = index;
    }

    @SuppressWarnings("UnusedDeclaration")
    public int getCount()
    {
        return count;
    }

    void setCount( int count )
    {
        this.count = count;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getQuery()
    {
        return query != null ? query.toString() : null;
    }

    void setQuery( String query )
    {
        this.query = new MaxLengthedString( query, 6000 );
    }

    @SuppressWarnings("UnusedDeclaration")
    public MaxLengthedString getTranslatedQuery()
    {
        return translatedQuery;
    }

    public void setTranslatedQuery( final String translatedQuery )
    {
        this.translatedQuery = new MaxLengthedString( translatedQuery, 6000 );
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getContentFilter()
    {
        return contentFilter;
    }

    void setContentFilter( String contentFilter )
    {
        this.contentFilter = contentFilter;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getSectionFilter()
    {
        return sectionFilter;
    }

    void setSectionFilter( String sectionFilter )
    {
        this.sectionFilter = sectionFilter;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getCategoryFilter()
    {
        return categoryFilter;
    }

    void setCategoryFilter( String categoryFilter )
    {
        this.categoryFilter = categoryFilter;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getContentTypeFilter()
    {
        return contentTypeFilter;
    }

    void setContentTypeFilter( String contentTypeFilter )
    {
        this.contentTypeFilter = contentTypeFilter;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getSecurityFilter()
    {
        return securityFilter;
    }

    void setSecurityFilter( String securityFilter )
    {
        this.securityFilter = securityFilter;
    }

    @SuppressWarnings("UnusedDeclaration")
    public String getCategoryAccessTypeFilter()
    {
        return categoryAccessTypeFilter;
    }

    void setCategoryAccessTypeFilter( String categoryAccessTypeFilter )
    {
        this.categoryAccessTypeFilter = categoryAccessTypeFilter;
    }

    @SuppressWarnings("UnusedDeclaration")
    public int getMatchCount()
    {
        return matchCount;
    }

    void setMatchCount( int matchCount )
    {
        this.matchCount = matchCount;
    }

    public void setElasticSearchStartTime( final DateTime time )
    {
        this.durationInElasticSearch.setStartTime( time );
    }

    public void setElasticSearchStopTime( final DateTime time )
    {
        this.durationInElasticSearch.setStopTime( time );
    }

    @SuppressWarnings("UnusedDeclaration")
    public Duration getDurationInElasticSearch()
    {
        return this.durationInElasticSearch;
    }
}
