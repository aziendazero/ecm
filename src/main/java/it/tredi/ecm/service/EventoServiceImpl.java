package it.tredi.ecm.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.cogeaps.CogeapsCaricaResponse;
import it.tredi.ecm.cogeaps.CogeapsStatoElaborazioneResponse;
import it.tredi.ecm.cogeaps.CogeapsWsRestClient;
import it.tredi.ecm.cogeaps.Helper;
import it.tredi.ecm.cogeaps.XmlReportBuilder;
import it.tredi.ecm.cogeaps.XmlReportValidator;
import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.DettaglioAttivitaRES;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoFAD;
import it.tredi.ecm.dao.entity.EventoFSC;
import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.dao.entity.FaseAzioniRuoliEventoFSCTypeA;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Partner;
import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.entity.ProgrammaGiornalieroRES;
import it.tredi.ecm.dao.entity.RendicontazioneInviata;
import it.tredi.ecm.dao.entity.RiepilogoRuoliFSC;
import it.tredi.ecm.dao.entity.Sponsor;
import it.tredi.ecm.dao.entity.VerificaApprendimentoFAD;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.RendicontazioneInviataResultEnum;
import it.tredi.ecm.dao.enumlist.RendicontazioneInviataStatoEnum;
import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import it.tredi.ecm.dao.enumlist.TipoMetodologiaEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoRESEnum;
import it.tredi.ecm.dao.repository.EventoRepository;
import it.tredi.ecm.dao.repository.PartnerRepository;
import it.tredi.ecm.dao.repository.PersonaEventoRepository;
import it.tredi.ecm.dao.repository.PersonaFullEventoRepository;
import it.tredi.ecm.dao.repository.SponsorRepository;
import it.tredi.ecm.exception.EcmException;
import it.tredi.ecm.service.bean.EcmProperties;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EventoWrapper;

@Service
public class EventoServiceImpl implements EventoService {
	public static final Logger LOGGER = Logger.getLogger(Evento.class);

	@Autowired
	private EventoRepository eventoRepository;

	@Autowired private PersonaEventoRepository personaEventoRepository;
	@Autowired private PersonaFullEventoRepository personaFullEventoRepository;
	@Autowired private SponsorRepository sponsorRepository;
	@Autowired private PartnerRepository partnerRepository;

	@Autowired
	private RendicontazioneInviataService rendicontazioneInviataService;

	@Autowired
	private FileService fileService;

	@Autowired
	private CogeapsWsRestClient cogeapsWsRestClient;

	@Autowired
	private EcmProperties ecmProperties;

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


	/*	SALVATAGGIO	*/
	@Override
	public Evento handleRipetibiliAndAllegati(EventoWrapper eventoWrapper) {
		Evento evento = eventoWrapper.getEvento();

		if(evento instanceof EventoRES){
			//date intermedie
			Set<LocalDate> dateIntermedie = new HashSet<LocalDate>();
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			for (String s : eventoWrapper.getDateIntermedieMapTemp().values()) {
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
			List<PersonaEvento> attachedList = new ArrayList<PersonaEvento>();
			while(it.hasNext()){
				PersonaEvento p = it.next();
				p = personaEventoRepository.findOne(p.getId());
				attachedList.add(p);
			}
			((EventoRES)evento).setDocenti(attachedList);

			retrieveProgrammaAndAddJoin(eventoWrapper);

			//Documento Verifica Ricadute Formative
			if (eventoWrapper.getDocumentoVerificaRicaduteFormative().getId() != null) {
				((EventoRES) evento).setDocumentoVerificaRicaduteFormative(eventoWrapper.getDocumentoVerificaRicaduteFormative());
			}
		}else if(evento instanceof EventoFSC){
			retrieveProgrammaAndAddJoin(eventoWrapper);
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
				dateIntermedieTemp.put(key, "");

			eventoWrapper.setDateIntermedieMapTemp(dateIntermedieTemp);
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
			//Programma
			eventoWrapper.setProgrammaEventoFSC(((EventoFSC) evento).getFasiAzioniRuoli());

			//mappa ruoli ore
			eventoWrapper.initMappaRuoloOreFSC();
		}else if(evento instanceof EventoFAD){
			//Docenti
			eventoWrapper.setDocenti(((EventoFAD) evento).getDocenti());

			//Requisiti Hardware Software
			if (((EventoFAD) evento).getRequisitiHardwareSoftware() != null) {
				eventoWrapper.setRequisitiHardwareSoftware(((EventoFAD) evento).getRequisitiHardwareSoftware());
			}

			//mappa verifica apprendimento
			eventoWrapper.initMappaVerificaApprendimentoFAD();
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
	public float calcoloDurataEvento(EventoWrapper eventoWrapper) {
		float durata = 0;

		if(eventoWrapper.getEvento() instanceof EventoRES){
			durata = calcoloDurataEventoRES(eventoWrapper.getProgrammaEventoRES());
			((EventoRES)eventoWrapper.getEvento()).setDurata(durata);
		}else if(eventoWrapper.getEvento() instanceof EventoFSC){
			durata = calcoloDurataEventoFSC(eventoWrapper.getProgrammaEventoFSC(), eventoWrapper.getRiepilogoRuoliFSC());
			((EventoFSC)eventoWrapper.getEvento()).setDurata(durata);
		}else if(eventoWrapper.getEvento() instanceof EventoFAD){

		}

		return durata;
	}

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

		return durata;
	}

	private float calcoloDurataEventoFSC(List<FaseAzioniRuoliEventoFSCTypeA> programma, Map<RuoloFSCEnum, RiepilogoRuoliFSC> riepilogoRuoliFSC){
		float durata = 0;

//		if(riepilogoRuoliFSC != null){
//			riepilogoRuoliFSC.forEach((k,v) ->{
//				v.clear();
//			});
//
//			if(programma!= null){
//				for(FaseAzioniRuoliEventoFSCTypeA fase : programma){
//					for(AzioneRuoliEventoFSC azione : fase.getAzioniRuoli()){
//						Set<RuoloFSCEnum> ruoliSelezionati = azione.getRuoli();
//						if(ruoliSelezionati != null){
//							ruoliSelezionati.forEach(ruolo -> {
//								//TODO sostituire con tempo relativo a singolo ruolo
//								float tempoDedicato = (azione.getTempoDedicato() != null) ? Utils.getRoundedFloatValue(azione.getTempoDedicato()) : 0.0f;
//								riepilogoRuoliFSC.get(ruolo).addTempo(tempoDedicato);
//							});
//						}
//					}
//				}
//
//			}
//		}
//
//		durata = getMax(riepilogoRuoliFSC);

		return durata;
	}

	private float getMax(Map<RuoloFSCEnum,RiepilogoRuoliFSC> riepilogoRuoliFSC){
		float max = 0.0f;

		if(riepilogoRuoliFSC != null){
			Iterator<Entry<RuoloFSCEnum,RiepilogoRuoliFSC>> iterator = riepilogoRuoliFSC.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<RuoloFSCEnum,RiepilogoRuoliFSC> pairs = iterator.next();
				if(pairs.getValue().getTempoDedicato() > max)
					max = pairs.getValue().getTempoDedicato();
			 }
		}

		return max;
	}

	private float calcoloDurataEventoFAD(){
		float durata = 0;


		return durata;
	}

	@Override
	public float calcoloCreditiEvento(EventoWrapper eventoWrapper) {
		float crediti = 0;

		if(eventoWrapper.getEvento() instanceof EventoRES){
			EventoRES evento = ((EventoRES)eventoWrapper.getEvento());
			crediti = calcoloCreditiFormativiEventoRES(evento.getTipologiaEvento(), evento.getDurata(), eventoWrapper.getProgrammaEventoRES(), evento.getNumeroPartecipanti());
			eventoWrapper.setCreditiProposti(crediti);
			LOGGER.info(Utils.getLogMessage("Calcolato crediti per evento RES"));
			return crediti;
		}else if(eventoWrapper.getEvento() instanceof EventoFSC){
			EventoFSC evento = ((EventoFSC)eventoWrapper.getEvento());
			crediti = calcoloCreditiFormativiEventoFSC(evento.getTipologiaEvento(), eventoWrapper.getRiepilogoRuoliFSC());
			evento.setCrediti(crediti);
			LOGGER.info(Utils.getLogMessage("Calcolato crediti per evento FSC"));
			return crediti;
		}else if(eventoWrapper.getEvento() instanceof EventoFAD){

		}

		return crediti;
	}

	private float calcoloCreditiFormativiEventoRES(TipologiaEventoRESEnum tipologiaEvento, float durata, List<ProgrammaGiornalieroRES> programma, int numeroPartecipanti){
		float crediti = 0.0f;

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
			float oreFrontale = 0f;
			float creditiInterattiva = 0f;
			float oreInterattiva = 0f;

			for(ProgrammaGiornalieroRES progrGio : programma) {
				for(DettaglioAttivitaRES a : progrGio.getProgramma()){
					if(a.getMetodologiaDidattica()!= null && a.getMetodologiaDidattica().getMetodologia() == TipoMetodologiaEnum.FRONTALE){
						oreFrontale += a.getOreAttivita();
					}else{
						oreInterattiva += a.getOreAttivita();
					}
				}
			}

			//metodologia frontale
			if(numeroPartecipanti >=1 && numeroPartecipanti <=20){
				creditiFrontale = oreFrontale * 1.0f;
				creditiFrontale = (creditiFrontale + (creditiFrontale*0.20f));
			}else if(numeroPartecipanti >=21 && numeroPartecipanti <= 50){
				//TODO 25% decrescente
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

		return crediti;
	}

	private float calcoloCreditiFormativiEventoFSC(TipologiaEventoFSCEnum tipologiaEvento, Map<RuoloFSCEnum,RiepilogoRuoliFSC> riepilogoRuoliFSC){
		float crediti = 0.0f;

		if(tipologiaEvento == TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO){

		}else if(tipologiaEvento == TipologiaEventoFSCEnum.GRUPPI_DI_MIGLIORAMENTO){

		}else if(tipologiaEvento == TipologiaEventoFSCEnum.ATTIVITA_DI_RICERCA){

		}else if(tipologiaEvento == TipologiaEventoFSCEnum.AUDIT_CLINICO_ASSISTENZIALE){

		}

		return crediti;
	}

	private float calcoloCreditiFormativiEventoFAD(){
		return (Float) null;
	}

	/*
	 *
	 * prendo il programma dal wrapper e aggancio l'evento alle fasi o ai giorni
	 * */
	@Override
	public void retrieveProgrammaAndAddJoin(EventoWrapper eventoWrapper) {
		Evento evento = eventoWrapper.getEvento();
		if(evento instanceof EventoRES){
			((EventoRES) evento).setProgramma(eventoWrapper.getProgrammaEventoRES());
			if(eventoWrapper.getProgrammaEventoRES() != null){
				for(ProgrammaGiornalieroRES p : ((EventoRES) evento).getProgramma()){
					p.setEventoRES((EventoRES) evento);
				}
			}
		}else if(evento instanceof EventoFSC){
			((EventoFSC)evento).setFasiAzioniRuoli(eventoWrapper.getProgrammaEventoFSC());
			if(eventoWrapper.getProgrammaEventoFSC() != null){
				for(FaseAzioniRuoliEventoFSCTypeA fase : ((EventoFSC)evento).getFasiAzioniRuoli()){
					fase.setEvento(((EventoFSC)evento));
				}

			}
		}else if(evento instanceof EventoFAD){
			//TODO FAD
		}
	}

}
