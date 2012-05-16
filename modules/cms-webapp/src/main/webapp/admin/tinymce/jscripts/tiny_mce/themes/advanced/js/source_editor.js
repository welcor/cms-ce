tinyMCEPopup.requireLangPack();
tinyMCEPopup.onInit.add(onLoadInit);

var g_codemirror;

function onLoadInit()
{
    document.getElementById('htmlSource').value = blockElementsWithNewlines(tinyMCEPopup.editor.getContent());

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

function blockElementsWithNewlines( content )
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
