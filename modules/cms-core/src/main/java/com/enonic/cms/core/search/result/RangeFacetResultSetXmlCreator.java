package com.enonic.cms.core.search.result;


import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.joda.time.DateTime;

import com.enonic.cms.core.CmsDateAndTimeFormats;
import com.enonic.cms.core.search.ElasticSearchFormatter;

public class RangeFacetResultSetXmlCreator
    extends AbstractFacetResultXmlCreator
{

    public Element createRangeFacetElement( RangeFacetResultSet rangeFacet )
    {
        final Element rangeFacetRootElement = createFacetRootElement( rangeFacet );

        final Set<RangeFacetResultEntry> resultEntries = rangeFacet.getResultEntries();

        for ( RangeFacetResultEntry result : resultEntries )
        {
            Element resultEl = new Element( "result" );
            addAttributeIfNotNull( resultEl, "from", getFacetRangeValueAsFormattedString( result.getFrom() ) );
            addAttributeIfNotNull( resultEl, "to", getFacetRangeValueAsFormattedString( result.getTo() ) );
            Element countEl = new Element( "count" );
            final Long count = result.getCount();
            countEl.addContent( "" + count );

            resultEl.addContent( countEl );

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

        if ( StringUtils.isNumeric( valueString ) )
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

}
