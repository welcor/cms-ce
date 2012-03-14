package com.enonic.cms.core.search.querymeasurer.comparators;

import java.util.Comparator;

import com.enonic.cms.core.search.querymeasurer.IndexQueryMeasure;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/27/12
 * Time: 4:26 PM
 */
public class IndexQueryMeasureInvocationComparator
    implements Comparator<IndexQueryMeasure>
{

    @Override
    public int compare( IndexQueryMeasure im1, IndexQueryMeasure im2 )
    {

        if ( im1.getNumberOfInvocations() < im2.getNumberOfInvocations() )
        {
            return 1;
        }
        else if ( im1.getNumberOfInvocations() > im2.getNumberOfInvocations() )
        {
            return -1;
        }
        else
        {
            return im1.getQuerySignature().getQueryDisplayValue().compareTo( im2.getQuerySignature().getQueryDisplayValue() );
        }
    }

}