package it.tredi.ecm.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.Authenticator;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.engine.jdbc.ReaderInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.PagDovutiLog;
import it.tredi.ecm.dao.entity.PagPagatiLog;
import it.tredi.ecm.dao.entity.Pagamento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.QuotaAnnuale;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.repository.EventoRepository;
import it.tredi.ecm.dao.repository.FileRepository;
import it.tredi.ecm.dao.repository.PagDovutiLogRepository;
import it.tredi.ecm.dao.repository.PagPagatiLogRepository;
import it.tredi.ecm.dao.repository.PagamentoRepository;
import it.tredi.ecm.service.bean.EngineeringProperties;
import it.tredi.ecm.utils.HttpAuthenticateProxy;
import it.tredi.ecm.utils.Utils;
import it.veneto.regione.pagamenti.ente.FaultBean;
import it.veneto.regione.pagamenti.ente.PaaSILInviaDovuti;
import it.veneto.regione.pagamenti.ente.PaaSILInviaDovutiRisposta;
import it.veneto.regione.pagamenti.ente.pagamentitelematicidovutipagati.PagamentiTelematiciDovutiPagati;
import it.veneto.regione.pagamenti.ente.pagamentitelematicidovutipagati.PagamentiTelematiciDovutiPagatiService;
import it.veneto.regione.pagamenti.ente.ppthead.IntestazionePPT;
import it.veneto.regione.schemas._2012.pagamenti.ente.CtDatiSingoloPagamentoPagati;
import it.veneto.regione.schemas._2012.pagamenti.ente.CtDatiSingoloVersamentoDovuti;
import it.veneto.regione.schemas._2012.pagamenti.ente.CtDatiVersamentoDovuti;
import it.veneto.regione.schemas._2012.pagamenti.ente.CtDovuti;
import it.veneto.regione.schemas._2012.pagamenti.ente.CtIdentificativoUnivocoPersonaFG;
import it.veneto.regione.schemas._2012.pagamenti.ente.CtPagati;
import it.veneto.regione.schemas._2012.pagamenti.ente.CtSoggettoPagatore;
import it.veneto.regione.schemas._2012.pagamenti.ente.StTipoIdentificativoUnivocoPersFG;

@Service
public class EngineeringServiceImpl implements EngineeringService {

	@Autowired private FileRepository fileRepository;
	@Autowired private PagamentoService pagamentoService;
	@Autowired private EventoRepository eventoRepository;
	@Autowired private PagDovutiLogRepository invioDovutiRepository;
	@Autowired private PagPagatiLogRepository chiediPagatiRepository;
	@Autowired private EngineeringProperties engineeringProperties;
	@Autowired private ProviderService providerService;
	@Autowired private QuotaAnnualeService quotaAnnualeService;

	/** Non credo varierà, ma meglio parametrizzare e settare su file o tabella di configurazione */
	public static final String VERSIONE = "6.0";

	/** Permette tutti i tipi di pagamento. Si può modificare se necessario impedire certe forme di pagamento (vedi documentazione) */
	public static final String TIPO_VERSAMENTO_ALL = "ALL";
	public static String ENDPOINT_PAGAMENTI = ""; 

	// costanti
	public static final String ENTE_NON_VALIDO = "PAA_ENTE_NON_VALIDO"; // codice IPA Ente non valido o password errata
	public static final String ID_SESSION_NON_VALIDO = "PAA_ID_SESSION_NON_VALIDO"; // idSession non valido
	public static final String PAGAMENTO_NON_INIZIATO = "PAA_PAGAMENTO_NON_INIZIATO"; // pagamento non iniziato
	public static final String PAGAMENTO_IN_CORSO = "PAA_PAGAMENTO_IN_CORSO"; // pagamento in corso
	public static final String PAGAMENTO_ANNULLATO = "PAA_PAGAMENTO_ANNULLATO"; // pagamento annullato
	public static final String PAGAMENTO_SCADUTO = "PAA_PAGAMENTO_SCADUTO"; // pagamento scaduto

	public static final String PAGAMENTO_ESEGUITO = "0";
	public static final String PAGAMENTO_NON_ESEGUITO = "1";
	public static final String PAGAMENTO_PARZIALMENTE_ESEGUITO = "2";
	public static final String DECORRENZA_TERMINI = "3";
	public static final String DECORRENZA_TERMINI_PARZIALE = "4";

	public static final String CAUSALE_PAGAMENTO_EVENTO = "Pagamento Evento";
	public static final String CAUSALE_PAGAMENTO_QUOTA_PROVIDER = "Pagamento Quota Accreditamento Provider - anno ";

	public static final Logger LOGGER = Logger.getLogger(EngineeringServiceImpl.class);

	private static JAXBContext jCtDovutiContext = null;
	private static JAXBContext jCtPagatiContext = null;
	private static JAXBContext jPaaSILInviaDovutiContext = null;

	@PostConstruct
	public void init(){
		ENDPOINT_PAGAMENTI = engineeringProperties.getEndpointPagamenti();
		setProxy();
	}

	protected static ThreadLocal<DateFormat> fmt = new ThreadLocal<DateFormat>() {
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyyMMdd'T'HHmmssSSS'Z'");
		}
	};

	protected static ThreadLocal<Transformer> tf = new ThreadLocal<Transformer>() {

		protected Transformer initialValue() {
			// An implementation of the TransformerFactory class is NOT guaranteed to be thread safe
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = null;
			try {
				transformer = tf.newTransformer();
			} catch (TransformerConfigurationException e) {
				LOGGER.error("Errore durante l'instanziazione del Transformer", e);
			}
			return transformer;
		}
	};

	protected static ThreadLocal<PagamentiTelematiciDovutiPagati> port = new ThreadLocal<PagamentiTelematiciDovutiPagati>() {

		protected PagamentiTelematiciDovutiPagati initialValue() {

			PagamentiTelematiciDovutiPagatiService service = new PagamentiTelematiciDovutiPagatiService();
			PagamentiTelematiciDovutiPagati port = service.getPagamentiTelematiciDovutiPagatiPort();

			BindingProvider bp = (BindingProvider) port;
			Map<String, Object> context = bp.getRequestContext();

			context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, ENDPOINT_PAGAMENTI);

			return port;
		}
	};

	public static synchronized JAXBContext getCtDovutiContext() throws JAXBException {
		if (jCtDovutiContext == null) {
			jCtDovutiContext = JAXBContext.newInstance(CtDovuti.class);
		}
		return jCtDovutiContext;
	}

	public static synchronized JAXBContext getCtPagatiContext() throws JAXBException {
		if (jCtPagatiContext == null) {
			jCtPagatiContext = JAXBContext.newInstance(CtPagati.class);
		}
		return jCtPagatiContext;
	}

	public static synchronized JAXBContext getPaaSILInviaDovutiContext() throws JAXBException {
		if (jPaaSILInviaDovutiContext == null) {
			jPaaSILInviaDovutiContext = JAXBContext.newInstance(PaaSILInviaDovuti.class);
		}
		return jPaaSILInviaDovutiContext;
	}


	/**
	 * Era prima nel costruttore, spostato qui per evitare errore autowired all' avvio
	 */
	public void setProxy(){
		if (engineeringProperties.isUseProxy()) {
			System.setProperty("https.proxyHost", engineeringProperties.getProxyHost());
			System.setProperty("https.proxyPort", engineeringProperties.getProxyPort());
			if (StringUtils.isNotBlank(engineeringProperties.getProxyUsername())) {
				Authenticator.setDefault(new HttpAuthenticateProxy(engineeringProperties.getProxyUsername(), engineeringProperties.getProxyPassword()));
			}
		}
	}

	public String pagaQuotaProvider(Long pagamentoId, String backURL) throws Exception {
		Pagamento p = pagamentoService.getPagamentoById(pagamentoId);
		String url = prepareDatiPagamentoPerQuotaAnnuale(p, p.getQuotaAnnuale(), CAUSALE_PAGAMENTO_QUOTA_PROVIDER + p.getQuotaAnnuale().getAnnoRiferimento(), backURL);
		return url;
	}

	public String pagaEvento(Long idEvento, String backURL) throws Exception {

		Evento e = eventoRepository.findOne(idEvento);

		Pagamento p = pagamentoService.getPagamentoByEvento(e);
		if (p == null) {
			p = new Pagamento();
			p.setEvento(e);
		}

		Provider soggetto = e.getProvider();


		// i provider sono Ragioni Sociali, valorizzo i dati obbligatori.
		p.setAnagrafica(soggetto.getDenominazioneLegale());
		//p.setCodiceFiscale(soggetto.getCodiceFiscale());
		p.setCodiceFiscale("");
		p.setPartitaIva(soggetto.getPartitaIva());
		p.setEmail(soggetto.getEmailStruttura());
		p.setTipoVersamento(EngineeringServiceImpl.TIPO_VERSAMENTO_ALL);
		p.setCausale("VERSAMENTO DI PROVA");
		p.setDatiSpecificiRiscossione(engineeringProperties.getDatiSpecificiRiscossione()); 

		// TODO E' necessario concordare un pattern per gli identificativi con 3D e RVE.
		String iud = StringUtils.rightPad(engineeringProperties.getServizio() + fmt.get().format(new Date()), 35, "0");
		LOGGER.info("IUD: " + iud);

		p.setIdentificativoUnivocoDovuto(iud);
		p.setImporto(e.getCosto());

		p.setDataInvio(new Date());
		pagamentoService.save(p);

		PaaSILInviaDovuti dovuti = createPagamentoMessage(p, backURL);

		IntestazionePPT header = new IntestazionePPT();
		header.setCodIpaEnte(engineeringProperties.getIpa());

		PaaSILInviaDovutiRisposta response = port.get().paaSILInviaDovuti(dovuti, header);

		p.setIdSession(response.getIdSession());		
		e = p.getEvento();
		e.setPagInCorso(true);

		PagDovutiLog log = new PagDovutiLog();

		log.setPagamento(p);
		log.setDataRichiesta(new Date());
		log.setEsito(response.getEsito());
		log.setIdSession(response.getIdSession());
		if (response.getFault() != null) {
			log.setFaultCode(response.getFault().getFaultCode());
			log.setFaultString(response.getFault().getFaultString());
			log.setFaultDescription(response.getFault().getDescription());
			e.setPagInCorso(false);
		}

		pagamentoService.save(p);
		eventoRepository.save(e);
		invioDovutiRepository.save(log);

		return response.getUrl();
	}

	private String prepareDatiPagamentoPerQuotaAnnuale(Pagamento p, QuotaAnnuale quotaAnnuale, String causale, String backURL) throws Exception{
		Provider soggetto = quotaAnnuale.getProvider();
		
		// i provider sono Ragioni Sociali, valorizzo i dati obbligatori.
		//		p.setAnagrafica(soggetto.getDenominazioneLegale());
		//		p.setPartitaIva(soggetto.getPartitaIva());
		//		p.setEmail(soggetto.getEmailStruttura());
		//		p.setTipoVersamento(EngineeringServiceImpl.TIPO_VERSAMENTO_ALL);
		//		p.setCausale(causale);
		p.setDatiSpecificiRiscossione(engineeringProperties.getDatiSpecificiRiscossione()); 

		// TODO E' necessario concordare un pattern per gli identificativi con 3D e RVE.
		String iud = StringUtils.rightPad(engineeringProperties.getServizio() + fmt.get().format(new Date()), 35, "0");
		LOGGER.info("IUD: " + iud);

		p.setIdentificativoUnivocoDovuto(iud);
		p.setDataInvio(new Date());
		pagamentoService.save(p);

		PaaSILInviaDovuti dovuti = createPagamentoMessage(p, backURL);

		IntestazionePPT header = new IntestazionePPT();
		header.setCodIpaEnte(engineeringProperties.getIpa());

		PaaSILInviaDovutiRisposta response = port.get().paaSILInviaDovuti(dovuti, header);

		p.setIdSession(response.getIdSession());		
		//soggetto.setPagInCorso(true);
		quotaAnnuale.setPagInCorso(true);

		PagDovutiLog log = new PagDovutiLog();

		log.setPagamento(p);
		log.setDataRichiesta(new Date());
		log.setEsito(response.getEsito());
		log.setIdSession(response.getIdSession());
		if (response.getFault() != null) {
			log.setFaultCode(response.getFault().getFaultCode());
			log.setFaultString(response.getFault().getFaultString());
			log.setFaultDescription(response.getFault().getDescription());
			//soggetto.setPagInCorso(false);
			quotaAnnuale.setPagInCorso(true);
		}

		pagamentoService.save(p);
		//providerService.save(soggetto);
		quotaAnnualeService.save(quotaAnnuale);
		invioDovutiRepository.save(log);
		return response.getUrl();
	}

	/**
	 * Crea l'oggetto PaaSILInviaDovuti per effettuare il pagamento su MyPay
	 * @param p l'oggetto Pagamento con le informazioni sul pagamento da effettuare
	 * @param backUrl l'url verso cui redirigere al termine o all'annullamento del pagamento.
	 * @return l'input per la chiamata al WS dei pagamenti
	 * @throws JAXBException 
	 * @throws TransformerException 
	 * @throws IOException 
	 */
	private PaaSILInviaDovuti createPagamentoMessage(Pagamento p, String backUrl) throws TransformerException, JAXBException, IOException {
		CtDatiSingoloVersamentoDovuti versamento = new CtDatiSingoloVersamentoDovuti();
		versamento.setCausaleVersamento(p.getCausale());
		versamento.setCommissioneCaricoPA(p.getCommissioneCaricoPa() != null ? BigDecimal.valueOf(p.getCommissioneCaricoPa()) : null);
		versamento.setDatiSpecificiRiscossione(p.getDatiSpecificiRiscossione());
		versamento.setIdentificativoTipoDovuto(engineeringProperties.getTipoDovuti());
		versamento.setIdentificativoUnivocoDovuto(p.getIdentificativoUnivocoDovuto());
		versamento.setImportoSingoloVersamento(BigDecimal.valueOf(p.getImporto()));

		CtDatiVersamentoDovuti datiVersamento = new CtDatiVersamentoDovuti();
		datiVersamento.setTipoVersamento(p.getTipoVersamento());
		datiVersamento.getDatiSingoloVersamento().add(versamento);

		CtIdentificativoUnivocoPersonaFG u = new CtIdentificativoUnivocoPersonaFG();
		u.setCodiceIdentificativoUnivoco(StringUtils.isNotBlank(p.getCodiceFiscale()) ? p.getCodiceFiscale() : p.getPartitaIva());
		u.setTipoIdentificativoUnivoco(StTipoIdentificativoUnivocoPersFG.G); // soggetto giuridico

		CtSoggettoPagatore soggettoPagatore = new CtSoggettoPagatore();
		soggettoPagatore.setAnagraficaPagatore(p.getAnagrafica());
		soggettoPagatore.setCapPagatore(p.getCap());
		soggettoPagatore.setCivicoPagatore(p.getCivico());
		soggettoPagatore.setEMailPagatore(p.getEmail());
		soggettoPagatore.setIdentificativoUnivocoPagatore(u); // SICURO?
		soggettoPagatore.setIndirizzoPagatore(p.getIndirizzo());
		soggettoPagatore.setLocalitaPagatore(p.getLocalita());
		soggettoPagatore.setNazionePagatore(p.getNazione());
		soggettoPagatore.setProvinciaPagatore(p.getProvincia());

		CtDovuti ctDovuti = new CtDovuti();
		ctDovuti.setDatiVersamento(datiVersamento);
		ctDovuti.setSoggettoPagatore(soggettoPagatore);
		ctDovuti.setVersioneOggetto(VERSIONE); 


		PaaSILInviaDovuti dovuti = new PaaSILInviaDovuti();
		dovuti.setPassword(engineeringProperties.getPassword());

		// uso la trasformazione per omettere la dichiarazione XML
		Transformer transformer = tf.get();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		ByteArrayOutputStream w = new ByteArrayOutputStream();
		transformer.transform(new JAXBSource(getCtDovutiContext(), ctDovuti), new StreamResult(w));

		dovuti.setDovuti(w.toByteArray());
		w.close();

		dovuti.setEnteSILInviaRispostaPagamentoUrl(backUrl);

		// marshalling in byte[]
		final Marshaller m2 = getPaaSILInviaDovutiContext().createMarshaller();
		m2.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m2.setProperty(Marshaller.JAXB_FRAGMENT, true);
		final StringWriter w2 = new StringWriter();
		m2.marshal(dovuti, w2);

		return dovuti;
	}

	/*
	 * TODO Da mettere nel Thread
	 * */
	public void esitoPagamentiEventi() throws Exception {
		Set<Pagamento> pagamenti = pagamentoService.getPagamentiEventiDaVerificare();

		Holder<FaultBean> fault;
		Holder<DataHandler> pagati;

		PagPagatiLog log;

		for (Pagamento p : pagamenti) {
			if (StringUtils.isNotBlank(p.getIdSession())) {
				fault = new Holder<FaultBean>();
				pagati = new Holder<DataHandler>();

				port.get().paaSILChiediPagati(engineeringProperties.getIpa(), engineeringProperties.getPassword(), p.getIdSession(), fault, pagati);

				// traccio la chiamata
				log = new PagPagatiLog();
				log.setPagamento(p);
				log.setDataRichiesta(new Date());
				log.setIdSession(p.getIdSession());

				// In presenza di un fault code posso avere 2 casi: pagamento in corso (ancora senza esito), oppure fallito (es timeout).
				if (fault != null && fault.value != null) {
					log.setFaultCode(fault.value.getFaultCode());
					log.setFaultDescription(fault.value.getDescription());
					log.setFaultString(fault.value.getFaultString());

					// Se fault e diverso da non iniziato o in corso, il pagamento è fallito e va ripetuto.
					if (!PAGAMENTO_IN_CORSO.equals(log.getFaultCode()) && !PAGAMENTO_NON_INIZIATO.equals(log.getFaultCode())) {
						Evento e = p.getEvento();
						e.setPagInCorso(false);
						eventoRepository.save(e);
					}

				} else if (pagati != null && pagati.value != null) {
					String xml = new String(java.util.Base64.getDecoder().decode(IOUtils.toByteArray(pagati.value.getInputStream())));
					StringReader reader = new StringReader(xml);
					final Unmarshaller um = getCtPagatiContext().createUnmarshaller();
					CtPagati pagatiXml = (CtPagati)um.unmarshal(reader);

					Evento e = p.getEvento();
					// se sono qui il pagamento e' concluso: potrebbe essere andato a buon fine o meno.
					e.setPagInCorso(false);
					log.setCodiceEsito(pagatiXml.getDatiPagamento().getCodiceEsitoPagamento());
					p.setCodiceEsito(log.getCodiceEsito());

					// Se esito = 0 allora il pagamento risulta eseguito correttamente. Altrimenti deve essere rifatto.
					if (PAGAMENTO_ESEGUITO.equals(p.getCodiceEsito())) {
						e.setPagato(true);
					} else {
						e.setPagato(false);
					}
					eventoRepository.save(e);

					CtDatiSingoloPagamentoPagati item = pagatiXml.getDatiPagamento().getDatiSingoloPagamento().get(0);
					p.setDataEsitoSingoloPagamento(item.getDataEsitoSingoloPagamento().getTime());
					p.setEsitoSingoloPagamento(item.getEsitoSingoloPagamento());
					p.setIdentificativoUnivocoRiscosse(item.getIdentificativoUnivocoRiscossione());
					p.setImportoTotalePagato(item.getSingoloImportoPagato().doubleValue());

					// informazioni ridondanti in caso qualcuno dovesse ripetere il pagamento posso risalire allo storico.
					log.setDataEsitoSingoloPagamento(item.getDataEsitoSingoloPagamento().getTime());
					log.setEsitoSingoloPagamento(item.getEsitoSingoloPagamento());
					log.setIdentificativoUnivocoRiscosse(item.getIdentificativoUnivocoRiscossione());
					log.setImportoTotalePagato(item.getSingoloImportoPagato().doubleValue());

					pagamentoService.save(p);

				}

				chiediPagatiRepository.save(log);

			}
		}

	}

	/*
	 * TODO Da mettere nel Thread
	 * */
	public void esitoPagamentiQuoteAnnuali() throws Exception {
		Set<Pagamento> pagamenti = quotaAnnualeService.getPagamentiProviderDaVerificare();
		
		Holder<FaultBean> fault;
		Holder<DataHandler> pagati;

		PagPagatiLog log;

		for (Pagamento p : pagamenti) {
			if (StringUtils.isNotBlank(p.getIdSession())) {
				fault = new Holder<FaultBean>();
				pagati = new Holder<DataHandler>();

				port.get().paaSILChiediPagati(engineeringProperties.getIpa(), engineeringProperties.getPassword(), p.getIdSession(), fault, pagati);

				// traccio la chiamata
				log = new PagPagatiLog();
				log.setPagamento(p);
				log.setDataRichiesta(new Date());
				log.setIdSession(p.getIdSession());

				// In presenza di un fault code posso avere 2 casi: pagamento in corso (ancora senza esito), oppure fallito (es timeout).
				if (fault != null && fault.value != null) {
					log.setFaultCode(fault.value.getFaultCode());
					log.setFaultDescription(fault.value.getDescription());
					log.setFaultString(fault.value.getFaultString());

					// Se fault e diverso da non iniziato o in corso, il pagamento è fallito e va ripetuto.
					if (!PAGAMENTO_IN_CORSO.equals(log.getFaultCode()) && !PAGAMENTO_NON_INIZIATO.equals(log.getFaultCode())) {
						QuotaAnnuale quotaAnnuale = p.getQuotaAnnuale();
						quotaAnnuale.setPagInCorso(false);
						quotaAnnualeService.save(quotaAnnuale);
					}
				} else if (pagati != null && pagati.value != null) {
					String xml = new String(java.util.Base64.getDecoder().decode(IOUtils.toByteArray(pagati.value.getInputStream())));
					StringReader reader = new StringReader(xml);
					final Unmarshaller um = getCtPagatiContext().createUnmarshaller();
					CtPagati pagatiXml = (CtPagati)um.unmarshal(reader);

					QuotaAnnuale quotaAnnuale = p.getQuotaAnnuale();
					quotaAnnuale.setPagInCorso(false);
					log.setCodiceEsito(pagatiXml.getDatiPagamento().getCodiceEsitoPagamento());
					p.setCodiceEsito(log.getCodiceEsito());

					// Se esito = 0 allora il pagamento risulta eseguito correttamente. Altrimenti deve essere rifatto.
					if (PAGAMENTO_ESEGUITO.equals(p.getCodiceEsito())) {
						quotaAnnuale.setPagato(true);
						//Se si riferisce al pagamento della prima quota per iscrizione -> abilito le funzionalita'
						if(quotaAnnuale.getPrimoAnno() != null && quotaAnnuale.getPrimoAnno().booleanValue())
							providerService.abilitaFunzionalitaAfterPagamento(quotaAnnuale.getProvider().getId());
						
					} else {
						quotaAnnuale.setPagato(false);
					}
					quotaAnnualeService.save(quotaAnnuale);

					CtDatiSingoloPagamentoPagati item = pagatiXml.getDatiPagamento().getDatiSingoloPagamento().get(0);
					p.setDataEsitoSingoloPagamento(item.getDataEsitoSingoloPagamento().getTime());
					p.setEsitoSingoloPagamento(item.getEsitoSingoloPagamento());
					p.setIdentificativoUnivocoRiscosse(item.getIdentificativoUnivocoRiscossione());
					p.setImportoTotalePagato(item.getSingoloImportoPagato().doubleValue());
					p.setDataPagamento(item.getDataEsitoSingoloPagamento().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
					
					// informazioni ridondanti in caso qualcuno dovesse ripetere il pagamento posso risalire allo storico.
					log.setDataEsitoSingoloPagamento(item.getDataEsitoSingoloPagamento().getTime());
					log.setEsitoSingoloPagamento(item.getEsitoSingoloPagamento());
					log.setIdentificativoUnivocoRiscosse(item.getIdentificativoUnivocoRiscossione());
					log.setImportoTotalePagato(item.getSingoloImportoPagato().doubleValue());

					pagamentoService.save(p);
				}
				chiediPagatiRepository.save(log);
			}
		}

	}

	public void azzeraPagamenti(Long idProvider) throws Exception {
		Iterable<PagDovutiLog> pdl = invioDovutiRepository.findAll();
		invioDovutiRepository.delete(pdl);


		Iterable<PagPagatiLog> ppl = chiediPagatiRepository.findAll();
		chiediPagatiRepository.delete(ppl);

		Iterable<Pagamento> p = pagamentoService.getAllPagamenti();
		pagamentoService.deleteAll(p);

		Set<Evento> evs = eventoRepository.findAllByProviderId(idProvider);
		for (Evento ev : evs) {
			ev.setPagato(false);
			ev.setPagInCorso(false);
			eventoRepository.save(ev);
		}
	}

	public void saveFileFirmato(String xml) throws Exception {

		xml = java.net.URLDecoder.decode(xml, "UTF-8");
		System.out.println(xml);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xml)));
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		String urlSignedBytes = xpath.compile("//FIRMA_FILES/DOCUMENTS/DOCUMENT/FILE/@urlSignedBytes").evaluate(doc);  
		String name = xpath.compile("//FIRMA_FILES/DOCUMENTS/DOCUMENT/FILE/@name").evaluate(doc);  
		String idString = xpath.compile("//FIRMA_FILES/DOCUMENTS/DOCUMENT/FILE/INFORMAZIONI").evaluate(doc);  

		File file = fileRepository.findOne(Long.parseLong(idString));

		InputStream is = null;
		URL url = new URL(urlSignedBytes);
		try {
			is = url.openStream();
			byte[] data = IOUtils.toByteArray(is);
			file.setNomeFile(name + ".p7m");
			file.setData(data);
			fileRepository.save(file);

		} finally {
			if (is != null) { is.close(); }
		}

	}

}
