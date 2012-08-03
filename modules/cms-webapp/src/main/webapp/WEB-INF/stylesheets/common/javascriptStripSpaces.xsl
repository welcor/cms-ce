<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="html"/>

  <xsl:template name="javascriptStripSpaces">
    <script type="text/javascript" language="JavaScript">

          function stripSpaces(str) {
            var x = str;
            return (x.replace(/^\W+/,'')).replace(/\W+$/,'');
          }
    </script>
  </xsl:template>

</xsl:stylesheet>
