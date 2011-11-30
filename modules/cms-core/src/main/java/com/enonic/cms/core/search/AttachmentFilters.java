package com.enonic.cms.core.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/2/11
 * Time: 2:30 PM
 */
public class AttachmentFilters
{
    List<AttachmentFilter> filters = new ArrayList<AttachmentFilter>();

    public void addAttachmentFilter( AttachmentFilterType valueType, String value )
    {
        filters.add( new AttachmentFilter( valueType, value ) );
    }


    public List<AttachmentFilter> getFilters()
    {
        return filters;
    }
}
