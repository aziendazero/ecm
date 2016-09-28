
package it.veneto.regione.schemas._2012.pagamenti.ente;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ctDovuti complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ctDovuti">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="versioneOggetto" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}stText16"/>
 *         &lt;element name="soggettoPagatore" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}ctSoggettoPagatore"/>
 *         &lt;element name="datiVersamento" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}ctDatiVersamentoDovuti"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ctDovuti", propOrder = {
    "versioneOggetto",
    "soggettoPagatore",
    "datiVersamento"
})
public class CtDovuti {

    @XmlElement(required = true)
    protected String versioneOggetto;
    @XmlElement(required = true)
    protected CtSoggettoPagatore soggettoPagatore;
    @XmlElement(required = true)
    protected CtDatiVersamentoDovuti datiVersamento;

    /**
     * Gets the value of the versioneOggetto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersioneOggetto() {
        return versioneOggetto;
    }

    /**
     * Sets the value of the versioneOggetto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersioneOggetto(String value) {
        this.versioneOggetto = value;
    }

    /**
     * Gets the value of the soggettoPagatore property.
     * 
     * @return
     *     possible object is
     *     {@link CtSoggettoPagatore }
     *     
     */
    public CtSoggettoPagatore getSoggettoPagatore() {
        return soggettoPagatore;
    }

    /**
     * Sets the value of the soggettoPagatore property.
     * 
     * @param value
     *     allowed object is
     *     {@link CtSoggettoPagatore }
     *     
     */
    public void setSoggettoPagatore(CtSoggettoPagatore value) {
        this.soggettoPagatore = value;
    }

    /**
     * Gets the value of the datiVersamento property.
     * 
     * @return
     *     possible object is
     *     {@link CtDatiVersamentoDovuti }
     *     
     */
    public CtDatiVersamentoDovuti getDatiVersamento() {
        return datiVersamento;
    }

    /**
     * Sets the value of the datiVersamento property.
     * 
     * @param value
     *     allowed object is
     *     {@link CtDatiVersamentoDovuti }
     *     
     */
    public void setDatiVersamento(CtDatiVersamentoDovuti value) {
        this.datiVersamento = value;
    }

}
