package com.enonic.cms.core.search.querymeasurer.comparators;

import java.util.Comparator;

import com.enonic.cms.core.search.querymeasurer.IndexQueryMeasure;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 3/1/12
 * Time: 1:55 PM
 */
public class IndexQueryMeasurerMaxTimeComparator
    implements Comparator<IndexQueryMeasure>
{

    @Override
    public int compare( IndexQueryMeasure im1, IndexQueryMeasure im2 )
    {

        final long im1avgTime = im1.getCurrentMaxTime();
        final long im2avgTime = im2.getCurrentMaxTime();

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
