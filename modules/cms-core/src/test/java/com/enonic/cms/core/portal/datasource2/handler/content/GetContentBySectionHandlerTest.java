package com.enonic.cms.core.portal.datasource2.handler.content;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.category.UnitEntity;
import com.enonic.cms.core.content.category.UnitKey;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.query.ContentBySectionQuery;
import com.enonic.cms.core.content.query.RelatedContentQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.ContentResultSetNonLazy;
import com.enonic.cms.core.content.resultset.RelatedContentResultSet;
import com.enonic.cms.core.content.resultset.RelatedContentResultSetImpl;
import com.enonic.cms.core.language.LanguageEntity;
import com.enonic.cms.core.language.LanguageKey;
import com.enonic.cms.core.portal.datasource2.DataSourceException;
import com.enonic.cms.core.portal.datasource2.handler.AbstractDataSourceHandlerTest;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.store.dao.GroupDao;

import static org.mockito.Matchers.any;

public class GetContentBySectionHandlerTest
    extends AbstractDataSourceHandlerTest<GetContentBySectionHandler>
{

    private ContentService contentService;

    private GroupDao groupDao;

    public GetContentBySectionHandlerTest()
    {
        super( GetContentBySectionHandler.class );
    }

    @Override
    protected void initTest()
        throws Exception
    {
        contentService = Mockito.mock( ContentService.class );
        groupDao = Mockito.mock( GroupDao.class );
        handler.setContentService( contentService );
        handler.setGroupDao( groupDao );

        final MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAccessor.setRequest( request ); // needed to avoid NPE in RenderTrace.getCurrentTraceContext()
    }

    @Test
    public void testHandler_content_by_section()
        throws Exception
    {
        this.request.addParam( "menuItemKeys", "11" );
        this.request.addParam( "levels", "1" );
        this.request.addParam( "query", "" );
        this.request.addParam( "orderBy", "" );
        this.request.addParam( "index", "0" );
        this.request.addParam( "count", "10" );
        this.request.addParam( "includeData", "true" );
        this.request.addParam( "childrenLevel", "1" );
        this.request.addParam( "parentLevel", "0" );

        setupContentForTest( 10 );

        testHandle( "getContentBySection_default" );
    }

    @Test
    public void testHandler_no_content()
        throws Exception
    {
        this.request.addParam( "menuItemKeys", "11" );
        this.request.addParam( "levels", "1" );
        this.request.addParam( "query", "" );
        this.request.addParam( "orderBy", "" );
        this.request.addParam( "index", "0" );
        this.request.addParam( "count", "10" );
        this.request.addParam( "includeData", "true" );
        this.request.addParam( "childrenLevel", "1" );
        this.request.addParam( "parentLevel", "0" );

        setupContentForTest( 0 );

        testHandle( "getContentBySection_empty" );
    }

    @Test(expected = DataSourceException.class)
    public void testHandler_missing_menuItemKeys_parameter()
        throws Exception
    {
        this.request.addParam( "levels", "1" );
        this.request.addParam( "query", "" );
        this.request.addParam( "orderBy", "" );
        this.request.addParam( "index", "0" );
        this.request.addParam( "count", "10" );
        this.request.addParam( "includeData", "true" );
        this.request.addParam( "childrenLevel", "1" );
        this.request.addParam( "parentLevel", "0" );

        setupContentForTest( 0 );

        testHandle( "getContentBySection_empty" );
    }

    private void setupContentForTest( int contentCount )
    {
        final ContentResultSet contentResultSet =
            new ContentResultSetNonLazy( createContents( 0, contentCount, createCategory( "name", "ctype", "unitName" ) ), 0,
                                         contentCount );
        Mockito.when( contentService.queryContent( any( ContentBySectionQuery.class ) ) ).thenReturn( contentResultSet );

        RelatedContentResultSet relatedContents = new RelatedContentResultSetImpl();
        Mockito.when( contentService.queryRelatedContent( any( RelatedContentQuery.class ) ) ).thenReturn( relatedContents );

    }

    private List<ContentEntity> createContents( int fromKey, int toKey, CategoryEntity category )
    {
        final UserEntity owner = new UserEntity();
        owner.setKey( new UserKey( User.ROOT_UID ) );
        owner.setName( User.ROOT_UID );
        owner.setDisplayName( User.ROOT_UID );
        owner.setDeleted( false );

        final List<ContentEntity> contents = new ArrayList<ContentEntity>();
        for ( int i = fromKey; i < toKey; i++ )
        {
            final ContentEntity content = createContent( i, "content-" + i, owner );
            content.setCategory( category );
            content.setLanguage( category.getLanguage() );
            content.setPriority( 0 );
            contents.add( content );
        }
        return contents;
    }

    private ContentEntity createContent( int key, String name, final UserEntity owner )
    {
        final ContentEntity content = new ContentEntity();
        content.setKey( new ContentKey( key ) );
        content.setName( name );
        content.setCreatedAt( new DateTime( 2000, 1, 2, 3, 4 ).toDate() );
        final ContentVersionEntity contentVersion = new ContentVersionEntity();
        contentVersion.setContent( content );
        contentVersion.setStatus( ContentStatus.APPROVED );
        contentVersion.setKey( new ContentVersionKey( key + 1000 ) );
        contentVersion.setModifiedBy( owner );
        contentVersion.setContentDataXml( "<dummy><mycontent title=\"test\"/></dummy>" );
        content.setMainVersion( contentVersion );
        content.setOwner( owner );
        return content;
    }

    public CategoryEntity createCategory( String name, String contentTypeName, String unitName )
    {
        final CategoryEntity category = new CategoryEntity();
        category.setKey( new CategoryKey( 42 ) );
        category.setName( name );
        final ContentTypeEntity contentType = new ContentTypeEntity();
        contentType.setName( contentTypeName );
        contentType.setKey( 84 );
        category.setContentType( contentType );
        category.setUnit( createUnit( unitName, "en" ) );
        category.setCreated( new Date() );
        category.setTimestamp( new Date() );
        category.setAutoMakeAvailable( true );
        category.setDeleted( false );
        return category;
    }

    public UnitEntity createUnit( String name, String languageCode )
    {
        final UnitEntity unit = new UnitEntity();
        unit.setKey( new UnitKey( 0 ) );
        unit.setName( name );
        final LanguageEntity languageEntity = new LanguageEntity();
        languageEntity.setCode( languageCode );
        languageEntity.setKey( new LanguageKey( 0 ) );
        unit.setLanguage( languageEntity );
        unit.setDeleted( false );
        return unit;
    }
}
