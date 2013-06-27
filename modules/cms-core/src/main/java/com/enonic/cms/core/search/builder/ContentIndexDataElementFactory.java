/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.builder;

import java.util.Date;
import java.util.Set;

import com.enonic.cms.core.search.ElasticSearchFormatter;
import com.enonic.cms.core.search.IndexFieldnameNormalizer;
import com.enonic.cms.core.search.IndexValueNormalizer;

public class ContentIndexDataElementFactory
    extends IndexFieldNameConstants
{
    public static ContentIndexDataElement create( final String fieldBaseName, final Set<Object> values, boolean addOrderBy )
    {
        return doCreateContentIndexDataElement( fieldBaseName, values, addOrderBy );
    }

    public static ContentIndexDataElement create( final String fieldBaseName, final Set<Object> values )
    {
        return doCreateContentIndexDataElement( fieldBaseName, values, true );
    }

    private static ContentIndexDataElement doCreateContentIndexDataElement( final String fieldBaseName, final Set<Object> values,
                                                                            boolean addOrderBy )
    {
        ContentIndexDataElement contentIndexDataElement = new ContentIndexDataElement();

        contentIndexDataElement.setFieldBaseName( IndexFieldnameNormalizer.normalizeFieldName( fieldBaseName ) );

        if ( values == null || values.isEmpty() )
        {
            return contentIndexDataElement;
        }

        for ( final Object value : values )
        {
            if ( value == null )
            {
                continue;
            }

            if ( value instanceof Number )
            {
                contentIndexDataElement.addNumericValue( ( (Number) value ).doubleValue() );
                contentIndexDataElement.addStringValue( IndexValueNormalizer.normalizeStringValue( value.toString() ) );
            }
            else if ( value instanceof Date )
            {
                contentIndexDataElement.addDateValue( (Date) value );
                contentIndexDataElement.addStringValue( ElasticSearchFormatter.formatDateAsStringIgnoreTimezone( (Date) value ) );
            }
            else
            {
                contentIndexDataElement.addStringValue( IndexValueNormalizer.normalizeStringValue( value.toString() ) );
                addNumberOrDateIfPossible( contentIndexDataElement, value );
            }

            if ( addOrderBy )
            {
                setOrderBy( contentIndexDataElement, value );
            }
        }

        return contentIndexDataElement;
    }

    private static void setOrderBy( final ContentIndexDataElement element, final Object value )
    {
        if ( element.getOrderBy() == null )
        {
            element.setOrderBy( ContentIndexOrderbyValueResolver.getOrderbyValue( value ) );
        }
    }

    private static void addNumberOrDateIfPossible( final ContentIndexDataElement element, final Object value )
    {
        final Double doubleValue = ContentIndexNumberValueResolver.resolveNumberValue( value );
        if ( doubleValue != null )
        {
            element.addNumericValue( doubleValue );
            return;
        }

        final Date dateValue = ContentIndexDateValueResolver.resolveDateValue( value );

        if ( dateValue != null )
        {
            element.addDateValue( dateValue );
        }
    }
}
