<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" exclude-result-prefixes="#all" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:portal="http://www.enonic.com/cms/portal">

  <xsl:template match="/">
    <output>
      <value>
        <xsl:value-of select="portal:localize('phrase')"/>
      </value>
      <value>
        <xsl:value-of select="portal:localize('phrase', ())"/>
      </value>
      <value>
        <xsl:value-of select="portal:localize('phrase', ('a', 3))"/>
      </value>
      <value>
        <xsl:value-of select="portal:localize('phrase', (), 'locale')"/>
      </value>
      <value>
        <xsl:value-of select="portal:localize('phrase', ('a', 3), 'locale')"/>
      </value>
    </output>
  </xsl:template>

</xsl:stylesheet>
