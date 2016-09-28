
package it.veneto.regione.schemas._2012.pagamenti.ente;

import java.math.BigDecimal;
import java.util.Calendar;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter2;


/**
 * <p>Java class for ctDatiVersamento complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ctDatiVersamento">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dataEsecuzionePagamento" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}stISODate"/>
 *         &lt;element name="tipoVersamento" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}stText32" minOccurs="0"/>
 *         &lt;element name="identificativoUnivocoVersamento" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}stText35" minOccurs="0"/>
 *         &lt;element name="identificativoUnivocoDovuto" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}stText35"/>
 *         &lt;element name="importoSingoloVersamento" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}stImporto"/>
 *         &lt;element name="commissioneCaricoPA" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}stImporto" minOccurs="0"/>
 *         &lt;element name="identificativoTipoDovuto" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}stText35"/>
 *         &lt;element name="causaleVersamento" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}stText140"/>
 *         &lt;element name="datiSpecificiRiscossione" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}stDatiSpecificiRiscossione"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ctDatiVersamento", propOrder = {
    "dataEsecuzionePagamento",
    "tipoVersamento",
    "identificativoUnivocoVersamento",
    "identificativoUnivocoDovuto",
    "importoSingoloVersamento",
    "commissioneCaricoPA",
    "identificativoTipoDovuto",
    "causaleVersamento",
    "datiSpecificiRiscossione"
})
public class CtDatiVersamento {

    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    protected Calendar dataEsecuzionePagamento;
    protected String tipoVersamento;
    protected String identificativoUnivocoVersamento;
    @XmlElement(required = true)
    protected String identificativoUnivocoDovuto;
    @XmlElement(required = true)
    protected BigDecimal importoSingoloVersamento;
    protected BigDecimal commissioneCaricoPA;
    @XmlElement(required = true)
    protected String identificativoTipoDovuto;
    @XmlElement(required = true)
    protected String causaleVersamento;
    @XmlElement(required = true)
    protected String datiSpecificiRiscossione;

    /**
     * Gets the value of the dataEsecuzionePagamento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Calendar getDataEsecuzionePagamento() {
        return dataEsecuzionePagamento;
    }

    /**
     * Sets the value of the dataEsecuzionePagamento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataEsecuzionePagamento(Calendar value) {
        this.dataEsecuzionePagamento = value;
    }

    /**
     * Gets the value of the tipoVersamento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoVersamento() {
        return tipoVersamento;
    }

    /**
     * Sets the value of the tipoVersamento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoVersamento(String value) {
        this.tipoVersamento = value;
    }

    /**
     * Gets the value of the identificativoUnivocoVersamento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificativoUnivocoVersamento() {
        return identificativoUnivocoVersamento;
    }

    /**
     * Sets the value of the identificativoUnivocoVersamento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificativoUnivocoVersamento(String value) {
        this.identificativoUnivocoVersamento = value;
    }

    /**
     * Gets the value of the identificativoUnivocoDovuto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificativoUnivocoDovuto() {
        return identificativoUnivocoDovuto;
    }

    /**
     * Sets the value of the identificativoUnivocoDovuto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificativoUnivocoDovuto(String value) {
        this.identificativoUnivocoDovuto = value;
    }

    /**
     * Gets the value of the importoSingoloVersamento property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getImportoSingoloVersamento() {
        return importoSingoloVersamento;
    }

    /**
     * Sets the value of the importoSingoloVersamento property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setImportoSingoloVersamento(BigDecimal value) {
        this.importoSingoloVersamento = value;
    }

    /**
     * Gets the value of the commissioneCaricoPA property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCommissioneCaricoPA() {
        return commissioneCaricoPA;
    }

    /**
     * Sets the value of the commissioneCaricoPA property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCommissioneCaricoPA(BigDecimal value) {
        this.commissioneCaricoPA = value;
    }

    /**
     * Gets the value of the identificativoTipoDovuto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificativoTipoDovuto() {
        return identificativoTipoDovuto;
    }

    /**
     * Sets the value of the identificativoTipoDovuto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificativoTipoDovuto(String value) {
        this.identificativoTipoDovuto = value;
    }

    /**
     * Gets the value of the causaleVersamento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCausaleVersamento() {
        return causaleVersamento;
    }

    /**
     * Sets the value of the causaleVersamento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCausaleVersamento(String value) {
        this.causaleVersamento = value;
    }

    /**
     * Gets the value of the datiSpecificiRiscossione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatiSpecificiRiscossione() {
        return datiSpecificiRiscossione;
    }

    /**
     * Sets the value of the datiSpecificiRiscossione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatiSpecificiRiscossione(String value) {
        this.datiSpecificiRiscossione = value;
    }

}
