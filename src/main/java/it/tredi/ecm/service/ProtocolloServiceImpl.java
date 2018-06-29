package it.tredi.ecm.service;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import it.peng.wr.webservice.protocollo.Corrispondente;
import it.peng.wr.webservice.protocollo.ObjectFactory;
import it.peng.wr.webservice.protocollo.Pecinviata;
import it.peng.wr.webservice.protocollo.Protocol;
import it.peng.wr.webservice.protocollo.ProtocolWebService;
import it.peng.wr.webservice.protocollo.Risultatoprotocollo;
import it.peng.wr.webservice.protocollo.WebServiceException;
import it.peng.wr.webservice.protocollo.WebServiceException_Exception;
import it.rve.protocollo.lapiswebsoap.LapisWebSOAPService;
import it.rve.protocollo.lapiswebsoap.LapisWebSOAPType;
import it.rve.protocollo.xsd.protocolla_arrivo.Allegati;
import it.rve.protocollo.xsd.protocolla_arrivo.Destinatari;
import it.rve.protocollo.xsd.protocolla_arrivo.DocumentoPrincipale;
import it.rve.protocollo.xsd.protocolla_arrivo.Files;
import it.rve.protocollo.xsd.protocolla_arrivo.Files.Documento;
import it.rve.protocollo.xsd.protocolla_arrivo.Mittente;
import it.rve.protocollo.xsd.protocolla_arrivo.Richiesta;
import it.rve.protocollo.xsd.protocolla_arrivo.Vettore;
import it.rve.protocollo.xsd.richiesta_protocollazione.Destinatari.Destinatario;
import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.FileData;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.ProtoBatchLog;
import it.tredi.ecm.dao.entity.Protocollo;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.ActionAfterProtocollaEnum;
import it.tredi.ecm.dao.enumlist.MotivazioneDecadenzaEnum;
import it.tredi.ecm.dao.enumlist.ProtocolloServiceVersioneEnum;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.enumlist.TipoWorkflowEnum;
import it.tredi.ecm.dao.enumlist.WorkflowTipoEnum;
import it.tredi.ecm.dao.repository.ProtoBatchLogRepository;
import it.tredi.ecm.dao.repository.ProtocolloRepository;
import it.tredi.ecm.service.bean.EcmProperties;
import it.tredi.ecm.service.bean.EngineeringProperties;
import it.tredi.ecm.utils.Utils;

@org.springframework.stereotype.Service
public class ProtocolloServiceImpl implements ProtocolloService {
	public static final Logger LOGGER = Logger.getLogger(ProtocolloServiceImpl.class);

	@Autowired private ProtocolloRepository protocolloRepository;
	@Autowired private ProtoBatchLogRepository protoBatchLogRepository;
	@Autowired private EngineeringProperties engineeringProperties;

	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private ProviderService providerService;
	@Autowired private FileService fileService;

	@Autowired private WorkflowService workflowService;
	@Autowired private EmailService emailService;
	@Autowired private EcmProperties ecmProperties;

	private Protocol protocolWRB = null;
 	private ProtocolWebService portWRB = null;
 	private ObjectFactory objectFactory = new ObjectFactory();

	private static JAXBContext protocollaArrivoReqContext = null;
	private static JAXBContext protoBatchReqContext = null;

	public static String ENDPOINT_PROTOCOLLO = "";

	public static final String AVVENUTA_CONSEGNA = "avvenuta-consegna";
	public static final String CONSEGNA_PEC_IN_CORSO = "consegna-pec-in-corso";
	public static final String PEC_NON_INVIATE = "pec-non-inviate";
	public static final String ERRORE = "errore";

	@PostConstruct
	public void init() throws MalformedURLException{
		ENDPOINT_PROTOCOLLO = engineeringProperties.getProtocolloEndpoint();
		if(engineeringProperties.getProtocolloServiceVersione().equalsIgnoreCase("webrainbow")) {
			protocolWRB = new Protocol(new URL(engineeringProperties.getProtocolloWebrainbowEndpoint()));
			portWRB = protocolWRB.getProtocolWebServicePort();
		}
	}

	public static synchronized JAXBContext getProtocollaArrivoReqContext() throws JAXBException {
		 if (protocollaArrivoReqContext == null) {
			 protocollaArrivoReqContext = JAXBContext.newInstance(Richiesta.class);
	     }
	     return protocollaArrivoReqContext;
	}

	public static synchronized JAXBContext getProtoBatchReqContext() throws JAXBException {
		 if (protoBatchReqContext == null) {
			 protoBatchReqContext = JAXBContext.newInstance(it.rve.protocollo.xsd.richiesta_protocollazione.Richiesta.class);
	     }
	     return protoBatchReqContext;
	}

	protected static ThreadLocal<Transformer> tf = new ThreadLocal<Transformer>() {

		protected Transformer initialValue() {
			// An implementation of the TransformerFactory class is NOT guaranteed to be thread safe
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = null;
			try {
				transformer = tf.newTransformer();
			} catch (TransformerConfigurationException e) {
				// LOGGER.error("Errore durante l'instanziazione del Transformer", e);
			}
			return transformer;
		}
	};

	protected static ThreadLocal<LapisWebSOAPType> protocolloThreadLocal = new ThreadLocal<LapisWebSOAPType>() {

		protected LapisWebSOAPType initialValue() {

			LapisWebSOAPService service = new LapisWebSOAPService();
			LapisWebSOAPType port = service.getLapisWebSOAPPort();


			BindingProvider bp = (BindingProvider) port;
			Map<String, Object> context = bp.getRequestContext();

			context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, ENDPOINT_PROTOCOLLO);

			return port;
		}
	};

	protected static ThreadLocal<Dispatch<Source>> dispatchThreadLocal = new ThreadLocal<Dispatch<Source>>() {

		protected Dispatch<Source> initialValue() {

			QName serviceQname = new QName("http://localhost/LapisWebSOAP", "LapisWebSOAPService");
			QName portName = new QName("http://localhost/LapisWebSOAP", "LapisWebSOAPPort");
			try {
				Service service = Service.create(new URL("classpath:LapisWebSoap.wsdl"), serviceQname);
				Dispatch<Source> dispatch = service.createDispatch(portName, Source.class, Service.Mode.PAYLOAD);

				Map<String, Object> requestContext = dispatch.getRequestContext();
				requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, ENDPOINT_PROTOCOLLO);

				return dispatch;
			} catch (MalformedURLException e) {
				LOGGER.error("Errore durante la creazione del dispatcher", e);
				return null;
			}
		}
	};

	@Override
	public Protocollo getProtollo(Long id) {
		LOGGER.debug("Recupero Protocollo: " + id);
		return protocolloRepository.findOne(id);
	}

	@Override
	public Set<Protocollo> getAllProtocolli() {
		LOGGER.debug("Recupero Tutti i protocolli");
		return protocolloRepository.findAll();
	}

	@Override
	public Set<Protocollo> getAllProtocolliInUscitaErrati(){
		LOGGER.debug("Recupero Tutti i protocolli in uscita che non sono stati consegnati");
		return protocolloRepository.findAllWithErrors(AVVENUTA_CONSEGNA);
	}

	@Override
	@Transactional
	public void protocollaDomandaInArrivo(Long accreditamentoId, Long fileId) throws Exception{
		protocollaDomandaInArrivo(accreditamentoId, fileId, new HashSet<Long>());
	}

	@Override
	@Transactional
	public void protocollaDomandaInArrivo(Long accreditamentoId, Long fileId, Set<Long> fileAllegatiIds) throws Exception{
		LOGGER.info(Utils.getLogMessage("Richiesta Protocollazione In Arrivo della domanda: " + accreditamentoId + " sul file " + fileId));

		Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);
		File file = fileService.getFile(fileId);

		Protocollo protocollo = new Protocollo();
		protocollo.setFile(file);
		protocollo.setAccreditamento(accreditamento);
		protocollo.setActionAfterProtocollo(null);

		if (engineeringProperties.getProtocolloServiceVersione().equalsIgnoreCase("rv")) {
			protocollo.setProtocolloServiceVersion(ProtocolloServiceVersioneEnum.RV);
			protocollaInEntrata_RV(protocollo, fileAllegatiIds);
		} else if (engineeringProperties.getProtocolloServiceVersione().equalsIgnoreCase("webrainbow")) {
			protocollo.setProtocolloServiceVersion(ProtocolloServiceVersioneEnum.WEBRAINBOW);
			protocollaInEntrata_WebRainbow(protocollo, fileAllegatiIds);
		}
	}

	@Override
	@Transactional
	public void protocollaAllegatoFlussoDomandaInUscita(Long accreditamentoId, Long fileId) throws Exception {
		protocollaAllegatoFlussoDomandaInUscita(accreditamentoId, fileId, new HashSet<Long>());
	}

	@Override
	@Transactional
	public void protocollaAllegatoFlussoDomandaInUscita(Long accreditamentoId, Long fileId, Set<Long> fileAllegatiIds) throws Exception {
		LOGGER.info(Utils.getLogMessage("Richiesta Protocollazione In Uscita per il file " + fileId + " della domanda " + accreditamentoId));

		Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);
		File file = fileService.getFile(fileId);

		if(file.isProtocollato()){
			throw new Exception("File già protocollato");
		}

		Protocollo protocollo = new Protocollo();
		protocollo.setFile(file);
		protocollo.setAccreditamento(accreditamento);
		protocollo.setActionAfterProtocollo(ActionAfterProtocollaEnum.ESEGUI_TASK);

		if(engineeringProperties.getProtocolloServiceVersione().equalsIgnoreCase("rv")) {
			protocollo.setProtocolloServiceVersion(ProtocolloServiceVersioneEnum.RV);
			protocollaInUscita_RV(protocollo, fileAllegatiIds);
		} else if (engineeringProperties.getProtocolloServiceVersione().equalsIgnoreCase("webrainbow")) {
			protocollo.setProtocolloServiceVersion(ProtocolloServiceVersioneEnum.WEBRAINBOW);
			protocollaInUscita_WebRainbow(protocollo, fileAllegatiIds);
		}
	}

	/*****	WebRainbow	*****/
	private void protocollaInEntrata_WebRainbow(Protocollo protocollo, Set<Long> fileAllegatiIds) throws Exception {
		LOGGER.info(Utils.getLogMessage("Protocollazione WebRainbow in Entrata - IN CORSO..."));
		Provider provider = protocollo.getAccreditamento().getProvider();
		Sede sedeLegale = provider.getSedeLegale();

		Corrispondente mittente = new Corrispondente();
		mittente.setCap(objectFactory.createCorrispondenteCap(sedeLegale.getCap()));
		mittente.setCitta(objectFactory.createCorrispondenteCitta(sedeLegale.getComune()));
		mittente.setIndirizzo(objectFactory.createCorrispondenteIndirizzo(sedeLegale.getIndirizzo()));
		mittente.setNominativo(objectFactory.createCorrispondenteNominativo(provider.getDenominazioneLegale()));

		Corrispondente assegnatario = new Corrispondente();
		assegnatario.setNominativo(objectFactory.createCorrispondenteNominativo(engineeringProperties.getProtocolloWebrainbowUfficioCreatoreUscita()));
		List<Corrispondente> assegnatari = new ArrayList<>();
		assegnatari.add(assegnatario);

		List<it.peng.wr.webservice.protocollo.Documento> documenti = getDocumentoPrincipaleAndAllegati(protocollo.getFile(), fileAllegatiIds);

		String oggetto = Utils.buildOggetto(protocollo.getFile().getTipo(), protocollo.getAccreditamento().getProvider());

		if(ecmProperties.isDebugSaltaProtocollo()) {
			fakeProtocolloInEntrata(protocollo);
		}else {
			Risultatoprotocollo responseWRB = portWRB.creaProtocolloInEntrata(oggetto, mittente,
					engineeringProperties.getProtocolloWebrainbowUfficioCreatoreEntrata(), assegnatari, null, null, null, documenti);

			if(responseWRB.getCodice().getValue().equals("OK")) {
				LOGGER.info(Utils.getLogMessage("Protocollazione WebRainbow in Entrata - ESEGUITA"));
				LOGGER.info(Utils.getLogMessage("Codice: " + responseWRB.getCodice().getValue()));
				LOGGER.info(Utils.getLogMessage("Descrizione: " + responseWRB.getDescrizione().getValue()));
				LOGGER.info(Utils.getLogMessage("Numero: " + responseWRB.getNumeroProtocollo().getValue()));
				LOGGER.info(Utils.getLogMessage("Data: " + responseWRB.getDataRegistrazione().getValue()));
				LOGGER.info(Utils.getLogMessage("Id: " + responseWRB.getId().getValue()));

				String data = responseWRB.getDataRegistrazione().getValue();
				String numero = responseWRB.getNumeroProtocollo().getValue();

				protocollo.setData(LocalDate.parse(data, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
				protocollo.setNumero(Integer.parseInt(numero));
				protocollo.setIdProtoBatch(null);
				protocollo.setStatoSpedizione(null);
				protocollo.setOggetto(oggetto);
			}else {
				LOGGER.info(Utils.getLogMessage("Protocollazione WebRainbow in Entrata - ERRORE"));
				if(responseWRB != null) {
					LOGGER.info(Utils.getLogMessage("Codice: " + responseWRB.getCodice().getValue()));
					LOGGER.info(Utils.getLogMessage("Descrizione: " + responseWRB.getDescrizione().getValue()));
					LOGGER.info(Utils.getLogMessage("Numero: " + responseWRB.getNumeroProtocollo().getValue()));
					LOGGER.info(Utils.getLogMessage("Data: " + responseWRB.getDataRegistrazione().getValue()));
					LOGGER.info(Utils.getLogMessage("Id: " + responseWRB.getId().getValue()));
				}

				throw new Exception("Protocollazione WebRainbow in Entrata - ERRORE");
			}
		}
		protocolloRepository.save(protocollo);
	}

	private void protocollaInUscita_WebRainbow(Protocollo protocollo) throws Exception {
		protocollaInUscita_WebRainbow(protocollo, new HashSet<Long>());
	}

	@Transactional
	private void protocollaInUscita_WebRainbow(Protocollo protocollo, Set<Long> fileAllegatiIds) throws Exception{
		LOGGER.info(Utils.getLogMessage("Protocollazione WebRainbow in Uscita - IN CORSO..."));
		Provider provider = protocollo.getAccreditamento().getProvider();
		Sede sedeLegale = provider.getSedeLegale();
		Persona legaleRappresentante = provider.getLegaleRappresentante();

//		Corrispondente assegnatario = new Corrispondente();
//		assegnatario.setNominativo(objectFactory.createCorrispondenteNominativo(engineeringProperties.getProtocolloWebrainbowUfficioCreatoreUscita()));
//		List<Corrispondente> assegnatari = new ArrayList<>();
//		assegnatari.add(assegnatario);

		Corrispondente destinatario = new Corrispondente();
		destinatario.setNominativo(objectFactory.createCorrispondenteNominativo(provider.getDenominazioneLegale()));
		destinatario.setPec(objectFactory.createCorrispondentePec(legaleRappresentante.getAnagrafica().getPec()));
		if(sedeLegale != null) {
			destinatario.setIndirizzo(objectFactory.createCorrispondenteIndirizzo(sedeLegale.getIndirizzo()));
			destinatario.setCap(objectFactory.createCorrispondenteCap(sedeLegale.getCap()));
			destinatario.setCitta(objectFactory.createCorrispondenteCitta(sedeLegale.getComune()));
		}

		List<Corrispondente> destinatari = new ArrayList<>();
		destinatari.add(destinatario);

		List<it.peng.wr.webservice.protocollo.Documento> documenti = getDocumentoPrincipaleAndAllegati(protocollo.getFile(), fileAllegatiIds);

		String oggetto = Utils.buildOggetto(protocollo.getFile().getTipo(), protocollo.getAccreditamento().getProvider());

		if(ecmProperties.isDebugSaltaProtocollo()) {
			fakeProtocolloInUscita(protocollo);
		} else {
			Risultatoprotocollo responseWRB = portWRB.creaProtocolloInUscita(oggetto, engineeringProperties.getProtocolloWebrainbowUfficioCreatoreUscita(),
					destinatari, null, null, null, null, true, documenti);
			if(responseWRB.getCodice().getValue().equals("OK")) {
				LocalDate date = LocalDate.parse(responseWRB.getDataRegistrazione().getValue(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

				LOGGER.info(Utils.getLogMessage("Codice: " + responseWRB.getCodice().getValue()));
				LOGGER.info(Utils.getLogMessage("Descrizione: " + responseWRB.getDescrizione().getValue()));
				LOGGER.info(Utils.getLogMessage("Numero: " + responseWRB.getNumeroProtocollo().getValue()));
				LOGGER.info(Utils.getLogMessage("Data: " + responseWRB.getDataRegistrazione().getValue()));
				LOGGER.info(Utils.getLogMessage("Id: " + responseWRB.getId().getValue()));

				protocollo.setIdProtoBatch(null);
				protocollo.setNumero(Integer.parseInt(responseWRB.getNumeroProtocollo().getValue()));
				protocollo.setData(date);
				protocollo.setStatoSpedizione(null);
				protocollo.setPecInviata(false);
				protocollo.setOggetto(oggetto);
			}else {
				LOGGER.error(Utils.getLogMessage("Protocollazione WebRainbow in Uscita - ERRORE"));
				if(responseWRB != null) {
					LOGGER.info(Utils.getLogMessage("Codice: " + responseWRB.getCodice().getValue()));
					LOGGER.info(Utils.getLogMessage("Descrizione: " + responseWRB.getDescrizione().getValue()));
					LOGGER.info(Utils.getLogMessage("Numero: " + responseWRB.getNumeroProtocollo().getValue()));
					LOGGER.info(Utils.getLogMessage("Data: " + responseWRB.getDataRegistrazione().getValue()));
					LOGGER.info(Utils.getLogMessage("Id: " + responseWRB.getId().getValue()));
				}

				throw new Exception("Protocollazione WebRainbow in Uscita - ERRORE");
			}
		}

		protocolloRepository.save(protocollo);
	}

	@SuppressWarnings("unused")
	private List<it.peng.wr.webservice.protocollo.Documento> getDocumentoPrincipaleAndAllegati(File file, Set<Long> fileAllegatiIds){
		List<it.peng.wr.webservice.protocollo.Documento> documenti = new ArrayList<it.peng.wr.webservice.protocollo.Documento>();

		/* AGGIUNGO ALLEGATO PRINCIPALE */
		it.peng.wr.webservice.protocollo.Documento documentoPrincipale = objectFactory.createDocumento();
		documentoPrincipale.setId(objectFactory.createDocumentoId(file.getId().toString()));
		documentoPrincipale.setNomeFile(file.getNomeFile());
		documentoPrincipale.setStream(file.getData());
		documentoPrincipale.setMimeType(URLConnection.guessContentTypeFromName(file.getNomeFile()));
		documentoPrincipale.setTipo("1");
		documenti.add(documentoPrincipale);

		/* AGGIUNGO ALTRI ALLEGATI SE PRESENTI */
		File f = null;
		for(Long id : fileAllegatiIds) {
			try {
				f = fileService.getFile(id);

				it.peng.wr.webservice.protocollo.Documento doc = new it.peng.wr.webservice.protocollo.Documento();
				doc.setId(objectFactory.createDocumentoId(f.getId().toString()));
				doc.setNomeFile(f.getNomeFile());
				doc.setStream(f.getData());
				doc.setMimeType(URLConnection.guessContentTypeFromName(f.getNomeFile()));
				doc.setTipo("2");
				documenti.add(doc);
			} catch (Exception e) {
				LOGGER.error("Errore aggiunta documenti al protocollo : ", e);
			}
		}

		return documenti;
	}
	/*****	WebRainbow	*****/


	/*****	RV	*****/
	@Transactional
	private void protocollaInEntrata_RV(Protocollo protocollo, Set<Long> fileAllegatiIds) throws Exception {
		Provider provider = protocollo.getAccreditamento().getProvider();
		Sede sedeLegale = provider.getSedeLegale();

		Mittente mittente = new Mittente();
		mittente.setTipoVettore(Vettore.SDI);
		mittente.setNominativo(provider.getDenominazioneLegale());
		mittente.setIndirizzo(sedeLegale.getIndirizzo());
		mittente.setCap(sedeLegale.getCap());
		mittente.setCitta(sedeLegale.getComune());
		//mittente.setProvincia(sedeLegale.getProvincia());

		Richiesta richiesta = buildRichiestaArrivo(protocollo, mittente, fileAllegatiIds);

		LapisWebSOAPType port = protocolloThreadLocal.get();
		Transformer transformer = tf.get();

		StringWriter writer = new StringWriter();
		transformer.transform(new JAXBSource(getProtocollaArrivoReqContext(), richiesta), new StreamResult(writer));

		String requestString = writer.toString();

		LOGGER.info("ProtocollaArrivo - " + requestString);

		if(ecmProperties.isDebugSaltaProtocollo()) {
			fakeProtocolloInEntrata(protocollo);
		} else {
			Object response = port.protocollaArrivo(requestString);
			LOGGER.debug(response);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document xmlResponse = builder.parse(new InputSource(new StringReader(response.toString())));
			XPathFactory xPathfactory = XPathFactory.newInstance();
			XPath xpath = xPathfactory.newXPath();

			String numero = xpath.compile("//protocollo/numero").evaluate(xmlResponse);
			String data = xpath.compile("//protocollo/data").evaluate(xmlResponse);

			//p.setData(new SimpleDateFormat("dd-MM-yyyyy").parse(data));
			protocollo.setData(LocalDate.parse(data, DateTimeFormatter.ofPattern("dd-MM-yyyy")));
			protocollo.setNumero(Integer.parseInt(numero));
			protocollo.setIdProtoBatch(null);
			protocollo.setStatoSpedizione(null);
			protocollo.setOggetto(Utils.buildOggetto(protocollo.getFile().getTipo(), protocollo.getAccreditamento().getProvider()));
		}

		protocolloRepository.save(protocollo);
	}

	private Richiesta buildRichiestaArrivo(Protocollo p, Mittente m, Set<Long> fileAllegatiIds) throws Exception {
		Richiesta richiesta = new Richiesta();

		richiesta.setCodApplicativo(engineeringProperties.getProtocolloCodApplicativo());
		richiesta.setOperatore(engineeringProperties.getProtocolloOperatoreEntrata());
		richiesta.setIDC(engineeringProperties.getProtocolloIdc());
		richiesta.setOggetto(Utils.buildOggetto(p.getFile().getTipo(), p.getAccreditamento().getProvider()));
		richiesta.setMittente(m);

		Destinatari d = new Destinatari();
		d.setCodDestinatario(engineeringProperties.getProtocolloCodStruttura());
		richiesta.setDestinatari(d);

		Documento doc = new Documento();
		doc.setNome(p.getFile().getNomeFile());
		doc.setContent(p.getFile().getData());
		Files files = new Files();
		files.getDocumento().add(doc);

		DocumentoPrincipale documentoPrincipale = new DocumentoPrincipale();
		documentoPrincipale.setFiles(files);

		if(fileAllegatiIds != null && !fileAllegatiIds.isEmpty()) {

			Allegati allegati = new Allegati();

			List<Files> filesAllegati =	new ArrayList<Files>();

			for(Long id : fileAllegatiIds) {
				File allegato = fileService.getFile(id);

				Files.Documento docAllegato = new Files.Documento();

				Files filesAllegato = new Files();

				docAllegato.setNome(allegato.getNomeFile());
				docAllegato.setContent(allegato.getData());

				filesAllegato.getDocumento().add(docAllegato);

				filesAllegati.add(filesAllegato);

			}

			allegati.setFiles(filesAllegati);

			richiesta.setAllegati(allegati);
		}

		richiesta.setDocumentoPrincipale(documentoPrincipale);

		return richiesta;
	}

	@Transactional
	private void protocollaInUscita_RV(Protocollo p) throws Exception {
		protocollaInUscita_RV(p, new HashSet<Long>());
	}

	@Transactional
	private void protocollaInUscita_RV(Protocollo p, Set<Long> fileAllegatiIds) throws Exception {
		Provider provider = p.getAccreditamento().getProvider();
		Sede sedeLegale = provider.getSedeLegale();
		Persona legaleRappresentante = provider.getLegaleRappresentante();

		Destinatario destinatario = new Destinatario();
		destinatario.setNominativo(provider.getDenominazioneLegale());
		destinatario.setPEC(legaleRappresentante.getAnagrafica().getPec());
		destinatario.setTipoVettore(it.rve.protocollo.xsd.richiesta_protocollazione.Vettore.PEC);
		if(sedeLegale != null){
			destinatario.setIndirizzo(sedeLegale.getIndirizzo());
			destinatario.setCap(sedeLegale.getCap());
			destinatario.setCitta(sedeLegale.getComune());
		}

		it.rve.protocollo.xsd.richiesta_protocollazione.Destinatari destinatari = new it.rve.protocollo.xsd.richiesta_protocollazione.Destinatari();
		destinatari.getDestinatario().add(destinatario);

		String idProtoBatch = null;
		it.rve.protocollo.xsd.richiesta_protocollazione.Richiesta richiesta = null;
		if(ecmProperties.isDebugSaltaProtocollo()) {
			fakeProtocolloInUscita(p);
		} else {
			LapisWebSOAPType port = protocolloThreadLocal.get();

			richiesta = buildRichiestaUscita(p, destinatari, fileAllegatiIds);

			Transformer transformer = tf.get();

			StringWriter writer = new StringWriter();
			transformer.transform(new JAXBSource(getProtoBatchReqContext(), richiesta), new StreamResult(writer));

			String requestString = writer.toString();

			LOGGER.info("ProtocollaInUscita - " + requestString);

			idProtoBatch = port.protoBatch(requestString);
			LOGGER.debug(idProtoBatch);

			p.setData(null);
			p.setNumero(null);
			p.setIdProtoBatch(idProtoBatch);
			p.setStatoSpedizione(null);
			p.setOggetto(Utils.buildOggetto(p.getFile().getTipo(), p.getAccreditamento().getProvider()));
		}

		protocolloRepository.save(p);
	}

	private it.rve.protocollo.xsd.richiesta_protocollazione.Richiesta buildRichiestaUscita(Protocollo p, it.rve.protocollo.xsd.richiesta_protocollazione.Destinatari d, Set<Long> fileAllegatiIds) throws Exception {
		it.rve.protocollo.xsd.richiesta_protocollazione.Richiesta richiesta = new it.rve.protocollo.xsd.richiesta_protocollazione.Richiesta();

		String operatore = p.getFile().getOperatoreProtocollo();

		richiesta.setCodApplicativo(engineeringProperties.getProtocolloCodApplicativo());
		richiesta.setOperatore(operatore);
		richiesta.setIDC(engineeringProperties.getProtocolloIdc());
		richiesta.setOggetto(Utils.buildOggetto(p.getFile().getTipo(), p.getAccreditamento().getProvider()));
		richiesta.setCodMittente(engineeringProperties.getProtocolloCodStruttura());
		richiesta.setDestinatari(d);

		it.rve.protocollo.xsd.richiesta_protocollazione.Files.Documento doc =
				new it.rve.protocollo.xsd.richiesta_protocollazione.Files.Documento();
		doc.setNome(p.getFile().getNomeFile());
		doc.setContent(p.getFile().getData());
		it.rve.protocollo.xsd.richiesta_protocollazione.Files files =
				new it.rve.protocollo.xsd.richiesta_protocollazione.Files();
		files.getDocumento().add(doc);

		it.rve.protocollo.xsd.richiesta_protocollazione.DocumentoPrincipale documentoPrincipale =
				new it.rve.protocollo.xsd.richiesta_protocollazione.DocumentoPrincipale();
		documentoPrincipale.setFiles(files);

		if(fileAllegatiIds != null && !fileAllegatiIds.isEmpty()) {

			it.rve.protocollo.xsd.richiesta_protocollazione.Allegati allegati =
					new it.rve.protocollo.xsd.richiesta_protocollazione.Allegati();

			List<it.rve.protocollo.xsd.richiesta_protocollazione.Files> filesAllegati =
					new ArrayList<it.rve.protocollo.xsd.richiesta_protocollazione.Files>();

			for(Long id : fileAllegatiIds) {
				File allegato = fileService.getFile(id);

				it.rve.protocollo.xsd.richiesta_protocollazione.Files.Documento docAllegato =
						new it.rve.protocollo.xsd.richiesta_protocollazione.Files.Documento();

				it.rve.protocollo.xsd.richiesta_protocollazione.Files filesAllegato =
						new it.rve.protocollo.xsd.richiesta_protocollazione.Files();

				docAllegato.setNome(allegato.getNomeFile());
				docAllegato.setContent(allegato.getData());

				filesAllegato.getDocumento().add(docAllegato);

				filesAllegati.add(filesAllegato);

			}

			allegati.setFiles(filesAllegati);

			richiesta.setAllegati(allegati);
		}

		richiesta.setDocumentoPrincipale(documentoPrincipale);

		return richiesta;
	}
	/*****	RV	*****/

	/*****	THREAD FASE 1	*****/
	public void protoBatchLog() throws Exception {
		Set<Protocollo> protocolliInUscita = protocolloRepository.getProtocolliInUscita();

		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
		ProtoBatchLog plog = new ProtoBatchLog();
		if(ecmProperties.isDebugSaltaProtocollo()) {
			String start = "2016-01-01 00:00";
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime startDT = LocalDateTime.parse(start, formatter);
			long secsFrom = ChronoUnit.SECONDS.between(startDT, LocalDateTime.now());
			for (Protocollo p : protocolliInUscita) {
				if(ecmProperties.isDebugBrokeProtocollo()) {
					plog.setCodStato("0");
					plog.setDtIns(null);
					plog.setDtUpd(null);
					plog.setLog("Inserimento in Debug Broke protocollazione");
					plog.setProtocollo(p);
					plog.setStato("errore");
					protoBatchLogRepository.save(plog);
				}else {
					p.setData(LocalDate.now());
					p.setNumero((int)secsFrom++);
					protocolloRepository.save(p);

					plog.setCodStato("0");
					plog.setDtIns(null);
					plog.setDtUpd(null);
					plog.setLog("Inserimento in Debug Salta protocollazione");
					plog.setProtocollo(p);
					plog.setStato("debug-salta-protocollazione");

					protoBatchLogRepository.save(plog);
				}
			}
		} else {
			for (Protocollo p : protocolliInUscita) {
				String stato = "";
				String cod_stato = "";
				String dt_insert = "";
				String dt_update = "";
				String n_proto = "";
				String d_proto = "";
				String log = "";
				Boolean pecInviata = false;
				fmt = new SimpleDateFormat("dd/MM/yyyy");

				if(p.getProtocolloServiceVersion() == null || p.getProtocolloServiceVersion().equals(ProtocolloServiceVersioneEnum.RV)) {
					LapisWebSOAPType port = protocolloThreadLocal.get();
					Object response = port.protoBatchLog(p.getIdProtoBatch());

					LOGGER.debug(response);

					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document xmlResponse = builder.parse(new InputSource(new StringReader(response.toString())));
					XPathFactory xPathfactory = XPathFactory.newInstance();
					XPath xpath = xPathfactory.newXPath();

					stato = xpath.compile("//proto_batch/@stato").evaluate(xmlResponse);
					cod_stato = xpath.compile("//proto_batch/@cod_stato").evaluate(xmlResponse);
					dt_insert = xpath.compile("//proto_batch/@dt_insert").evaluate(xmlResponse);
					dt_update = xpath.compile("//proto_batch/@dt_update").evaluate(xmlResponse);
		//			String cod_applicativo = xpath.compile("//proto_batch/@cod_applicativo").evaluate(xmlResponse);
					n_proto = xpath.compile("//proto_batch/n_proto").evaluate(xmlResponse);
					d_proto = xpath.compile("//proto_batch/d_proto").evaluate(xmlResponse);
					log = xpath.compile("//protocollo/log").evaluate(xmlResponse);

					plog.setDtIns(StringUtils.hasText(dt_insert) ? fmt.parse(dt_insert) : null);
					plog.setDtUpd(StringUtils.hasText(dt_update) ? fmt.parse(dt_update) : null);

					if (StringUtils.hasText(d_proto)) {
						p.setData(LocalDate.parse(d_proto, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
					}
					if (StringUtils.hasText(n_proto)) {
						p.setNumero(Integer.parseInt(n_proto));
					}
					protocolloRepository.save(p);
				} else if (p.getProtocolloServiceVersion().equals(ProtocolloServiceVersioneEnum.WEBRAINBOW)) {
					it.peng.wr.webservice.protocollo.Protocollo protocolloWR = null;

					String numeroProtocollo = p.getNumeroFormattedWebRainbow();
					String dataProtocollo = p.getDataFormattedWebRainbow();
					protocolloWR = portWRB.getProtocollo(numeroProtocollo, dataProtocollo, false, true);

					//se il WS di WebRanbow risponde correttamente allora posso procedere con la verifica delle PEC e quindi non passare più da qui
					//settiamo a idProtoBatch=id del protocollo in modo tale da seguire la stessa logica del protcollo RV per il recupero dei protocolli in uscita da dare a GetStatoSpedizione
					if(protocolloWR.getDataRegistrazione() != null && !protocolloWR.getDataRegistrazione().toString().isEmpty() &&
							protocolloWR.getNumeroProtocollo() != null && !protocolloWR.getNumeroProtocollo().isEmpty()) {
						dt_insert = protocolloWR.getDataRegistrazione().toString();
						stato = null;
						p.setIdProtoBatch(protocolloWR.getId().getValue());
						protocolloRepository.save(p);
					}else {
						dt_insert = dataProtocollo;
						stato = ERRORE;
					}

					dt_update = LocalDateTime.now().toString();

					plog.setDtIns(StringUtils.hasText(dt_insert) ? new SimpleDateFormat("yyyy-MM-ddXXX").parse(dt_insert) : null);
					plog.setDtUpd(StringUtils.hasText(dt_update) ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(dt_update) : null);
				}

				plog.setCodStato(cod_stato);
				plog.setLog(log);
				plog.setProtocollo(p);
				plog.setStato(stato);
				plog.setPecInviata(pecInviata);

				protoBatchLogRepository.save(plog);
			}
		}
	}

	/**
	 * Questo metodo e' stato gestito col dispatcher in quanto andava in errore sul namespace con JAX-WS.
	 * Credo ci sia un problema sulla response che restituisce il server, ma con SOAP UI e con questo metodo non crea problemi.
	 *
	 * @throws Exception
	 */
	/*****	THREAD FASE 2	*****/
	public void getStatoSpedizione() throws Exception {
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
		ProtoBatchLog plog = new ProtoBatchLog();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();

		Set<Protocollo> protocolliInUscita = protocolloRepository.getStatoSpedizioneNonConsegnateENonInErrore();
		for (Protocollo p : protocolliInUscita) {
			String stato = null;
			String nr_spedizione = null;
			String dt_spedizione = null;
			Boolean pecInviata = false;
			if(ecmProperties.isDebugSaltaProtocollo()) {
				String start = "2016-01-01 00:00";
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
				LocalDateTime startDT = LocalDateTime.parse(start, formatter);
				long secsFrom = ChronoUnit.SECONDS.between(startDT, LocalDateTime.now());
				stato = AVVENUTA_CONSEGNA;
				nr_spedizione = Long.toString(secsFrom++);
				dt_spedizione = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			} else {

				if(p.getProtocolloServiceVersion() == null || p.getProtocolloServiceVersion().equals(ProtocolloServiceVersioneEnum.RV)) {
					// creo la request
					Document request = builder.newDocument();

					Element root = request.createElement("getStatoSpedizione");
					request.appendChild(root);

					Element struttura = request.createElement("struttura");struttura.appendChild(request.createTextNode(engineeringProperties.getProtocolloCodStruttura()));root.appendChild(struttura);
					Element numero_proto = request.createElement("numero_proto");numero_proto.appendChild(request.createTextNode(Integer.toString(p.getNumero())));root.appendChild(numero_proto);
					Element data_proto = request.createElement("data_proto");data_proto.appendChild(request.createTextNode(p.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));root.appendChild(data_proto);
					Element cod_applicativo = request.createElement("cod_applicativo");cod_applicativo.appendChild(request.createTextNode(engineeringProperties.getProtocolloCodApplicativo()));root.appendChild(cod_applicativo);

					// invoco il WS
					Source response = dispatchThreadLocal.get().invoke(new DOMSource(request));

					// converto in DOM la risposta
					Document xmlResponse = builder.newDocument();
					Transformer transformer = tf.get();
					transformer.transform(response, new DOMResult(xmlResponse));

					XPathFactory xPathfactory = XPathFactory.newInstance();
					XPath xpath = xPathfactory.newXPath();

					LOGGER.debug(xpath.compile("//.").evaluate(xmlResponse));

					// estraggo l'xml innestato.
					//String xmlResult = xpath.compile("//getStatoSpedizioneResponse/getStatoSpedizioneReturn").evaluate(xmlResponse);
					String xmlResult = xpath.compile("/").evaluate(xmlResponse);

					// converto in DOM l'xml innestato per poter estrarre le informazioni tramite xpath.
					InputSource is = new InputSource();
					is.setCharacterStream(new StringReader(xmlResult));

					Document xmlResultDocument = builder.parse(is);

					stato = xpath.compile("//protocollo/destinatario/@stato").evaluate(xmlResultDocument);
					nr_spedizione = xpath.compile("//protocollo/destinatario/@nr_spedizione").evaluate(xmlResultDocument);
					dt_spedizione = xpath.compile("//protocollo/destinatario/@dt_spedizione").evaluate(xmlResultDocument);
					plog.setDtSpedizione(StringUtils.hasText(dt_spedizione) ? fmt.parse(dt_spedizione) : null);

				} else if (p.getProtocolloServiceVersion().equals(ProtocolloServiceVersioneEnum.WEBRAINBOW)) {
					it.peng.wr.webservice.protocollo.Protocollo protocolloWR = null;
					String numeroProtocollo = p.getNumeroFormattedWebRainbow();
					String dataProtocollo = p.getDataFormattedWebRainbow();
					protocolloWR = portWRB.getProtocollo(numeroProtocollo, dataProtocollo, false, true);

					if(protocolloWR.getDataRegistrazione() != null && !protocolloWR.getDataRegistrazione().toString().isEmpty() &&
							protocolloWR.getNumeroProtocollo() != null && !protocolloWR.getNumeroProtocollo().isEmpty()) {

						dt_spedizione = protocolloWR.getDataRegistrazione().toString();
						nr_spedizione = protocolloWR.getNumeroProtocollo();

						int dim = protocolloWR.getPecInviate().size();
						boolean allPecSent = false;
						LOGGER.info(dim + " Pec trovate per il protocollo WebRainbow " + nr_spedizione + " del " + dt_spedizione);
						if(dim > 0) {
							stato = CONSEGNA_PEC_IN_CORSO;
							int pecSent = 0;
							int pecNotSent = 0;

							//allPecSent = true;
							for (int i=0; (i<dim && pecNotSent == 0); i++) {
								LOGGER.info("getStatoPEC: " + protocolloWR.getPecInviate().get(i).getId().getValue());
								String status = portWRB.getStatoPEC(nr_spedizione, dt_spedizione, protocolloWR.getPecInviate().get(i).getId().getValue());
								LOGGER.info("getStatoPEC: " + protocolloWR.getPecInviate().get(i).getId().getValue() + " -> RESULT: " + status);
								if(status.equalsIgnoreCase("OK"))
									pecSent++;
								else
									pecNotSent++;
							}

							allPecSent = (dim == pecSent) ? true : false;

						}else {
							stato = PEC_NON_INVIATE;
						}

						pecInviata = allPecSent;
						if(pecInviata) {
							stato = AVVENUTA_CONSEGNA;
						}
					}else {
						stato = null;
						pecInviata = false;
					}

					plog.setPecInviata(pecInviata);
					plog.setDtSpedizione(StringUtils.hasText(dt_spedizione) ? new SimpleDateFormat("yyyy-MM-ddXXX").parse(dt_spedizione) : null);
				}
			}

			if(p.getStatoSpedizione() == null || !p.getStatoSpedizione().equals(stato)) {
				LOGGER.info("update statoSpedizione to: " + stato);
				p.setStatoSpedizione(stato);
				if(pecInviata) //Avoid adding pecInviata in protocolls with old service
					p.setPecInviata(pecInviata);
				protocolloRepository.save(p);

				plog.setNSpedizione(nr_spedizione);
				plog.setProtocollo(p);
				plog.setStato(stato);
				protoBatchLogRepository.save(plog);

				//Verifico se deve essere eseguita qualche istruzione automatica dopo la protocollazione
				if(p.getAccreditamento() != null && stato != null && (stato.equalsIgnoreCase(AVVENUTA_CONSEGNA))){
					if(p.getActionAfterProtocollo() == ActionAfterProtocollaEnum.ESEGUI_TASK) {
						LOGGER.info("ProtocolloID: " + p.getId() + " - Avanzamento Task per Accreditamento: " + p.getAccreditamento().getId());
						if(p.getAccreditamento().getStato() == AccreditamentoStatoEnum.ACCREDITATO_IN_PROTOCOLLAZIONE
								|| p.getAccreditamento().getStato() == AccreditamentoStatoEnum.DINIEGO_IN_PROTOCOLLAZIONE
								|| p.getAccreditamento().getStato() == AccreditamentoStatoEnum.RICHIESTA_INTEGRAZIONE_IN_PROTOCOLLAZIONE
								|| p.getAccreditamento().getStato() == AccreditamentoStatoEnum.RICHIESTA_PREAVVISO_RIGETTO_IN_PROTOCOLLAZIONE) {
							try {
								workflowService.eseguiTaskProtocolloEseguitoForAccreditamentoStateAndSystemUser(p.getAccreditamento());

							} catch(Exception e) {
								String msg = "Impossibile eseguire il task di avvenuta protocollazione sull'accreditamento id: " + p.getAccreditamento().getId() + " in stato: " + p.getAccreditamento().getStato() + " del provider " + p.getAccreditamento().getProvider().getDenominazioneLegale();
								LOGGER.error(msg);
								emailService.inviaAlertErroreDiSistema(msg);
							}
							try {
								p.getAccreditamento().setDataoraInvioProtocollazione(null);
								accreditamentoService.save(p.getAccreditamento());
							} catch(Exception e) {
								String msg = "Impossibile rimuovere la Data ora di invio protocollazione sull'accreditamento id: " + p.getAccreditamento().getId() + " in stato: " + p.getAccreditamento().getStato() + " del provider " + p.getAccreditamento().getProvider().getDenominazioneLegale();
								LOGGER.error(msg);
								emailService.inviaAlertErroreDiSistema(msg);
							}
						}
					}
					else if(p.getActionAfterProtocollo() == ActionAfterProtocollaEnum.MANCATO_PAGAMENTO_QUOTA ||
							p.getActionAfterProtocollo() == ActionAfterProtocollaEnum.SCADENZA_INSERIMENTO_DOMANDA_STANDARD ||
							p.getActionAfterProtocollo() == ActionAfterProtocollaEnum.BLOCCA_PER_RICHIESTA_PROVIDER) {
						LOGGER.info("ProtocolloID: " + p.getId() + " - Blocco del provider: " + p.getAccreditamento().getProvider());
						Provider provider = p.getAccreditamento().getProvider();
						bloccaProvider(provider);
						provider.setStatus(ProviderStatoEnum.SOSPESO);
						providerService.save(provider);
					}
				}
			}
		}
	}

	@Override
	public void bloccaProvider(Provider provider) {
		provider.setCanInsertAccreditamentoProvvisorio(false);
		provider.setCanInsertAccreditamentoStandard(false);
		provider.setCanInsertEvento(false);
		provider.setCanInsertPianoFormativo(false);
		provider.setCanInsertRelazioneAnnuale(false);
	}

	@Override
	@Transactional
	public void protocollaBloccoProviderInUscita(Long providerId, File fileDaProtocollare, MotivazioneDecadenzaEnum motivazione) throws Exception {
		LOGGER.info(Utils.getLogMessage("Richiesta Protocollazione In Uscita per il file " + fileDaProtocollare.getId() + " del provider " + providerId));

		//prende sempre l'ultimo accreditamento del provider a prescindere dallo stato
		Accreditamento accreditamento = accreditamentoService.getLastAccreditamentoForProviderId(providerId);

		if(fileDaProtocollare.isProtocollato()){
			throw new Exception("File già protocollato");
		}

		Protocollo protocollo = new Protocollo();
		protocollo.setFile(fileDaProtocollare);
		protocollo.setAccreditamento(accreditamento);

		if(motivazione == MotivazioneDecadenzaEnum.SCADENZA_INSERIMENTO_DOMANDA_STANDARD)
			protocollo.setActionAfterProtocollo(ActionAfterProtocollaEnum.SCADENZA_INSERIMENTO_DOMANDA_STANDARD);
		else if(motivazione == MotivazioneDecadenzaEnum.MANCATO_PAGAMENTO_QUOTA_ANNUALE)
			protocollo.setActionAfterProtocollo(ActionAfterProtocollaEnum.MANCATO_PAGAMENTO_QUOTA);
		else if(motivazione == MotivazioneDecadenzaEnum.RICHIESTA_PROVIDER)
			protocollo.setActionAfterProtocollo(ActionAfterProtocollaEnum.BLOCCA_PER_RICHIESTA_PROVIDER);

		if(engineeringProperties.getProtocolloServiceVersione().equalsIgnoreCase("rv")) {
			protocollo.setProtocolloServiceVersion(ProtocolloServiceVersioneEnum.RV);
			protocollaInUscita_RV(protocollo);
		} else if (engineeringProperties.getProtocolloServiceVersione().equalsIgnoreCase("webrainbow")) {
			protocollo.setProtocolloServiceVersion(ProtocolloServiceVersioneEnum.WEBRAINBOW);
			protocollaInUscita_WebRainbow(protocollo);
		}
	}

	private void fakeProtocolloInEntrata(Protocollo protocollo) throws Exception {
		String start = "2016-01-01 00:00";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime startDT = LocalDateTime.parse(start, formatter);
		long secsFrom = ChronoUnit.SECONDS.between(startDT, LocalDateTime.now());
		protocollo.setData(LocalDate.now());
		protocollo.setNumero((int)secsFrom);
		protocollo.setIdProtoBatch(null);
		protocollo.setStatoSpedizione(null);
		protocollo.setOggetto(Utils.buildOggetto(protocollo.getFile().getTipo(), protocollo.getAccreditamento().getProvider()));
	}

	private void fakeProtocolloInUscita(Protocollo protocollo) throws Exception {
		String start = "2016-01-01 00:00";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime startDT = LocalDateTime.parse(start, formatter);
		long secsFrom = ChronoUnit.SECONDS.between(startDT, LocalDateTime.now());
		String idProtoBatch = Long.toString(secsFrom);
		protocollo.setData(null);
		protocollo.setNumero(null);
		protocollo.setIdProtoBatch(idProtoBatch);
		protocollo.setStatoSpedizione(null);
		protocollo.setOggetto(Utils.buildOggetto(protocollo.getFile().getTipo(), protocollo.getAccreditamento().getProvider()));
	}
	@Override
	@Transactional
	public void annullaProtocollo(Long oldProtocolloId) throws Exception{
		LOGGER.info(Utils.getLogMessage("Annullamento protocollo " + oldProtocolloId + " - entering"));
		Protocollo oldProtocollo = getProtollo(oldProtocolloId);
		Accreditamento accreditamento = oldProtocollo.getAccreditamento();
		File oldFile = oldProtocollo.getFile();

		oldProtocollo.setNumero(-99);//non viene pescato da protoBatchLog
		oldProtocollo.setStatoSpedizione("annullato");//non viene pescato da getStatoSpedizione
		oldProtocollo.setFile(null);//sgancio evenetuale file vecchio
		oldProtocollo.setAccreditamento(null);//sgancio accreditamento
		protocolloRepository.save(oldProtocollo);

		ProtoBatchLog pLog = new ProtoBatchLog();
		pLog.setCodStato("-99");
		pLog.setDtUpd(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(LocalDateTime.now().toString()));
		pLog.setLog("Annullato Protocollo per file: " + oldFile.getId() + " su accreditamento: " + accreditamento.getId() + " da Utente: " + Utils.getAuthenticatedUser().getAccount().getId());
		pLog.setStato("errore");
		protoBatchLogRepository.save(pLog);
		LOGGER.info(Utils.getLogMessage("Annullamento protocollo " + oldProtocolloId + " - exiting"));
	}


	@Override
	@Transactional
	public void rieseguiProtocollo(Long oldProtocolloId) throws Exception {
		LOGGER.info(Utils.getLogMessage("ReloadProtocolloInErrore " + oldProtocolloId + " - entering"));
		Protocollo oldProtocollo = getProtollo(oldProtocolloId);
		Accreditamento accreditamento = oldProtocollo.getAccreditamento();
		File oldFile = oldProtocollo.getFile();

		//Ripristiniamo solo se la domanda ha un flusso di Accreditamento in corso ed è in uno stato di "IN_PROTOCOLLAZIONE"
		if(oldProtocollo.isRieseguibile()) {
			annullaProtocollo(oldProtocolloId);
			accreditamentoService.changeState(accreditamento.getId(), accreditamento.getStato());
		}else {
			LOGGER.error("Impossibile annullare e ripetere la protocollazione " + oldProtocolloId + " in quanto la domanda non ha un flusso di accreditamento in corso!");
		}

		LOGGER.info(Utils.getLogMessage("ReloadProtocolloInErrore " + oldProtocolloId + " - exiting"));
	}
}
