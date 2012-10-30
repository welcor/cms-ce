package com.enonic.cms.upgrade.task.datasource;

import org.jdom.Element;

public interface DataSourceConverter
{
    public void setCurrentContext(String context);

    public Element convert( Element root )
        throws Exception;
}
