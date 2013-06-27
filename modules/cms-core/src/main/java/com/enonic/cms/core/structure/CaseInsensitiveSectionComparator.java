/*
 * Copyright 2000-2013 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.core.structure;

import java.util.Comparator;

class CaseInsensitiveSectionComparator
    implements Comparator<Section>
{
    public int compare( Section s1, Section s2 )
    {
        return s1.compareTo( s2 );
    }
}
