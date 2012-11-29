package com.enonic.cms.core.portal.datasource.handler.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.jdom.Document;
import org.jdom.Element;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.portal.datasource.handler.DataSourceRequest;
import com.enonic.cms.core.portal.datasource.handler.base.ParamsDataSourceHandler;
import com.enonic.cms.core.time.TimeService;

@Component("ds.GetFormattedDateHandler")
public final class GetFormattedDateHandler
    extends ParamsDataSourceHandler<GetFormattedDateParams>
{
    private TimeService timeService;

    public GetFormattedDateHandler()
    {
        super( "getFormattedDate", GetFormattedDateParams.class );
    }

    @Override
    protected Document handle( final DataSourceRequest req, final GetFormattedDateParams params )
        throws Exception
    {
        final int offset = params.offset;
        final String dateFormat = params.dateFormat;
        final String language = params.language;
        final String country = params.country;

        final Locale locale = new Locale( language, country );
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat( dateFormat, locale );

        final DateTime now = this.timeService.getNowAsDateTime();
        return getFormattedDate( now.toCalendar( locale ), offset, simpleDateFormat );
    }

    @Autowired
    public void setTimeService( final TimeService timeService )
    {
        this.timeService = timeService;
    }

    private Document getFormattedDate( final Calendar today, final int offset, final SimpleDateFormat sdf )
    {
        if ( offset != 0 )
        {
            today.add( Calendar.DATE, offset );
        }

        final Element root = new Element( "formatteddate" );
        root.addContent( new Element( "datetimestring" ).setText( sdf.format( today.getTime() ) ) );
        root.addContent( new Element( "day" ).setText( Integer.toString( today.get( Calendar.DATE ) ) ) );
        root.addContent( new Element( "month" ).setText( Integer.toString( today.get( Calendar.MONTH ) ) ) );
        root.addContent( new Element( "monthofyear" ).setText(
            Integer.toString( today.get( Calendar.MONTH ) + ( today.getActualMinimum( Calendar.MONTH ) == 0 ? 1 : 0 ) ) ) );
        root.addContent( new Element( "year" ).setText( Integer.toString( today.get( Calendar.YEAR ) ) ) );
        root.addContent( new Element( "hour" ).setText( Integer.toString( today.get( Calendar.HOUR_OF_DAY ) ) ) );
        root.addContent( new Element( "minute" ).setText( Integer.toString( today.get( Calendar.MINUTE ) ) ) );
        root.addContent( new Element( "second" ).setText( Integer.toString( today.get( Calendar.SECOND ) ) ) );
        sdf.applyPattern( "EEEE" );
        root.addContent( new Element( "weekday" ).setText( sdf.format( today.getTime() ) ) );
        return new Document( root );
    }
}
