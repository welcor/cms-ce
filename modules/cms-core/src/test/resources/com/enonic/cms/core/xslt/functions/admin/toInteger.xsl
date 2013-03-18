<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" exclude-result-prefixes="#all" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:admin="http://www.enonic.com/cms/admin">

  <xsl:template match="/">
    <output>
      <value>
        <xsl:value-of select="admin:toInteger(10000000 + 999)"/>
      </value>
      <value>
        <xsl:value-of select="admin:toInteger('123456789')"/>
      </value>
    </output>
  </xsl:template>

</xsl:stylesheet>
