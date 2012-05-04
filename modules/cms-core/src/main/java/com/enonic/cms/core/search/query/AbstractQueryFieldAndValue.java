package com.enonic.cms.core.search.query;

import com.enonic.cms.core.search.builder.IndexFieldNameConstants;

public abstract class AbstractQueryFieldAndValue
    extends IndexFieldNameConstants
{
    private final QueryField queryField;

    protected abstract boolean isQueryValueNumeric();

    protected abstract boolean isQueryOnDateValue();

    protected abstract boolean queryPathIsDateAndValueEmpty();

    protected AbstractQueryFieldAndValue( final QueryField queryField )
    {
        this.queryField = queryField;
    }

    protected AbstractQueryFieldAndValue( final String path )
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

        return isWildcardQuery() ? ALL_USERDATA_FIELDNAME : queryField.getFieldName();
    }

    private boolean isWildcardQuery()
    {
        return queryField.isWildcardQueryField();
    }

    public boolean doRenderAsHasChildQuery()
    {
        return queryField.doRenderAsHasChildQuery();
    }

    public boolean doBuildAsIdQuery()
    {
        return queryField.doBuildAsIdQuery();
    }

    protected boolean isDateField()
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
