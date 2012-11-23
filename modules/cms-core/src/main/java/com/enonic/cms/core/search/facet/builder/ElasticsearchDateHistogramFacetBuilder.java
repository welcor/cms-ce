package com.enonic.cms.core.search.facet.builder;

import org.elasticsearch.search.facet.datehistogram.DateHistogramFacetBuilder;
import org.joda.time.DateTimeZone;

import com.google.common.base.Strings;

import com.enonic.cms.core.search.facet.FacetQueryException;
import com.enonic.cms.core.search.facet.model.DateHistogramFacetModel;

public class ElasticsearchDateHistogramFacetBuilder
    extends AbstractElasticsearchFacetBuilder
{
    final DateHistogramFacetBuilder build( DateHistogramFacetModel dateHistogramFacetModel )
    {
        try
        {
            dateHistogramFacetModel.validate();
        }
        catch ( Exception e )
        {
            throw new FacetQueryException( "Error in date histogram-facet definition", e );
        }

        DateHistogramFacetBuilder builder = new DateHistogramFacetBuilder( dateHistogramFacetModel.getName() );

        setField( dateHistogramFacetModel, builder );

        setInterval( dateHistogramFacetModel, builder );

        setTimeZoneSettings( dateHistogramFacetModel, builder );

        return builder;
    }

    private void setTimeZoneSettings( final DateHistogramFacetModel model, final DateHistogramFacetBuilder builder )
    {
        if ( Strings.isNullOrEmpty( model.getPreZone() ) )
        {
            builder.preZone( DateTimeZone.getDefault().getID() );
        }
        else
        {
            builder.preZone( model.getPreZone() );
        }

        if ( !Strings.isNullOrEmpty( model.getPostZone() ) )
        {
            builder.postZone( model.getPostZone() );
        }

        builder.preZoneAdjustLargeInterval( true );
    }


    private void setInterval( final DateHistogramFacetModel dateHistogramFacetModel, final DateHistogramFacetBuilder builder )
    {
        builder.interval( dateHistogramFacetModel.getInterval() );
    }


    protected void setField( final DateHistogramFacetModel histogramFacetModel, final DateHistogramFacetBuilder builder )
    {
        final String fieldName = histogramFacetModel.getIndex();

        if ( !Strings.isNullOrEmpty( fieldName ) )
        {
            builder.field( getDateFieldName( fieldName ) );

        }
        else if ( !Strings.isNullOrEmpty( histogramFacetModel.getKeyField() ) &&
            !Strings.isNullOrEmpty( histogramFacetModel.getValueField() ) )
        {
            builder.keyField( getDateFieldName( histogramFacetModel.getKeyField() ) );
            builder.valueField( getNumericFieldName( histogramFacetModel.getValueField() ) );
        }
    }

}
