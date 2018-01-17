
package it.peng.wr.webservice.protocollo;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.peng.wr.webservice.protocollo package. 
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

    private final static QName _TrasmettiPECResponse_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "trasmettiPECResponse");
    private final static QName _Ricevuta_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "Ricevuta");
    private final static QName _GetProtocolloResponse_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "getProtocolloResponse");
    private final static QName _RisultatoProtocollo_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "RisultatoProtocollo");
    private final static QName _GetDocumentoPrincipaleResponse_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "getDocumentoPrincipaleResponse");
    private final static QName _GetRicevutePECResponse_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "getRicevutePECResponse");
    private final static QName _WebServiceException_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "WebServiceException");
    private final static QName _CreaProtocolloInEntrata_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "creaProtocolloInEntrata");
    private final static QName _PECInviata_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "PECInviata");
    private final static QName _Protocollo_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "Protocollo");
    private final static QName _GetDocumentoPrincipale_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "getDocumentoPrincipale");
    private final static QName _CreaProtocolloInUscita_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "creaProtocolloInUscita");
    private final static QName _CreaProtocolloInEntrataResponse_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "creaProtocolloInEntrataResponse");
    private final static QName _Corrispondente_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "Corrispondente");
    private final static QName _TrasmettiPEC_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "trasmettiPEC");
    private final static QName _GetAllegati_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "getAllegati");
    private final static QName _GetProtocollo_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "getProtocollo");
    private final static QName _GetStatoPECResponse_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "getStatoPECResponse");
    private final static QName _GetAllegatiResponse_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "getAllegatiResponse");
    private final static QName _GetStatoPEC_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "getStatoPEC");
    private final static QName _CreaProtocolloInUscitaResponse_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "creaProtocolloInUscitaResponse");
    private final static QName _Documento_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "Documento");
    private final static QName _Riferimento_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "Riferimento");
    private final static QName _GetRicevutePEC_QNAME = new QName("http://protocollo.webservice.wr.peng.it/", "getRicevutePEC");
    private final static QName _RicevutaId_QNAME = new QName("", "id");
    private final static QName _RisultatoprotocolloDescrizione_QNAME = new QName("", "descrizione");
    private final static QName _RisultatoprotocolloCodice_QNAME = new QName("", "codice");
    private final static QName _RisultatoprotocolloLink_QNAME = new QName("", "link");
    private final static QName _RisultatoprotocolloNumeroProtocollo_QNAME = new QName("", "numeroProtocollo");
    private final static QName _RisultatoprotocolloDataRegistrazione_QNAME = new QName("", "dataRegistrazione");
    private final static QName _CorrispondenteCap_QNAME = new QName("", "cap");
    private final static QName _CorrispondenteNominativo_QNAME = new QName("", "nominativo");
    private final static QName _CorrispondenteIndirizzo_QNAME = new QName("", "indirizzo");
    private final static QName _CorrispondenteProvincia_QNAME = new QName("", "provincia");
    private final static QName _CorrispondenteFax_QNAME = new QName("", "fax");
    private final static QName _CorrispondenteCitta_QNAME = new QName("", "citta");
    private final static QName _CorrispondentePec_QNAME = new QName("", "pec");
    private final static QName _RiferimentoAnnoProtocollo_QNAME = new QName("", "annoProtocollo");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.peng.wr.webservice.protocollo
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Ricevuta }
     * 
     */
    public Ricevuta createRicevuta() {
        return new Ricevuta();
    }

    /**
     * Create an instance of {@link TrasmettiPECResponse }
     * 
     */
    public TrasmettiPECResponse createTrasmettiPECResponse() {
        return new TrasmettiPECResponse();
    }

    /**
     * Create an instance of {@link GetProtocolloResponse }
     * 
     */
    public GetProtocolloResponse createGetProtocolloResponse() {
        return new GetProtocolloResponse();
    }

    /**
     * Create an instance of {@link WebServiceException }
     * 
     */
    public WebServiceException createWebServiceException() {
        return new WebServiceException();
    }

    /**
     * Create an instance of {@link CreaProtocolloInEntrata }
     * 
     */
    public CreaProtocolloInEntrata createCreaProtocolloInEntrata() {
        return new CreaProtocolloInEntrata();
    }

    /**
     * Create an instance of {@link Risultatoprotocollo }
     * 
     */
    public Risultatoprotocollo createRisultatoprotocollo() {
        return new Risultatoprotocollo();
    }

    /**
     * Create an instance of {@link GetDocumentoPrincipaleResponse }
     * 
     */
    public GetDocumentoPrincipaleResponse createGetDocumentoPrincipaleResponse() {
        return new GetDocumentoPrincipaleResponse();
    }

    /**
     * Create an instance of {@link GetRicevutePECResponse }
     * 
     */
    public GetRicevutePECResponse createGetRicevutePECResponse() {
        return new GetRicevutePECResponse();
    }

    /**
     * Create an instance of {@link Pecinviata }
     * 
     */
    public Pecinviata createPecinviata() {
        return new Pecinviata();
    }

    /**
     * Create an instance of {@link Protocollo }
     * 
     */
    public Protocollo createProtocollo() {
        return new Protocollo();
    }

    /**
     * Create an instance of {@link GetDocumentoPrincipale }
     * 
     */
    public GetDocumentoPrincipale createGetDocumentoPrincipale() {
        return new GetDocumentoPrincipale();
    }

    /**
     * Create an instance of {@link CreaProtocolloInUscita }
     * 
     */
    public CreaProtocolloInUscita createCreaProtocolloInUscita() {
        return new CreaProtocolloInUscita();
    }

    /**
     * Create an instance of {@link Corrispondente }
     * 
     */
    public Corrispondente createCorrispondente() {
        return new Corrispondente();
    }

    /**
     * Create an instance of {@link CreaProtocolloInEntrataResponse }
     * 
     */
    public CreaProtocolloInEntrataResponse createCreaProtocolloInEntrataResponse() {
        return new CreaProtocolloInEntrataResponse();
    }

    /**
     * Create an instance of {@link TrasmettiPEC }
     * 
     */
    public TrasmettiPEC createTrasmettiPEC() {
        return new TrasmettiPEC();
    }

    /**
     * Create an instance of {@link GetAllegati }
     * 
     */
    public GetAllegati createGetAllegati() {
        return new GetAllegati();
    }

    /**
     * Create an instance of {@link GetStatoPECResponse }
     * 
     */
    public GetStatoPECResponse createGetStatoPECResponse() {
        return new GetStatoPECResponse();
    }

    /**
     * Create an instance of {@link GetProtocollo }
     * 
     */
    public GetProtocollo createGetProtocollo() {
        return new GetProtocollo();
    }

    /**
     * Create an instance of {@link GetAllegatiResponse }
     * 
     */
    public GetAllegatiResponse createGetAllegatiResponse() {
        return new GetAllegatiResponse();
    }

    /**
     * Create an instance of {@link CreaProtocolloInUscitaResponse }
     * 
     */
    public CreaProtocolloInUscitaResponse createCreaProtocolloInUscitaResponse() {
        return new CreaProtocolloInUscitaResponse();
    }

    /**
     * Create an instance of {@link GetStatoPEC }
     * 
     */
    public GetStatoPEC createGetStatoPEC() {
        return new GetStatoPEC();
    }

    /**
     * Create an instance of {@link Riferimento }
     * 
     */
    public Riferimento createRiferimento() {
        return new Riferimento();
    }

    /**
     * Create an instance of {@link Documento }
     * 
     */
    public Documento createDocumento() {
        return new Documento();
    }

    /**
     * Create an instance of {@link GetRicevutePEC }
     * 
     */
    public GetRicevutePEC createGetRicevutePEC() {
        return new GetRicevutePEC();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrasmettiPECResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "trasmettiPECResponse")
    public JAXBElement<TrasmettiPECResponse> createTrasmettiPECResponse(TrasmettiPECResponse value) {
        return new JAXBElement<TrasmettiPECResponse>(_TrasmettiPECResponse_QNAME, TrasmettiPECResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Ricevuta }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "Ricevuta")
    public JAXBElement<Ricevuta> createRicevuta(Ricevuta value) {
        return new JAXBElement<Ricevuta>(_Ricevuta_QNAME, Ricevuta.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetProtocolloResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "getProtocolloResponse")
    public JAXBElement<GetProtocolloResponse> createGetProtocolloResponse(GetProtocolloResponse value) {
        return new JAXBElement<GetProtocolloResponse>(_GetProtocolloResponse_QNAME, GetProtocolloResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Risultatoprotocollo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "RisultatoProtocollo")
    public JAXBElement<Risultatoprotocollo> createRisultatoProtocollo(Risultatoprotocollo value) {
        return new JAXBElement<Risultatoprotocollo>(_RisultatoProtocollo_QNAME, Risultatoprotocollo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDocumentoPrincipaleResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "getDocumentoPrincipaleResponse")
    public JAXBElement<GetDocumentoPrincipaleResponse> createGetDocumentoPrincipaleResponse(GetDocumentoPrincipaleResponse value) {
        return new JAXBElement<GetDocumentoPrincipaleResponse>(_GetDocumentoPrincipaleResponse_QNAME, GetDocumentoPrincipaleResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRicevutePECResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "getRicevutePECResponse")
    public JAXBElement<GetRicevutePECResponse> createGetRicevutePECResponse(GetRicevutePECResponse value) {
        return new JAXBElement<GetRicevutePECResponse>(_GetRicevutePECResponse_QNAME, GetRicevutePECResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link WebServiceException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "WebServiceException")
    public JAXBElement<WebServiceException> createWebServiceException(WebServiceException value) {
        return new JAXBElement<WebServiceException>(_WebServiceException_QNAME, WebServiceException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreaProtocolloInEntrata }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "creaProtocolloInEntrata")
    public JAXBElement<CreaProtocolloInEntrata> createCreaProtocolloInEntrata(CreaProtocolloInEntrata value) {
        return new JAXBElement<CreaProtocolloInEntrata>(_CreaProtocolloInEntrata_QNAME, CreaProtocolloInEntrata.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Pecinviata }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "PECInviata")
    public JAXBElement<Pecinviata> createPECInviata(Pecinviata value) {
        return new JAXBElement<Pecinviata>(_PECInviata_QNAME, Pecinviata.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Protocollo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "Protocollo")
    public JAXBElement<Protocollo> createProtocollo(Protocollo value) {
        return new JAXBElement<Protocollo>(_Protocollo_QNAME, Protocollo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDocumentoPrincipale }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "getDocumentoPrincipale")
    public JAXBElement<GetDocumentoPrincipale> createGetDocumentoPrincipale(GetDocumentoPrincipale value) {
        return new JAXBElement<GetDocumentoPrincipale>(_GetDocumentoPrincipale_QNAME, GetDocumentoPrincipale.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreaProtocolloInUscita }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "creaProtocolloInUscita")
    public JAXBElement<CreaProtocolloInUscita> createCreaProtocolloInUscita(CreaProtocolloInUscita value) {
        return new JAXBElement<CreaProtocolloInUscita>(_CreaProtocolloInUscita_QNAME, CreaProtocolloInUscita.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreaProtocolloInEntrataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "creaProtocolloInEntrataResponse")
    public JAXBElement<CreaProtocolloInEntrataResponse> createCreaProtocolloInEntrataResponse(CreaProtocolloInEntrataResponse value) {
        return new JAXBElement<CreaProtocolloInEntrataResponse>(_CreaProtocolloInEntrataResponse_QNAME, CreaProtocolloInEntrataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Corrispondente }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "Corrispondente")
    public JAXBElement<Corrispondente> createCorrispondente(Corrispondente value) {
        return new JAXBElement<Corrispondente>(_Corrispondente_QNAME, Corrispondente.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrasmettiPEC }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "trasmettiPEC")
    public JAXBElement<TrasmettiPEC> createTrasmettiPEC(TrasmettiPEC value) {
        return new JAXBElement<TrasmettiPEC>(_TrasmettiPEC_QNAME, TrasmettiPEC.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllegati }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "getAllegati")
    public JAXBElement<GetAllegati> createGetAllegati(GetAllegati value) {
        return new JAXBElement<GetAllegati>(_GetAllegati_QNAME, GetAllegati.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetProtocollo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "getProtocollo")
    public JAXBElement<GetProtocollo> createGetProtocollo(GetProtocollo value) {
        return new JAXBElement<GetProtocollo>(_GetProtocollo_QNAME, GetProtocollo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStatoPECResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "getStatoPECResponse")
    public JAXBElement<GetStatoPECResponse> createGetStatoPECResponse(GetStatoPECResponse value) {
        return new JAXBElement<GetStatoPECResponse>(_GetStatoPECResponse_QNAME, GetStatoPECResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAllegatiResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "getAllegatiResponse")
    public JAXBElement<GetAllegatiResponse> createGetAllegatiResponse(GetAllegatiResponse value) {
        return new JAXBElement<GetAllegatiResponse>(_GetAllegatiResponse_QNAME, GetAllegatiResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStatoPEC }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "getStatoPEC")
    public JAXBElement<GetStatoPEC> createGetStatoPEC(GetStatoPEC value) {
        return new JAXBElement<GetStatoPEC>(_GetStatoPEC_QNAME, GetStatoPEC.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreaProtocolloInUscitaResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "creaProtocolloInUscitaResponse")
    public JAXBElement<CreaProtocolloInUscitaResponse> createCreaProtocolloInUscitaResponse(CreaProtocolloInUscitaResponse value) {
        return new JAXBElement<CreaProtocolloInUscitaResponse>(_CreaProtocolloInUscitaResponse_QNAME, CreaProtocolloInUscitaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Documento }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "Documento")
    public JAXBElement<Documento> createDocumento(Documento value) {
        return new JAXBElement<Documento>(_Documento_QNAME, Documento.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Riferimento }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "Riferimento")
    public JAXBElement<Riferimento> createRiferimento(Riferimento value) {
        return new JAXBElement<Riferimento>(_Riferimento_QNAME, Riferimento.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRicevutePEC }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://protocollo.webservice.wr.peng.it/", name = "getRicevutePEC")
    public JAXBElement<GetRicevutePEC> createGetRicevutePEC(GetRicevutePEC value) {
        return new JAXBElement<GetRicevutePEC>(_GetRicevutePEC_QNAME, GetRicevutePEC.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "id", scope = Ricevuta.class)
    public JAXBElement<String> createRicevutaId(String value) {
        return new JAXBElement<String>(_RicevutaId_QNAME, String.class, Ricevuta.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "descrizione", scope = Risultatoprotocollo.class)
    public JAXBElement<String> createRisultatoprotocolloDescrizione(String value) {
        return new JAXBElement<String>(_RisultatoprotocolloDescrizione_QNAME, String.class, Risultatoprotocollo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "codice", scope = Risultatoprotocollo.class)
    public JAXBElement<String> createRisultatoprotocolloCodice(String value) {
        return new JAXBElement<String>(_RisultatoprotocolloCodice_QNAME, String.class, Risultatoprotocollo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "link", scope = Risultatoprotocollo.class)
    public JAXBElement<String> createRisultatoprotocolloLink(String value) {
        return new JAXBElement<String>(_RisultatoprotocolloLink_QNAME, String.class, Risultatoprotocollo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "numeroProtocollo", scope = Risultatoprotocollo.class)
    public JAXBElement<String> createRisultatoprotocolloNumeroProtocollo(String value) {
        return new JAXBElement<String>(_RisultatoprotocolloNumeroProtocollo_QNAME, String.class, Risultatoprotocollo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "dataRegistrazione", scope = Risultatoprotocollo.class)
    public JAXBElement<String> createRisultatoprotocolloDataRegistrazione(String value) {
        return new JAXBElement<String>(_RisultatoprotocolloDataRegistrazione_QNAME, String.class, Risultatoprotocollo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "id", scope = Risultatoprotocollo.class)
    public JAXBElement<String> createRisultatoprotocolloId(String value) {
        return new JAXBElement<String>(_RicevutaId_QNAME, String.class, Risultatoprotocollo.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "id", scope = Documento.class)
    public JAXBElement<String> createDocumentoId(String value) {
        return new JAXBElement<String>(_RicevutaId_QNAME, String.class, Documento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "cap", scope = Corrispondente.class)
    public JAXBElement<String> createCorrispondenteCap(String value) {
        return new JAXBElement<String>(_CorrispondenteCap_QNAME, String.class, Corrispondente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "nominativo", scope = Corrispondente.class)
    public JAXBElement<String> createCorrispondenteNominativo(String value) {
        return new JAXBElement<String>(_CorrispondenteNominativo_QNAME, String.class, Corrispondente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "indirizzo", scope = Corrispondente.class)
    public JAXBElement<String> createCorrispondenteIndirizzo(String value) {
        return new JAXBElement<String>(_CorrispondenteIndirizzo_QNAME, String.class, Corrispondente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "provincia", scope = Corrispondente.class)
    public JAXBElement<String> createCorrispondenteProvincia(String value) {
        return new JAXBElement<String>(_CorrispondenteProvincia_QNAME, String.class, Corrispondente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "fax", scope = Corrispondente.class)
    public JAXBElement<String> createCorrispondenteFax(String value) {
        return new JAXBElement<String>(_CorrispondenteFax_QNAME, String.class, Corrispondente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "citta", scope = Corrispondente.class)
    public JAXBElement<String> createCorrispondenteCitta(String value) {
        return new JAXBElement<String>(_CorrispondenteCitta_QNAME, String.class, Corrispondente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "pec", scope = Corrispondente.class)
    public JAXBElement<String> createCorrispondentePec(String value) {
        return new JAXBElement<String>(_CorrispondentePec_QNAME, String.class, Corrispondente.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "numeroProtocollo", scope = Riferimento.class)
    public JAXBElement<String> createRiferimentoNumeroProtocollo(String value) {
        return new JAXBElement<String>(_RisultatoprotocolloNumeroProtocollo_QNAME, String.class, Riferimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "annoProtocollo", scope = Riferimento.class)
    public JAXBElement<String> createRiferimentoAnnoProtocollo(String value) {
        return new JAXBElement<String>(_RiferimentoAnnoProtocollo_QNAME, String.class, Riferimento.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "id", scope = Pecinviata.class)
    public JAXBElement<String> createPecinviataId(String value) {
        return new JAXBElement<String>(_RicevutaId_QNAME, String.class, Pecinviata.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "id", scope = Protocollo.class)
    public JAXBElement<String> createProtocolloId(String value) {
        return new JAXBElement<String>(_RicevutaId_QNAME, String.class, Protocollo.class, value);
    }

}
