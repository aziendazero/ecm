
package it.veneto.regione.pagamenti.ente;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for paaSILAvvisoPendente complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="paaSILAvvisoPendente">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dtCreazioneCodIuv" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="dovuti" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "paaSILAvvisoPendente", propOrder = {
    "dtCreazioneCodIuv",
    "dovuti"
})
public class PaaSILAvvisoPendente {

    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dtCreazioneCodIuv;
    @XmlMimeType("application/octet-stream")
    protected DataHandler dovuti;

    /**
     * Gets the value of the dtCreazioneCodIuv property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDtCreazioneCodIuv() {
        return dtCreazioneCodIuv;
    }

    /**
     * Sets the value of the dtCreazioneCodIuv property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDtCreazioneCodIuv(XMLGregorianCalendar value) {
        this.dtCreazioneCodIuv = value;
    }

    /**
     * Gets the value of the dovuti property.
     * 
     * @return
     *     possible object is
     *     {@link DataHandler }
     *     
     */
    public DataHandler getDovuti() {
        return dovuti;
    }

    /**
     * Sets the value of the dovuti property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataHandler }
     *     
     */
    public void setDovuti(DataHandler value) {
        this.dovuti = value;
    }

}
