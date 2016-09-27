
package it.veneto.regione.schemas._2012.pagamenti.ente;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ctIdentificativoUnivocoPersonaFG complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ctIdentificativoUnivocoPersonaFG">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tipoIdentificativoUnivoco" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}stTipoIdentificativoUnivocoPersFG"/>
 *         &lt;element name="codiceIdentificativoUnivoco" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}stText35"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ctIdentificativoUnivocoPersonaFG", propOrder = {
    "tipoIdentificativoUnivoco",
    "codiceIdentificativoUnivoco"
})
public class CtIdentificativoUnivocoPersonaFG {

    @XmlElement(required = true)
    protected StTipoIdentificativoUnivocoPersFG tipoIdentificativoUnivoco;
    @XmlElement(required = true)
    protected String codiceIdentificativoUnivoco;

    /**
     * Gets the value of the tipoIdentificativoUnivoco property.
     * 
     * @return
     *     possible object is
     *     {@link StTipoIdentificativoUnivocoPersFG }
     *     
     */
    public StTipoIdentificativoUnivocoPersFG getTipoIdentificativoUnivoco() {
        return tipoIdentificativoUnivoco;
    }

    /**
     * Sets the value of the tipoIdentificativoUnivoco property.
     * 
     * @param value
     *     allowed object is
     *     {@link StTipoIdentificativoUnivocoPersFG }
     *     
     */
    public void setTipoIdentificativoUnivoco(StTipoIdentificativoUnivocoPersFG value) {
        this.tipoIdentificativoUnivoco = value;
    }

    /**
     * Gets the value of the codiceIdentificativoUnivoco property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodiceIdentificativoUnivoco() {
        return codiceIdentificativoUnivoco;
    }

    /**
     * Sets the value of the codiceIdentificativoUnivoco property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodiceIdentificativoUnivoco(String value) {
        this.codiceIdentificativoUnivoco = value;
    }

}
