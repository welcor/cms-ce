/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.query;

import com.enonic.cms.core.search.builder.IndexFieldNameConstants;

public abstract class AbstractQueryFieldAndValue
    extends IndexFieldNameConstants
{
    private final QueryField queryField;

    protected abstract boolean isQueryValueNumeric();

    protected abstract boolean isQueryOnDateValue();

    protected abstract boolean queryPathIsDateAndValueEmpty();

    AbstractQueryFieldAndValue( final QueryField queryField )
    {
        this.queryField = queryField;
    }

    AbstractQueryFieldAndValue( final String path )
    {
        this.queryField = QueryFieldFactory.resolveQueryField( path );
    }

    public String getFieldName()
    {
        if ( isQueryValueNumeric() )
        {
            return isWildcardQuery() ? ALL_USERDATA_FIELDNAME_NUMBER : queryField.getFieldNameForNumericQueries();
        }

        if ( useDateFieldPath() )
        {
            return isWildcardQuery() ? ALL_USERDATA_FIELDNAME_DATE : queryField.getFieldNameForDateQueries();
        }

        if ( isWildcardQuery() )
        {
            return isAttachmentQuery() ? ATTACHMENT_FIELDNAME : ALL_USERDATA_FIELDNAME;
        }

        return queryField.getFieldName();
    }

    private boolean isWildcardQuery()
    {
        return queryField.isWildcardQueryField();
    }

    private boolean isAttachmentQuery()
    {
        return queryField.isAttachmentField();
    }

    public boolean doRenderAsHasChildQuery()
    {
        return queryField.doRenderAsHasChildQuery();
    }

    public boolean doBuildAsIdQuery()
    {
        return queryField.doBuildAsIdQuery();
    }

    boolean isDateField()
    {
        return queryField.isDateField();
    }

    public String getIndexType()
    {
        return queryField.getIndexType().toString();
    }

    final boolean useDateFieldPath()
    {
        return queryPathIsDateAndValueEmpty() || isQueryOnDateValue();
    }
}
