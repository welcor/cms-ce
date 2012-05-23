<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" exclude-result-prefixes="#all" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:admin="http://www.enonic.com/cms/admin">

  <xsl:template match="/">
    <output>
      <value>
        <xsl:copy-of select="//item[1]"/>
      </value>
      <value>
        <xsl:copy-of select="admin:evaluate('//item[1]')"/>
      </value>
      <value>
        <xsl:copy-of select="."/>
      </value>
      <value>
        <xsl:copy-of select="admin:evaluate('.')"/>
      </value>
    </output>
  </xsl:template>

</xsl:stylesheet>
