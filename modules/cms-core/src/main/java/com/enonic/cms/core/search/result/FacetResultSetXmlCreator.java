/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.result;

import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;

public class FacetResultSetXmlCreator
{
    private final RangeFacetResultSetXmlCreator rangeFacetResultSetXmlCreator = new RangeFacetResultSetXmlCreator();

    private final TermFacetResultSetXmlCreator termFacetResultSetXmlCreator = new TermFacetResultSetXmlCreator();

    private final HistogramFacetResultSetXmlCreator histogramFacetResultSetXmlCreator = new HistogramFacetResultSetXmlCreator();

    private final DateHistogramFacetResultSetXmlCreator dateHistogramFacetResultSetXmlCreator = new DateHistogramFacetResultSetXmlCreator();

    private final TermsStatsFacetResultSetXmlCreator termsStatsFacetResultSetXmlCreator = new TermsStatsFacetResultSetXmlCreator();

    public void addFacetResultXml( Document doc, FacetsResultSet facetsResultSet )
    {
        Element root = doc.getRootElement();

        Element facetsNode = new Element( "facets" );

        root.addContent( facetsNode );

        final Iterator<FacetResultSet> iterator = facetsResultSet.iterator();

        while ( iterator.hasNext() )
        {
            final FacetResultSet facetResultSet = iterator.next();

            final FacetResultType facetResultType = facetResultSet.getFacetResultType();

            switch ( facetResultType )
            {
                case TERMS:
                    addTermFacetResult( facetsNode, (TermsFacetResultSet) facetResultSet );
                    break;
                case RANGE:
                    addRangeFacetResult( facetsNode, (RangeFacetResultSet) facetResultSet );
                    break;
                case HISTOGRAM:
                    addHistogramFacetResult( facetsNode, (HistogramFacetResultSet) facetResultSet );
                    break;
                case DATE_HISTOGRAM:
                    addDateHistogramFacetResult( facetsNode, (DateHistogramFacetResultSet) facetResultSet );
                    break;
                case TERMS_STATS:
                    addTermsStatsFacetResult( facetsNode, (TermsStatsFacetResultSet) facetResultSet );
                    break;
            }
        }
    }

    private void addTermsStatsFacetResult( final Element facetsRoot, final TermsStatsFacetResultSet facetResultSet )
    {
        facetsRoot.addContent( termsStatsFacetResultSetXmlCreator.create( facetResultSet ) );
    }

    private void addDateHistogramFacetResult( final Element facetsRoot, final DateHistogramFacetResultSet facet )
    {
        facetsRoot.addContent( dateHistogramFacetResultSetXmlCreator.create( facet ) );
    }

    private void addHistogramFacetResult( final Element facetsRoot, final HistogramFacetResultSet facet )
    {
        facetsRoot.addContent( histogramFacetResultSetXmlCreator.create( facet ) );
    }

    private void addRangeFacetResult( final Element facetsRoot, final RangeFacetResultSet facet )
    {
        facetsRoot.addContent( rangeFacetResultSetXmlCreator.create( facet ) );
    }

    private void addTermFacetResult( final Element facetsRoot, final TermsFacetResultSet facet )
    {
        facetsRoot.addContent( termFacetResultSetXmlCreator.create( facet ) );
    }
}
