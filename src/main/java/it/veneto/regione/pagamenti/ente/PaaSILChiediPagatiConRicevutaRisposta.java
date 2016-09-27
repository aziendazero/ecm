
package it.veneto.regione.pagamenti.ente;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for paaSILChiediPagatiConRicevutaRisposta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="paaSILChiediPagatiConRicevutaRisposta">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.regione.veneto.it/pagamenti/ente/}risposta">
 *       &lt;sequence>
 *         &lt;element name="pagati" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="tipoFirma" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rt" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "paaSILChiediPagatiConRicevutaRisposta", propOrder = {
    "pagati",
    "tipoFirma",
    "rt"
})
public class PaaSILChiediPagatiConRicevutaRisposta
    extends Risposta
{

    @XmlMimeType("application/octet-stream")
    protected DataHandler pagati;
    protected String tipoFirma;
    @XmlMimeType("application/octet-stream")
    protected DataHandler rt;

    /**
     * Gets the value of the pagati property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    public DataHandler getPagati() {
        return pagati;
    }

    /**
     * Sets the value of the pagati property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataHandler }
     *     
     */
    public void setPagati(DataHandler value) {
        this.pagati = value;
    }

    /**
     * Gets the value of the tipoFirma property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoFirma() {
        return tipoFirma;
    }

    /**
     * Sets the value of the tipoFirma property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoFirma(String value) {
        this.tipoFirma = value;
    }

    /**
     * Gets the value of the rt property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    public DataHandler getRt() {
        return rt;
    }

    /**
     * Sets the value of the rt property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataHandler }
     *     
     */
    public void setRt(DataHandler value) {
        this.rt = value;
    }

}
