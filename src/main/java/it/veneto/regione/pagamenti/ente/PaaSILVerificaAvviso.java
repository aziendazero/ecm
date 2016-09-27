
package it.veneto.regione.pagamenti.ente;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for paaSILVerificaAvviso complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="paaSILVerificaAvviso">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="password" type="{http://www.regione.veneto.it/pagamenti/ente/}stPassword" minOccurs="0"/>
 *         &lt;element name="identificativoUnivocoVersamento" type="{http://www.regione.veneto.it/pagamenti/ente/}stText35"/>
 *         &lt;element name="enteSILInviaRispostaPagamentoUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "paaSILVerificaAvviso", propOrder = {
    "password",
    "identificativoUnivocoVersamento",
    "enteSILInviaRispostaPagamentoUrl"
})
public class PaaSILVerificaAvviso {

    protected String password;
    @XmlElement(required = true)
    protected String identificativoUnivocoVersamento;
    @XmlElement(defaultValue = "")
    protected String enteSILInviaRispostaPagamentoUrl;

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
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
     * Gets the value of the enteSILInviaRispostaPagamentoUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnteSILInviaRispostaPagamentoUrl() {
        return enteSILInviaRispostaPagamentoUrl;
    }

    /**
     * Sets the value of the enteSILInviaRispostaPagamentoUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnteSILInviaRispostaPagamentoUrl(String value) {
        this.enteSILInviaRispostaPagamentoUrl = value;
    }

}
