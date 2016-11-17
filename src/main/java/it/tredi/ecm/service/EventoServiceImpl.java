package it.tredi.ecm.service;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import it.tredi.ecm.cogeaps.CogeapsCaricaResponse;
import it.tredi.ecm.cogeaps.CogeapsStatoElaborazioneResponse;
import it.tredi.ecm.cogeaps.CogeapsWsRestClient;
import it.tredi.ecm.cogeaps.Helper;
import it.tredi.ecm.cogeaps.XmlReportBuilder;
import it.tredi.ecm.cogeaps.XmlReportValidator;
import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.AzioneRuoliEventoFSC;
import it.tredi.ecm.dao.entity.DettaglioAttivitaFAD;
import it.tredi.ecm.dao.entity.DettaglioAttivitaRES;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoFAD;
import it.tredi.ecm.dao.entity.EventoFSC;
import it.tredi.ecm.dao.entity.EventoPianoFormativo;
import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.dao.entity.FaseAzioniRuoliEventoFSCTypeA;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Partner;
import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.entity.PianoFormativo;
import it.tredi.ecm.dao.entity.ProgrammaGiornalieroRES;
import it.tredi.ecm.dao.entity.RendicontazioneInviata;
import it.tredi.ecm.dao.entity.RiepilogoFAD;
import it.tredi.ecm.dao.entity.RiepilogoRES;
import it.tredi.ecm.dao.entity.RiepilogoRuoliFSC;
import it.tredi.ecm.dao.entity.RuoloOreFSC;
import it.tredi.ecm.dao.entity.Sponsor;
import it.tredi.ecm.dao.entity.VerificaApprendimentoFAD;
import it.tredi.ecm.dao.enumlist.EventoStatoEnum;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.MetodoDiLavoroEnum;
import it.tredi.ecm.dao.enumlist.RendicontazioneInviataResultEnum;
import it.tredi.ecm.dao.enumlist.RendicontazioneInviataStatoEnum;
import it.tredi.ecm.dao.enumlist.RuoloFSCBaseEnum;
import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import it.tredi.ecm.dao.enumlist.TipoMetodologiaEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoRESEnum;
import it.tredi.ecm.dao.repository.EventoPianoFormativoRepository;
import it.tredi.ecm.dao.repository.EventoRepository;
import it.tredi.ecm.dao.repository.PartnerRepository;
import it.tredi.ecm.dao.repository.PersonaEventoRepository;
import it.tredi.ecm.dao.repository.SponsorRepository;
import it.tredi.ecm.exception.EcmException;
import it.tredi.ecm.service.bean.VerificaFirmaDigitale;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EventoRESProgrammaGiornalieroWrapper;
import it.tredi.ecm.web.bean.EventoWrapper;
import it.tredi.ecm.web.validator.FileValidator;

@Service
public class EventoServiceImpl implements EventoService {
	public static final Logger LOGGER = Logger.getLogger(Evento.class);

	@Autowired private EventoRepository eventoRepository;

	@Autowired private PersonaEventoRepository personaEventoRepository;
	@Autowired private SponsorRepository sponsorRepository;
	@Autowired private PartnerRepository partnerRepository;
	@Autowired private EventoPianoFormativoRepository eventoPianoFormativoRepository;
	@PersistenceContext EntityManager entityManager;


	@Autowired private RendicontazioneInviataService rendicontazioneInviataService;
	@Autowired private FileService fileService;
	@Autowired private CogeapsWsRestClient cogeapsWsRestClient;
	
	@Autowired private ProviderService providerService;
	@Autowired private FileValidator fileValidator;
	@Autowired private PianoFormativoService pianoFormativoService;
	
	@Override
	public Evento getEvento(Long id) {
		LOGGER.debug("Recupero evento: " + id);
		return eventoRepository.findOne(id);
	}

	@Override
	@Transactional
	public void save(Evento evento) {
		LOGGER.debug("Salvataggio evento");
		if(evento.isNew()) {
			eventoRepository.saveAndFlush(evento);
			evento.buildPrefix();
		}
		evento.setDataUltimaModifica(LocalDateTime.now());
		eventoRepository.save(evento);

//		if(evento.isEventoDaPianoFormativo() && !evento.getEventoPianoFormativo().isAttuato()) {
//			EventoPianoFormativo eventoPianoFormativo = evento.getEventoPianoFormativo();
//			eventoPianoFormativo.setAttuato(true);
//			eventoPianoFormativoRepository.save(eventoPianoFormativo);
//		}

		//se attuazione di evento del piano formativo aggiorna il flag
		//se attuazione di evento del piano formativo con data fine all'anno successivo...l'evento viene inserito nel piano formativo dell'anno successivo
		if(evento.isEventoDaPianoFormativo()){
			EventoPianoFormativo eventoPianoFormativo = evento.getEventoPianoFormativo();
			
			LocalDate dataFine = evento.getDataFine();
			if(dataFine != null){
				int annoPianoFormativo = dataFine.getYear();
				PianoFormativo pf = pianoFormativoService.getPianoFormativoAnnualeForProvider(evento.getProvider().getId(), annoPianoFormativo);
				if(pf == null){
					pf = pianoFormativoService.create(evento.getProvider().getId(), annoPianoFormativo);
				}
				
				
				pf.addEvento(eventoPianoFormativo);
				pianoFormativoService.save(pf);
			}
			
			if(!evento.getEventoPianoFormativo().isAttuato()){
				eventoPianoFormativo.setAttuato(true);
			}
			eventoPianoFormativoRepository.save(eventoPianoFormativo);
		}
		
	}

	@Override
	@Transactional
	public void delete(Long id) {
		LOGGER.debug("Eliminazione evento:" + id);

		//controllo se attuazione di un evento del piano formativo
		Evento evento = getEvento(id);
		if(evento.isEventoDaPianoFormativo() && evento.getEventoPianoFormativo().isAttuato()) {
			EventoPianoFormativo eventoPianoFormativo = evento.getEventoPianoFormativo();
			eventoPianoFormativo.setAttuato(false);
			eventoPianoFormativoRepository.save(eventoPianoFormativo);
		}

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
		return eventoRepository.findAll(new Sort(Direction.DESC, "dataUltimaModifica"));
	}

	@Override
	public Set<Evento> getAllEventiForProviderId(Long providerId) {
		LOGGER.debug("Recupero tutti gli eventi del provider: " + providerId);
		return eventoRepository.findAllByProviderIdOrderByDataUltimaModificaDesc(providerId);
	}

	@Override
	public boolean canCreateEvento(Account account) {
		// TODO Auto-generated method stub
		return true;
	}

	/*	SALVATAGGIO	*/
	@Override
	public Evento handleRipetibiliAndAllegati(EventoWrapper eventoWrapper) throws Exception{
		Evento evento = eventoWrapper.getEvento();

		calculateAutoCompilingData(eventoWrapper);

		if(evento instanceof EventoRES){
			EventoRES eventoRES = ((EventoRES) evento);

			//date intermedie e programma giornaliero
			eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().updateEventoRES();

			//Risultati Attesi
			Set<String> risultatiAttesi = new HashSet<String>();
			for (String s : eventoWrapper.getRisultatiAttesiMapTemp().values()) {
				if(s != null && !s.isEmpty()) {
					risultatiAttesi.add(s);
				}
			}
			eventoRES.setRisultatiAttesi(risultatiAttesi);

			//Docenti
			Iterator<PersonaEvento> it = eventoWrapper.getDocenti().iterator();
			List<PersonaEvento> attachedList = new ArrayList<PersonaEvento>();
			while(it.hasNext()){
				PersonaEvento p = it.next();
				p = personaEventoRepository.findOne(p.getId());
				attachedList.add(p);
			}
			eventoRES.setDocenti(attachedList);

			//retrieveProgrammaAndAddJoin(eventoWrapper);

			//Documento Verifica Ricadute Formative
			if (eventoWrapper.getDocumentoVerificaRicaduteFormative().getId() != null) {
				eventoRES.setDocumentoVerificaRicaduteFormative(eventoWrapper.getDocumentoVerificaRicaduteFormative());
			}
		}else if(evento instanceof EventoFSC){
			retrieveProgrammaAndAddJoin(eventoWrapper);

			if(eventoWrapper.getRiepilogoRuoliFSC() != null) {
				((EventoFSC) evento).getRiepilogoRuoli().clear();
				((EventoFSC) evento).getRiepilogoRuoli().addAll(eventoWrapper.getRiepilogoRuoliFSC().values());
			}
		}else if(evento instanceof EventoFAD){
			//Docenti
			Iterator<PersonaEvento> it = eventoWrapper.getDocenti().iterator();
			List<PersonaEvento> attachedList = new ArrayList<PersonaEvento>();
			while(it.hasNext()){
				PersonaEvento p = it.next();
				p = personaEventoRepository.findOne(p.getId());
				attachedList.add(p);
			}
			((EventoFAD)evento).setDocenti(attachedList);

			//Risultati Attesi
//			Set<String> risultatiAttesi = new HashSet<String>();
			List<String> risultatiAttesi = new ArrayList<String>();
			for (String s : eventoWrapper.getRisultatiAttesiMapTemp().values()) {
				if(s != null && !s.isEmpty()) {
					risultatiAttesi.add(s);
				}
			}
			((EventoFAD) evento).setRisultatiAttesi(risultatiAttesi);

			//Requisiti Hardware Software
			if (eventoWrapper.getRequisitiHardwareSoftware().getId() != null) {
				((EventoFAD) evento).setRequisitiHardwareSoftware(eventoWrapper.getRequisitiHardwareSoftware());
			}

			//Mappa verifica apprendimento
			List<VerificaApprendimentoFAD> nuoviVAF = new ArrayList<VerificaApprendimentoFAD>();
			for(VerificaApprendimentoFAD vaf : eventoWrapper.getMappaVerificaApprendimento().values()) {
				//rimuove l'inner se non è stat checkata verificaApprendimentoFADEnum corrispondente
				if(vaf.getVerificaApprendimento() == null)
					vaf.setVerificaApprendimentoInner(null);
				nuoviVAF.add(vaf);
			}
			((EventoFAD) evento).getVerificaApprendimento().clear();
			((EventoFAD) evento).getVerificaApprendimento().addAll(nuoviVAF);

			retrieveProgrammaAndAddJoin(eventoWrapper);
		}
		
		//valuto se salvare i crediti proposti o quelli calcolati dal sistema
		if(evento.getConfermatiCrediti().booleanValue()){
			evento.setCrediti(eventoWrapper.getCreditiProposti());
		}

		//Responsabili
		Iterator<PersonaEvento> itPersona = eventoWrapper.getResponsabiliScientifici().iterator();
		List<PersonaEvento> attachedListPersona = new ArrayList<PersonaEvento>();
		while(itPersona.hasNext()){
			PersonaEvento p = itPersona.next();
			p = personaEventoRepository.findOne(p.getId());
			attachedListPersona.add(p);
		}
		evento.setResponsabili(attachedListPersona);

		//Sponsor
		Iterator<Sponsor> itSponsor = eventoWrapper.getSponsors().iterator();
		Set<Sponsor> attachedSetSponsor = new HashSet<Sponsor>();
		while(itSponsor.hasNext()){
			Sponsor s = itSponsor.next();
			//s.setEvento(evento);
			sponsorRepository.save(s);
			attachedSetSponsor.add(s);
		}
		evento.setSponsors(attachedSetSponsor);

		//Partner
		Iterator<Partner> itPartner = eventoWrapper.getPartners().iterator();
		Set<Partner> attachedSetPartner = new HashSet<Partner>();
		while(itPartner.hasNext()){
			Partner s = itPartner.next();
			//s.setEvento(evento);
			partnerRepository.save(s);
			attachedSetPartner.add(s);
		}
		evento.setPartners(attachedSetPartner);

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

		//Autocertificazione Assenza Aziende Alimenti Prima Infanzia
		if (eventoWrapper.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia().getId() != null) {
			evento.setAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia(eventoWrapper.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia());
		}

		//Autocertificazione Autorizzazione Ministero Salute
		if (eventoWrapper.getAutocertificazioneAutorizzazioneMinisteroSalute().getId() != null) {
			evento.setAutocertificazioneAutorizzazioneMinisteroSalute(eventoWrapper.getAutocertificazioneAutorizzazioneMinisteroSalute());
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
			
			//il file deve essere firmato digitalmente e con un certificato appartenente al Legale Rappresentante o al suo Delegato
			boolean validateCFFirma = fileValidator.validateFirmaCF(evento.getReportPartecipantiXML(), evento.getProvider().getId());
			if(!validateCFFirma)
				throw new Exception("error.codiceFiscale.firmatario");

			CogeapsCaricaResponse cogeapsCaricaResponse = cogeapsWsRestClient.carica(reportFileName, evento.getReportPartecipantiXML().getData(), evento.getProvider().getCodiceCogeaps());

			if (cogeapsCaricaResponse.getStatus() != 0) //errore HTTP (auth...) - 401
				throw new Exception(cogeapsCaricaResponse.getError() + ": " + cogeapsCaricaResponse.getMessage());
			if (cogeapsCaricaResponse.getErrCode() != 0) //errore su provider - 401,404 (provider non trovato o provider non di competenza dell'ente accreditante)
				throw new Exception(cogeapsCaricaResponse.getErrMsg());
			if (cogeapsCaricaResponse.getHttpStatusCode() != 200) //se non 200 (errore server imprevisto)
				throw new Exception(cogeapsCaricaResponse.getMessage());

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
	public void statoElaborazioneCogeaps(Long id) throws Exception {
		Evento evento = getEvento(id);
		try {
			RendicontazioneInviata ultimaRendicontazioneInviata = evento.getUltimaRendicontazioneInviata();
			if (ultimaRendicontazioneInviata == null || !ultimaRendicontazioneInviata.getStato().equals(RendicontazioneInviataStatoEnum.PENDING)) //se non sono presenti invii pendenti -> impossibile richiedere lo stato dell'elaborazione
				throw new Exception("error.nessuna_elaborazione_pendente");

			CogeapsStatoElaborazioneResponse cogeapsStatoElaborazioneResponse = cogeapsWsRestClient.statoElaborazione(ultimaRendicontazioneInviata.getFileName());

			if (cogeapsStatoElaborazioneResponse.getStatus() != 0) //errore HTTP (auth...) 401
				throw new Exception(cogeapsStatoElaborazioneResponse.getError() + ": " + cogeapsStatoElaborazioneResponse.getMessage());
			if (cogeapsStatoElaborazioneResponse.getHttpStatusCode() == 400) //400 (fileName non trovato)
				throw new Exception(cogeapsStatoElaborazioneResponse.getErrMsg());
			if (cogeapsStatoElaborazioneResponse.getHttpStatusCode() != 200) //se non 200 (errore server imprevisto)
				throw new Exception(cogeapsStatoElaborazioneResponse.getMessage());

			//se si passa di qua significa che la richiesta HTTP ha avuto esito 200.
			//se elaborazione completata segno eventuali errori altrimenti non faccio nulla (non si tiene traccia delle richieste la cui risposta porta ancora in uno stato pending)

			//se elaborazione completata -> update rendicontazione_inviata
			if (cogeapsStatoElaborazioneResponse.isElaborazioneCompletata()) {
				ultimaRendicontazioneInviata.setResponse(cogeapsStatoElaborazioneResponse.getResponse());
				if (cogeapsStatoElaborazioneResponse.getErrCode() != 0 || cogeapsStatoElaborazioneResponse.getCodiceErroreBloccante() != 0)
					ultimaRendicontazioneInviata.setResult(RendicontazioneInviataResultEnum.ERROR);
				else
					ultimaRendicontazioneInviata.setResult(RendicontazioneInviataResultEnum.SUCCESS);
				ultimaRendicontazioneInviata.setStato(RendicontazioneInviataStatoEnum.COMPLETED);
				rendicontazioneInviataService.save(ultimaRendicontazioneInviata);
			}
		}
		catch (Exception e) {
			throw new EcmException("error.stato_elaborazione_cogeaps", e.getMessage(), e);
		}
	}

	/*	CARICAMENTO	*/
	@Override
	public EventoWrapper prepareRipetibiliAndAllegati(EventoWrapper eventoWrapper) {
		Evento evento = eventoWrapper.getEvento();

		if(evento instanceof EventoRES){
			//date intermedie
			Long key = 1L;
			Map<Long, String> dateIntermedieTemp = new LinkedHashMap<Long, String>();
			for (LocalDate d : ((EventoRES) evento).getDateIntermedie()) {
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String dataToString = d.format(dtf);
				dateIntermedieTemp.put(key++, dataToString);
			}
			if(dateIntermedieTemp.size() == 0)
				dateIntermedieTemp.put(key, null);

			//risultati attesi
			key = 1L;
			Map<Long, String> risultatiAttesiTemp = new LinkedHashMap<Long, String>();
			for (String s : ((EventoRES) evento).getRisultatiAttesi()) {
				risultatiAttesiTemp.put(key++, s);
			}
			if(risultatiAttesiTemp.size() == 0)
				risultatiAttesiTemp.put(key, null);

			eventoWrapper.setRisultatiAttesiMapTemp(risultatiAttesiTemp);

			//Docenti
			eventoWrapper.setDocenti(((EventoRES) evento).getDocenti());

			//Documento Verifica Ricadute Formative
			if (((EventoRES) evento).getDocumentoVerificaRicaduteFormative() != null) {
				eventoWrapper.setDocumentoVerificaRicaduteFormative(((EventoRES) evento).getDocumentoVerificaRicaduteFormative());
			}
		}else if(evento instanceof EventoFSC){
			//Programma
			eventoWrapper.setProgrammaEventoFSC(((EventoFSC) evento).getFasiAzioniRuoli());

			//mappa ruoli ore
			eventoWrapper.initMappaRuoloOreFSC();

			//Riepilogo RuoloOreFSC
			eventoWrapper.initRiepilogoRuoliFSC();
			for(RiepilogoRuoliFSC r : ((EventoFSC) evento).getRiepilogoRuoli())
				eventoWrapper.getRiepilogoRuoliFSC().put(r.getRuolo(), r);

		}else if(evento instanceof EventoFAD){
			//Docenti
			eventoWrapper.setDocenti(((EventoFAD) evento).getDocenti());

			//risultati attesi
			Long key = 1L;
			Map<Long, String> risultatiAttesiTemp = new LinkedHashMap<Long, String>();
			for (String s : ((EventoFAD) evento).getRisultatiAttesi()) {
				risultatiAttesiTemp.put(key++, s);
			}
			if(risultatiAttesiTemp.size() == 0)
				risultatiAttesiTemp.put(key, "");

			eventoWrapper.setRisultatiAttesiMapTemp(risultatiAttesiTemp);

			//Requisiti Hardware Software
			if (((EventoFAD) evento).getRequisitiHardwareSoftware() != null) {
				eventoWrapper.setRequisitiHardwareSoftware(((EventoFAD) evento).getRequisitiHardwareSoftware());
			}

			//mappa verifica apprendimento
			eventoWrapper.initMappaVerificaApprendimentoFAD();

			//Programma
			eventoWrapper.setProgrammaEventoFAD(((EventoFAD) evento).getProgrammaFAD());
		}

		//responsabili scientifici
		eventoWrapper.setResponsabiliScientifici(evento.getResponsabili());

		//sponsor
		List<Sponsor> sponsors = new ArrayList<Sponsor>();
		sponsors.addAll(evento.getSponsors());
		eventoWrapper.setSponsors(sponsors);

		//partner
		List<Partner> partners = new ArrayList<Partner>();
		partners.addAll(evento.getPartners());
		eventoWrapper.setPartners(partners);

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

		//Autocertificazione Assenza Aziende Alimenti Prima Infanzia
		if (evento.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia() != null) {
			eventoWrapper.setAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia(evento.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia());
		}

		//Autocertificazione Autorizzazione Ministero Salute
		if (evento.getAutocertificazioneAutorizzazioneMinisteroSalute() != null) {
			eventoWrapper.setAutocertificazioneAutorizzazioneMinisteroSalute(evento.getAutocertificazioneAutorizzazioneMinisteroSalute());
		}

		return eventoWrapper;
	}


	@Override
	public void calculateAutoCompilingData(EventoWrapper eventoWrapper) throws Exception {
		calcoloDurataEvento(eventoWrapper);
		calcoloCreditiEvento(eventoWrapper);
		eventoWrapper.getEvento().calcolaCosto();
	}

	private float calcoloDurataEvento(EventoWrapper eventoWrapper) {
		float durata = 0;

		if(eventoWrapper.getEvento() instanceof EventoRES){

			//durata = calcoloDurataEventoRES(eventoWrapper.getProgrammaEventoRES());
			durata = calcoloDurataEventoRES(eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().values());


			((EventoRES)eventoWrapper.getEvento()).setDurata(durata);
		}else if(eventoWrapper.getEvento() instanceof EventoFSC){
			durata = calcoloDurataEventoFSC(eventoWrapper.getProgrammaEventoFSC(), eventoWrapper.getRiepilogoRuoliFSC());
			((EventoFSC)eventoWrapper.getEvento()).setDurata(durata);
			//calcolo partecipanti
			int numPartecipanti = calcolaNumeroRuoloFSC(RuoloFSCBaseEnum.PARTECIPANTE, eventoWrapper.getRiepilogoRuoliFSC());
			eventoWrapper.getEvento().setNumeroPartecipanti(numPartecipanti);
			//calcolo tutor
			int numTutor = calcolaNumeroRuoloFSC(RuoloFSCBaseEnum.TUTOR, eventoWrapper.getRiepilogoRuoliFSC());
			((EventoFSC) eventoWrapper.getEvento()).setNumeroTutor(numTutor);
		}else if(eventoWrapper.getEvento() instanceof EventoFAD){
			durata = calcoloDurataEventoFAD(eventoWrapper.getProgrammaEventoFAD(), ((EventoFAD)eventoWrapper.getEvento()).getRiepilogoFAD());
			((EventoFAD)eventoWrapper.getEvento()).setDurata(durata);
		}

		durata = Utils.getRoundedFloatValue(durata, 2);
		return durata;
	}

	@Override
	public void aggiornaDati(EventoWrapper eventoWrapper) {
		if(eventoWrapper.getEvento() instanceof EventoRES){
			eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().aggiornaDati();
		}
	}

	/*
	private float calcoloDurataEventoRES(List<ProgrammaGiornalieroRES> programma){
		float durata = 0;

		if(programma != null){
			for(ProgrammaGiornalieroRES progrGior : programma){
				for(DettaglioAttivitaRES dett : progrGior.getProgramma()){
					if(!dett.isPausa())
						durata += dett.getOreAttivita();
				}
			}
		}

		durata = Utils.getRoundedFloatValue(durata);
		return durata;
	}
	 */

	private float calcoloDurataEventoRES(Collection<EventoRESProgrammaGiornalieroWrapper> programma){
		float durata = 0;

		if(programma != null){
			for(EventoRESProgrammaGiornalieroWrapper progrGior : programma){
				for(DettaglioAttivitaRES dett : progrGior.getProgramma().getProgramma()){
					if(!dett.isPausa() && !dett.isValutazioneApprendimento())
						durata += dett.getOreAttivita();
				}
			}
		}

		return durata;
	}

	private float calcoloDurataEventoFSC(List<FaseAzioniRuoliEventoFSCTypeA> programma, Map<RuoloFSCEnum, RiepilogoRuoliFSC> riepilogoRuoliFSC){
		float durata = 0;

		prepareRiepilogoRuoli(programma, riepilogoRuoliFSC);
		durata = getMaxDurataPatecipanti(riepilogoRuoliFSC);

		durata = Utils.getRoundedFloatValue(durata, 2);
		return durata;
	}

	private float getMaxDurataPatecipanti(Map<RuoloFSCEnum,RiepilogoRuoliFSC> riepilogoRuoliFSC){
		float max = 0.0f;

		if(riepilogoRuoliFSC != null){
			Iterator<Entry<RuoloFSCEnum,RiepilogoRuoliFSC>> iterator = riepilogoRuoliFSC.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<RuoloFSCEnum,RiepilogoRuoliFSC> pairs = iterator.next();
				if(((RuoloFSCEnum)pairs.getKey()) != null && ((RuoloFSCEnum)pairs.getKey()).getRuoloBase() == RuoloFSCBaseEnum.PARTECIPANTE && pairs.getValue().getTempoDedicato() > max)
					max = pairs.getValue().getTempoDedicato();
			 }
		}

		return max;
	}

	private int calcolaNumeroRuoloFSC(RuoloFSCBaseEnum ruolo, Map<RuoloFSCEnum, RiepilogoRuoliFSC> riepilogoRuoliMap) {
		int counter = 0;
		if(riepilogoRuoliMap != null){
			for(RiepilogoRuoliFSC rrf : riepilogoRuoliMap.values()) {
				if(rrf.getRuolo().getRuoloBase() == ruolo) {
					counter = counter + rrf.getNumeroPartecipanti();
				}
			}
		}
		return counter;
	}

	private float calcoloDurataEventoFAD(List<DettaglioAttivitaFAD> programma, RiepilogoFAD riepilogoFAD){
		float durata = 0;
		riepilogoFAD.clear();

		if(programma != null){
			for(DettaglioAttivitaFAD dett : programma){
				durata += dett.getOreAttivita();

				//popolo la lista di obiettivi
				if(dett.getObiettivoFormativo() != null)
					riepilogoFAD.getObiettivi().add(dett.getObiettivoFormativo());

				//popolo la lista di metodologie con annesso calcolo di ore
				if(dett.getMetodologiaDidattica() != null){
					if(riepilogoFAD.getMetodologie().containsKey(dett.getMetodologiaDidattica())){
						float ore = riepilogoFAD.getMetodologie().get(dett.getMetodologiaDidattica());
						riepilogoFAD.getMetodologie().put(dett.getMetodologiaDidattica(), ore + dett.getOreAttivita());
					}else{
						riepilogoFAD.getMetodologie().put(dett.getMetodologiaDidattica(), dett.getOreAttivita());
					}
				}
			}
		}

		durata = Utils.getRoundedFloatValue(durata, 2);
		return durata;
	}

	private float calcoloCreditiEvento(EventoWrapper eventoWrapper) {
		float crediti = 0;

		if(eventoWrapper.getEvento() instanceof EventoRES){
			EventoRES evento = ((EventoRES)eventoWrapper.getEvento());
			crediti = calcoloCreditiFormativiEventoRES(evento.getTipologiaEvento(), evento.getDurata(), eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().values(), evento.getNumeroPartecipanti(), evento.getRiepilogoRES());
			eventoWrapper.setCreditiProposti(crediti);
			LOGGER.info(Utils.getLogMessage("Calcolato crediti per evento RES"));
			return crediti;
		}else if(eventoWrapper.getEvento() instanceof EventoFSC){
			EventoFSC evento = ((EventoFSC)eventoWrapper.getEvento());
			crediti = calcoloCreditiFormativiEventoFSC(evento.getTipologiaEvento(), eventoWrapper);
			eventoWrapper.setCreditiProposti(crediti);
			LOGGER.info(Utils.getLogMessage("Calcolato crediti per evento FSC"));
			return crediti;
		}else if(eventoWrapper.getEvento() instanceof EventoFAD){
			EventoFAD evento = ((EventoFAD)eventoWrapper.getEvento());
			crediti = calcoloCreditiFormativiEventoFAD(evento.getDurata(), evento.getSupportoSvoltoDaEsperto());
			eventoWrapper.setCreditiProposti(crediti);
			LOGGER.info(Utils.getLogMessage("Calcolato crediti per evento FAD"));
			return crediti;
		}

		return crediti;
	}

	private float calcoloCreditiFormativiEventoRES(TipologiaEventoRESEnum tipologiaEvento, float durata, Collection<EventoRESProgrammaGiornalieroWrapper> programma, Integer numeroPartecipanti, RiepilogoRES riepilogoRES){
		float crediti = 0.0f;
		float oreFrontale = 0f;
		float oreInterattiva = 0f;

		riepilogoRES.clear();

		for(EventoRESProgrammaGiornalieroWrapper progrGio : programma) {
			for(DettaglioAttivitaRES a : progrGio.getProgramma().getProgramma()){
				if(a.getMetodologiaDidattica()!= null && a.getMetodologiaDidattica().getMetodologia() == TipoMetodologiaEnum.FRONTALE){
					oreFrontale += a.getOreAttivita();
				}else if(a.getMetodologiaDidattica()!= null && a.getMetodologiaDidattica().getMetodologia() == TipoMetodologiaEnum.INTERATTIVA){
					oreInterattiva += a.getOreAttivita();
				}

				//popolo la lista di obiettivi formativi utilizzati
				if(a.getObiettivoFormativo() != null)
					riepilogoRES.getObiettivi().add(a.getObiettivoFormativo());

				//popolo la lista di metodologie con annesso calcolo di ore
				if(a.getMetodologiaDidattica() != null){
					if(riepilogoRES.getMetodologie().containsKey(a.getMetodologiaDidattica())){
						float ore = riepilogoRES.getMetodologie().get(a.getMetodologiaDidattica());
						riepilogoRES.getMetodologie().put(a.getMetodologiaDidattica(), ore + a.getOreAttivita());
					}else{
						riepilogoRES.getMetodologie().put(a.getMetodologiaDidattica(), a.getOreAttivita());
					}
				}
			}
		}

		riepilogoRES.setTotaleOreFrontali(oreFrontale);
		riepilogoRES.setTotaleOreInterattive(oreInterattiva);

		if(tipologiaEvento == TipologiaEventoRESEnum.CONVEGNO_CONGRESSO){
			crediti = (0.20f * durata);
			if(crediti > 5.0f)
				crediti = 5.0f;
		}

		if(tipologiaEvento == TipologiaEventoRESEnum.WORKSHOP_SEMINARIO){
			crediti = 1 * durata;
			if(crediti > 50f)
				crediti = 50f;
		}

		if(tipologiaEvento == TipologiaEventoRESEnum.CORSO_AGGIORNAMENTO){
			float creditiFrontale = 0f;
			float creditiInterattiva = 0f;

			//metodologia frontale
			numeroPartecipanti = numeroPartecipanti!= null ? numeroPartecipanti.intValue() : 0;

			if(numeroPartecipanti >=1 && numeroPartecipanti <=20){
				creditiFrontale = oreFrontale * 1.25f;
			}else if(numeroPartecipanti >=21 && numeroPartecipanti <= 50){
				float creditiDecrescenti = getQuotaFasciaDecrescenteForRES(numeroPartecipanti);
				creditiFrontale = oreFrontale * creditiDecrescenti;
			}else if(numeroPartecipanti >=51 && numeroPartecipanti <=100){
				creditiFrontale = oreFrontale * 1.0f;
			}else if(numeroPartecipanti >= 101 && numeroPartecipanti <= 150){
				creditiFrontale = oreFrontale * 0.75f;
			}else if(numeroPartecipanti >= 151 && numeroPartecipanti <= 200){
				creditiFrontale = oreFrontale * 0.5f;
			}

			//metodologia interattiva
			creditiInterattiva = oreInterattiva * 1.5f;

			crediti = creditiFrontale + creditiInterattiva;
		}

		crediti = Utils.getRoundedFloatValue(crediti, 1);

		return crediti;
	}

	private float getQuotaFasciaDecrescenteForRES(int numeroPartecipanti){
		switch (numeroPartecipanti){
			case 21: return 1.24f;
			case 22: return 1.23f;
			case 23: return 1.23f;
			case 24: return 1.22f;
			case 25: return 1.21f;
			case 26: return 1.20f;
			case 27: return 1.19f;
			case 28: return 1.19f;
			case 29: return 1.18f;
			case 30: return 1.17f;
			case 31: return 1.16f;
			case 32: return 1.15f;
			case 33: return 1.15f;
			case 34: return 1.14f;
			case 35: return 1.13f;
			case 36: return 1.12f;
			case 37: return 1.11f;
			case 38: return 1.10f;
			case 39: return 1.10f;
			case 40: return 1.08f;
			case 41: return 1.08f;
			case 42: return 1.07f;
			case 43: return 1.06f;
			case 44: return 1.06f;
			case 45: return 1.05f;
			case 46: return 1.04f;
			case 47: return 1.03f;
			case 48: return 1.02f;
			case 49: return 1.02f;
			case 50: return 1.01f;

			default: return 0.0f;
		}

	}

	private float calcoloCreditiFormativiEventoFSC(TipologiaEventoFSCEnum tipologiaEvento, EventoWrapper wrapper){
		float crediti = 0.0f;

		calcolaCreditiPartecipantiFSC(tipologiaEvento, wrapper.getRiepilogoRuoliFSC());
		crediti = getMaxCreditiPartecipantiFSC(wrapper.getRiepilogoRuoliFSC());
		calcolaCreditiAltriRuoliFSC(tipologiaEvento, wrapper.getRiepilogoRuoliFSC(),crediti);

		return crediti;
	}

	/*
	 * Ragruppo i Ruoli coinvolti in una mappa <Ruolo,RiepilogoRuoloOreFSC>
	 * dove il RiepilogoRuoloOreFSC avra la somma delle ore dei ruoli
	 * */
	private void prepareRiepilogoRuoli(List<FaseAzioniRuoliEventoFSCTypeA> programma, Map<RuoloFSCEnum,RiepilogoRuoliFSC> riepilogoRuoliFSC){
		if(riepilogoRuoliFSC != null)
		{
			Set<RuoloFSCEnum> ruoliUsati = new HashSet<RuoloFSCEnum>();

			Iterator<Entry<RuoloFSCEnum, RiepilogoRuoliFSC>> iterator = riepilogoRuoliFSC.entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry<RuoloFSCEnum,RiepilogoRuoliFSC> pairs = iterator.next();
				pairs.getValue().setTempoDedicato(0f);
				pairs.getValue().setCrediti(0f);
				if(pairs.getValue().getRuolo() == null)
					iterator.remove();
			}

			for(FaseAzioniRuoliEventoFSCTypeA fase : programma){
				for(AzioneRuoliEventoFSC azione : fase.getAzioniRuoli()){
					for(RuoloOreFSC ruolo : azione.getRuoli())
					{
						ruoliUsati.add(ruolo.getRuolo());

						if(riepilogoRuoliFSC.containsKey(ruolo.getRuolo())){
							RiepilogoRuoliFSC r = riepilogoRuoliFSC.get(ruolo.getRuolo());
							float tempoDedicato = ruolo.getTempoDedicato() != null ? ruolo.getTempoDedicato() : 0.0f;
							r.addTempo(tempoDedicato);
						}else{
							float tempoDedicato = ruolo.getTempoDedicato() != null ? ruolo.getTempoDedicato() : 0.0f;
							RiepilogoRuoliFSC r = new RiepilogoRuoliFSC(ruolo.getRuolo(), tempoDedicato, 0.0f);
							riepilogoRuoliFSC.put(ruolo.getRuolo(), r);
						}
					}
				}
			}

			iterator = riepilogoRuoliFSC.entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry<RuoloFSCEnum,RiepilogoRuoliFSC> pairs = iterator.next();
				if(!ruoliUsati.contains(pairs.getValue().getRuolo()))
					iterator.remove();
			}


		}
	}

	/*
	 * Data la mappa <Ruolo,RiepilogoRuoloOreFSC> calcolo i crediti dei PARTECIPANTI
	 * */
	private void calcolaCreditiPartecipantiFSC(TipologiaEventoFSCEnum tipologia, Map<RuoloFSCEnum,RiepilogoRuoliFSC> riepilogoRuoliFSC){
		if(riepilogoRuoliFSC != null){
			Iterator<Entry<RuoloFSCEnum,RiepilogoRuoliFSC>> iterator = riepilogoRuoliFSC.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<RuoloFSCEnum,RiepilogoRuoliFSC> pairs = iterator.next();
				if(((RuoloFSCEnum)pairs.getKey()) != null && ((RuoloFSCEnum)pairs.getKey()).getRuoloBase() == RuoloFSCBaseEnum.PARTECIPANTE)
					pairs.getValue().calcolaCrediti(tipologia,0f);
			 }
		}
	}

	/*
	 * Data la mappa <Ruolo,RiepilogoRuoloOreFSC> calcolo i crediti degli altri RUOLI
	 * */
	private void calcolaCreditiAltriRuoliFSC(TipologiaEventoFSCEnum tipologia, Map<RuoloFSCEnum,RiepilogoRuoliFSC> riepilogoRuoliFSC, float maxValue){
		if(riepilogoRuoliFSC != null){
			Iterator<Entry<RuoloFSCEnum,RiepilogoRuoliFSC>> iterator = riepilogoRuoliFSC.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<RuoloFSCEnum,RiepilogoRuoliFSC> pairs = iterator.next();
				if(((RuoloFSCEnum)pairs.getKey()) != null && ((RuoloFSCEnum)pairs.getKey()).getRuoloBase() != RuoloFSCBaseEnum.PARTECIPANTE)
					pairs.getValue().calcolaCrediti(tipologia,maxValue);
			 }
		}
	}

	/*
	 * Data la mappa <Ruolo,RiepilogoRuoloOreFSC> individuo il valore MAX numero crediti attribuito a un PARTECIPANTE
	 * */
	private float getMaxCreditiPartecipantiFSC(Map<RuoloFSCEnum,RiepilogoRuoliFSC> riepilogoRuoliFSC){
		float max = 0.0f;

		if(riepilogoRuoliFSC != null){
			Iterator<Entry<RuoloFSCEnum,RiepilogoRuoliFSC>> iterator = riepilogoRuoliFSC.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<RuoloFSCEnum,RiepilogoRuoliFSC> pairs = iterator.next();
				if(((RuoloFSCEnum)pairs.getKey()) != null && ((RuoloFSCEnum)pairs.getKey()).getRuoloBase() == RuoloFSCBaseEnum.PARTECIPANTE && pairs.getValue().getCrediti() > max)
					max = pairs.getValue().getCrediti();
			 }
		}

		return max;
	}


	private float calcoloCreditiFormativiEventoFAD(float durata, Boolean conTutor){
		float crediti = 0.0f;

		if(conTutor != null && conTutor)
			crediti = durata * 1.5f;
		else
			crediti = durata * 1.0f;

		crediti = Utils.getRoundedFloatValue(crediti, 1);
		return crediti;
	}

	/*
	 *
	 * prendo il programma dal wrapper e aggancio l'evento alle fasi o ai giorni
	 * */
	@Override
	public void retrieveProgrammaAndAddJoin(EventoWrapper eventoWrapper) {
		Evento evento = eventoWrapper.getEvento();
		if(evento instanceof EventoRES){
			//Spostato fatto insieme alle date intermedie
			/*
			((EventoRES) evento).setProgramma(eventoWrapper.getProgrammaEventoRES());
			if(eventoWrapper.getProgrammaEventoRES() != null){
				for(ProgrammaGiornalieroRES p : ((EventoRES) evento).getProgramma()){
					p.setEventoRES((EventoRES) evento);
				}
			}
			*/
		}else if(evento instanceof EventoFSC){
			if(eventoWrapper.getProgrammaEventoFSC() != null){
				((EventoFSC)evento).setFasiAzioniRuoli(eventoWrapper.getProgrammaEventoFSC());
				for(FaseAzioniRuoliEventoFSCTypeA fase : ((EventoFSC)evento).getFasiAzioniRuoli()){
					fase.setEvento(((EventoFSC)evento));
				}
			}
		}else if(evento instanceof EventoFAD){
			if(eventoWrapper.getProgrammaEventoFAD() != null){
				((EventoFAD) evento).setProgrammaFAD(eventoWrapper.getProgrammaEventoFAD());
			}else{
				((EventoFAD) evento).setProgrammaFAD(new ArrayList<DettaglioAttivitaFAD>());
			}
		}
	}

	//metodo di Barduz con i sottograph (da rivedere, per ora inutilizzato)
	@Override
	public Evento getEventoForRiedizione(Long eventoId) {
		return eventoRepository.findOneForRiedizione(eventoId);
	}

	//seleziona gli eventi rieditabili
	@Override
	public Set<Evento> getAllEventiRieditabiliForProviderId(Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Recupero tutti gli eventi del piano formativo rieditabili per il provider: " + providerId));
		return eventoRepository.findAllByProviderIdAndStatoNotAndDataInizioBefore(providerId, EventoStatoEnum.BOZZA, LocalDate.now());
	}

	//trovo ultima edizione di un evento con il determinato prefix
	@Override
	public int getLastEdizioneEventoByPrefix(String prefix) {
		Page<Integer> result = eventoRepository.findLastEdizioneOfEventoByPrefix(prefix, new PageRequest(0, 1));
		List<Integer> edizioneL = result.getContent();
		int edizione = edizioneL.get(0) != null ? edizioneL.get(0) : - 1;
		return edizione;
	}

	//TODO da qui in poi sta roba non funzionerà mai
	@Override
	@Transactional
	public Evento prepareRiedizioneEvento(Evento eventoPadre) throws Exception {
		Evento eventoPadreTemp = eventoPadre;
		int edizione = getLastEdizioneEventoByPrefix(eventoPadre.getPrefix()) + 1;
		Evento riedizione = detachEvento(eventoPadre);
		cloneDetachedEvento(riedizione);
		riedizione.setEdizione(edizione);
		riedizione.setEventoPadre(eventoPadreTemp);
		return riedizione;
	}

	//si può fareeeeeee (Iomminstein mode on)... sigh devo proprio andare in vacanza...
	/* funzione di detach ad hoc (detachare veramente tutto ricorsivamente non conviene proprio a
	 * causa di Entity come Provider e Accreditamento presenti in Evento.
	 * */
	@Override
	public Evento detachEvento(Evento eventoPadre) throws Exception{
		LOGGER.debug(Utils.getLogMessage("DETACH evento id: " + eventoPadre.getId()));

		touchFirstLevelOfEverything(eventoPadre);

		//casi specifici
		if(eventoPadre instanceof EventoFAD) {

			LOGGER.debug(Utils.getLogMessage("Procedura di detach EventoFAD - start"));

			LOGGER.debug(Utils.getLogMessage("Detach Docenti"));
			for(PersonaEvento d : ((EventoFAD) eventoPadre).getDocenti()) {
				LOGGER.debug(Utils.getLogMessage("Detach Docente: " + d.getId()));
				entityManager.detach(d);
			}
		}

		else if(eventoPadre instanceof EventoRES) {

			LOGGER.debug(Utils.getLogMessage("Procedura di detach EventoRES - start"));

			LOGGER.debug(Utils.getLogMessage("Detach Docenti"));
			for(PersonaEvento d : ((EventoRES) eventoPadre).getDocenti()) {
				LOGGER.debug(Utils.getLogMessage("Detach Docente: " + d.getId()));
				entityManager.detach(d);
			}

			LOGGER.debug(Utils.getLogMessage("Detach Programmi RES"));
			for(ProgrammaGiornalieroRES pgr : ((EventoRES) eventoPadre).getProgramma()) {
				pgr.getProgramma().size(); //touch che non viene raggiunto perchè al secondo livello
				LOGGER.debug(Utils.getLogMessage("Detach Programma RES: " + pgr.getId()));
				entityManager.detach(pgr);
			}
		}

		else if(eventoPadre instanceof EventoFSC) {

			LOGGER.debug(Utils.getLogMessage("Procedura di detach EventoFSC - start"));

			LOGGER.debug(Utils.getLogMessage("Detach Fasi Azioni Ruoli FSC"));
			for(FaseAzioniRuoliEventoFSCTypeA far : ((EventoFSC) eventoPadre).getFasiAzioniRuoli()) {
				LOGGER.debug(Utils.getLogMessage("Detach Fase: " + far.getId()));
				for(AzioneRuoliEventoFSC aref : far.getAzioniRuoli()) {
					LOGGER.debug(Utils.getLogMessage("Detach Azioni Ruoli: " + aref.getId()));
					aref.getRuoli().size(); //touch che non viene raggiunto perchè al terzo livello
					aref.getMetodiDiLavoro().size(); //touch che non viene raggiunto perchè al terzo livello
					entityManager.detach(aref);
				}
				entityManager.detach(far);
			}
		}

		//parte in comune
		LOGGER.debug(Utils.getLogMessage("Detach Responsabili Scientifici"));
		for(PersonaEvento r : eventoPadre.getResponsabili()) {
			LOGGER.debug(Utils.getLogMessage("Detach Responsabile: " + r.getId()));
			entityManager.detach(r);
		}

		LOGGER.debug(Utils.getLogMessage("Detach Sponsors"));
		for(Sponsor s : eventoPadre.getSponsors()) {
			LOGGER.debug(Utils.getLogMessage("Detach Sponsor: " + s.getId()));
			entityManager.detach(s);
		}

		LOGGER.debug(Utils.getLogMessage("Detach Partners"));
		for(Partner p : eventoPadre.getPartners()) {
			LOGGER.debug(Utils.getLogMessage("Detach Partner: " + p.getId()));
			entityManager.detach(p);
		}

		LOGGER.debug(Utils.getLogMessage("Detach Responsabile Segreteria"));
		entityManager.detach(eventoPadre.getResponsabileSegreteria());

		entityManager.detach(eventoPadre);

		LOGGER.debug(Utils.getLogMessage("Procedura di detach Evento - success"));

		return eventoPadre;
	}

	//nobel per il workaround 2016 (in pratica fa una get di tutto | solo il primo livello della entity passata)
	public <T> void touchFirstLevelOfEverything(T obj) throws Exception{
		BeanInfo info = Introspector.getBeanInfo(obj.getClass());
		for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
			Method method = pd.getReadMethod();
			if(method != null) {
				Object innerEntity = method.invoke(obj);
				if(innerEntity != null)
					innerEntity.toString();
			}
		}
	}

	//sistema l'Evento detatchato clonando i campi che devono essere clonati
	private void cloneDetachedEvento(Evento riedizione) throws CloneNotSupportedException {

		if(riedizione instanceof EventoFAD) {

			LOGGER.debug(Utils.getLogMessage("Procedura di clonazione EventoFAD - start"));

			LOGGER.debug(Utils.getLogMessage("Clonazione e salvataggio Docenti"));
			for(PersonaEvento d : ((EventoFAD) riedizione).getDocenti()) {
				LOGGER.debug(Utils.getLogMessage("Clonazione Docente: " + d.getId()));
				d.setId(null);
				d.getAnagrafica().setCv(fileService.copyFile(d.getAnagrafica().getCv()));
				personaEventoRepository.save(d);
				LOGGER.debug(Utils.getLogMessage("Docente clonato salvato: " + d.getId()));
			}

			((EventoFAD) riedizione).setConfermatiCrediti(null);

			((EventoFAD) riedizione).setRequisitiHardwareSoftware(fileService.copyFile(((EventoFAD) riedizione).getRequisitiHardwareSoftware()));

			//ricalcolato
			((EventoFAD) riedizione).setRiepilogoFAD(new RiepilogoFAD());

			//liste di embedded da gestire ad hoc
			LOGGER.debug(Utils.getLogMessage("Clonazione programma FAD"));
			List<DettaglioAttivitaFAD> programmaFAD = new ArrayList<DettaglioAttivitaFAD>();
			programmaFAD.addAll(Arrays.asList(((EventoFAD) riedizione).getProgrammaFAD().toArray(new DettaglioAttivitaFAD[((EventoFAD) riedizione).getProgrammaFAD().size()])));
			((EventoFAD) riedizione).setProgrammaFAD(programmaFAD);
			LOGGER.debug(Utils.getLogMessage("Clonazione verifica apprendimento FAD"));
			List<VerificaApprendimentoFAD> verificaApprendimentoFAD = new ArrayList<VerificaApprendimentoFAD>();
			verificaApprendimentoFAD.addAll(Arrays.asList(((EventoFAD) riedizione).getVerificaApprendimento().toArray(new VerificaApprendimentoFAD[((EventoFAD) riedizione).getVerificaApprendimento().size()])));
			((EventoFAD) riedizione).setVerificaApprendimento(verificaApprendimentoFAD);
		}

		else if(riedizione instanceof EventoRES) {

			LOGGER.debug(Utils.getLogMessage("Procedura di clonazione EventoRES - start"));

			LOGGER.debug(Utils.getLogMessage("Clonazione e salvataggio Docenti"));
			for(PersonaEvento d : ((EventoRES) riedizione).getDocenti()) {
				LOGGER.debug(Utils.getLogMessage("Clonazione Docente: " + d.getId()));
				d.setId(null);
				d.getAnagrafica().setCv(fileService.copyFile(d.getAnagrafica().getCv()));
				personaEventoRepository.save(d);
				LOGGER.debug(Utils.getLogMessage("Docente clonato salvato: " + d.getId()));
			}

			//va fatto così o hibernate si offende p.s. grande Barduz!!
			LOGGER.debug(Utils.getLogMessage("Clonazione Programmi RES"));
			List<ProgrammaGiornalieroRES> programmaRES = new ArrayList<ProgrammaGiornalieroRES>();
			for(ProgrammaGiornalieroRES pgr : ((EventoRES) riedizione).getProgramma()) {
				LOGGER.debug(Utils.getLogMessage("Clonazione ProgrammaRES: " + pgr.getId()));
				pgr.setId(null);
				LOGGER.debug(Utils.getLogMessage("Clonazione del suo dettaglioAttività RES"));
				List<DettaglioAttivitaRES> dettaglioAttivitaRESList = new ArrayList<DettaglioAttivitaRES>();
				dettaglioAttivitaRESList.addAll(Arrays.asList(pgr.getProgramma().toArray(new DettaglioAttivitaRES[pgr.getProgramma().size()])));
				pgr.setProgramma(dettaglioAttivitaRESList);
				programmaRES.add(pgr);
			}
			((EventoRES) riedizione).setProgramma(programmaRES);

			((EventoRES) riedizione).setConfermatiCrediti(null);

			((EventoRES) riedizione).setDocumentoVerificaRicaduteFormative(fileService.copyFile(((EventoRES) riedizione).getDocumentoVerificaRicaduteFormative()));

			//ricalcolato
			((EventoRES) riedizione).setRiepilogoRES(new RiepilogoRES());
		}

		else if(riedizione instanceof EventoFSC) {

			LOGGER.debug(Utils.getLogMessage("Procedura di clonazione EventoFSC - start"));

			((EventoFSC) riedizione).setOttenutoComitatoEtico(null);

			//solito giro strano per non fare agitare hibernate, stavolta doppio.. sigh
			LOGGER.debug(Utils.getLogMessage("Clonazione Fasi Azioni Ruoli FSC"));
			List<FaseAzioniRuoliEventoFSCTypeA> fasiAzioniRuoli = new ArrayList<FaseAzioniRuoliEventoFSCTypeA>();
			for(FaseAzioniRuoliEventoFSCTypeA far : ((EventoFSC) riedizione).getFasiAzioniRuoli()) {
				LOGGER.debug(Utils.getLogMessage("Clonazione fase FSC: " + far.getId()));
				far.setId(null);
				LOGGER.debug(Utils.getLogMessage("Clonazione dei sui azioni ruoli FSC"));
				List<AzioneRuoliEventoFSC> azioniRuoli = new ArrayList<AzioneRuoliEventoFSC>();
				for(AzioneRuoliEventoFSC aref : far.getAzioniRuoli()) {
					LOGGER.debug(Utils.getLogMessage("Clonazione azione ruoli FSC: " + aref.getId()));
					aref.setId(null);
					LOGGER.debug(Utils.getLogMessage("Clonazione dei ruoli"));
					List<RuoloOreFSC> ruoli = new ArrayList<RuoloOreFSC>();
					ruoli.addAll(Arrays.asList(aref.getRuoli().toArray(new RuoloOreFSC[aref.getRuoli().size()])));
					aref.setRuoli(ruoli);
					LOGGER.debug(Utils.getLogMessage("Clonazione metodi di lavoro"));
					Set<MetodoDiLavoroEnum> metodiDiLavoro = new HashSet<MetodoDiLavoroEnum>();
					metodiDiLavoro.addAll(Arrays.asList(aref.getMetodiDiLavoro().toArray(new MetodoDiLavoroEnum[aref.getMetodiDiLavoro().size()])));
					aref.setMetodiDiLavoro(metodiDiLavoro);
					azioniRuoli.add(aref);
				}
				far.setAzioniRuoli(azioniRuoli);
				fasiAzioniRuoli.add(far);
			}
			((EventoFSC) riedizione).setFasiAzioniRuoli(fasiAzioniRuoli);

			//ricalcolato
			((EventoFSC) riedizione).setRiepilogoRuoli(new ArrayList<RiepilogoRuoliFSC>());
		}

		//parte in comune
		LOGGER.debug(Utils.getLogMessage("Clonazione e salvataggio Responsabili"));
		for(PersonaEvento r : riedizione.getResponsabili()) {
			LOGGER.debug(Utils.getLogMessage("Clonazione Responsabile: " + r.getId()));
			r.setId(null);
			r.getAnagrafica().setCv(fileService.copyFile(r.getAnagrafica().getCv()));
			personaEventoRepository.save(r);
			LOGGER.debug(Utils.getLogMessage("Responsabile clonato salvato: " + r.getId()));
		}

		LOGGER.debug(Utils.getLogMessage("Clonazione Sponsors"));
		for(Sponsor s : riedizione.getSponsors()) {
			LOGGER.debug(Utils.getLogMessage("Clonazione Sponsor: " + s.getId()));
			s.setId(null);
			s.setSponsorFile(fileService.copyFile(s.getSponsorFile()));
		}

		LOGGER.debug(Utils.getLogMessage("Clonazione Partner"));
		for(Partner p : riedizione.getPartners()) {
			LOGGER.debug(Utils.getLogMessage("Clonazione Partner: " + p.getId()));
			p.setId(null);
			p.setPartnerFile(fileService.copyFile(p.getPartnerFile()));
		}

		LOGGER.debug(Utils.getLogMessage("Clonazione Responsabile Segreteria"));
		riedizione.getResponsabileSegreteria().setId(null);

		//flag e parti da settare a new o null
		LOGGER.debug(Utils.getLogMessage("Azzeramento dei campi da ricalcolare"));
		//riedizione.setCanAttachSponsor(true);
		//riedizione.setCanDoPagamento(false);
		riedizione.setSponsorUploaded(false);
		riedizione.setDataScadenzaInvioRendicontazione(null);
		riedizione.setCanDoRendicontazione(false);
		riedizione.setValidatorCheck(false);
		riedizione.setReportPartecipantiXML(null);
		riedizione.setReportPartecipantiCSV(null);
		riedizione.setEventoPianoFormativo(null);
		riedizione.setDataScadenzaPagamento(null);
		riedizione.setInviiRendicontazione(new HashSet<RendicontazioneInviata>());
		riedizione.setPagato(null);
		riedizione.setPagInCorso(null);
		riedizione.setProceduraVerificaQualitaPercepita(null);
		riedizione.setAutorizzazionePrivacy(null);
		riedizione.setLetteInfoAllegatoSponsor(null);

		LOGGER.debug(Utils.getLogMessage("Copia dei File"));
		riedizione.setBrochureEvento(fileService.copyFile(riedizione.getBrochureEvento()));
		riedizione.setAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia(fileService.copyFile(riedizione.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia()));
		riedizione.setAutocertificazioneAutorizzazioneMinisteroSalute(fileService.copyFile(riedizione.getAutocertificazioneAutorizzazioneMinisteroSalute()));
		riedizione.setAutocertificazioneAssenzaFinanziamenti(fileService.copyFile(riedizione.getAutocertificazioneAssenzaFinanziamenti()));
		riedizione.setContrattiAccordiConvenzioni(fileService.copyFile(riedizione.getContrattiAccordiConvenzioni()));
		riedizione.setDichiarazioneAssenzaConflittoInteresse(fileService.copyFile(riedizione.getDichiarazioneAssenzaConflittoInteresse()));

		LOGGER.debug(Utils.getLogMessage("Stato settato: BOZZA"));
		riedizione.setStato(EventoStatoEnum.BOZZA);

		riedizione.setId(null);

		LOGGER.debug(Utils.getLogMessage("Procedura di detach e clonazione Evento - success"));
	}

	@Override
	public Set<Evento> getEventiByProviderIdAndAnnoRiferimento(Long providerId, Integer annoRiferimento) {
		LocalDate leftDate = LocalDate.of(annoRiferimento, 1, 1);
		LocalDate rightDate = LocalDate.of(annoRiferimento, 12, 31);
		return eventoRepository.findAllByProviderIdAndDataFineBetween(providerId, leftDate, rightDate);
	}

	/* Eventi Rendicontati. Utilizzato per determinare la fascia di pagamento per la quota annuale */
	@Override
	public Set<Evento> getEventiRendicontatiByProviderIdAndAnnoRiferimento(Long providerId, Integer annoRiferimento) {
		LocalDate leftDate = LocalDate.of(annoRiferimento, 1, 1);
		LocalDate rightDate = LocalDate.of(annoRiferimento, 12, 31);
		return eventoRepository.findAllByProviderIdAndDataFineBetweenAndStato(providerId, leftDate, rightDate, EventoStatoEnum.RAPPORTATO);
	}

	/* Eventi Attuati nell'anno annoRiferimento dal provider */
	@Override
	public Set<Evento> getEventiForRelazioneAnnualeByProviderIdAndAnnoRiferimento(Long providerId, Integer annoRiferimento) {
		LocalDate leftDate = LocalDate.of(annoRiferimento, 1, 1);
		LocalDate rightDate = LocalDate.of(annoRiferimento, 12, 31);
		return eventoRepository.findAllByProviderIdAndDataFineBetweenAndStatoNot(providerId, leftDate, rightDate, EventoStatoEnum.BOZZA);
	}
	
	/* Vaschetta provider */
	@Override
	public Set<Evento> getEventiForProviderIdInScadenzaDiPagamento(Long providerId) {
		return eventoRepository.findAllByProviderIdAndDataScadenzaPagamentoBetweenAndPagatoFalse(providerId, LocalDate.now(), LocalDate.now().plusDays(30));
	}
	
	@Override
	public int countEventiForProviderIdInScadenzaDiPagamento(Long providerId) {
		Set<Evento> listaEventi = getEventiForProviderIdInScadenzaDiPagamento(providerId);
		if(listaEventi != null)
			return listaEventi.size();
		return 0;
	}
	
	/* Vaschetta provider */
	@Override
	public Set<Evento> getEventiForProviderIdPagamentoScaduti(Long providerId) {
		return eventoRepository.findAllByProviderIdAndDataScadenzaPagamentoBeforeAndPagatoFalse(providerId, LocalDate.now());
	}
	
	@Override
	public int countEventiForProviderIdPagamentoScaduti(Long providerId) {
		Set<Evento> listaEventi = getEventiForProviderIdPagamentoScaduti(providerId);
		if(listaEventi != null)
			return listaEventi.size();
		return 0;
	}
}
