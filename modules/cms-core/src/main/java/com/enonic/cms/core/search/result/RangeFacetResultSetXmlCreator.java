package com.enonic.cms.core.search.result;


import java.util.Set;

import org.jdom.Element;
import org.joda.time.DateTime;

import com.enonic.cms.core.CmsDateAndTimeFormats;
import com.enonic.cms.core.search.ElasticSearchFormatter;

public class RangeFacetResultSetXmlCreator
    extends AbstractFacetResultXmlCreator
{
    public Element createRangeFacetElement( RangeFacetResultSet rangeFacet )
    {
        final Element rangeFacetRootElement = createFacetRootElement( "ranges", rangeFacet );

        final Set<RangeFacetResultEntry> resultEntries = rangeFacet.getResultEntries();

        for ( RangeFacetResultEntry result : resultEntries )
        {
            Element resultEl = new Element( "result" );
            addAttributeIfNotNull( resultEl, "count", result.getCount() );
            addAttributeIfNotNull( resultEl, "from", getFacetRangeValueAsFormattedString( result.getFrom() ) );
            addAttributeIfNotNull( resultEl, "to", getFacetRangeValueAsFormattedString( result.getTo() ) );
            addAttributeIfNotNull( resultEl, "min", result.getMin() );
            addAttributeIfNotNull( resultEl, "mean", result.getMean() );
            addAttributeIfNotNull( resultEl, "max", result.getMax() );

            rangeFacetRootElement.addContent( resultEl );
        }

        return rangeFacetRootElement;
    }

    private String getFacetRangeValueAsFormattedString( final String valueString )
    {
        if ( valueString == null )
        {
            return null;
        }

        if ( isNumber( valueString ) )
        {
            return valueString;
        }

        final DateTime parsedDateTime = ElasticSearchFormatter.parseStringAsElasticsearchDateOptionalTimeFormat( valueString );

        if ( parsedDateTime != null )
        {
            return CmsDateAndTimeFormats.printAs_XML_TIMESTAMP( parsedDateTime.toLocalDateTime().toDate() );
        }

        return null;
    }

    private boolean isNumber( String value )
    {
        try
        {
            new Double( value );
            return true;
        }
        catch ( NumberFormatException e )
        {
            // do Nothing
            return false;
        }
    }
}
