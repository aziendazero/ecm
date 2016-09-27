
package it.veneto.regione.schemas._2012.pagamenti.ente;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ctDominio complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ctDominio">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identificativoDominio" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}stText35"/>
 *         &lt;element name="identificativoStazioneRichiedente" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}stText35" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ctDominio", propOrder = {
    "identificativoDominio",
    "identificativoStazioneRichiedente"
})
public class CtDominio {

    @XmlElement(required = true)
    protected String identificativoDominio;
    protected String identificativoStazioneRichiedente;

    /**
     * Gets the value of the identificativoDominio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificativoDominio() {
        return identificativoDominio;
    }

    /**
     * Sets the value of the identificativoDominio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificativoDominio(String value) {
        this.identificativoDominio = value;
    }

    /**
     * Gets the value of the identificativoStazioneRichiedente property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificativoStazioneRichiedente() {
        return identificativoStazioneRichiedente;
    }

    /**
     * Sets the value of the identificativoStazioneRichiedente property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificativoStazioneRichiedente(String value) {
        this.identificativoStazioneRichiedente = value;
    }

}
