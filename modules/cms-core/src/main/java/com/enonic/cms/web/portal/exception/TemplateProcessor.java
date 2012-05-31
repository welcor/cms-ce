package com.enonic.cms.web.portal.exception;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.springframework.stereotype.Component;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Component
public final class TemplateProcessor
{
    private final Configuration config;

    public TemplateProcessor()
    {
        this.config = new Configuration();
        this.config.setDefaultEncoding( "UTF-8" );
        this.config.setClassForTemplateLoading( getClass(), "" );
    }

    public String process( final String name, final Map<String, Object> model )
        throws IOException
    {
        final Template template = this.config.getTemplate( name );

        try
        {
            final StringWriter writer = new StringWriter();
            template.process( model, writer );
            return writer.toString();
        }
        catch ( final TemplateException e )
        {
            throw new IOException( e );
        }
    }
}
