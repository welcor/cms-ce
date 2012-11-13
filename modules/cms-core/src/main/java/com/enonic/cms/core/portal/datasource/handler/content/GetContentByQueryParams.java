package com.enonic.cms.core.portal.datasource.handler.content;

public final class GetContentByQueryParams
{
    public String query = "";

    public String orderBy = "";

    public int index = 0;

    public int count = 10;

    public boolean includeData = true;

    public int childrenLevel = 1;

    public int parentLevel = 0;

    public String facetsDefinition;
}
