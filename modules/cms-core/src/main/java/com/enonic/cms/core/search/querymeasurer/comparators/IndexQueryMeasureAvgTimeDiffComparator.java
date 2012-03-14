package com.enonic.cms.core.search.querymeasurer.comparators;

import java.util.Comparator;

import com.enonic.cms.core.search.querymeasurer.IndexQueryMeasure;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/27/12
 * Time: 5:11 PM
 */
public class IndexQueryMeasureAvgTimeDiffComparator
    implements Comparator<IndexQueryMeasure>
{

    @Override
    public int compare( IndexQueryMeasure im1, IndexQueryMeasure im2 )
    {

        final int im1avgTimeDiff = im1.getAvgTimeDiff();
        final int im2avgTimeDiff = im2.getAvgTimeDiff();

        if ( im1avgTimeDiff < im2avgTimeDiff )
        {
            return 1;
        }
        else if ( im1avgTimeDiff > im2avgTimeDiff )
        {
            return -1;
        }
        else
        {
            return im1.getQuerySignature().getQueryDisplayValue().compareTo( im2.getQuerySignature().getQueryDisplayValue() );
        }
    }

}
