package com.enonic.cms.core.portal.datasource2.handler.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.portal.datasource2.handler.DataSourceHandler;
import com.enonic.cms.core.portal.datasource2.handler.DataSourceRequest;
import com.enonic.cms.core.time.TimeService;

public final class GetFormattedDateHandler
    extends DataSourceHandler
{
    private TimeService timeService;

    public GetFormattedDateHandler()
    {
        super( "getFormattedDate" );
    }

    @Override
    public Document handle( final DataSourceRequest req )
        throws Exception
    {
        final int offset = req.param( "offset" ).asInteger( 0 );
        final String dateFormat = req.param( "dateFormat" ).asString( "EEEE d. MMMM yyyy" );
        final String language = req.param( "language" ).required().asString();
        final String country = req.param( "country" ).required().asString();

        final Locale locale = new Locale( language, country );
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat( dateFormat, locale );

        return getFormattedDate( offset, simpleDateFormat );
    }

    private Document getFormattedDate( final int dayOffset, final SimpleDateFormat dateFormat )
    {
        final long now = this.timeService.getNowAsMilliseconds();

        final Calendar date = Calendar.getInstance();
        date.setTimeInMillis( now );

        if ( dayOffset != 0 )
        {
            date.add( Calendar.DATE, dayOffset );
        }

        final Element root = new Element( "formatteddate" );
        root.addContent( new Element( "datetimestring" ).setText( dateFormat.format( date.getTime() ) ) );
        root.addContent( new Element( "day" ).setText( Integer.toString( date.get( Calendar.DATE ) ) ) );
        root.addContent( new Element( "month" ).setText( Integer.toString( date.get( Calendar.MONTH ) ) ) );
        root.addContent( new Element( "monthofyear" ).setText(
            Integer.toString( date.get( Calendar.MONTH ) + ( date.getActualMinimum( Calendar.MONTH ) == 0 ? 1 : 0 ) ) ) );
        root.addContent( new Element( "year" ).setText( Integer.toString( date.get( Calendar.YEAR ) ) ) );
        root.addContent( new Element( "hour" ).setText( Integer.toString( date.get( Calendar.HOUR_OF_DAY ) ) ) );
        root.addContent( new Element( "minute" ).setText( Integer.toString( date.get( Calendar.MINUTE ) ) ) );
        root.addContent( new Element( "second" ).setText( Integer.toString( date.get( Calendar.SECOND ) ) ) );

        dateFormat.applyPattern( "EEEE" );
        root.addContent( new Element( "weekday" ).setText( dateFormat.format( date.getTime() ) ) );

        return new Document( root );
    }

    @Autowired
    public void setTimeService( final TimeService timeService )
    {
        this.timeService = timeService;
    }
}
