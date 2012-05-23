package com.enonic.cms.core.xslt.localizer;

import java.util.Map;

import com.google.common.collect.Maps;

import net.sf.saxon.expr.number.Numberer_en;
import net.sf.saxon.lib.LocalizerFactory;
import net.sf.saxon.lib.Numberer;

public final class LocalizerFactoryImpl
    extends LocalizerFactory
{
    private final Map<String, Numberer> map;

    private final Numberer defaultNumberer;

    public LocalizerFactoryImpl()
    {
        this.map = Maps.newHashMap();
        this.defaultNumberer = new Numberer_en();

        register( "no", new Numberer_no() );
        register( "cn", new Numberer_cn() );
        register( "ru", new Numberer_ru() );
        register( "de", new Numberer_de() );
    }

    private void register( final String language, final Numberer numberer )
    {
        this.map.put( language.toLowerCase(), numberer );
    }

    @Override
    public Numberer getNumberer( final String language, final String country )
    {
        final Numberer numberer = this.map.get( language.toLowerCase() );
        if ( numberer != null )
        {
            return numberer;
        }

        return this.defaultNumberer;
    }
}
