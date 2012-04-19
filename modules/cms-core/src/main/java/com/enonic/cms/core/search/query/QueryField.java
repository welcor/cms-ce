package com.enonic.cms.core.search.query;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Sets;

import com.enonic.cms.core.content.index.config.IndexFieldType;
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

    private String fieldName;

    private boolean renderAsHasChildQuery = false;

    private boolean renderAsIdQuery = false;

    private IndexType indexType;

    public String getFieldName()
    {
        return fieldName;
    }

    public String getFieldNameForNumericQueries()
    {
        return fieldName + INDEX_FIELD_TYPE_SEPARATOR + IndexFieldType.NUMBER.toString();
    }

    public String getFieldNameForDateQueries()
    {
        return fieldName + INDEX_FIELD_TYPE_SEPARATOR + IndexFieldType.DATE.toString();
    }

    public void setPath( String path )
    {
        this.fieldName = path;
    }

    public boolean doBuildAsIdQuery()
    {
        return renderAsIdQuery;
    }

    public void setRenderAsIdQuery( boolean renderAsIdQuery )
    {
        this.renderAsIdQuery = renderAsIdQuery;
    }

    public QueryField setRenderAsHasChildQuery( boolean renderAsHasChildQuery )
    {
        this.renderAsHasChildQuery = renderAsHasChildQuery;
        return this;
    }

    public QueryField( String fieldName )
    {
        this.fieldName = fieldName;
    }

    public boolean doRenderAsHasChildQuery()
    {
        return renderAsHasChildQuery;
    }

    public IndexType getIndexType()
    {
        return indexType;
    }


    public QueryField setIndexType( IndexType indexType )
    {
        this.indexType = indexType;
        return this;
    }

    public boolean isWildcardQueyField()
    {
        return StringUtils.contains( this.fieldName, "*" );
    }

    public void setMatchAllPath()
    {
        this.fieldName = ALL_USERDATA_FIELDNAME;
    }

    public boolean isDateField()
    {
        return dateFields.contains( this.fieldName );
    }

}
