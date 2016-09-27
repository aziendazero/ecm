
package it.veneto.regione.pagamenti.ente;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for paaSILAutorizzaImportFlussoRisposta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="paaSILAutorizzaImportFlussoRisposta">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.regione.veneto.it/pagamenti/ente/}risposta">
 *       &lt;sequence>
 *         &lt;element name="uploadUrl" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="authorizationToken" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="requestToken" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="importPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "paaSILAutorizzaImportFlussoRisposta", propOrder = {
    "uploadUrl",
    "authorizationToken",
    "requestToken",
    "importPath"
})
public class PaaSILAutorizzaImportFlussoRisposta
    extends Risposta
{

    @XmlElement(required = true)
    protected String uploadUrl;
    @XmlElement(required = true)
    protected String authorizationToken;
    @XmlElement(required = true)
    protected String requestToken;
    @XmlElement(required = true)
    protected String importPath;

    /**
     * Gets the value of the uploadUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUploadUrl() {
        return uploadUrl;
    }

    /**
     * Sets the value of the uploadUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUploadUrl(String value) {
        this.uploadUrl = value;
    }

    /**
     * Gets the value of the authorizationToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthorizationToken() {
        return authorizationToken;
    }

    /**
     * Sets the value of the authorizationToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthorizationToken(String value) {
        this.authorizationToken = value;
    }

    /**
     * Gets the value of the requestToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestToken() {
        return requestToken;
    }

    /**
     * Sets the value of the requestToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestToken(String value) {
        this.requestToken = value;
    }

    /**
     * Gets the value of the importPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImportPath() {
        return importPath;
    }

    /**
     * Sets the value of the importPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImportPath(String value) {
        this.importPath = value;
    }

}
