package com.enonic.cms.core.search.builder;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.google.common.collect.Sets;

import com.enonic.cms.core.search.ElasticSearchUtils;
import com.enonic.cms.core.search.IndexFieldnameNormalizer;
import com.enonic.cms.core.search.IndexValueNormalizer;

public class ContentIndexDataElement
    extends IndexFieldNameConstants
{
    private String fieldBaseName;

    private final Set<Date> dateTimeValues = Sets.newHashSet();

    private final Set<Double> numericValues = Sets.newHashSet();

    private final Set<String> stringValues = Sets.newHashSet();

    private String orderBy;

    private String orderByNumber;

    public ContentIndexDataElement( final String fieldBaseName, final Set<Object> values )
    {
        this.fieldBaseName = IndexFieldnameNormalizer.normalizeFieldName( fieldBaseName );

        if ( values == null || values.isEmpty() )
        {
            return;
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
                stringValues.add( IndexValueNormalizer.normalizeStringValue( value.toString() ) );
                setOrderbyNumericIfNull( (Number) value );
                setOrderbyStringIfNull( value );
            }
            else if ( value instanceof Date )
            {
                dateTimeValues.add( (Date) value );
                stringValues.add( ElasticSearchUtils.formatDateForElasticSearch( new DateTime( value ) ) );
                setOrderbyDateIfNull( (Date) value );
                setOrderbyStringIfNull( value );
            }
            else
            {
                stringValues.add( IndexValueNormalizer.normalizeStringValue( value.toString() ) );
                setOrderbyStringIfNull( value );
                addNumberOrDateIfPossible( value );
            }
        }
    }

    private void setOrderbyStringIfNull( final Object value )
    {
        if ( orderBy == null )
        {
            orderBy = ContentIndexOrderbyValueResolver.getOrderbyValueForString( value.toString() );
        }
    }

    private void setOrderbyDateIfNull( final Date value )
    {
        if ( orderBy == null )
        {
            orderBy = ContentIndexOrderbyValueResolver.getOrderbyValueForDate( value );
        }
    }

    private void setOrderbyNumericIfNull( final Number value )
    {
        if ( orderByNumber == null )
        {
            orderByNumber = ContentIndexOrderbyValueResolver.getNumericOrderBy( value );
        }
    }

    private void addNumberOrDateIfPossible( final Object value )
    {
        final Double doubleValue = ContentIndexNumberValueResolver.resolveNumberValue( value );
        if ( doubleValue != null )
        {
            numericValues.add( doubleValue );
            setOrderbyNumericIfNull( doubleValue );
            return;
        }

        final Date dateValue = ContentIndexDateValueResolver.resolveDateValue( value );

        if ( dateValue != null )
        {
            dateTimeValues.add( dateValue );
        }
    }

    public Set<ContentIndexDataFieldValue> getAllFieldValuesForElement()
    {
        final Set<ContentIndexDataFieldValue> set = Sets.newHashSet();

        addStringFieldValue( set );
        addNumericFieldValue( set );
        addDateFieldValue( set );
        addSortFieldValues( set );

        return set;
    }

    private void addStringFieldValue( final Set<ContentIndexDataFieldValue> set )
    {
        if ( stringValues != null && !stringValues.isEmpty() )
        {
            set.add( new ContentIndexDataFieldValue( this.fieldBaseName, stringValues ) );
        }
    }

    private void addNumericFieldValue( final Set<ContentIndexDataFieldValue> set )
    {
        if ( numericValues != null && !numericValues.isEmpty() )
        {
            set.add(
                new ContentIndexDataFieldValue( this.fieldBaseName + INDEX_FIELD_TYPE_SEPARATOR + NUMBER_FIELD_POSTFIX, numericValues ) );
        }
    }

    private void addDateFieldValue( final Set<ContentIndexDataFieldValue> set )
    {
        if ( dateTimeValues != null && !dateTimeValues.isEmpty() )
        {
            set.add(
                new ContentIndexDataFieldValue( this.fieldBaseName + INDEX_FIELD_TYPE_SEPARATOR + DATE_FIELD_POSTFIX, dateTimeValues ) );
        }
    }

    private void addSortFieldValues( final Set<ContentIndexDataFieldValue> set )
    {
        if ( StringUtils.isNotBlank( this.orderBy ) )
        {
            set.add(
                new ContentIndexDataFieldValue( this.fieldBaseName + INDEX_FIELD_TYPE_SEPARATOR + ORDERBY_FIELDNAME_POSTFIX, orderBy ) );
        }

        if ( StringUtils.isNotBlank( this.orderByNumber ) )
        {
            set.add( new ContentIndexDataFieldValue( this.fieldBaseName + INDEX_FIELD_TYPE_SEPARATOR + ORDERBY_NUMERIC_FIELDNAME_POSTFIX,
                                                     orderByNumber ) );
        }

    }

}
