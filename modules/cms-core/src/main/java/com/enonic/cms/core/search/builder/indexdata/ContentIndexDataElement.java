package com.enonic.cms.core.search.builder.indexdata;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.google.common.collect.Sets;

import com.enonic.cms.core.content.index.config.IndexFieldType;
import com.enonic.cms.core.search.ElasticSearchUtils;
import com.enonic.cms.core.search.builder.IndexFieldNameConstants;

public class ContentIndexDataElement
    extends IndexFieldNameConstants
{
    private String fieldBaseName;

    private final Set<Date> dateTimeValues = Sets.newHashSet();

    private final Set<Double> numericValues = Sets.newHashSet();

    private final Set<String> stringValues = Sets.newHashSet();

    private String orderBy;


    public ContentIndexDataElement( final String fieldBaseName, final Set<Object> values )
    {
        doSetValues( fieldBaseName, values );
    }

    private void doSetValues( String fieldBaseName, final Set<Object> values )
    {
        this.fieldBaseName = doNormalizeFieldName( fieldBaseName );

        if ( values == null || values.isEmpty() )
        {
            return;
        }
        else
        {
            // TODO: FIX ORDERBY
            this.orderBy = ContentIndexOrderbyValueResolver.resolveOrderbyValue( values );
        }

        for ( final Object value : values )
        {
            if ( value == null )
            {
                continue;
            }

            if ( value instanceof Number )
            {
                numericValues.add( ( (Number) value ).doubleValue() );
                stringValues.add( doNormalizeStringValue( value.toString() ) );
            }
            else if ( value instanceof Date )
            {
                dateTimeValues.add( (Date) value );
                stringValues.add( ElasticSearchUtils.formatDateForElasticSearch( new DateTime( value ) ) );
            }
            else
            {
                stringValues.add( doNormalizeStringValue( value.toString() ) );
                tryConvertValuesToValidTypes( value );
            }
        }
    }

    private void tryConvertValuesToValidTypes( final Object value )
    {
        final Double doubleValue = ContentIndexNumberValueResolver.resolveNumberValue( value );
        if ( doubleValue != null )
        {
            numericValues.add( doubleValue );
        }
        else
        {
            final Date dateValue = ContentIndexDateValueResolver.resolveDateValue( value );

            if ( dateValue != null )
            {
                dateTimeValues.add( dateValue );
            }
        }
    }


    private String doNormalizeFieldName( final String stringValue )
    {
        if ( StringUtils.isBlank( stringValue ) )
        {
            return "";
        }

        String normalized = replaceSeparators( stringValue );
        normalized = replaceFieldTypeSeparators( normalized );
        normalized = removeAttributeSeparator( normalized );

        return normalized.toLowerCase();
    }

    private String doNormalizeStringValue( final String stringValue )
    {
        if ( StringUtils.isBlank( stringValue ) )
        {
            return "";
        }

        return stringValue.toLowerCase();
    }

    private String replaceSeparators( final String stringValue )
    {
        return StringUtils.replace( stringValue, QUERYLANGUAGE_PROPERTY_SEPARATOR, INDEX_FIELDNAME_PROPERTY_SEPARATOR );
    }

    private String replaceFieldTypeSeparators( final String stringValue )
    {
        return StringUtils.replace( stringValue, INDEX_FIELD_TYPE_SEPARATOR, INDEX_FIELDNAME_PROPERTY_SEPARATOR );
    }

    private String removeAttributeSeparator( final String stringValue )
    {
        return StringUtils.remove( stringValue, "@" );
    }

    public Set<ContentIndexDataFieldValue> getAllFieldValuesForElement()
    {
        final Set<ContentIndexDataFieldValue> set = Sets.newHashSet();

        addStringFieldValue( set );
        addNumericFieldValue( set );
        addDateFieldValue( set );
        addSortFieldValue( set );

        return set;
    }

    private void addSortFieldValue( final Set<ContentIndexDataFieldValue> set )
    {
        if ( StringUtils.isNotBlank( this.orderBy ) )
        {
            set.add(
                new ContentIndexDataFieldValue( this.fieldBaseName + INDEX_FIELD_TYPE_SEPARATOR + ORDERBY_FIELDNAME_POSTFIX, orderBy ) );
        }
    }

    private void addStringFieldValue( final Set<ContentIndexDataFieldValue> set )
    {
        if ( stringValues != null && !stringValues.isEmpty() )
        {
            set.add( new ContentIndexDataFieldValue( this.fieldBaseName, stringValues ) );
        }
    }

    private void addDateFieldValue( final Set<ContentIndexDataFieldValue> set )
    {
        if ( dateTimeValues != null && !dateTimeValues.isEmpty() )
        {
            set.add( new ContentIndexDataFieldValue( this.fieldBaseName + INDEX_FIELD_TYPE_SEPARATOR + IndexFieldType.DATE.toString(),
                                                     dateTimeValues ) );
        }
    }

    private void addNumericFieldValue( final Set<ContentIndexDataFieldValue> set )
    {
        if ( numericValues != null && !numericValues.isEmpty() )
        {
            set.add( new ContentIndexDataFieldValue( this.fieldBaseName + INDEX_FIELD_TYPE_SEPARATOR + IndexFieldType.NUMBER.toString(),
                                                     numericValues ) );
        }
    }


}
