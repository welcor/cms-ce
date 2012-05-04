package com.enonic.cms.core.search;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/22/11
 * Time: 3:40 PM
 */
public final class ContentIndexDataBuilderSpecification
{
    private boolean buildAttachments = false;

    public ContentIndexDataBuilderSpecification( final boolean buildAttachments )
    {
        this.buildAttachments = buildAttachments;
    }

    public boolean doBuildAttachments()
    {
        return buildAttachments;
    }

}
