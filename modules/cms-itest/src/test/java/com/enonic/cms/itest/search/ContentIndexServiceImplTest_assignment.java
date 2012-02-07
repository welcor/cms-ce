package com.enonic.cms.itest.search;

import org.joda.time.DateTime;
import org.junit.Test;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentDocument;
import com.enonic.cms.core.content.index.ContentIndexQuery;


public class ContentIndexServiceImplTest_assignment
    extends ContentIndexServiceTestBase
{

    @Test
    public void testIndexingAndSearchOnAssigneeQualifiedName()
    {
        ContentDocument assignedToJVS = new ContentDocument( new ContentKey( 1101 ) );
        assignedToJVS.setCategoryKey( new CategoryKey( 9 ) );
        assignedToJVS.setContentTypeKey( new ContentTypeKey( 32 ) );
        assignedToJVS.setContentTypeName( "Article" );
        assignedToJVS.setTitle( "title" );
        assignedToJVS.setStatus( 2 );
        assignedToJVS.setPriority( 0 );
        assignedToJVS.setAssigneeQualifiedName( "incamono\\jvs" );

        ContentDocument assignedToTAN = new ContentDocument( new ContentKey( 1102 ) );
        assignedToTAN.setCategoryKey( new CategoryKey( 9 ) );
        assignedToTAN.setContentTypeKey( new ContentTypeKey( 32 ) );
        assignedToTAN.setContentTypeName( "Article" );
        assignedToTAN.setTitle( "title" );
        assignedToTAN.setStatus( 2 );
        assignedToTAN.setPriority( 0 );
        assignedToTAN.setAssigneeQualifiedName( "incamono\\tan" );

        ContentDocument assignedToNone = new ContentDocument( new ContentKey( 1103 ) );
        assignedToNone.setCategoryKey( new CategoryKey( 9 ) );
        assignedToNone.setContentTypeKey( new ContentTypeKey( 32 ) );
        assignedToNone.setContentTypeName( "Article" );
        assignedToNone.setTitle( "title" );
        assignedToNone.setStatus( 2 );
        assignedToNone.setPriority( 0 );

        contentIndexService.index( assignedToJVS, false );
        contentIndexService.index( assignedToTAN, false );
        contentIndexService.index( assignedToNone, false );
        //flushIndex();

        assertContentResultSetEquals( new int[]{1101, 1102, 1103}, contentIndexService.query( new ContentIndexQuery( "categorykey = 9" ) ) );
        assertContentResultSetEquals( new int[]{1101}, contentIndexService.query(
            new ContentIndexQuery( "categorykey = 9 and assignee/qualifiedName = 'incamono\\jvs'" ) ) );
        assertContentResultSetEquals( new int[]{1102}, contentIndexService.query(
            new ContentIndexQuery( "categorykey = 9 and assignee/qualifiedName = 'incamono\\tan'" ) ) );
    }

    @Test
    public void testIndexingAndSearchWithOrderyByAssigneeQualifiedName()
    {
        ContentDocument assignedToJVS = new ContentDocument( new ContentKey( 1101 ) );
        assignedToJVS.setCategoryKey( new CategoryKey( 9 ) );
        assignedToJVS.setContentTypeKey( new ContentTypeKey( 32 ) );
        assignedToJVS.setContentTypeName( "Article" );
        assignedToJVS.setTitle( "title" );
        assignedToJVS.setStatus( 2 );
        assignedToJVS.setPriority( 0 );
        assignedToJVS.setAssigneeQualifiedName( "incamono\\jvs" );

        ContentDocument assignedToTAN = new ContentDocument( new ContentKey( 1102 ) );
        assignedToTAN.setCategoryKey( new CategoryKey( 9 ) );
        assignedToTAN.setContentTypeKey( new ContentTypeKey( 32 ) );
        assignedToTAN.setContentTypeName( "Article" );
        assignedToTAN.setTitle( "title" );
        assignedToTAN.setStatus( 2 );
        assignedToTAN.setPriority( 0 );
        assignedToTAN.setAssigneeQualifiedName( "incamono\\tan" );

        ContentDocument assignedToNone = new ContentDocument( new ContentKey( 1103 ) );
        assignedToNone.setCategoryKey( new CategoryKey( 9 ) );
        assignedToNone.setContentTypeKey( new ContentTypeKey( 32 ) );
        assignedToNone.setContentTypeName( "Article" );
        assignedToNone.setTitle( "title" );
        assignedToNone.setStatus( 2 );
        assignedToNone.setPriority( 0 );

        contentIndexService.index( assignedToJVS, false );
        contentIndexService.index( assignedToTAN, false );
        contentIndexService.index( assignedToNone, false );
        //flushIndex();

        assertContentResultSetEquals( new int[]{1101, 1102, 1103}, contentIndexService.query( new ContentIndexQuery( "categorykey = 9" ) ) );
        assertContentResultSetEquals( new int[]{1102, 1101, 1103},
                                      contentIndexService.query( new ContentIndexQuery( "categorykey = 9", "assignee/qualifiedname desc" ) ) );
        assertContentResultSetEquals( new int[]{1102, 1101}, contentIndexService.query( new ContentIndexQuery(
            "categorykey = 9 AND ( assignee/qualifiedName = 'incamono\\jvs' OR assignee/qualifiedName = 'incamono\\tan' )",
            "assignee/qualifiedname desc" ) ) );
        assertContentResultSetEquals( new int[]{1103, 1101, 1102,},
                                      contentIndexService.query( new ContentIndexQuery( "categorykey = 9", "assignee/qualifiedname asc" ) ) );
    }

    @Test
    public void testIndexingAndSearchOnAssignerQualifiedName()
    {
        ContentDocument assignerIsJVS = new ContentDocument( new ContentKey( 1101 ) );
        assignerIsJVS.setCategoryKey( new CategoryKey( 9 ) );
        assignerIsJVS.setContentTypeKey( new ContentTypeKey( 32 ) );
        assignerIsJVS.setContentTypeName( "Article" );
        assignerIsJVS.setTitle( "title" );
        assignerIsJVS.setStatus( 2 );
        assignerIsJVS.setPriority( 0 );
        assignerIsJVS.setAssignerQualifiedName( "incamono\\jvs" );

        ContentDocument assignerIsTAN = new ContentDocument( new ContentKey( 1102 ) );
        assignerIsTAN.setCategoryKey( new CategoryKey( 9 ) );
        assignerIsTAN.setContentTypeKey( new ContentTypeKey( 32 ) );
        assignerIsTAN.setContentTypeName( "Article" );
        assignerIsTAN.setTitle( "title" );
        assignerIsTAN.setStatus( 2 );
        assignerIsTAN.setPriority( 0 );
        assignerIsTAN.setAssignerQualifiedName( "incamono\\tan" );

        ContentDocument assigerIsNone = new ContentDocument( new ContentKey( 1103 ) );
        assigerIsNone.setCategoryKey( new CategoryKey( 9 ) );
        assigerIsNone.setContentTypeKey( new ContentTypeKey( 32 ) );
        assigerIsNone.setContentTypeName( "Article" );
        assigerIsNone.setTitle( "title" );
        assigerIsNone.setStatus( 2 );
        assigerIsNone.setPriority( 0 );

        contentIndexService.index( assignerIsJVS, false );
        contentIndexService.index( assignerIsTAN, false );
        contentIndexService.index( assigerIsNone, false );
        //flushIndex();

        assertContentResultSetEquals( new int[]{1101, 1102, 1103}, contentIndexService.query( new ContentIndexQuery( "categorykey = 9" ) ) );
        assertContentResultSetEquals( new int[]{1101}, contentIndexService.query(
            new ContentIndexQuery( "categorykey = 9 and assigner/qualifiedName = 'incamono\\jvs'" ) ) );
        assertContentResultSetEquals( new int[]{1102}, contentIndexService.query(
            new ContentIndexQuery( "categorykey = 9 and assigner/qualifiedName = 'incamono\\tan'" ) ) );
    }

    @Test
    public void testIndexingAndSearchWithOrderyByAssignerQualifiedName()
    {
        ContentDocument assignerIsJVS = new ContentDocument( new ContentKey( 1101 ) );
        assignerIsJVS.setCategoryKey( new CategoryKey( 9 ) );
        assignerIsJVS.setContentTypeKey( new ContentTypeKey( 32 ) );
        assignerIsJVS.setContentTypeName( "Article" );
        assignerIsJVS.setTitle( "title" );
        assignerIsJVS.setStatus( 2 );
        assignerIsJVS.setPriority( 0 );
        assignerIsJVS.setAssignerQualifiedName( "incamono\\jvs" );

        ContentDocument assignerIsTAN = new ContentDocument( new ContentKey( 1102 ) );
        assignerIsTAN.setCategoryKey( new CategoryKey( 9 ) );
        assignerIsTAN.setContentTypeKey( new ContentTypeKey( 32 ) );
        assignerIsTAN.setContentTypeName( "Article" );
        assignerIsTAN.setTitle( "title" );
        assignerIsTAN.setStatus( 2 );
        assignerIsTAN.setPriority( 0 );
        assignerIsTAN.setAssignerQualifiedName( "incamono\\tan" );

        ContentDocument assignerIsNone = new ContentDocument( new ContentKey( 1103 ) );
        assignerIsNone.setCategoryKey( new CategoryKey( 9 ) );
        assignerIsNone.setContentTypeKey( new ContentTypeKey( 32 ) );
        assignerIsNone.setContentTypeName( "Article" );
        assignerIsNone.setTitle( "title" );
        assignerIsNone.setStatus( 2 );
        assignerIsNone.setPriority( 0 );

        contentIndexService.index( assignerIsJVS, false );
        contentIndexService.index( assignerIsTAN, false );
        contentIndexService.index( assignerIsNone, false );

        assertContentResultSetEquals( new int[]{1101, 1102, 1103}, contentIndexService.query( new ContentIndexQuery( "categorykey = 9" ) ) );
        assertContentResultSetEquals( new int[]{1102, 1101, 1103},
                                      contentIndexService.query( new ContentIndexQuery( "categorykey = 9", "assigner/qualifiedname desc" ) ) );
        assertContentResultSetEquals( new int[]{1102, 1101}, contentIndexService.query( new ContentIndexQuery(
            "categorykey = 9 AND ( assigner/qualifiedName = 'incamono\\jvs' OR assigner/qualifiedName = 'incamono\\tan' )",
            "assigner/qualifiedname desc" ) ) );
        assertContentResultSetEquals( new int[]{1103, 1101, 1102,},
                                      contentIndexService.query( new ContentIndexQuery( "categorykey = 9", "assigner/qualifiedname asc" ) ) );
    }

    @Test
    public void testIndexingAndSearchOnAssigmentDueDate()
    {
        ContentDocument due2010_06_01T00_00_00 = new ContentDocument( new ContentKey( 1101 ) );
        due2010_06_01T00_00_00.setCategoryKey( new CategoryKey( 9 ) );
        due2010_06_01T00_00_00.setContentTypeKey( new ContentTypeKey( 32 ) );
        due2010_06_01T00_00_00.setContentTypeName( "Article" );
        due2010_06_01T00_00_00.setTitle( "title" );
        due2010_06_01T00_00_00.setStatus( 2 );
        due2010_06_01T00_00_00.setPriority( 0 );
        due2010_06_01T00_00_00.setAssignmentDueDate( new DateTime( 2010, 6, 1, 0, 0, 0, 0 ).toDate() );

        ContentDocument due2010_06_01T12_00_00 = new ContentDocument( new ContentKey( 1102 ) );
        due2010_06_01T12_00_00.setCategoryKey( new CategoryKey( 9 ) );
        due2010_06_01T12_00_00.setContentTypeKey( new ContentTypeKey( 32 ) );
        due2010_06_01T12_00_00.setContentTypeName( "Article" );
        due2010_06_01T12_00_00.setTitle( "title" );
        due2010_06_01T12_00_00.setStatus( 2 );
        due2010_06_01T12_00_00.setPriority( 0 );
        due2010_06_01T12_00_00.setAssignmentDueDate( new DateTime( 2010, 6, 1, 12, 0, 0, 0 ).toDate() );

        ContentDocument notDue = new ContentDocument( new ContentKey( 1103 ) );
        notDue.setCategoryKey( new CategoryKey( 9 ) );
        notDue.setContentTypeKey( new ContentTypeKey( 32 ) );
        notDue.setContentTypeName( "Article" );
        notDue.setTitle( "title" );
        notDue.setStatus( 2 );
        notDue.setPriority( 0 );

        contentIndexService.index( due2010_06_01T00_00_00, false );
        contentIndexService.index( due2010_06_01T12_00_00, false );
        contentIndexService.index( notDue, false );
        //flushIndex();

        printAllIndexContent();

        assertContentResultSetEquals( new int[]{1101, 1102, 1103}, contentIndexService.query( new ContentIndexQuery( "categorykey = 9" ) ) );
        assertContentResultSetEquals( new int[]{1101}, contentIndexService.query(
            new ContentIndexQuery( "categorykey = 9 and assignmentDueDate = '2010-06-01T00:00:00'" ) ) );
        assertContentResultSetEquals( new int[]{1102}, contentIndexService.query(
            new ContentIndexQuery( "categorykey = 9 and assignmentDueDate = date('2010-06-01 12:00:00')" ) ) );
        assertContentResultSetEquals( new int[]{1103},
                                      contentIndexService.query( new ContentIndexQuery( "categorykey = 9 and assignmentDueDate = ''" ) ) );
        assertContentResultSetEquals( new int[]{1101, 1102},
                                      contentIndexService.query( new ContentIndexQuery( "categorykey = 9 and assignmentDueDate != ''" ) ) );
    }


}
