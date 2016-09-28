
package it.veneto.regione.schemas._2012.pagamenti.ente;

import java.util.Calendar;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter1;
import org.w3._2001.xmlschema.Adapter2;


/**
 * <p>Java class for ctPagati complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ctPagati">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="versioneOggetto" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}stText16"/>
 *         &lt;element name="dominio" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}ctDominio"/>
 *         &lt;element name="identificativoMessaggioRicevuta" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}stText35"/>
 *         &lt;element name="dataOraMessaggioRicevuta" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}stISODateTime"/>
 *         &lt;element name="riferimentoMessaggioRichiesta" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}stText35"/>
 *         &lt;element name="riferimentoDataRichiesta" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}stISODate"/>
 *         &lt;element name="istitutoAttestante" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}ctIstitutoAttestante"/>
 *         &lt;element name="enteBeneficiario" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}ctEnteBeneficiario"/>
 *         &lt;element name="soggettoPagatore" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}ctSoggettoPagatore"/>
 *         &lt;element name="datiPagamento" type="{http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/}ctDatiVersamentoPagati"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ctPagati", propOrder = {
    "versioneOggetto",
    "dominio",
    "identificativoMessaggioRicevuta",
    "dataOraMessaggioRicevuta",
    "riferimentoMessaggioRichiesta",
    "riferimentoDataRichiesta",
    "istitutoAttestante",
    "enteBeneficiario",
    "soggettoPagatore",
    "datiPagamento"
})
public class CtPagati {

    @XmlElement(required = true)
    protected String versioneOggetto;
    @XmlElement(required = true)
    protected CtDominio dominio;
    @XmlElement(required = true)
    protected String identificativoMessaggioRicevuta;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected Calendar dataOraMessaggioRicevuta;
    @XmlElement(required = true)
    protected String riferimentoMessaggioRichiesta;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    protected Calendar riferimentoDataRichiesta;
    @XmlElement(required = true)
    protected CtIstitutoAttestante istitutoAttestante;
    @XmlElement(required = true)
    protected CtEnteBeneficiario enteBeneficiario;
    @XmlElement(required = true)
    protected CtSoggettoPagatore soggettoPagatore;
    @XmlElement(required = true)
    protected CtDatiVersamentoPagati datiPagamento;

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
     * Gets the value of the dominio property.
     * 
     * @return
     *     possible object is
     *     {@link CtDominio }
     *     
     */
    public CtDominio getDominio() {
        return dominio;
    }

    /**
     * Sets the value of the dominio property.
     * 
     * @param value
     *     allowed object is
     *     {@link CtDominio }
     *     
     */
    public void setDominio(CtDominio value) {
        this.dominio = value;
    }

    /**
     * Gets the value of the identificativoMessaggioRicevuta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificativoMessaggioRicevuta() {
        return identificativoMessaggioRicevuta;
    }

    /**
     * Sets the value of the identificativoMessaggioRicevuta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificativoMessaggioRicevuta(String value) {
        this.identificativoMessaggioRicevuta = value;
    }

    /**
     * Gets the value of the dataOraMessaggioRicevuta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Calendar getDataOraMessaggioRicevuta() {
        return dataOraMessaggioRicevuta;
    }

    /**
     * Sets the value of the dataOraMessaggioRicevuta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataOraMessaggioRicevuta(Calendar value) {
        this.dataOraMessaggioRicevuta = value;
    }

    /**
     * Gets the value of the riferimentoMessaggioRichiesta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRiferimentoMessaggioRichiesta() {
        return riferimentoMessaggioRichiesta;
    }

    /**
     * Sets the value of the riferimentoMessaggioRichiesta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRiferimentoMessaggioRichiesta(String value) {
        this.riferimentoMessaggioRichiesta = value;
    }

    /**
     * Gets the value of the riferimentoDataRichiesta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public Calendar getRiferimentoDataRichiesta() {
        return riferimentoDataRichiesta;
    }

    /**
     * Sets the value of the riferimentoDataRichiesta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRiferimentoDataRichiesta(Calendar value) {
        this.riferimentoDataRichiesta = value;
    }

    /**
     * Gets the value of the istitutoAttestante property.
     * 
     * @return
     *     possible object is
     *     {@link CtIstitutoAttestante }
     *     
     */
    public CtIstitutoAttestante getIstitutoAttestante() {
        return istitutoAttestante;
    }

    /**
     * Sets the value of the istitutoAttestante property.
     * 
     * @param value
     *     allowed object is
     *     {@link CtIstitutoAttestante }
     *     
     */
    public void setIstitutoAttestante(CtIstitutoAttestante value) {
        this.istitutoAttestante = value;
    }

    /**
     * Gets the value of the enteBeneficiario property.
     * 
     * @return
     *     possible object is
     *     {@link CtEnteBeneficiario }
     *     
     */
    public CtEnteBeneficiario getEnteBeneficiario() {
        return enteBeneficiario;
    }

    /**
     * Sets the value of the enteBeneficiario property.
     * 
     * @param value
     *     allowed object is
     *     {@link CtEnteBeneficiario }
     *     
     */
    public void setEnteBeneficiario(CtEnteBeneficiario value) {
        this.enteBeneficiario = value;
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
     * Gets the value of the datiPagamento property.
     * 
     * @return
     *     possible object is
     *     {@link CtDatiVersamentoPagati }
     *     
     */
    public CtDatiVersamentoPagati getDatiPagamento() {
        return datiPagamento;
    }

    /**
     * Sets the value of the datiPagamento property.
     * 
     * @param value
     *     allowed object is
     *     {@link CtDatiVersamentoPagati }
     *     
     */
    public void setDatiPagamento(CtDatiVersamentoPagati value) {
        this.datiPagamento = value;
    }

}
