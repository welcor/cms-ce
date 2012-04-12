package com.enonic.cms.core.search.builder;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.content.index.config.IndexFieldType;

public class IndexFieldName
    extends IndexFieldNameConstants
{
    private final IndexFieldType indexFieldType;

    private final String fieldBaseName;

    public IndexFieldName( final String fieldBaseName )
    {
        this.indexFieldType = IndexFieldType.STRING;
        this.fieldBaseName = doNormalizeFieldName( fieldBaseName );
    }

    public IndexFieldName( final String fieldBaseName, final IndexFieldType indexFieldType )
    {
        this.indexFieldType = indexFieldType;
        this.fieldBaseName = doNormalizeFieldName( fieldBaseName );
    }

    public String getFieldName()
    {
        switch ( indexFieldType )
        {
            case STRING:
            {
                return this.fieldBaseName;
            }
            default:
            {
                return this.fieldBaseName + "." + indexFieldType.toString();
            }
        }
    }

    private static String doNormalizeFieldName( final String fieldName )
    {
        if ( StringUtils.isBlank( fieldName ) )
        {
            return "";
        }

        return fieldName.replace( QUERY_LANGUAGE_PROPERTY_SEPARATOR, INDEX_FIELDNAME_PROPERTY_SEPARATOR ).replace( ".",
                                                                                                                   INDEX_FIELDNAME_PROPERTY_SEPARATOR ).replaceAll(
            "@", "" ).toLowerCase();
    }

}

