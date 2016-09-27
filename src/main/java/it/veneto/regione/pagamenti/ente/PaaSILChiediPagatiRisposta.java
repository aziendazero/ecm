
package it.veneto.regione.pagamenti.ente;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for paaSILChiediPagatiRisposta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="paaSILChiediPagatiRisposta">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.regione.veneto.it/pagamenti/ente/}risposta">
 *       &lt;sequence>
 *         &lt;element name="pagati" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "paaSILChiediPagatiRisposta", propOrder = {
    "pagati"
})
public class PaaSILChiediPagatiRisposta
    extends Risposta
{

    @XmlMimeType("application/octet-stream")
    protected DataHandler pagati;

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

}
