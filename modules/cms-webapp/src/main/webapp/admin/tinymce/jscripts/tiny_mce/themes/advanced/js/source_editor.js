tinyMCEPopup.requireLangPack();
tinyMCEPopup.onInit.add(onLoadInit);

var g_codemirror; // Reference to the codemirror instance.

function onLoadInit()
{
    var textAreaToTransform = document.getElementById('htmlSource');

    textAreaToTransform.value = addNewlinesAfterBlockLevelTags(tinyMCEPopup.editor.getContent());

    // When the textarea has no content and the user tries to paste content from the system's
    // clipboard Firefox will not paste the content properly.
    // Adding a newline to the textarea fixes this and has no effect on the content as
    // TinyMCE strips newlines on get/set content anyway.
    if ( tinyMCEPopup.getWin().tinymce.isGecko && textAreaToTransform.value.length == 0 )
    {
        textAreaToTransform.value = '\n';
    }

    g_codemirror = CodeMirror.fromTextArea('htmlSource', {
        width: '', // Leave blank for 100%
        height: '450px',
        lineNumbers: true,
        textWrapping: true,
        path: "../../../../../codemirror/js/",
        tabMode: 'shift',
        indentUnit: 2,
        parserfile: ["parsexml.js", "parsecss.js", "tokenizejavascript.js", "parsejavascript.js", "parsehtmlmixed.js"],
        stylesheet: ["../../../../../codemirror/css/cms.xmlcolors.css", "../../../../../codemirror/css/cms.jscolors.css", "../../../../../codemirror/css/cms.csscolors.css"],
        parserConfig: { useHTMLKludges: true },
        reindentOnLoad: true
    });
}

function addNewlinesAfterBlockLevelTags( content )
{
    var contentWithNewlines = content;

    // P
    contentWithNewlines     = contentWithNewlines.replace(/(<p(?:\s+[^>]*)?>)/gim, '$1\n');
    contentWithNewlines     = contentWithNewlines.replace(/<\/p>/gim, '\n</p>');

    // H1-6
    contentWithNewlines     = contentWithNewlines.replace(/(<h[1-6].*?>)/gim, '$1\n');
    contentWithNewlines     = contentWithNewlines.replace(/(<\/h[1-6].*?>)/gim, '\n$1');

    return contentWithNewlines;
}

function saveContent()
{
    tinyMCEPopup.editor.setContent(g_codemirror.getCode());
    tinyMCEPopup.close();
}
