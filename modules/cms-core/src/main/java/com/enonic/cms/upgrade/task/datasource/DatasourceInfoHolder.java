package com.enonic.cms.upgrade.task.datasource;

public class DatasourceInfoHolder
{
    private String xml;

    private String objectName;

    private String site;

    public String getXml()
    {
        return xml;
    }

    public void setXml( final String xml )
    {
        this.xml = xml;
    }

    public String getObjectName()
    {
        return objectName;
    }

    public void setObjectName( final String objectName )
    {
        this.objectName = objectName;
    }

    public String getSite()
    {
        return site;
    }

    public void setSite( final String site )
    {
        this.site = site;
    }

    public String getContextString()
    {
        return ( "'" + this.site  + ":" + this.objectName + "'" );
    }

}

