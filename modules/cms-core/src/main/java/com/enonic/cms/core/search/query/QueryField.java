package com.enonic.cms.core.search.query;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Sets;

import com.enonic.cms.core.search.IndexType;
import com.enonic.cms.core.search.builder.IndexFieldNameConstants;

public class QueryField
    extends IndexFieldNameConstants
{
    private static final Set<String> dateFields;

    static
    {
        dateFields = Sets.newTreeSet( String.CASE_INSENSITIVE_ORDER );
        Collections.addAll( dateFields, PUBLISH_FROM_FIELDNAME, PUBLISH_TO_FIELDNAME, ASSIGNMENT_DUE_DATE_FIELDNAME, TIMESTAMP_FIELDNAME,
                            CONTENT_CREATED, CONTENT_MODIFIED );
    }

    private final String fieldName;

    private boolean renderAsHasChildQuery = false;

    private boolean renderAsIdQuery = false;

    private IndexType indexType;

    public QueryField( final String fieldName )
    {
        this.fieldName = fieldName;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public String getFieldNameForNumericQueries()
    {
        return fieldName + INDEX_FIELD_TYPE_SEPARATOR + NUMBER_FIELD_POSTFIX;
    }

    public String getFieldNameForDateQueries()
    {
        return fieldName + INDEX_FIELD_TYPE_SEPARATOR + DATE_FIELD_POSTFIX;
    }

    public boolean doBuildAsIdQuery()
    {
        return renderAsIdQuery;
    }

    public void setRenderAsIdQuery( final boolean renderAsIdQuery )
    {
        this.renderAsIdQuery = renderAsIdQuery;
    }

    public QueryField setRenderAsHasChildQuery( final boolean renderAsHasChildQuery )
    {
        this.renderAsHasChildQuery = renderAsHasChildQuery;
        return this;
    }


    public boolean doRenderAsHasChildQuery()
    {
        return renderAsHasChildQuery;
    }

    public IndexType getIndexType()
    {
        return indexType;
    }


    public QueryField setIndexType( final IndexType indexType )
    {
        this.indexType = indexType;
        return this;
    }

    public boolean isWildcardQueryField()
    {
        return StringUtils.contains( this.fieldName, "*" );
    }

    public boolean isDateField()
    {
        return dateFields.contains( this.fieldName );
    }

}
