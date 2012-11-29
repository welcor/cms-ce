package com.enonic.cms.core.portal.livetrace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;


public class Traces<T extends Trace>
    implements Iterable<T>
{
    private static final PeriodFormatter hoursMinutesMillis =
        new PeriodFormatterBuilder().appendHours().appendSuffix( " h ", " h " ).appendMinutes().appendSuffix( " m ",
                                                                                                              " m " ).appendSeconds().appendSuffix(
            " s ", " s " ).appendMillis().appendSuffix( " ms", " ms" ).toFormatter();

    private List<T> list = new ArrayList<T>();

    private int totalPeriodTimeInMilliseconds = 0;

    private Traces()
    {
        // protection
    }

    @Override
    public Iterator<T> iterator()
    {
        return list.iterator();
    }

    public void add( T trace )
    {
        trace.setContainer( this );
        list.add( trace );
        computeTotalPeriod();
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<T> getList()
    {
        return list;
    }

    public int getTotalPeriodInMilliseconds()
    {
        return totalPeriodTimeInMilliseconds;
    }

    public String getTotalPeriodInHRFormat()
    {
        return hoursMinutesMillis.print( new Period( totalPeriodTimeInMilliseconds ) );
    }

    void computeTotalPeriod()
    {
        int newTotalPeriodTimeInMilliseconds = 0;
        for ( Trace trace : list )
        {
            long asMilliseconds = trace.getDuration().getAsMilliseconds();
            newTotalPeriodTimeInMilliseconds += asMilliseconds;
        }

        totalPeriodTimeInMilliseconds = newTotalPeriodTimeInMilliseconds;
    }

    public static <T extends Trace> Traces<T> create()
    {
        return new Traces<T>();
    }
}
