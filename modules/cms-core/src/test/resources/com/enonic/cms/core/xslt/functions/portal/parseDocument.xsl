<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" exclude-result-prefixes="#all" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:portal="http://www.enonic.com/cms/portal">

  <xsl:template match="/">
    <xsl:variable name="parsedDocument1"
                  select="portal:parseDocument('&lt;test1>&lt;test2>&lt;test3>expectedValue&lt;/test3>&lt;/test2>&lt;/test1>')"/>
    <output>
      <value>
        <xsl:copy-of select="$parsedDocument1"/>
      </value>
      <value>
        <xsl:value-of select="$parsedDocument1/test1/test2/test3"/>
      </value>
    </output>
  </xsl:template>

</xsl:stylesheet>



