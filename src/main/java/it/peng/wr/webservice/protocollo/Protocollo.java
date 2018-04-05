
package it.peng.wr.webservice.protocollo;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for protocollo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="protocollo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="annoProtocollo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="numeroProtocollo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tipo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="mittente" type="{http://protocollo.webservice.wr.peng.it/}corrispondente"/>
 *         &lt;element name="corrispondenti" type="{http://protocollo.webservice.wr.peng.it/}corrispondente" maxOccurs="unbounded"/>
 *         &lt;element name="dataRegistrazione" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="ufficioCreatore" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="classificazioni" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="fascicolazioni" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="oggetto" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="documenti" type="{http://protocollo.webservice.wr.peng.it/}documento" maxOccurs="unbounded"/>
 *         &lt;element name="pecInviate" type="{http://protocollo.webservice.wr.peng.it/}pecinviata" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "protocollo", propOrder = {
    "id",
    "annoProtocollo",
    "numeroProtocollo",
    "tipo",
    "mittente",
    "corrispondenti",
    "dataRegistrazione",
    "ufficioCreatore",
    "classificazioni",
    "fascicolazioni",
    "oggetto",
    "documenti",
    "pecInviate"
})
public class Protocollo {

    @XmlElementRef(name = "id", type = JAXBElement.class, required = false)
    protected JAXBElement<String> id;
    @XmlElement(required = true, nillable = true)
    protected String annoProtocollo;
    @XmlElement(required = true, nillable = true)
    protected String numeroProtocollo;
    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer tipo;
    @XmlElement(required = true)
    protected Corrispondente mittente;
    @XmlElement(required = true)
    protected List<Corrispondente> corrispondenti;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataRegistrazione;
    @XmlElement(required = true, nillable = true)
    protected String ufficioCreatore;
    @XmlElement(required = true, nillable = true)
    protected List<String> classificazioni;
    @XmlElement(required = true, nillable = true)
    protected List<String> fascicolazioni;
    @XmlElement(required = true)
    protected String oggetto;
    @XmlElement(required = true, nillable = true)
    protected List<Documento> documenti;
    @XmlElement(required = true, nillable = true)
    protected List<Pecinviata> pecInviate;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setId(JAXBElement<String> value) {
        this.id = value;
    }

    /**
     * Gets the value of the annoProtocollo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnnoProtocollo() {
        return annoProtocollo;
    }

    /**
     * Sets the value of the annoProtocollo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnnoProtocollo(String value) {
        this.annoProtocollo = value;
    }

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
     * Gets the value of the tipo property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTipo() {
        return tipo;
    }

    /**
     * Sets the value of the tipo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTipo(Integer value) {
        this.tipo = value;
    }

    /**
     * Gets the value of the mittente property.
     * 
     * @return
     *     possible object is
     *     {@link Corrispondente }
     *     
     */
    public Corrispondente getMittente() {
        return mittente;
    }

    /**
     * Sets the value of the mittente property.
     * 
     * @param value
     *     allowed object is
     *     {@link Corrispondente }
     *     
     */
    public void setMittente(Corrispondente value) {
        this.mittente = value;
    }

    /**
     * Gets the value of the corrispondenti property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the corrispondenti property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCorrispondenti().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Corrispondente }
     * 
     * 
     */
    public List<Corrispondente> getCorrispondenti() {
        if (corrispondenti == null) {
            corrispondenti = new ArrayList<Corrispondente>();
        }
        return this.corrispondenti;
    }

    /**
     * Gets the value of the dataRegistrazione property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataRegistrazione() {
        return dataRegistrazione;
    }

    /**
     * Sets the value of the dataRegistrazione property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataRegistrazione(XMLGregorianCalendar value) {
        this.dataRegistrazione = value;
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

    /**
     * Gets the value of the pecInviate property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pecInviate property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPecInviate().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Pecinviata }
     * 
     * 
     */
    public List<Pecinviata> getPecInviate() {
        if (pecInviate == null) {
            pecInviate = new ArrayList<Pecinviata>();
        }
        return this.pecInviate;
    }

}
