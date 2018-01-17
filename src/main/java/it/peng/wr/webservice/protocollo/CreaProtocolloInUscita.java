
package it.peng.wr.webservice.protocollo;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for creaProtocolloInUscita complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="creaProtocolloInUscita">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="oggetto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ufficioCreatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="destinatari" type="{http://protocollo.webservice.wr.peng.it/}corrispondente" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="assegnatari" type="{http://protocollo.webservice.wr.peng.it/}corrispondente" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="riferimenti" type="{http://protocollo.webservice.wr.peng.it/}riferimento" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="classificazioni" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="fascicolazioni" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="invioPec" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="documenti" type="{http://protocollo.webservice.wr.peng.it/}documento" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "creaProtocolloInUscita", propOrder = {
    "oggetto",
    "ufficioCreatore",
    "destinatari",
    "assegnatari",
    "riferimenti",
    "classificazioni",
    "fascicolazioni",
    "invioPec",
    "documenti"
})
public class CreaProtocolloInUscita {

    protected String oggetto;
    protected String ufficioCreatore;
    protected List<Corrispondente> destinatari;
    protected List<Corrispondente> assegnatari;
    protected List<Riferimento> riferimenti;
    protected List<String> classificazioni;
    protected List<String> fascicolazioni;
    protected Boolean invioPec;
    protected List<Documento> documenti;

    /**
     * Gets the value of the oggetto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOggetto() {
        return oggetto;
    }

    /**
     * Sets the value of the oggetto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOggetto(String value) {
        this.oggetto = value;
    }

    /**
     * Gets the value of the ufficioCreatore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUfficioCreatore() {
        return ufficioCreatore;
    }

    /**
     * Sets the value of the ufficioCreatore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUfficioCreatore(String value) {
        this.ufficioCreatore = value;
    }

    /**
     * Gets the value of the destinatari property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the destinatari property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDestinatari().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Corrispondente }
     * 
     * 
     */
    public List<Corrispondente> getDestinatari() {
        if (destinatari == null) {
            destinatari = new ArrayList<Corrispondente>();
        }
        return this.destinatari;
    }

    /**
     * Gets the value of the assegnatari property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the assegnatari property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAssegnatari().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Corrispondente }
     * 
     * 
     */
    public List<Corrispondente> getAssegnatari() {
        if (assegnatari == null) {
            assegnatari = new ArrayList<Corrispondente>();
        }
        return this.assegnatari;
    }

    /**
     * Gets the value of the riferimenti property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the riferimenti property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRiferimenti().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Riferimento }
     * 
     * 
     */
    public List<Riferimento> getRiferimenti() {
        if (riferimenti == null) {
            riferimenti = new ArrayList<Riferimento>();
        }
        return this.riferimenti;
    }

    /**
     * Gets the value of the classificazioni property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the classificazioni property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClassificazioni().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getClassificazioni() {
        if (classificazioni == null) {
            classificazioni = new ArrayList<String>();
        }
        return this.classificazioni;
    }

    /**
     * Gets the value of the fascicolazioni property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fascicolazioni property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFascicolazioni().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getFascicolazioni() {
        if (fascicolazioni == null) {
            fascicolazioni = new ArrayList<String>();
        }
        return this.fascicolazioni;
    }

    /**
     * Gets the value of the invioPec property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isInvioPec() {
        return invioPec;
    }

    /**
     * Sets the value of the invioPec property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setInvioPec(Boolean value) {
        this.invioPec = value;
    }

    /**
     * Gets the value of the documenti property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the documenti property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDocumenti().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Documento }
     * 
     * 
     */
    public List<Documento> getDocumenti() {
        if (documenti == null) {
            documenti = new ArrayList<Documento>();
        }
        return this.documenti;
    }

}
