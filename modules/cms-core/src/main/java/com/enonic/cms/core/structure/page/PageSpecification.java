/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.page;

import com.enonic.cms.core.structure.page.template.PageTemplateSpecification;

/**
 * Oct 1, 2009
 */
public class PageSpecification
{
    private PageTemplateSpecification templateSpecification;

    public PageTemplateSpecification getTemplateSpecification()
    {
        return templateSpecification;
    }

    public void setTemplateSpecification( PageTemplateSpecification templateSpecification )
    {
        this.templateSpecification = templateSpecification;
    }
}
