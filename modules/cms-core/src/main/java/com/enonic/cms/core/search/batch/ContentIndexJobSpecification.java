package com.enonic.cms.core.search.batch;

import com.enonic.cms.core.content.ContentSpecification;
import com.enonic.cms.core.search.ContentIndexDataBuilderSpecification;


/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/22/11
 * Time: 3:38 PM
 */
public class ContentIndexJobSpecification
{

    private final ContentSpecification contentSpecification;

    private final ContentIndexDataBuilderSpecification contentIndexDataBuilderSpecification;

    private boolean reset;

    public ContentIndexJobSpecification( ContentSpecification contentSpecification,
                                         ContentIndexDataBuilderSpecification contentIndexDataBuilderSpecification )
    {
        this.contentSpecification = contentSpecification;
        this.contentIndexDataBuilderSpecification = contentIndexDataBuilderSpecification;
    }

    public ContentSpecification getContentSpecification()
    {
        return contentSpecification;
    }

    public ContentIndexDataBuilderSpecification getContentIndexDataBuilderSpecification()
    {
        return contentIndexDataBuilderSpecification;
    }

    public static ContentIndexJobSpecification createAllContentSpecification()
    {
        ContentSpecification contentSpec = new ContentSpecification();
        contentSpec.setIncludeDeleted( false );

        ContentIndexDataBuilderSpecification indexDataBuilderSpec = new ContentIndexDataBuilderSpecification( true, true );

        ContentIndexJobSpecification spec = new ContentIndexJobSpecification( contentSpec, indexDataBuilderSpec );

        return spec;
    }

}

