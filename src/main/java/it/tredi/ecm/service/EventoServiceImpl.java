package it.tredi.ecm.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.cogeaps.CogeapsCaricaResponse;
import it.tredi.ecm.cogeaps.CogeapsWsRestClient;
import it.tredi.ecm.cogeaps.Helper;
import it.tredi.ecm.cogeaps.XmlReportBuilder;
import it.tredi.ecm.cogeaps.XmlReportValidator;
import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoFAD;
import it.tredi.ecm.dao.entity.EventoFSC;
import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.RendicontazioneInviata;
import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.entity.ProgrammaGiornalieroRES;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.RendicontazioneInviataStatoEnum;
import it.tredi.ecm.dao.repository.EventoRepository;
import it.tredi.ecm.dao.repository.PersonaEventoRepository;
import it.tredi.ecm.dao.repository.PersonaFullEventoRepository;
import it.tredi.ecm.exception.EcmException;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EventoWrapper;

@Service
public class EventoServiceImpl implements EventoService {
	public static final Logger LOGGER = Logger.getLogger(Evento.class);

	@Autowired
	private EventoRepository eventoRepository;
	
	@Autowired private PersonaEventoRepository personaEventoRepository;
	@Autowired private PersonaFullEventoRepository personaFullEventoRepository;

	@Autowired
	private RendicontazioneInviataService rendicontazioneInviataService;	
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private CogeapsWsRestClient cogeapsWsRestClient;

	@Override
	public Evento getEvento(Long id) {
		LOGGER.debug("Recupero evento: " + id);
		return eventoRepository.findOne(id);
	}

	@Override
	public Set<Evento> getAllEventiFromProvider(Long providerId) {
		LOGGER.debug("Recupero eventi del provider: " + providerId);
		return eventoRepository.findAllByProviderId(providerId);
	}

	@Override
	@Transactional
	public void save(Evento evento) {
		LOGGER.debug("Salvataggio evento");
		if(evento.isNew()) {
			eventoRepository.saveAndFlush(evento);
			evento.buildPrefix();
		}
		eventoRepository.save(evento);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		LOGGER.debug("Eliminazione evento:" + id);
		eventoRepository.delete(id);
	}

	@Override
	public void validaRendiconto(Long id, File rendiconto) throws Exception {
		Evento evento = getEvento(id);

		String fileName = rendiconto.getNomeFile();
		if (fileName.trim().toUpperCase().endsWith(".CSV")) { //CSV -> produzione XML
			rendiconto.setTipo(FileEnum.FILE_REPORT_PARTECIPANTI_CSV);
			evento.setReportPartecipantiCSV(rendiconto);

			//produzione xml da csv
			byte []xml_b = null;
			try {
				xml_b = XmlReportBuilder.buildXMLReportForCogeaps(rendiconto.getData(), evento);
			}
			catch (Exception e) {
				throw new EcmException("error.csv_to_xml_report_error", e.getMessage(), e);
			}

			//xsd validation
			try {
				XmlReportValidator.validateXmlWithXsd(rendiconto.getNomeFile(), xml_b, Helper.getSchemaEvento_1_16_XSD());
			}
			catch (Exception e) {
				throw new EcmException("error.xml_validation", e.getMessage(), e);
			}

			//salvo file xml
			File rendicontoXml = new File(FileEnum.FILE_REPORT_PARTECIPANTI_XML);
			rendicontoXml.setNomeFile(Helper.createReportXmlFileName());
			rendicontoXml.setData(xml_b);
			evento.setReportPartecipantiXML(rendicontoXml);
			fileService.save(rendicontoXml);
		}
		else { //XML, XML.P7M, XML.ZIP.P7M
			evento.setReportPartecipantiCSV(null);
			rendiconto.setTipo(FileEnum.FILE_REPORT_PARTECIPANTI_XML);
			evento.setReportPartecipantiXML(rendiconto);

			//evento validation (rispetto al db)
			try {
				XmlReportValidator.validateEventoXmlWithDb(rendiconto.getNomeFile(), rendiconto.getData(), evento);
			}
			catch (Exception e) {
				throw new EcmException("error.xml_evento_validation_with_db", e.getMessage(), e);
			}

			//xsd validation
			try {
				XmlReportValidator.validateXmlWithXsd(rendiconto.getNomeFile(), rendiconto.getData(), Helper.getSchemaEvento_1_16_XSD());
			}
			catch (Exception e) {
				throw new EcmException("error.xml_validation", e.getMessage(), e);
			}
		}
		save(evento);
	}

	@Override
	public List<Evento> getAllEventi() {
		LOGGER.debug("Recupero tutti gli eventi");
		return eventoRepository.findAll();
	}

	@Override
	public Set<Evento> getAllEventiForProviderId(Long providerId) {
		LOGGER.debug("Recupero tutti gli eventi del provider: " + providerId);
		return eventoRepository.findAllByProviderId(providerId);
	}

	@Override
	public boolean canCreateEvento(Account account) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Evento handleRipetibiliAndAllegati(EventoWrapper eventoWrapper) {
		Evento evento = eventoWrapper.getEvento();
		
		if(evento instanceof EventoRES){
			//date intermedie
			Set<LocalDate> dateIntermedie = new HashSet<LocalDate>();
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			for (String s : eventoWrapper.getDateIntermedieTemp()) {
				if(s != null && !s.isEmpty()) {
					LocalDate data = LocalDate.parse(s, dtf);
					dateIntermedie.add(data);
				}
			}
			((EventoRES) evento).setDateIntermedie(dateIntermedie);
			
			//Risultati Attesi
			((EventoRES) evento).setRisultatiAttesi(eventoWrapper.getRisultatiAttesiTemp());
			
			//Docenti
			Iterator<PersonaEvento> it = eventoWrapper.getDocenti().iterator();
			while(it.hasNext()){
				PersonaEvento p = it.next();
				p = personaEventoRepository.findOne(p.getId());
			}
			((EventoRES)evento).setDocenti(eventoWrapper.getDocenti());
			
			//Programma evento
			((EventoRES) evento).setProgramma(eventoWrapper.getProgrammaEventoRES());
			for(ProgrammaGiornalieroRES p : ((EventoRES) evento).getProgramma()){
				p.setEventoRES((EventoRES) evento);
			}
			
			//Documento Verifica Ricadute Formative
			if (eventoWrapper.getDocumentoVerificaRicaduteFormative().getId() != null) {
				((EventoRES) evento).setDocumentoVerificaRicaduteFormative(eventoWrapper.getDocumentoVerificaRicaduteFormative());
			}
		}else if(evento instanceof EventoFSC){
			//TODO campi solo in EVENTO FSC
		}else if(evento instanceof EventoFAD){
			//TODO campi solo in EVENTO FAD
		}
		
		//Responsabili
		Iterator<PersonaEvento> it = eventoWrapper.getResponsabiliScientifici().iterator();
		while(it.hasNext()){
			PersonaEvento p = it.next();
			p = personaEventoRepository.findOne(p.getId());
		}
		evento.setResponsabili(eventoWrapper.getResponsabiliScientifici());
		
		//brochure
		if (eventoWrapper.getBrochure().getId() != null) {
			evento.setBrochureEvento(eventoWrapper.getBrochure());
		}

		//Autocertificazione Assenza Finanziamenti
		if (eventoWrapper.getAutocertificazioneAssenzaFinanziamenti().getId() != null) {
			evento.setAutocertificazioneAssenzaFinanziamenti(eventoWrapper.getAutocertificazioneAssenzaFinanziamenti());
		}

		//Contratti Accordi Convenzioni
		if (eventoWrapper.getContrattiAccordiConvenzioni().getId() != null) {
			evento.setContrattiAccordiConvenzioni(eventoWrapper.getContrattiAccordiConvenzioni());
		}

		//Dichiarazione Assenza Conflitto Interesse
		if (eventoWrapper.getDichiarazioneAssenzaConflittoInteresse().getId() != null) {
			evento.setDichiarazioneAssenzaConflittoInteresse(eventoWrapper.getDichiarazioneAssenzaConflittoInteresse());
		}

		return evento;
	}
	
	@Override
	public void inviaRendicontoACogeaps(Long id) throws Exception {
		Evento evento = getEvento(id);
		try {
			RendicontazioneInviata ultimaRendicontazioneInviata = evento.getUltimaRendicontazioneInviata();
			if (ultimaRendicontazioneInviata != null && ultimaRendicontazioneInviata.getStato().equals(RendicontazioneInviataStatoEnum.PENDING)) //se ultima elaborazione pendente -> invio non concesso
				throw new Exception("error.elaborazione_pendente");
			
			String reportFileName = evento.getReportPartecipantiXML().getNomeFile();
			if (!reportFileName.trim().toUpperCase().endsWith(".P7M")) { //file non firmato -> invio non concesso
				throw new Exception("error.file_non_firmato");
			}
			
			CogeapsCaricaResponse cogeapsCaricaResponse = cogeapsWsRestClient.carica(reportFileName, evento.getReportPartecipantiXML().getData(), evento.getProvider().getCodiceCogeaps());

			if (cogeapsCaricaResponse.getStatus() != 0) //errore HTTP (auth...)
				throw new Exception(cogeapsCaricaResponse.getError() + ": " + cogeapsCaricaResponse.getMessage());
			if (cogeapsCaricaResponse.getErrCode() != 0) //errore su provider
				throw new Exception(cogeapsCaricaResponse.getErrMsg());

			//salvataggio entity rendicontazione_inviata (siamo sicuri che il file sia stato preso in carico dal cogeaps)
			RendicontazioneInviata rendicontazioneInviata = new RendicontazioneInviata();
			rendicontazioneInviata.setEvento(evento);
			rendicontazioneInviata.setFileName(cogeapsCaricaResponse.getNomeFile());
			rendicontazioneInviata.setResponse(cogeapsCaricaResponse.getResponse());
			rendicontazioneInviata.setFileRendicontazione(evento.getReportPartecipantiXML());
			rendicontazioneInviata.setDataInvio(LocalDateTime.now());
			rendicontazioneInviata.setStato(RendicontazioneInviataStatoEnum.PENDING);
			rendicontazioneInviata.setAccountInvio(Utils.getAuthenticatedUser().getAccount());
			rendicontazioneInviataService.save(rendicontazioneInviata);
		}
		catch (Exception e) {
			throw new EcmException("error.invio_report_cogeaps", e.getMessage(), e);
		}
		
	}

	@Override
	public EventoWrapper prepareRipetibiliAndAllegati(EventoWrapper eventoWrapper) {
		Evento evento = eventoWrapper.getEvento();

		//programma evento
		eventoWrapper.setResponsabiliScientifici(evento.getResponsabili());
		if(evento instanceof EventoRES){
			//date intermedie
			List<String> dateIntermedieTemp = new ArrayList<String>();
			for (LocalDate d : ((EventoRES) evento).getDateIntermedie()) {
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String dataToString = d.format(dtf);
				dateIntermedieTemp.add(dataToString);
			}
			eventoWrapper.setDateIntermedieTemp(dateIntermedieTemp);
			
			//risultati attesi
			eventoWrapper.setRisultatiAttesiTemp(((EventoRES) evento).getRisultatiAttesi());
			
			//Docenti
			eventoWrapper.setDocenti(((EventoRES) evento).getDocenti());
			
			//Programma
			eventoWrapper.setProgrammaEventoRES(((EventoRES) evento).getProgramma());
			
			//Documento Verifica Ricadute Formative
			if (((EventoRES) evento).getDocumentoVerificaRicaduteFormative() != null) {
				eventoWrapper.setDocumentoVerificaRicaduteFormative(((EventoRES) evento).getDocumentoVerificaRicaduteFormative());
			}
		}else if(evento instanceof EventoFSC){
			//TODO campi solo in EVENTO FSC
		}else if(evento instanceof EventoFAD){
			//TODO campi solo in EVENTO FAD
		}
		
		//brochure
		if (evento.getBrochureEvento() != null) {
			eventoWrapper.setBrochure(evento.getBrochureEvento());
		}

		//Autocertificazione Assenza Finanziamenti
		if (evento.getAutocertificazioneAssenzaFinanziamenti() != null) {
			eventoWrapper.setAutocertificazioneAssenzaFinanziamenti(evento.getAutocertificazioneAssenzaFinanziamenti());
		}

		//Contratti Accordi Convenzioni
		if (evento.getContrattiAccordiConvenzioni() != null) {
			eventoWrapper.setContrattiAccordiConvenzioni(evento.getContrattiAccordiConvenzioni());
		}

		//Dichiarazione Assenza Conflitto Interesse
		if (evento.getDichiarazioneAssenzaConflittoInteresse() != null) {
			eventoWrapper.setDichiarazioneAssenzaConflittoInteresse(evento.getDichiarazioneAssenzaConflittoInteresse());
		}

		return eventoWrapper;
	}

}
