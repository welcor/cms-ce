package com.enonic.cms.core.search.builder;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.util.NumericUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;


/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/22/11
 * Time: 4:35 PM
 */
class AbstractIndexDataBuilder
{

    protected void addField( String fieldName, String value, XContentBuilder builder )
        throws Exception
    {
        doAddField( fieldName, value, builder, true );
    }

    protected void addField( String fieldName, String value, XContentBuilder builder, boolean addOrderField )
        throws Exception
    {
        doAddField( fieldName, value, builder, addOrderField );
    }

    protected void doAddField( String fieldName, String value, XContentBuilder builder, boolean addOrderField )
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
            builder.field( IndexFieldNameResolver.getNumericField( fieldName ), numericValue );
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

    private String normalizeValue( String value )
    {
        if ( StringUtils.isBlank( value ) )
        {
            return "";
        }

        return value.trim().toLowerCase();
    }


    protected void addField( String fieldName, Date value, XContentBuilder builder )
        throws Exception
    {
        doAddField( fieldName, value, builder, true );
    }

    protected void addField( String fieldName, Date value, XContentBuilder builder, boolean addOrderField )
        throws Exception
    {
        doAddField( fieldName, value, builder, addOrderField );
    }


    private void doAddField( String fieldName, Date value, XContentBuilder builder, boolean addOrderField )
        throws Exception
    {

        fieldName = IndexFieldNameResolver.normalizeFieldName( fieldName );
        builder.field( fieldName, value );
        if ( addOrderField )
        {
            addOrderField( fieldName, value, builder );
        }
    }


    protected void addField( String fieldName, Integer value, XContentBuilder builder )
        throws Exception
    {
        doAddField( fieldName, value, builder, true );
    }

    protected void addField( String fieldName, Integer value, XContentBuilder builder, boolean addOrderField )
        throws Exception
    {
        doAddField( fieldName, value, builder, addOrderField );
    }


    private void doAddField( String fieldName, Integer value, XContentBuilder builder, boolean addOrderField )
        throws Exception
    {

        fieldName = IndexFieldNameResolver.normalizeFieldName( fieldName );
        builder.field( IndexFieldNameResolver.getNumericField( fieldName ), value );
        builder.field( fieldName, value );
        if ( addOrderField )
        {
            addOrderField( fieldName, value, builder );
        }
    }


    protected void addField( String fieldName, Double value, XContentBuilder builder )
        throws Exception
    {
        doAddField( fieldName, value, builder, true );
    }

    protected void addField( String fieldName, Double value, XContentBuilder builder, boolean addOrderField )
        throws Exception
    {
        doAddField( fieldName, value, builder, addOrderField );
    }


    private void doAddField( String fieldName, Double value, XContentBuilder builder, boolean addOrderField )
        throws Exception
    {

        fieldName = IndexFieldNameResolver.normalizeFieldName( fieldName );
        builder.field( IndexFieldNameResolver.getNumericField( fieldName ), value );
        builder.field( fieldName, value );
        if ( addOrderField )
        {
            addOrderField( fieldName, value, builder );
        }
    }

    private void addOrderField( String fieldName, Number value, XContentBuilder builder )
        throws Exception
    {
        String orderByFieldName = IndexFieldNameResolver.getOrderByFieldName( fieldName );
        String orderByValue = IndexValueResolver.getOrderByValue( value );

        builder.field( orderByFieldName, orderByValue );
    }

    private void addOrderField( String fieldName, String value, XContentBuilder builder )
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

    private void addOrderField( String fieldName, Date value, XContentBuilder builder )
        throws Exception
    {
        /*
        String prderByFieldName = QueryFieldNameResolver.getOrderByFieldName( fieldName );
        builder.field( prderByFieldName, value );
        */
    }


}
