
package it.veneto.regione.pagamenti.ente;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for risposta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="risposta">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fault" type="{http://www.regione.veneto.it/pagamenti/ente/}faultBean" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "risposta", propOrder = {
    "fault"
})
@XmlSeeAlso({
    PaaSILPrenotaExportFlussoIncrementaleConRicevutaRisposta.class,
    PaaSILChiediStatoExportFlussoRisposta.class,
    PaaSILChiediAvvisiPendentiRisposta.class,
    PaaSILImportaDovutoRisposta.class,
    PaaSILAutorizzaImportFlussoRisposta.class,
    PaaSILChiediStatoImportFlussoRisposta.class,
    PaaSILVerificaAvvisoRisposta.class,
    PaaSILPrenotaExportFlussoRisposta.class,
    PaaSILChiediPagatiConRicevutaRisposta.class,
    PaaSILInviaDovutiRisposta.class,
    PaaSILChiediPagatiRisposta.class
})
public class Risposta {

    protected FaultBean fault;

    /**
     * Gets the value of the fault property.
     * 
     * @return
     *     possible object is
     *     {@link FaultBean }
     *     
     */
    public FaultBean getFault() {
        return fault;
    }

    /**
     * Sets the value of the fault property.
     * 
     * @param value
     *     allowed object is
     *     {@link FaultBean }
     *     
     */
    public void setFault(FaultBean value) {
        this.fault = value;
    }

}
