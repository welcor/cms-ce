/*
    Class: Editor

    See also:

    <TinyMCE>
    <CmsUtil>
*/
function Editor( id )
{

    this.id = id;           
    this.cmsutil = new CMSUtil();
    // -------------------------------------------------------------------------------------------------------------------------------------

    /*
        Method: create

        Wrapper, creates a new editor instance.
    */
    this.create = function( tinyMceConfigObject )
    {
        if ( tinyMceConfigObject )
        {
            var editor = new tinymce.Editor(this.id, tinyMceConfigObject);
            editor.render();
        }
    };
    // -------------------------------------------------------------------------------------------------------------------------------------

    /*
        Method: createOverlayForDisabledMode

        Create a overlay for shading when the editor is disabled.
    */

    this.createOverlayForDisabledMode = function()
    {
        try {
            var editor = tinyMCE.get(this.id);
            var editorIframeElem = document.getElementById(this.id + '_ifr');

            editorIframeElem.scrolling = 'no';
            editorIframeElem.scrollbar = 'no';

            var editorBodyElem = editor.getBody();
            this.cmsutil.setOpacity(editorBodyElem, .5);
        }
        catch(exception)
        {
        }
    };

}