package com.enonic.cms.core.search.result;

import java.util.Set;

import org.jdom.Element;
import org.joda.time.LocalDateTime;

import com.enonic.cms.core.CmsDateAndTimeFormats;

public class DateHistogramFacetResultSetXmlCreator
    extends AbstractFacetResultXmlCreator
{

    public Element create( DateHistogramFacetResultSet dateHistogramFacetResultSet )
    {
        final Element rangeFacetRootElement = createFacetRootElement( "date-histogram", dateHistogramFacetResultSet );

        final Set<DateHistogramFacetResultEntry> resultEntries = dateHistogramFacetResultSet.getResultEntries();

        for ( DateHistogramFacetResultEntry result : resultEntries )
        {
            Element resultEl = new Element( "interval" );
            addAttributeIfNotNull( resultEl, "sum", result.getTotal() );
            addAttributeIfNotNull( resultEl, "total-count", result.getTotalCount() );
            addAttributeIfNotNull( resultEl, "hits", result.getCount() );
            addAttributeIfNotNull( resultEl, "min", result.getMin() );
            addAttributeIfNotNull( resultEl, "mean", result.getMean() );
            addAttributeIfNotNull( resultEl, "max", result.getMax() );

            resultEl.addContent( getTimeAsFormattedString( result ) );

            rangeFacetRootElement.addContent( resultEl );
        }

        return rangeFacetRootElement;
    }

    private String getTimeAsFormattedString( final DateHistogramFacetResultEntry result )
    {
        LocalDateTime localDateTime = new LocalDateTime( result.getTime() );

        return CmsDateAndTimeFormats.printAs_XML_TIMESTAMP( localDateTime.toDate() );
    }

}
