
package it.veneto.regione.pagamenti.ente;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for paaSILChiediPagatiConRicevuta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="paaSILChiediPagatiConRicevuta">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="codIpaEnte" type="{http://www.regione.veneto.it/pagamenti/ente/ppthead}stText35"/>
 *         &lt;element name="password" type="{http://www.regione.veneto.it/pagamenti/ente/}stPassword" minOccurs="0"/>
 *         &lt;element name="idSession" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "paaSILChiediPagatiConRicevuta", propOrder = {
    "codIpaEnte",
    "password",
    "idSession"
})
public class PaaSILChiediPagatiConRicevuta {

    @XmlElement(required = true)
    protected String codIpaEnte;
    protected String password;
    @XmlElement(required = true)
    protected String idSession;

    /**
     * Gets the value of the codIpaEnte property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodIpaEnte() {
        return codIpaEnte;
    }

    /**
     * Sets the value of the codIpaEnte property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodIpaEnte(String value) {
        this.codIpaEnte = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the idSession property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdSession() {
        return idSession;
    }

    /**
     * Sets the value of the idSession property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdSession(String value) {
        this.idSession = value;
    }

}
