package com.enonic.cms.core.portal.datasource2.handler.content;

import org.junit.Test;
import org.mockito.Mockito;
import org.w3c.dom.Document;

import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.PresentationEngine;

import com.enonic.cms.core.portal.datasource2.DataSourceException;
import com.enonic.cms.core.portal.datasource2.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.security.user.UserEntity;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

public class GetCategoriesHandlerTest
    extends AbstractDataSourceHandlerTest<GetCategoriesHandler>
{

    private PresentationEngine presentationEngine;

    private static final String PRESENTATION_ENGINE_GET_CATEGORIES_RESPONSE = "<categories count=\"1\" totalcount=\"1\">\n" +
        "<category created=\"2009-10-07 12:42\" key=\"11\" timestamp=\"2010-06-01 12:16\">\n" +
        "  <owner key=\"F5C828FF122CD8D0509051584236CCEB28C78BFA\" uid=\"admin\">Enterprise Administrator</owner>\n" +
        "  <modifier key=\"F5C828FF122CD8D0509051584236CCEB28C78BFA\" uid=\"admin\">Enterprise Administrator</modifier>\n" +
        "  <title>Public content</title>\n" +
        "  <categories count=\"2\" totalcount=\"8\">\n" +
        "    <category contenttypekey=\"1002\" created=\"2009-10-07 13:00\" key=\"12\" superkey=\"11\" timestamp=\"2009-10-07 13:00\">\n" +
        "      <owner key=\"F5C828FF122CD8D0509051584236CCEB28C78BFA\" uid=\"admin\">Enterprise Administrator</owner>\n" +
        "      <modifier key=\"F5C828FF122CD8D0509051584236CCEB28C78BFA\" uid=\"admin\">Enterprise Administrator</modifier>\n" +
        "      <title>Files</title>\n" +
        "      <categories count=\"0\" totalcount=\"0\" />\n" +
        "    </category>\n" +
        "    <category contenttypekey=\"1005\" created=\"2009-10-07 13:05\" key=\"19\" superkey=\"11\" timestamp=\"2009-10-07 13:05\">\n" +
        "      <owner key=\"F5C828FF122CD8D0509051584236CCEB28C78BFA\" uid=\"admin\">Enterprise Administrator</owner>\n" +
        "      <modifier key=\"F5C828FF122CD8D0509051584236CCEB28C78BFA\" uid=\"admin\">Enterprise Administrator</modifier>\n" +
        "      <title>Events</title>\n" +
        "      <categories count=\"0\" totalcount=\"0\" />\n" +
        "    </category>\n" +
        "  </categories>\n" +
        "</category>\n" +
        "</categories>";

    public GetCategoriesHandlerTest()
    {
        super( GetCategoriesHandler.class );
    }

    @Override
    protected void initTest()
        throws Exception
    {
        presentationEngine = Mockito.mock( PresentationEngine.class );
        handler.setPresentationEngine( presentationEngine );
    }

    @Test
    public void testHandler_categories()
        throws Exception
    {
        int categoryKey = 11;
        int levels = 0;
        boolean topLevel = true;
        boolean details = true;
        boolean catCount = true;
        boolean contentCount = false;
        Document doc = XMLTool.domparse( PRESENTATION_ENGINE_GET_CATEGORIES_RESPONSE );
        Mockito.when(
            presentationEngine.getCategories( any( UserEntity.class ), eq( categoryKey ), eq( levels ), eq( topLevel ), eq( details ),
                                              eq( catCount ), eq( contentCount ) ) ).thenReturn( doc );

        this.request.addParam( "categoryKey", "11" );
        this.request.addParam( "levels", "0" );
        this.request.addParam( "includeContentCount", "false" );
        this.request.addParam( "includeTopCategory", "true" );
        testHandle( "getCategories_default" );
    }

    @Test
    public void testHandler_default_parameter_values()
        throws Exception
    {
        int categoryKey = 11;
        int levels = 0;
        boolean topLevel = true;
        boolean details = true;
        boolean catCount = true;
        boolean contentCount = false;
        Document doc = XMLTool.domparse( PRESENTATION_ENGINE_GET_CATEGORIES_RESPONSE );
        Mockito.when(
            presentationEngine.getCategories( any( UserEntity.class ), eq( categoryKey ), eq( levels ), eq( topLevel ), eq( details ),
                                              eq( catCount ), eq( contentCount ) ) ).thenReturn( doc );

        this.request.addParam( "categoryKey", "11" );
        testHandle( "getCategories_default" );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_missing_required_parameter()
        throws Exception
    {
        this.request.addParam( "levels", "0" );
        this.request.addParam( "includeContentCount", "false" );
        this.request.addParam( "includeTopCategory", "true" );
        testHandle( "getCategories_default" );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_wrong_parameter_type()
        throws Exception
    {
        this.request.addParam( "categoryKey", "CAT" );
        this.request.addParam( "levels", "0" );
        this.request.addParam( "includeContentCount", "false" );
        this.request.addParam( "includeTopCategory", "true" );
        testHandle( "getCategories_default" );
    }
}
