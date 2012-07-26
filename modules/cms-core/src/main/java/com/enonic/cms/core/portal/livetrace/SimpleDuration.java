package com.enonic.cms.core.portal.livetrace;


import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class SimpleDuration
{
    protected static final PeriodFormatter HOURS_MINUTES_MILLIS =
        new PeriodFormatterBuilder().appendHours().appendSuffix( " h ", " h " ).appendMinutes().appendSuffix( " m ",
                                                                                                              " m " ).appendSeconds().appendSuffix(
            " s ", " s " ).appendMillis().appendSuffix( " ms", " ms" ).toFormatter();


    private long durationInMilliseconds;

    private String durationAsHRFormat;


    public long getAsMilliseconds()
    {
        return durationInMilliseconds;
    }

    void setDurationInMilliseconds( long durationInMilliseconds )
    {
        this.durationInMilliseconds = durationInMilliseconds;
        this.durationAsHRFormat = HOURS_MINUTES_MILLIS.print( new Period( durationInMilliseconds ) ).trim();
    }

    public String getAsHRFormat()
    {
        return durationAsHRFormat;
    }
}