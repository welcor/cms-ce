/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model.content;

public interface StringValueInput
    extends Input
{

    public String getValueAsString();

    public int getLength();
}
