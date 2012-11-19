package com.enonic.cms.core.search.result;

import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;

public class FacetResultSetXmlCreator
{
    private final RangeFacetResultSetXmlCreator rangeFacetResultSetXmlCreator = new RangeFacetResultSetXmlCreator();

    private final TermFacetResultSetXmlCreator termFacetResultSetXmlCreator = new TermFacetResultSetXmlCreator();

    public void addFacetResultXml( Document doc, FacetsResultSet facetsResultSet )
    {
        Element root = doc.getRootElement();

        Element facetsNode = new Element( "facets" );

        root.addContent( facetsNode );

        final Iterator<FacetResultSet> iterator = facetsResultSet.iterator();

        while ( iterator.hasNext() )
        {
            final FacetResultSet facet = iterator.next();

            final FacetResultType facetResultType = facet.getFacetResultType();

            switch ( facetResultType )
            {
                case TERMS:
                    addTermFacetResult( facetsNode, (TermsFacetResultSet) facet );
                    break;
                case RANGE:
                    addRangeFacetResult( facetsNode, (RangeFacetResultSet) facet );
                    break;
            }
        }
    }

    private void addRangeFacetResult( final Element facetsRoot, final RangeFacetResultSet facet )
    {
        facetsRoot.addContent( rangeFacetResultSetXmlCreator.createRangeFacetElement( facet ) );
    }

    private void addTermFacetResult( final Element facetsRoot, final TermsFacetResultSet facet )
    {
        facetsRoot.addContent( termFacetResultSetXmlCreator.createTermFacetElement( facet ) );
    }
}
