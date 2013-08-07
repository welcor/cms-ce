<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" />

    <xsl:template name="codearea-scripts">
        <!-- CSS -->
        <link type="text/css" rel="stylesheet" href="codemirror/lib/codemirror.css"/>
        <link type="text/css" rel="stylesheet" href="javascript/cms/ui/codearea/codearea.css"/>

        <!-- JS -->
        <script type="text/javascript" src="codemirror/lib/codemirror.js">//</script>
        <script type="text/javascript" src="codemirror/addon/selection/active-line.js">//</script>
        <script type="text/javascript" src="codemirror/addon/search/match-highlighter.js">//</script>
        <script type="text/javascript" src="codemirror/addon/edit/closetag.js">//</script>
        <script type="text/javascript" src="codemirror/mode/xml/xml.js">//</script>

        <script type="text/javascript" src="javascript/cms/ui/codearea/codearea.js">//</script>
    </xsl:template>

</xsl:stylesheet>