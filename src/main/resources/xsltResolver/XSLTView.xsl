<xsl:stylesheet
  version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  exclude-result-prefixes="xs">

<xsl:output method="html" indent="yes" version="5.0"/>

<xsl:template match="evento">
  <html lang="it">
    <head>
      <title>Partecipanti</title>
    </head>
    <body>
      <h2>Partecipanti dell'Evento <xsl:value-of select="@cod_evento"/><xsl:if test="@cod_edi &gt; 1">-<xsl:value-of select="@cod_edi"/></xsl:if></h2>

	<table id="tableXSLT" class="no-order table table-striped table-bordered dt-responsive nowrap" cellspacing="0" width="100%">
		<thead>
			<tr>
				<th>Nome</th>
				<th>Cognome</th>
				<th>Codice Fiscale</th>
				<th>Rec.</th>
				<th>Sponsor</th>
				<th>Tipologia Partecipante</th>
				<th>N. Crediti</th>
				<th>Data crediti acquisiti</th>
				<th>Professione</th>
			</tr>
		</thead>
		<tbody>
			<xsl:apply-templates/>
		</tbody>
	</table>
    </body>
  </html>
</xsl:template>

<xsl:template match="partecipante">
	<tr>
		<td><xsl:value-of select="@nome"/></td>
		<td><xsl:value-of select="@cognome"/></td>
		<td><xsl:value-of select="@cod_fisc"/></td>
        <td>
			<xsl:call-template name="reclutato">
				<xsl:with-param name="p" select="@part_reclutato"/>
			</xsl:call-template>
        </td>
		<td><xsl:value-of select="@sponsor"/></td>
        <td>
			<xsl:call-template name="ruolo">
				<xsl:with-param name="r" select="@ruolo"/>
			</xsl:call-template>
        </td>
		<td><xsl:value-of select="translate(@cred_acq,'.',',')"/></td>
		<td><xsl:value-of select="@data_acq"/></td>
		<td>
			<xsl:for-each select="professione">
				<xsl:variable name="cod_prof"><xsl:value-of select="@cod_prof"/></xsl:variable>
				<xsl:variable name="prof">
					<xsl:value-of select="document('professioni.xml')//p[@id=$cod_prof]/text()"/>
				</xsl:variable>
				<xsl:call-template name="toUpperCase">
					<xsl:with-param name="s" select="$prof"/>
				</xsl:call-template>
				<xsl:if test="position()!=last()">
					<br/>
				</xsl:if>
			</xsl:for-each>
		</td>
	</tr>
</xsl:template>

<xsl:template name="toUpperCase">
  <xsl:param name="s"/>
  <xsl:variable name="smallcase" select="'abcdefghijklmnopqrstuvwxyz'" />
  <xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />
  <xsl:value-of select="translate($s, $smallcase, $uppercase)" />
</xsl:template>

<xsl:template name="reclutato">
	<xsl:param name="p"/>
	<xsl:choose>
		<xsl:when test="$p='1'">
			<xsl:text>SI</xsl:text>
		</xsl:when>
		<xsl:otherwise>
			<xsl:text>NO</xsl:text>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="ruolo">
	<xsl:param name="r"/>
	<xsl:choose>
		<xsl:when test="$r='P'">
			<xsl:text>PARTECIPANTE</xsl:text>
		</xsl:when>
		<xsl:when test="$r='D'">
			<xsl:text>DOCENTE</xsl:text>
		</xsl:when>
		<xsl:when test="$r='T'">
			<xsl:text>TUTOR</xsl:text>
		</xsl:when>
		<xsl:when test="$r='R'">
			<xsl:text>RELATORE</xsl:text>
		</xsl:when>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>