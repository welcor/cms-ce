package com.enonic.cms.core.search.builder;

import java.util.Date;
import java.util.Set;
import java.util.logging.Logger;

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
    private final Logger LOG = Logger.getLogger( AbstractIndexDataFactory.class.getName() );

    protected void addStringField( String indexFieldName, final String value, final XContentBuilder builder )
        throws Exception
    {
        doAddStringField( indexFieldName, value, builder );
    }

    private void doAddStringField( final String indexFieldName, final String value, final XContentBuilder builder )
        throws Exception
    {
        final String normalizedValue = getNormalizedFieldName( value );

        if ( StringUtils.isBlank( normalizedValue ) )
        {
            return;
        }

        builder.field( getNormalizedFieldName( indexFieldName ), normalizedValue );
    }

    private String getNormalizedFieldName( final String indexFieldName )
    {
        return IndexValueResolver.normalizeValue( indexFieldName );
    }

    protected void addDateField( String indexFieldName, final Date value, final XContentBuilder builder )
        throws Exception
    {
        if ( value == null )
        {
            doAddStringField( indexFieldName, null, builder );
            return;
        }

        doAddDateField( indexFieldName, value, builder );
    }

    private void doAddDateField( String indexFieldName, final Date value, final XContentBuilder builder )
        throws Exception
    {
        final String fieldValue = IndexValueResolver.normalizeDateValue( value );

        builder.field( getNormalizedFieldName( indexFieldName ), fieldValue );
    }

    protected void addIntegerField( String indexFieldName, final Integer value, final XContentBuilder builder )
        throws Exception
    {
        doAddIntegerField( indexFieldName, value, builder );
    }

    private void doAddIntegerField( final String indexFieldName, final Integer value, final XContentBuilder builder )
        throws Exception
    {
        builder.field( getNormalizedFieldName( indexFieldName ), value );
    }

    protected void addDoubleField( final String indexFieldName, final Double value, final XContentBuilder builder )
        throws Exception
    {
        doAddDoubleField( indexFieldName, value, builder );
    }

    private void doAddDoubleField( final String indexFieldName, final Double value, final XContentBuilder builder )
        throws Exception
    {
        builder.field( getNormalizedFieldName( indexFieldName ), value );
    }

    public void addStringSet( final String indexFieldName, final Set<String> values, final XContentBuilder builder )
        throws Exception
    {
        if ( values.size() == 0 )
        {
            return;
        }

        doAddStringSet( indexFieldName, values, builder );
    }

    private void doAddStringSet( final String indexFieldName, Set<String> values, XContentBuilder builder )
        throws Exception
    {
        builder.array( indexFieldName, IndexValueResolver.getNormalizedStringValues( values ) );
    }


    public void translateAndAddNumericSet( final String indexFieldName, final Set<String> values, final XContentBuilder builder )
        throws Exception
    {
        Set<Double> doubleValues = Sets.newTreeSet();

        for ( String value : values )
        {
            try
            {
                doubleValues.add( Double.parseDouble( value ) );
            }
            catch ( NumberFormatException e )
            {
                LOG.warning( "Failed to map index value " + value + " for field " + indexFieldName +
                                 " to number as expected by index field definition, skipping" );
            }

        }

        if ( doubleValues.size() == 0 )
        {
            return;
        }

        doAddNumericSet( indexFieldName, doubleValues, builder );
    }

    public void translateAndAddDateSet( final String indexFieldName, final Set<String> values, final XContentBuilder builder )
        throws Exception
    {
        Set<Date> dateValues = ContentIndexDateSetBuilder.translateIndexValueSetToDates( indexFieldName, values );

        if ( dateValues.size() == 0 )
        {
            return;
        }

        builder.array( getNormalizedFieldName( indexFieldName ), dateValues.toArray( new Date[dateValues.size()] ) );
    }

    public void addNumericSet( final String indexFieldName, final Set<Double> values, final XContentBuilder builder )
        throws Exception
    {
        if ( values.size() == 0 )
        {
            return;
        }

        doAddNumericSet( indexFieldName, values, builder );
    }

    private void doAddNumericSet( final String indexFieldName, final Set<Double> values, final XContentBuilder builder )
        throws Exception
    {
        builder.array( getNormalizedFieldName( indexFieldName ), values.toArray( new Number[values.size()] ) );
    }
}
