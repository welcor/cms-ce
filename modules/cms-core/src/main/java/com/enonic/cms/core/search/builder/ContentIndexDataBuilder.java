package com.enonic.cms.core.search.builder;

import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.search.ContentIndexDataBuilderSpecification;
import com.enonic.cms.core.search.index.ContentIndexData;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 11/22/11
 * Time: 1:49 PM
 */
public interface ContentIndexDataBuilder
{

    public ContentIndexData build( ContentDocument content, ContentIndexDataBuilderSpecification spec );

}
