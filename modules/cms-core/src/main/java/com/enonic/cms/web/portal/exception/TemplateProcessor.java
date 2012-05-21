package com.enonic.cms.web.portal.exception;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Component
public final class TemplateProcessor
{
    private Configuration config;

    @Autowired
    public void setFreeMarkerConfigurer( final FreeMarkerConfigurer configurer )
    {
        this.config = configurer.getConfiguration();
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
