package com.enonic.cms.core.search.facet.model;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;

import com.enonic.cms.core.search.ElasticSearchFormatter;

public class FacetRangeDateValue
    extends FacetRangeValue
{
    Date value;

    public FacetRangeDateValue( final Date value )
    {
        this.value = value;
    }

    @Override
    public String getStringValue()
    {
        DateTime dateMidnight = new DateTime( value );

        ReadableDateTime UTCTime = ElasticSearchFormatter.toUTCTimeZone( dateMidnight );

        return value != null ? ElasticSearchFormatter.formatDateAsStringFullWithTimezone( UTCTime.toDateTime().toDate() ) : null;
    }

    public FacetRangeDateValue()
    {
    }
}
