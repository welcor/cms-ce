/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver.locale;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.resolver.AbstractXsltScriptResolver;
import com.enonic.cms.core.resolver.ScriptResolverResult;

@Component
public class LocaleXsltScriptResolver
    extends AbstractXsltScriptResolver
{
    private static final Logger LOG = LoggerFactory.getLogger( LocaleXsltScriptResolver.class );

    public final static String LOCALE_RETURN_VALUE_KEY = "locale";

    protected ScriptResolverResult populateScriptResolverResult( String resolvedValue )
    {
        ScriptResolverResult result = new ScriptResolverResult();

        if ( StringUtils.isNotEmpty( resolvedValue ) )
        {
            Locale locale = null;
            try
            {
                locale = LocaleParser.parseLocale( resolvedValue );
            }
            catch ( Exception e )
            {
                LOG.warn( "Could not parse script-result: '" + resolvedValue + "' to a valid locale" );
            }

            result.getResolverReturnValues().put( LOCALE_RETURN_VALUE_KEY, locale );
        }

        return result;
    }
}
