
package it.peng.wr.webservice.protocollo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getRicevutePEC complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getRicevutePEC">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="numeroProtocollo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dataRegistrazione" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="idPecInviata" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getRicevutePEC", propOrder = {
    "numeroProtocollo",
    "dataRegistrazione",
    "idPecInviata"
})
public class GetRicevutePEC {

    protected String numeroProtocollo;
    protected String dataRegistrazione;
    protected String idPecInviata;

    /**
     * Gets the value of the numeroProtocollo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroProtocollo() {
        return numeroProtocollo;
    }

    /**
     * Sets the value of the numeroProtocollo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroProtocollo(String value) {
        this.numeroProtocollo = value;
    }

    /**
     * Gets the value of the dataRegistrazione property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataRegistrazione() {
        return dataRegistrazione;
    }

    /**
     * Sets the value of the dataRegistrazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataRegistrazione(String value) {
        this.dataRegistrazione = value;
    }

    /**
     * Gets the value of the idPecInviata property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdPecInviata() {
        return idPecInviata;
    }

    /**
     * Sets the value of the idPecInviata property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdPecInviata(String value) {
        this.idPecInviata = value;
    }

}
