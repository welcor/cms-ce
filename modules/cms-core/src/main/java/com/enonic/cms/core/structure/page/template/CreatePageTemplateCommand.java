package com.enonic.cms.core.structure.page.template;

public class CreatePageTemplateCommand
{
    private String xmlData;

    public CreatePageTemplateCommand( final String xmlData )
    {
        this.xmlData = xmlData;
    }

    public String getXmlData()
    {
        return xmlData;
    }
}
