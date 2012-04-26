package com.enonic.cms.core.search.query;

import com.enonic.cms.core.search.builder.IndexFieldNameConstants;

public abstract class AbstractQueryFieldAndValue
    extends IndexFieldNameConstants
{
    private final QueryField queryField;

    protected abstract boolean isQueryValueNumeric();

    protected abstract boolean useDateFieldPath();

    protected abstract boolean isQueryOnDateValue();

    protected AbstractQueryFieldAndValue( final QueryField queryField )
    {
        this.queryField = queryField;
    }

    protected AbstractQueryFieldAndValue( final String path )
    {
        this.queryField = QueryFieldResolver.resolveQueryField( path );
    }

    public String getFieldName()
    {
        if ( isQueryValueNumeric() )
        {
            return isWildcardQueyField() ? ALL_USERDATA_FIELDNAME_NUMBER : queryField.getFieldNameForNumericQueries();
        }

        if ( useDateFieldPath() )
        {
            return isWildcardQueyField() ? ALL_USERDATA_FIELDNAME_DATE : queryField.getFieldNameForDateQueries();
        }

        return isWildcardQueyField() ? ALL_USERDATA_FIELDNAME : queryField.getFieldName();
    }

    protected boolean isWildcardQueyField()
    {
        return queryField.isWildcardQueyField();
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
}
