package com.enonic.cms.core.structure.page.template;

public interface PageTemplateService
{
    void deletePageTemplate( final DeletePageTemplateCommand command );

    void copyPageTemplate( final CopyPageTemplateCommand command );

    void createPageTemplate( final CreatePageTemplateCommand command );

    void updatePageTemplate( final UpdatePageTemplateCommand command );
}
