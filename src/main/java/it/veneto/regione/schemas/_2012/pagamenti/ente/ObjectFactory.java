
package it.veneto.regione.schemas._2012.pagamenti.ente;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.veneto.regione.schemas._2012.pagamenti.ente package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Dovuti_QNAME = new QName("http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/", "Dovuti");
    private final static QName _Pagati_QNAME = new QName("http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/", "Pagati");
    private final static QName _Versamento_QNAME = new QName("http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/", "Versamento");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.veneto.regione.schemas._2012.pagamenti.ente
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CtEnteBeneficiario }
     * 
     */
    public CtEnteBeneficiario createCtEnteBeneficiario() {
        return new CtEnteBeneficiario();
    }

    /**
     * Create an instance of {@link CtDatiVersamentoPagati }
     * 
     */
    public CtDatiVersamentoPagati createCtDatiVersamentoPagati() {
        return new CtDatiVersamentoPagati();
    }

    /**
     * Create an instance of {@link CtIstitutoAttestante }
     * 
     */
    public CtIstitutoAttestante createCtIstitutoAttestante() {
        return new CtIstitutoAttestante();
    }

    /**
     * Create an instance of {@link CtDatiSingoloVersamentoDovuti }
     * 
     */
    public CtDatiSingoloVersamentoDovuti createCtDatiSingoloVersamentoDovuti() {
        return new CtDatiSingoloVersamentoDovuti();
    }

    /**
     * Create an instance of {@link CtDovuti }
     * 
     */
    public CtDovuti createCtDovuti() {
        return new CtDovuti();
    }

    /**
     * Create an instance of {@link CtIdentificativoUnivoco }
     * 
     */
    public CtIdentificativoUnivoco createCtIdentificativoUnivoco() {
        return new CtIdentificativoUnivoco();
    }

    /**
     * Create an instance of {@link CtSoggettoPagatore }
     * 
     */
    public CtSoggettoPagatore createCtSoggettoPagatore() {
        return new CtSoggettoPagatore();
    }

    /**
     * Create an instance of {@link CtDominio }
     * 
     */
    public CtDominio createCtDominio() {
        return new CtDominio();
    }

    /**
     * Create an instance of {@link CtIdentificativoUnivocoPersonaG }
     * 
     */
    public CtIdentificativoUnivocoPersonaG createCtIdentificativoUnivocoPersonaG() {
        return new CtIdentificativoUnivocoPersonaG();
    }

    /**
     * Create an instance of {@link CtDatiVersamentoDovuti }
     * 
     */
    public CtDatiVersamentoDovuti createCtDatiVersamentoDovuti() {
        return new CtDatiVersamentoDovuti();
    }

    /**
     * Create an instance of {@link CtDatiVersamento }
     * 
     */
    public CtDatiVersamento createCtDatiVersamento() {
        return new CtDatiVersamento();
    }

    /**
     * Create an instance of {@link CtDatiSingoloPagamentoPagati }
     * 
     */
    public CtDatiSingoloPagamentoPagati createCtDatiSingoloPagamentoPagati() {
        return new CtDatiSingoloPagamentoPagati();
    }

    /**
     * Create an instance of {@link CtPagati }
     * 
     */
    public CtPagati createCtPagati() {
        return new CtPagati();
    }

    /**
     * Create an instance of {@link CtIdentificativoUnivocoPersonaFG }
     * 
     */
    public CtIdentificativoUnivocoPersonaFG createCtIdentificativoUnivocoPersonaFG() {
        return new CtIdentificativoUnivocoPersonaFG();
    }

    /**
     * Create an instance of {@link CtVersamento }
     * 
     */
    public CtVersamento createCtVersamento() {
        return new CtVersamento();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CtDovuti }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/", name = "Dovuti")
    public JAXBElement<CtDovuti> createDovuti(CtDovuti value) {
        return new JAXBElement<CtDovuti>(_Dovuti_QNAME, CtDovuti.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CtPagati }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/", name = "Pagati")
    public JAXBElement<CtPagati> createPagati(CtPagati value) {
        return new JAXBElement<CtPagati>(_Pagati_QNAME, CtPagati.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CtVersamento }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.regione.veneto.it/schemas/2012/Pagamenti/Ente/", name = "Versamento")
    public JAXBElement<CtVersamento> createVersamento(CtVersamento value) {
        return new JAXBElement<CtVersamento>(_Versamento_QNAME, CtVersamento.class, null, value);
    }

}
