
package it.veneto.regione.pagamenti.ente;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for paaSILChiediStatoImportFlussoRisposta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="paaSILChiediStatoImportFlussoRisposta">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.regione.veneto.it/pagamenti/ente/}risposta">
 *       &lt;sequence>
 *         &lt;element name="stato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "paaSILChiediStatoImportFlussoRisposta", propOrder = {
    "stato"
})
public class PaaSILChiediStatoImportFlussoRisposta
    extends Risposta
{

    @XmlElement(required = true)
    protected String stato;

    /**
     * Gets the value of the stato property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStato() {
        return stato;
    }

    /**
     * Sets the value of the stato property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStato(String value) {
        this.stato = value;
    }

}
