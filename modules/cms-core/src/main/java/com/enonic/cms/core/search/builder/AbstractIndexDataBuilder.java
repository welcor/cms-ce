package com.enonic.cms.core.search.builder;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.util.NumericUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;


/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/22/11
 * Time: 4:35 PM
 */
class AbstractIndexDataBuilder
    extends IndexFieldNameConstants
{

    protected void addField( String fieldName, final String value, final XContentBuilder builder )
        throws Exception
    {
        doAddField( fieldName, value, builder, true );
    }

    protected void addField( String fieldName, final String value, final XContentBuilder builder, boolean addOrderField )
        throws Exception
    {
        doAddField( fieldName, value, builder, addOrderField );
    }

    private void doAddField( String fieldName, String value, final XContentBuilder builder, boolean addOrderField )
        throws Exception
    {
        fieldName = IndexFieldNameResolver.normalizeFieldName( fieldName );
        value = normalizeValue( value );

        if ( StringUtils.isBlank( value ) )
        {
            return;
        }

        try
        {
            Double numericValue = Double.parseDouble( value );
            builder.field( IndexFieldNameResolver.getNumericFieldName( fieldName ), numericValue );

            if ( addOrderField )
            {
                addOrderField( fieldName, numericValue, builder );
            }
        }
        catch ( NumberFormatException e )
        {
            if ( addOrderField )
            {
                addOrderField( fieldName, value, builder );
            }
        }

        builder.field( fieldName, value );

    }

    private String normalizeValue( final String value )
    {
        if ( StringUtils.isBlank( value ) )
        {
            return "";
        }

        return value.trim().toLowerCase();
    }


    protected void addField( String fieldName, final Date value, final XContentBuilder builder )
        throws Exception
    {
        doAddField( fieldName, value, builder, true );
    }

    protected void addField( String fieldName, final Date value, final XContentBuilder builder, boolean addOrderField )
        throws Exception
    {
        doAddField( fieldName, value, builder, addOrderField );
    }


    private void doAddField( String fieldName, final Date value, final XContentBuilder builder, boolean addOrderField )
        throws Exception
    {

        fieldName = IndexFieldNameResolver.normalizeFieldName( fieldName );
        builder.field( fieldName, value );
        if ( addOrderField )
        {
            addOrderField( fieldName, value, builder );
        }
    }


    protected void addField( String fieldName, final Integer value, final XContentBuilder builder )
        throws Exception
    {
        doAddField( fieldName, value, builder, true );
    }

    protected void addField( String fieldName, final Integer value, final XContentBuilder builder, boolean addOrderField )
        throws Exception
    {
        doAddField( fieldName, value, builder, addOrderField );
    }


    private void doAddField( String fieldName, final Integer value, final XContentBuilder builder, boolean addOrderField )
        throws Exception
    {

        fieldName = IndexFieldNameResolver.normalizeFieldName( fieldName );
        builder.field( IndexFieldNameResolver.getNumericFieldName( fieldName ), value );
        builder.field( fieldName, value );
        if ( addOrderField )
        {
            addOrderField( fieldName, value, builder );
        }
    }


    protected void addField( String fieldName, final Double value, final XContentBuilder builder )
        throws Exception
    {
        doAddField( fieldName, value, builder, true );
    }

    protected void addField( String fieldName, final Double value, final XContentBuilder builder, boolean addOrderField )
        throws Exception
    {
        doAddField( fieldName, value, builder, addOrderField );
    }


    private void doAddField( String fieldName, final Double value, final XContentBuilder builder, boolean addOrderField )
        throws Exception
    {

        fieldName = IndexFieldNameResolver.normalizeFieldName( fieldName );
        builder.field( IndexFieldNameResolver.getNumericFieldName( fieldName ), value );
        builder.field( fieldName, value );
        if ( addOrderField )
        {
            addOrderField( fieldName, value, builder );
        }
    }

    private void addOrderField( String fieldName, final Number value, final XContentBuilder builder )
        throws Exception
    {
        String orderByFieldName = IndexFieldNameResolver.getOrderByFieldName( fieldName );
        String orderByValue = IndexValueResolver.getOrderValueForNumber( value );

        builder.field( orderByFieldName, orderByValue );
    }

    private void addOrderField( String fieldName, final String value, final XContentBuilder builder )
        throws Exception
    {

        String orderByFieldName = IndexFieldNameResolver.getOrderByFieldName( fieldName );

        try
        {
            Double numericValue = Double.parseDouble( value );
            String orderByValue = NumericUtils.doubleToPrefixCoded( numericValue );
            builder.field( orderByFieldName, orderByValue );
        }
        catch ( NumberFormatException e )
        {
            builder.field( orderByFieldName, value );
        }
    }

    private void addOrderField( String fieldName, final Date value, final XContentBuilder builder )
        throws Exception
    {

        String orderByFieldName = IndexFieldNameResolver.getOrderByFieldName( fieldName );
        builder.field( orderByFieldName, value );

    }

    public void addStringSet( final String fieldName, final Set<String> values, final XContentBuilder builder, final boolean includeNumeric,
                              boolean addOrderField )
        throws Exception
    {
        doAddStringSet( fieldName, values, builder, addOrderField );

        if ( includeNumeric )
        {
            Set<Double> valuesAsDoubles = getNumericValuesAsSet( values );

            if ( !valuesAsDoubles.isEmpty() )
            {
                doAddNumericSet( fieldName, valuesAsDoubles, builder, false );
            }
        }
    }

    private void doAddStringSet( String fieldName, Set<String> values, XContentBuilder builder, final boolean addOrderField )
        throws Exception
    {
        String normalizedFieldName = IndexFieldNameResolver.normalizeFieldName( fieldName );
        builder.array( normalizedFieldName, values.toArray( new String[values.size()] ) );

        if ( addOrderField )
        {
            addOrderField( fieldName, getSortValueForSet( values ), builder );
        }
    }

    public void addNumericSet( String fieldName, final Set<Double> values, final XContentBuilder builder, final boolean addOrderField )
        throws Exception
    {
        doAddNumericSet( fieldName, values, builder, addOrderField );
    }

    private void doAddNumericSet( final String fieldName, final Set<Double> values, final XContentBuilder builder,
                                  final boolean addOrderField )
        throws Exception
    {
        String numericFieldName = IndexFieldNameResolver.getNumericFieldName( fieldName );
        builder.array( numericFieldName, values.toArray( new Double[values.size()] ) );

        Double sortValue = getSortValueForSet( values );

        if ( addOrderField )
        {
            addOrderField( fieldName, sortValue, builder );
        }
    }

    private Double getSortValueForSet( Set<Double> values )
    {
        return Iterables.get( values, 0 );
    }

    private String getSortValueForSet( Set<String> values )
    {
        return Iterables.get( values, 0 );
    }


    private Set<Double> getNumericValuesAsSet( final Set<String> values )
    {
        final Set<Double> valuesAsDoubles = Sets.newHashSet();

        for ( String value : values )
        {
            final Double numericValue = parseNumericValue( value );
            if ( numericValue != null )
            {
                valuesAsDoubles.add( numericValue );
            }
        }

        return valuesAsDoubles;
    }

    private Double parseNumericValue( final String value )
    {
        try
        {
            return Double.parseDouble( value );

        }
        catch ( NumberFormatException e )
        {
            return null;
        }
    }

}
