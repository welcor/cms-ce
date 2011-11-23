package com.enonic.cms.core.search.builder;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/22/11
 * Time: 3:40 PM
 */
public class ContentIndexDataBuilderSpecification
{

    private boolean buildAttachments = false;

    private boolean buildCustomData = false;


    public ContentIndexDataBuilderSpecification( boolean buildAttachments, boolean buildCustomData )
    {
        this.buildAttachments = buildAttachments;
        this.buildCustomData = buildCustomData;
    }

    public boolean doBuildAttachments()
    {
        return buildAttachments;
    }

    public boolean doBuildCustomData()
    {
        return buildCustomData;
    }


    public static ContentIndexDataBuilderSpecification createBuildAllConfig()
    {
        return new ContentIndexDataBuilderSpecification( true, true );
    }
}
