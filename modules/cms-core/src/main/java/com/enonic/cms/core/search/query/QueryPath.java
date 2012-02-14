package com.enonic.cms.core.search.query;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Sets;

import com.enonic.cms.core.search.IndexType;


public class QueryPath
{

    protected static final String ALL_FIELDS_PATH = "_all";

    private static final Set<String> dateFields = Sets.newHashSet( "publishfrom", "publishto", "assignmentduedate" );

    private String path;

    private boolean renderAsHasChildQuery = false;

    private boolean renderAsFilter = false;

    private boolean renderAsIdQuery = false;

    private IndexType indexType;

    private String contextRelativePath;

    public String getPath()
    {
        return path;
    }

    public void setPath( String path )
    {
        this.path = path;
    }

    public boolean isRenderAsIdQuery()
    {
        return renderAsIdQuery;
    }

    public void setRenderAsIdQuery( boolean renderAsIdQuery )
    {
        this.renderAsIdQuery = renderAsIdQuery;
    }

    public QueryPath setRenderAsHasChildQuery( boolean renderAsHasChildQuery )
    {
        this.renderAsHasChildQuery = renderAsHasChildQuery;
        return this;
    }

    public QueryPath( String path )
    {
        this.path = path;
    }

    public boolean doRenderAsHasChildQuery()
    {
        return renderAsHasChildQuery;
    }

    public IndexType getIndexType()
    {
        return indexType;
    }


    public QueryPath setIndexType( IndexType indexType )
    {
        this.indexType = indexType;
        return this;
    }

    public boolean isWildCardPath()
    {
        return StringUtils.contains( this.path, "*" );
    }

    public void setMatchAllPath()
    {
        this.path = ALL_FIELDS_PATH;
    }

    public boolean isDateField()
    {
        return dateFields.contains( this.path );
    }
}
