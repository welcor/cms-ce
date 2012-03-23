package com.enonic.cms.core.search.query;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Sets;

import com.enonic.cms.core.search.IndexType;
import static com.enonic.cms.core.search.builder.IndexFieldNameConstants.*;

public class QueryPath
{

    protected static final String ALL_FIELDS_PATH = "_all";

    private static final Set<String> dateFields;
    static
    {
        dateFields = Sets.newTreeSet( String.CASE_INSENSITIVE_ORDER );
        Collections.addAll( dateFields, PUBLISH_FROM_FIELDNAME, PUBLISH_TO_FIELDNAME, ASSIGNMENT_DUE_DATE_FIELDNAME, TIMESTAMP_FIELDNAME,
                            CONTENT_CREATED, CONTENT_MODIFIED);
    }

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

    public boolean doBuildAsIdQuery()
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
