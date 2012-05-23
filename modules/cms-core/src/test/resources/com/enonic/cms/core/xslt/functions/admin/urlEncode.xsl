<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" exclude-result-prefixes="#all" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:admin="http://www.enonic.com/cms/admin">

  <xsl:template match="/">
    <output>
      <value>
        <xsl:value-of select="admin:urlEncode('http://www.enonic.com/test')"/>
      </value>
    </output>
  </xsl:template>

</xsl:stylesheet>
