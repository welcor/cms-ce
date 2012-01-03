package com.enonic.cms.core.search.query;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.search.IndexType;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/13/11
 * Time: 9:28 PM
 */
public class QueryPath
{

    protected static final String ALL_FIELDS_PATH = "_all";

    private String path;

    private boolean renderAsHasChildQuery = false;

    private boolean renderAsFilter = false;

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

    public String getContextRelativePath()
    {
        return contextRelativePath;
    }


    public QueryPath setIndexType( IndexType indexType )
    {
        this.indexType = indexType;
        return this;
    }

    public QueryPath setContextRelativePath( String contextRelativePath )
    {
        this.contextRelativePath = contextRelativePath;
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
}
