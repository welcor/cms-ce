package com.enonic.cms.core.structure.page.template;

public class UpdatePageTemplateCommand
{
    private String xmlData;

    public UpdatePageTemplateCommand( final String xmlData )
    {
        this.xmlData = xmlData;
    }

    public String getXmlData()
    {
        return xmlData;
    }
}
