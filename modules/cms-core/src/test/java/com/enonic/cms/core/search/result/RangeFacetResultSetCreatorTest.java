package com.enonic.cms.core.search.result;

import java.util.List;

import org.elasticsearch.search.facet.range.RangeFacet;
import org.junit.Test;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RangeFacetResultSetCreatorTest
{
    private RangeFacetResultSetCreator rangeFacetResultSetCreator = new RangeFacetResultSetCreator();

    @Test
    public void assert_no_min_mean_max_on_daterange()
    {
        RangeFacet rangeFacet = mock( RangeFacet.class );
        RangeFacet.Entry entry = mock( RangeFacet.Entry.class );
        List<RangeFacet.Entry> entries = Lists.newArrayList( entry );

        when( rangeFacet.getEntries() ).thenReturn( entries );

        when( entry.getFromAsString() ).thenReturn( "-Infinity" );
        when( entry.getToAsString() ).thenReturn( "2001-01-02" );
        when( entry.getMax() ).thenReturn( (double) Double.POSITIVE_INFINITY );
        when( entry.getMin() ).thenReturn( new Double( -123456 ) );
        when( entry.getMean() ).thenReturn( new Double( -123456 ) );

        final FacetResultSet facetResultSet = rangeFacetResultSetCreator.create( "myRangeFacet", rangeFacet );
        assertTrue( facetResultSet instanceof RangeFacetResultSet );
        assertEquals( "myRangeFacet", facetResultSet.getName() );

        final RangeFacetResultSet rangeFacetResultSet = (RangeFacetResultSet) facetResultSet;
        final RangeFacetResultEntry next = rangeFacetResultSet.getResultEntries().iterator().next();
        assertNotNull( next );
        assertTrue( next.getFrom() == null );
        assertNotNull( next.getTo() );
        assertTrue( next.getMax() == null );
        assertTrue( next.getMin() == null );
        assertTrue( next.getMean() == null );
    }
}
