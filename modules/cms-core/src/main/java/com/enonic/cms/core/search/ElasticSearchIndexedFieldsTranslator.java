/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.get.GetField;
import org.joda.time.DateTime;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.search.query.ContentIndexFieldSet;

import static com.enonic.cms.core.search.builder.IndexFieldNameConstants.ALL_USERDATA_FIELDNAME;
import static com.enonic.cms.core.search.builder.IndexFieldNameConstants.CATEGORY_KEY_FIELDNAME;
import static com.enonic.cms.core.search.builder.IndexFieldNameConstants.CONTENTDATA_PREFIX;
import static com.enonic.cms.core.search.builder.IndexFieldNameConstants.CONTENTTYPE_KEY_FIELDNAME;
import static com.enonic.cms.core.search.builder.IndexFieldNameConstants.CONTENT_KEY_FIELDNAME;
import static com.enonic.cms.core.search.builder.IndexFieldNameConstants.DATE_FIELD_POSTFIX;
import static com.enonic.cms.core.search.builder.IndexFieldNameConstants.INDEX_FIELD_TYPE_SEPARATOR;
import static com.enonic.cms.core.search.builder.IndexFieldNameConstants.NUMBER_FIELD_POSTFIX;
import static com.enonic.cms.core.search.builder.IndexFieldNameConstants.STATUS_FIELDNAME;

public class ElasticSearchIndexedFieldsTranslator
{

    public List<ContentIndexedFields> generateContentIndexFieldSet( ContentKey contentKey, Map<String, GetField> fields )
    {
        if ( fields.isEmpty() )
        {
            return Collections.emptyList();
        }

        final CategoryKey categoryKey = new CategoryKey( (String) fields.get( CATEGORY_KEY_FIELDNAME ).getValue() );


        final ContentIndexFieldSet indexFieldSet = new ContentIndexFieldSet();
        indexFieldSet.setCategoryKey( categoryKey );
        indexFieldSet.setKey( contentKey );

        final String statusFieldName = STATUS_FIELDNAME + INDEX_FIELD_TYPE_SEPARATOR + NUMBER_FIELD_POSTFIX;
        indexFieldSet.setStatus( getFieldAsInt( fields.get( statusFieldName ) ) );

        final String ctypeFieldName = CONTENTTYPE_KEY_FIELDNAME + INDEX_FIELD_TYPE_SEPARATOR + NUMBER_FIELD_POSTFIX;
        indexFieldSet.setContentTypeKey( new ContentTypeKey( getFieldAsInt( fields.get( ctypeFieldName ) ) ) );

        for ( final String name : fields.keySet() )
        {
            if ( skipField( name ) )
            {
                continue;
            }

            if ( name.startsWith( CONTENTDATA_PREFIX ) && ( !name.contains( INDEX_FIELD_TYPE_SEPARATOR ) ) )
            {
                final GetField field = fields.get( name );
                final String value = field.getValue().toString();
                if ( StringUtils.isNotEmpty( value ) )
                {
                    final String customFieldName = StringUtils.substringAfter( name, CONTENTDATA_PREFIX );
                    indexFieldSet.addFieldWithStringValue( "data#" + customFieldName, value );
                }
            }
            else if ( ( !name.startsWith( CONTENTDATA_PREFIX ) ) && ( !name.contains( INDEX_FIELD_TYPE_SEPARATOR ) ) )
            {
                addIndexEntityField( indexFieldSet, name, fields );
            }
        }

        return indexFieldSet.getEntitites();
    }

    private void addIndexEntityField( ContentIndexFieldSet indexFieldSet, String name, Map<String, GetField> fields )
    {
        final GetField field;
        final String fieldPath = name.replace( "_", "/" );
        if ( fields.containsKey( name + INDEX_FIELD_TYPE_SEPARATOR + DATE_FIELD_POSTFIX ) )
        {
            field = fields.get( name + INDEX_FIELD_TYPE_SEPARATOR + DATE_FIELD_POSTFIX );
            final DateTime dateTime = DateTime.parse( (String) field.getValue() );
            indexFieldSet.addFieldWithDateValue( fieldPath, dateTime.toDate(), "" );
        }
        else
        {
            field = fields.get( name );
            indexFieldSet.addFieldWithAnyValue( fieldPath, StringUtils.join( field.getValues(), ',' ) );
        }
    }

    private int getFieldAsInt( GetField field )
    {
        final Double valueNum = (Double) field.getValue();
        return valueNum.intValue();
    }

    private boolean skipField( String fieldName )
    {
        return STATUS_FIELDNAME.equals( fieldName ) || CONTENT_KEY_FIELDNAME.equals( fieldName ) ||
                fieldName.contains( INDEX_FIELD_TYPE_SEPARATOR ) ||
                ALL_USERDATA_FIELDNAME.equals( fieldName ) || fieldName.startsWith( "access_" ) ||
                fieldName.endsWith( "_name" );
    }
}
