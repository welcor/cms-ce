package com.enonic.cms.core.search.builder;

import java.util.Collection;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public abstract class ContentIndexDataTestBase
{
    void verifyElementExistsAndNumberOfValues( Set<ContentIndexDataElement> contentDataElements, String name, int expectedValueElements )
    {
        for ( ContentIndexDataElement contentDataElement : contentDataElements )
        {
            final Set<ContentIndexDataFieldValue> allFieldValuesForElement = contentDataElement.getAllFieldValuesForElement();
            for ( ContentIndexDataFieldValue contentIndexDataFieldValue : allFieldValuesForElement )
            {
                if ( name.equals( contentIndexDataFieldValue.getFieldName() ) )
                {
                    final Object value = contentIndexDataFieldValue.getValue();

                    if ( expectedValueElements > 1 )
                    {
                        assertTrue( value instanceof Collection );
                        Collection collection = (Collection) value;
                        assertEquals( expectedValueElements, collection.size() );
                    }
                    else
                    {
                        if ( value instanceof Collection )
                        {
                            assertEquals( 1, ( (Collection) value ).size() );
                        }
                    }

                    return;
                }
            }
        }

        fail( "Did not find Element with name: " + name );
    }

}
