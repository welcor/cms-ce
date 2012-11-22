package com.enonic.cms.core.search.facet.builder;

import org.elasticsearch.search.facet.datehistogram.DateHistogramFacetBuilder;
import org.joda.time.DateTimeZone;

import com.google.common.base.Strings;

import com.enonic.cms.core.search.facet.FacetQueryException;
import com.enonic.cms.core.search.facet.model.DateHistogramFacetModel;
import com.enonic.cms.core.search.query.QueryField;

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

        builder.preZone( DateTimeZone.getDefault().getID() );

        builder.preZoneAdjustLargeInterval( true );

        return builder;
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
            builder.valueField( getDateFieldName( histogramFacetModel.getValueField() ) );
        }
    }

    private String getDateFieldName( String fieldName )
    {
        QueryField queryField = new QueryField( createQueryFieldName( fieldName ) );
        return queryField.getFieldNameForDateQueries();
    }
}
