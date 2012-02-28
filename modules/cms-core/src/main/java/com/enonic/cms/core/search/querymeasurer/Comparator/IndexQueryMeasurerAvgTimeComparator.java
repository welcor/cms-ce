package com.enonic.cms.core.search.querymeasurer.comparator;

import java.util.Comparator;

import com.enonic.cms.core.search.querymeasurer.IndexQueryMeasure;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 2/28/12
 * Time: 12:04 PM
 */
public class IndexQueryMeasurerAvgTimeComparator
    implements Comparator<IndexQueryMeasure>
{

    @Override
    public int compare( IndexQueryMeasure im1, IndexQueryMeasure im2 )
    {

        final int im1avgTime = im1.getHighestAvgTime();
        final int im2avgTime = im2.getHighestAvgTime();

        if ( im1avgTime < im2avgTime )
        {
            return 1;
        }
        else if ( im1avgTime > im2avgTime )
        {
            return -1;
        }
        else
        {
            return im1.getQuerySignature().getQueryDisplayValue().compareTo( im2.getQuerySignature().getQueryDisplayValue() );
        }
    }

}
