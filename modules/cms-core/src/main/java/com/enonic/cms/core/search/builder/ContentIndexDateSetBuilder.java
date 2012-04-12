package com.enonic.cms.core.search.builder;

import java.text.ParseException;
import java.util.Date;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang.time.DateUtils;

import com.google.common.collect.Sets;

public class ContentIndexDateSetBuilder
{
    private final static Logger LOG = Logger.getLogger( ContentIndexDateSetBuilder.class.getName() );

    //private static String dateFormat = "dd.MM.yyyy";

    //private static String dateFormatWithTime = "dd.MM.yyyy HH:mm";

    //private static String dateFormatWithTimeSeconds = "dd.MM.yyyy HH:mm:ss";

    private static String isoDateFormatNoTime = "yyyy-MM-dd";

    private static String isoDateFormatWithTime = "yyyy-MM-dd HH:mm";

    private static String[] validDatePatterns = new String[]{isoDateFormatNoTime, isoDateFormatWithTime};

    public static Set<Date> translateIndexValueSetToDates( final String indexFieldName, final Set<String> values )
    {
        Set<Date> dateValues = Sets.newTreeSet();

        for ( String value : values )
        {
            try
            {
                dateValues.add( DateUtils.parseDate( value, validDatePatterns ) );
            }
            catch ( ParseException e )
            {
                LOG.warning( "Failed to map index value " + value + " for field " + indexFieldName +
                                 " to date as expected by index field definition, skipping" );
            }
        }

        return dateValues;

    }

}
