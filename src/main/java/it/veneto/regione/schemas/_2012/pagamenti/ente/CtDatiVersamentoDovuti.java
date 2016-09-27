
package it.veneto.regione.schemas._2012.pagamenti.ente;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ctDatiVersamentoDovuti complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ctDatiVersamentoDovuti">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tipoVersamento" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}stText32"/>
 *         &lt;element name="identificativoUnivocoVersamento" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}stText35" minOccurs="0"/>
 *         &lt;element name="datiSingoloVersamento" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}ctDatiSingoloVersamentoDovuti" maxOccurs="5"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ctDatiVersamentoDovuti", propOrder = {
    "tipoVersamento",
    "identificativoUnivocoVersamento",
    "datiSingoloVersamento"
})
public class CtDatiVersamentoDovuti {

    @XmlElement(required = true)
    protected String tipoVersamento;
    protected String identificativoUnivocoVersamento;
    @XmlElement(required = true)
    protected List<CtDatiSingoloVersamentoDovuti> datiSingoloVersamento;

    /**
     * Gets the value of the tipoVersamento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoVersamento() {
        return tipoVersamento;
    }

    /**
     * Sets the value of the tipoVersamento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoVersamento(String value) {
        this.tipoVersamento = value;
    }

    /**
     * Gets the value of the identificativoUnivocoVersamento property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificativoUnivocoVersamento() {
        return identificativoUnivocoVersamento;
    }

    /**
     * Sets the value of the identificativoUnivocoVersamento property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificativoUnivocoVersamento(String value) {
        this.identificativoUnivocoVersamento = value;
    }

    /**
     * Gets the value of the datiSingoloVersamento property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the datiSingoloVersamento property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDatiSingoloVersamento().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CtDatiSingoloVersamentoDovuti }
     * 
     * 
     */
    public List<CtDatiSingoloVersamentoDovuti> getDatiSingoloVersamento() {
        if (datiSingoloVersamento == null) {
            datiSingoloVersamento = new ArrayList<CtDatiSingoloVersamentoDovuti>();
        }
        return this.datiSingoloVersamento;
    }

}
