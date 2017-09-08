package it.tredi.ecm.service;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
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
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.ProtoBatchLog;
import it.tredi.ecm.dao.entity.Protocollo;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.ActionAfterProtocollaEnum;
import it.tredi.ecm.dao.enumlist.MotivazioneDecadenzaEnum;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
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

	private static JAXBContext protocollaArrivoReqContext = null;
	private static JAXBContext protoBatchReqContext = null;

	public static String ENDPOINT_PROTOCOLLO = "";

	public static final String AVVENUTA_CONSEGNA = "avvenuta-consegna";
	
	@PostConstruct
	public void init(){
		ENDPOINT_PROTOCOLLO = engineeringProperties.getProtocolloEndpoint();
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

		if(file.isProtocollato()){
			throw new Exception("File già protocollato");
		}

		Protocollo protocollo = new Protocollo();
		protocollo.setFile(file);
		protocollo.setAccreditamento(accreditamento);
		protocollo.setActionAfterProtocollo(null);

		Provider provider = accreditamento.getProvider();
		Sede sedeLegale = provider.getSedeLegale();

		Mittente mittente = new Mittente();
		mittente.setTipoVettore(Vettore.SDI);
		mittente.setNominativo(provider.getDenominazioneLegale());
		mittente.setIndirizzo(sedeLegale.getIndirizzo());
		mittente.setCap(sedeLegale.getCap());
		mittente.setCitta(sedeLegale.getComune());
		//mittente.setProvincia(sedeLegale.getProvincia());

		protocollaArrivo(protocollo, mittente, fileAllegatiIds);
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

		Provider provider = accreditamento.getProvider();
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
		protocollaInUscita(protocollo, destinatari, fileAllegatiIds);
	}

	@Transactional
	private void protocollaArrivo(Protocollo protocollo, Mittente mittente) throws Exception {
		protocollaArrivo(protocollo, mittente, new HashSet<Long>());
	}

	@Transactional
	private void protocollaArrivo(Protocollo protocollo, Mittente mittente, Set<Long> fileAllegatiIds) throws Exception {
		Richiesta richiesta = buildRichiestaArrivo(protocollo, mittente, fileAllegatiIds);

		LapisWebSOAPType port = protocolloThreadLocal.get();

		Transformer transformer = tf.get();

		StringWriter writer = new StringWriter();
		transformer.transform(new JAXBSource(getProtocollaArrivoReqContext(), richiesta), new StreamResult(writer));

		String requestString = writer.toString();

		LOGGER.info("ProtocollaArrivo - " + requestString);

		if(ecmProperties.isDebugSaltaProtocollo()) {
			String start = "2016-01-01 00:00";
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime startDT = LocalDateTime.parse(start, formatter);
			long secsFrom = ChronoUnit.SECONDS.between(startDT, LocalDateTime.now());
			protocollo.setData(LocalDate.now());
			protocollo.setNumero((int)secsFrom);
			protocollo.setIdProtoBatch(null);
			protocollo.setStatoSpedizione(null);
			protocollo.setOggetto(Utils.buildOggetto(protocollo.getFile().getTipo(), protocollo.getAccreditamento().getProvider()));

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
	private void protocollaInUscita(Protocollo p, it.rve.protocollo.xsd.richiesta_protocollazione.Destinatari d) throws Exception {
		protocollaInUscita(p, d, new HashSet<Long>());
	}

	@Transactional
	private void protocollaInUscita(Protocollo p, it.rve.protocollo.xsd.richiesta_protocollazione.Destinatari d, Set<Long> fileAllegatiIds) throws Exception {
		String idProtoBatch = null;
		it.rve.protocollo.xsd.richiesta_protocollazione.Richiesta richiesta = null;
		if(ecmProperties.isDebugSaltaProtocollo()) {
			String start = "2016-01-01 00:00";
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime startDT = LocalDateTime.parse(start, formatter);
			long secsFrom = ChronoUnit.SECONDS.between(startDT, LocalDateTime.now());
			idProtoBatch = Long.toString(secsFrom);
		} else {
			LapisWebSOAPType port = protocolloThreadLocal.get();

			richiesta = buildRichiestaUscita(p,d, fileAllegatiIds);

			Transformer transformer = tf.get();

			StringWriter writer = new StringWriter();
			transformer.transform(new JAXBSource(getProtoBatchReqContext(), richiesta), new StreamResult(writer));

			String requestString = writer.toString();

			LOGGER.info("ProtocollaInUscita - " + requestString);

			idProtoBatch = port.protoBatch(requestString);
			LOGGER.debug(idProtoBatch);
		}

		p.setData(null);
		p.setNumero(null);
		p.setIdProtoBatch(idProtoBatch);
		p.setStatoSpedizione(null);
		p.setOggetto(Utils.buildOggetto(p.getFile().getTipo(), p.getAccreditamento().getProvider()));

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

	public void protoBatchLog() throws Exception {
		Set<Protocollo> protocolliInUscita = protocolloRepository.getProtocolliInUscita();
		LapisWebSOAPType port = protocolloThreadLocal.get();

		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

		if(ecmProperties.isDebugSaltaProtocollo()) {
			String start = "2016-01-01 00:00";
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime startDT = LocalDateTime.parse(start, formatter);
			long secsFrom = ChronoUnit.SECONDS.between(startDT, LocalDateTime.now());
			for (Protocollo p : protocolliInUscita) {
				p.setData(LocalDate.now());
				p.setNumero((int)secsFrom++);
				protocolloRepository.save(p);

				ProtoBatchLog plog = new ProtoBatchLog();
				plog.setCodStato("0");
				plog.setDtIns(null);
				plog.setDtUpd(null);
				plog.setLog("Inserimento in Debug Salta protocollazione");
				plog.setProtocollo(p);
				plog.setStato("debug-salta-protocollazione");

				protoBatchLogRepository.save(plog);
			}
		} else {
			for (Protocollo p : protocolliInUscita) {
				Object response = port.protoBatchLog(p.getIdProtoBatch());

				LOGGER.debug(response);

				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document xmlResponse = builder.parse(new InputSource(new StringReader(response.toString())));
				XPathFactory xPathfactory = XPathFactory.newInstance();
				XPath xpath = xPathfactory.newXPath();

				String stato = xpath.compile("//proto_batch/@stato").evaluate(xmlResponse);
				String cod_stato = xpath.compile("//proto_batch/@cod_stato").evaluate(xmlResponse);
				String dt_insert = xpath.compile("//proto_batch/@dt_insert").evaluate(xmlResponse);
				String dt_update = xpath.compile("//proto_batch/@dt_update").evaluate(xmlResponse);
	//			String cod_applicativo = xpath.compile("//proto_batch/@cod_applicativo").evaluate(xmlResponse);
				String n_proto = xpath.compile("//proto_batch/n_proto").evaluate(xmlResponse);
				String d_proto = xpath.compile("//proto_batch/d_proto").evaluate(xmlResponse);
				String log = xpath.compile("//protocollo/log").evaluate(xmlResponse);

				if (StringUtils.hasText(d_proto)) {
					//p.setData(new SimpleDateFormat("dd/MM/yyyy").parse(d_proto));
					p.setData(LocalDate.parse(d_proto, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
				}
				if (StringUtils.hasText(n_proto)) {
					p.setNumero(Integer.parseInt(n_proto));
				}
				protocolloRepository.save(p);

				ProtoBatchLog plog = new ProtoBatchLog();
				plog.setCodStato(cod_stato);
				plog.setDtIns(StringUtils.hasText(dt_insert) ? fmt.parse(dt_insert) : null);
				plog.setDtUpd(StringUtils.hasText(dt_update) ? fmt.parse(dt_update) : null);
				plog.setLog(log);
				plog.setProtocollo(p);
				plog.setStato(stato);

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
	public void getStatoSpedizione() throws Exception {
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();

		Set<Protocollo> protocolliInUscita = protocolloRepository.getStatoSpedizioneNonConsegnateENonInErrore();
		for (Protocollo p : protocolliInUscita) {
			String stato = null;
			String nr_spedizione = null;
			String dt_spedizione = null;
			if(ecmProperties.isDebugSaltaProtocollo()) {
				String start = "2016-01-01 00:00";
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
				LocalDateTime startDT = LocalDateTime.parse(start, formatter);
				long secsFrom = ChronoUnit.SECONDS.between(startDT, LocalDateTime.now());
				stato = AVVENUTA_CONSEGNA;
				nr_spedizione = Long.toString(secsFrom++);
				dt_spedizione = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			} else {

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

	//			String numero = xpath.compile("//protocollo/@numero").evaluate(xmlResultDocument);
	//			String data = xpath.compile("//protocollo/@data").evaluate(xmlResultDocument);
	//			String destinatario = xpath.compile("//protocollo/destinatario/@vettore").evaluate(xmlResultDocument);
	//			String ragione_sociale = xpath.compile("//protocollo/destinatario/@ragione_sociale").evaluate(xmlResultDocument);
	//			String riferimento = xpath.compile("//protocollo/destinatario/@riferimento").evaluate(xmlResultDocument);
	//			String indirizzo = xpath.compile("//protocollo/destinatario/@riferimento").evaluate(xmlResultDocument);
	//			String comune = xpath.compile("//protocollo/destinatario/@comune").evaluate(xmlResultDocument);
	//			String provincia = xpath.compile("//protocollo/destinatario/@provincia").evaluate(xmlResultDocument);
	//			String cap = xpath.compile("//protocollo/destinatario/@cap").evaluate(xmlResultDocument);
	//			String email = xpath.compile("//protocollo/destinatario/@email").evaluate(xmlResultDocument);
			}

			if(p.getStatoSpedizione() == null || !p.getStatoSpedizione().equals(stato)) {
				p.setStatoSpedizione(stato);
				protocolloRepository.save(p);

				ProtoBatchLog plog = new ProtoBatchLog();
				plog.setDtSpedizione(StringUtils.hasText(dt_spedizione) ? fmt.parse(dt_spedizione) : null);
				plog.setNSpedizione(nr_spedizione);
				plog.setProtocollo(p);
				plog.setStato(stato);
				protoBatchLogRepository.save(plog);

				//Verifico se deve essere eseguita qualche istruzione automatica dopo la protocollazione
				if(p.getAccreditamento() != null && stato != null && stato.equalsIgnoreCase(AVVENUTA_CONSEGNA)){
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

	private void bloccaProvider(Provider provider) {
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

		Provider provider = providerService.getProvider(providerId);
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
		protocollaInUscita(protocollo, destinatari);
	}
}
