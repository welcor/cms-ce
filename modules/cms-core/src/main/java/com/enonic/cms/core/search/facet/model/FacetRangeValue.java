/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.search.facet.model;

import javax.xml.bind.annotation.XmlAttribute;

public abstract class FacetRangeValue
{
    @XmlAttribute(name = "value")
    public abstract String getStringValue();
}
