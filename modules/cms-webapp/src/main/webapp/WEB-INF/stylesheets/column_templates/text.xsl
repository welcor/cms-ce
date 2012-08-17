<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="#all"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    >

	<xsl:template match="text" mode="display">
		<xsl:value-of select="."/>
	</xsl:template>
	
</xsl:stylesheet>
