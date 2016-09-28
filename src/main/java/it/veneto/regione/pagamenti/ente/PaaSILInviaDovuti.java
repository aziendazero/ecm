
package it.veneto.regione.pagamenti.ente;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for paaSILInviaDovuti complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="paaSILInviaDovuti">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="password" type="{http://www.regione.veneto.it/pagamenti/ente/}stPassword" minOccurs="0"/>
 *         &lt;element name="dovuti" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *         &lt;element name="enteSILInviaRispostaPagamentoUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name="paaSILInviaDovuti")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "paaSILInviaDovuti", propOrder = {
    "password",
    "dovuti",
    "enteSILInviaRispostaPagamentoUrl"
})
public class PaaSILInviaDovuti {

    protected String password;
    @XmlElement(required = true)
    protected byte[] dovuti;
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
     * Gets the value of the dovuti property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getDovuti() {
        return dovuti;
    }

    /**
     * Sets the value of the dovuti property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setDovuti(byte[] value) {
        this.dovuti = ((byte[]) value);
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
