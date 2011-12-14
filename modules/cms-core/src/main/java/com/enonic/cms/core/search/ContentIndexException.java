package com.enonic.cms.core.search;

import org.elasticsearch.ElasticSearchException;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 12/13/11
 * Time: 3:31 PM
 */
public class ContentIndexException extends RuntimeException {

    public ContentIndexException(String message, ElasticSearchException e) {
        super(message, e);
    }
}
