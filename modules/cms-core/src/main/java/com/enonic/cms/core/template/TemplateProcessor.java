package com.enonic.cms.core.template;

import java.io.IOException;
import java.util.Map;

public interface TemplateProcessor
{
    public String process( String name, Map<String, Object> model )
        throws IOException;

    public String process( Class context, String name, Map<String, Object> model )
        throws IOException;
}
