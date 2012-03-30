package com.enonic.cms.core.search.builder;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;

import com.google.common.collect.Sets;


/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/22/11
 * Time: 4:35 PM
 */
class AbstractIndexDataFactory
    extends IndexFieldNameConstants
{
    protected void addField( String fieldName, final String value, final XContentBuilder builder )
        throws Exception
    {
        doAddField( fieldName, value, builder, true );
    }

    private void doAddField( final String fieldName, final String value, final XContentBuilder builder, boolean addOrderField )
        throws Exception
    {
        final String normalizedFieldName = IndexFieldNameResolver.normalizeFieldName( fieldName );
        final String normalizedValue = IndexValueResolver.normalizeValue( value );

        if ( StringUtils.isBlank( normalizedValue ) )
        {
            return;
        }

        /*  try
     {
         Double numericValue = Double.parseDouble( normalizedValue );
         builder.field( IndexFieldNameResolver.getNumericFieldName( normalizedFieldName ), numericValue );

         if ( addOrderField )
         {
             addOrderField( normalizedFieldName, numericValue, builder );
         }
     }
     catch ( NumberFormatException e )
     {
        if ( addOrderField )
         {
             addOrderField( normalizedFieldName, normalizedValue, builder );
         }

     }
        */

        builder.field( normalizedFieldName, normalizedValue );
    }

    protected void addField( String fieldName, final Date value, final XContentBuilder builder )
        throws Exception
    {
        if ( value == null )
        {
            doAddField( fieldName, (String) null, builder, true );
            return;
        }

        doAddField( fieldName, value, builder, true );
    }

    private void doAddField( String fieldName, final Date value, final XContentBuilder builder, boolean addOrderField )
        throws Exception
    {
        fieldName = IndexFieldNameResolver.normalizeFieldName( fieldName );
        final String fieldValue = IndexValueResolver.normalizeDateValue( value );

        builder.field( fieldName, fieldValue );

        /*   if ( addOrderField )
        {
            final String orderByFieldName = IndexFieldNameResolver.getOrderByFieldName( fieldName );
            builder.field( orderByFieldName, value );
        }
        */
    }

    protected void addField( String fieldName, final Integer value, final XContentBuilder builder )
        throws Exception
    {
        doAddField( fieldName, value, builder, true );
    }

    private void doAddField( final String fieldName, final Integer value, final XContentBuilder builder, boolean addOrderField )
        throws Exception
    {

        final String normalizedFieldName = IndexFieldNameResolver.normalizeFieldName( fieldName );
        builder.field( IndexFieldNameResolver.normalizeFieldName( fieldName ), value );

        /*  builder.field( normalizedFieldName, value );
        if ( addOrderField )
        {
            addOrderField( normalizedFieldName, value, builder );
        }
        */
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

    private void doAddField( final String fieldName, final Double value, final XContentBuilder builder, boolean addOrderField )
        throws Exception
    {

        final String normalizedFieldName = IndexFieldNameResolver.normalizeFieldName( fieldName );
        builder.field( IndexFieldNameResolver.normalizeFieldName( fieldName ), value );     //.getNumericFieldName( fieldName ), value );
        builder.field( normalizedFieldName, value );
        //   if ( addOrderField )
        //   {
        //       addOrderField( normalizedFieldName, value, builder );
        //   }
    }
    /*

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
    */

    public void addStringSet( final String fieldName, final Set<String> values, final XContentBuilder builder, final boolean includeNumeric,
                              boolean addOrderField )
        throws Exception
    {
        if ( values.size() == 0 )
        {
            return;
        }

        doAddStringSet( fieldName, values, builder, addOrderField );

        if ( includeNumeric )
        {
            Set<Integer> valuesAsDoubles = getNumericValuesAsSet( values );

            if ( !valuesAsDoubles.isEmpty() )
            {
                doAddNumericSet( fieldName, valuesAsDoubles, builder, false );
            }
        }
    }

    private void doAddStringSet( final String fieldName, Set<String> values, XContentBuilder builder, final boolean addOrderField )
        throws Exception
    {
        final String normalizedFieldName = IndexFieldNameResolver.normalizeFieldName( fieldName );

        builder.array( normalizedFieldName, IndexValueResolver.getNormalizedStringValues( values ) );
        /*
          if ( addOrderField )
          {
              addOrderField( fieldName, getSortValueForSet( values ), builder );
          }

        */
    }

    public void addNumericSet( String fieldName, final Set<Integer> values, final XContentBuilder builder, final boolean addOrderField )
        throws Exception
    {
        if ( values.size() == 0 )
        {
            return;
        }

        doAddNumericSet( fieldName, values, builder, addOrderField );
    }

    private void doAddNumericSet( final String fieldName, final Set<Integer> values, final XContentBuilder builder,
                                  final boolean addOrderField )
        throws Exception
    {

        String numericFieldName = IndexFieldNameResolver.normalizeFieldName( fieldName );
        builder.array( numericFieldName, values.toArray( new Integer[values.size()] ) );

        //Double sortValue = getSortValueForSet( values );

        /* if ( addOrderField )
        {
            addOrderField( fieldName, sortValue, builder );
        }
        */
    }
    /*
    private Double getSortValueForSet( Set<Double> values )
    {
        return Iterables.get( values, 0 );
    }

    private String getSortValueForSet( Set<String> values )
    {
        return Iterables.get( values, 0 );
    }
      */

    private Set<Integer> getNumericValuesAsSet( final Set<String> values )
    {
        final Set<Integer> valuesAsDoubles = Sets.newHashSet();

        for ( String value : values )
        {
            final Integer numericValue = parseNumericValue( value );
            if ( numericValue != null )
            {
                valuesAsDoubles.add( numericValue );
            }
        }

        return valuesAsDoubles;
    }

    private Integer parseNumericValue( final String value )
    {
        try
        {
            return Integer.parseInt( value );

        }
        catch ( NumberFormatException e )
        {
            return null;
        }
    }

}
