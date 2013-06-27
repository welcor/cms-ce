/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.builder;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Sets;

public class ContentIndexDataFieldValueSetFactory
    extends IndexFieldNameConstants
{

    public static Set<ContentIndexDataFieldAndValue> create( ContentIndexDataElement element )
    {
        final Set<ContentIndexDataFieldAndValue> contentIndexDataFieldAndValues = Sets.newHashSet();

        addStringFieldValue( element, contentIndexDataFieldAndValues );
        addNumericFieldValue( element, contentIndexDataFieldAndValues );
        addDateFieldValue( element, contentIndexDataFieldAndValues );

        if ( !contentIndexDataFieldAndValues.isEmpty() )
        {
            addOrderbyValue( element, contentIndexDataFieldAndValues );
        }

        return contentIndexDataFieldAndValues;
    }

    private static void addStringFieldValue( ContentIndexDataElement element, final Set<ContentIndexDataFieldAndValue> set )
    {
        final Set<String> elementStringValues = element.getStringValues();

        if ( elementStringValues != null && !elementStringValues.isEmpty() )
        {
            set.add( new ContentIndexDataFieldAndValue( element.getFieldBaseName(), elementStringValues ) );
        }
    }

    private static void addNumericFieldValue( ContentIndexDataElement element,
                                              final Set<ContentIndexDataFieldAndValue> contentIndexDataFieldAndValues )
    {
        final Set<Double> elementNumericValues = element.getNumericValues();

        if ( elementNumericValues != null && !elementNumericValues.isEmpty() )
        {
            contentIndexDataFieldAndValues.add(
                new ContentIndexDataFieldAndValue( element.getFieldBaseName() + INDEX_FIELD_TYPE_SEPARATOR + NUMBER_FIELD_POSTFIX,
                                                   elementNumericValues ) );
        }
    }

    private static void addDateFieldValue( ContentIndexDataElement element,
                                           final Set<ContentIndexDataFieldAndValue> contentIndexDataFieldAndValues )
    {
        final Set<Date> elementDateTimeValues = element.getDateTimeValues();

        if ( elementDateTimeValues != null && !elementDateTimeValues.isEmpty() )
        {
            contentIndexDataFieldAndValues.add(
                new ContentIndexDataFieldAndValue( element.getFieldBaseName() + INDEX_FIELD_TYPE_SEPARATOR + DATE_FIELD_POSTFIX,
                                                   elementDateTimeValues ) );
        }
    }

    private static void addOrderbyValue( ContentIndexDataElement element,
                                         final Set<ContentIndexDataFieldAndValue> contentIndexDataFieldAndValues )
    {
        final String elementOrderBy = element.getOrderBy();

        if ( StringUtils.isNotBlank( elementOrderBy ) )
        {
            contentIndexDataFieldAndValues.add(
                new ContentIndexDataFieldAndValue( element.getFieldBaseName() + INDEX_FIELD_TYPE_SEPARATOR + ORDERBY_FIELDNAME_POSTFIX,
                                                   elementOrderBy ) );
        }
    }


}
