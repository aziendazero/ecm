package it.tredi.ecm.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import it.tredi.ecm.cogeaps.CogeapsCaricaResponse;
import it.tredi.ecm.cogeaps.CogeapsStatoElaborazioneResponse;
import it.tredi.ecm.cogeaps.CogeapsWsRestClient;
import it.tredi.ecm.cogeaps.Helper;
import it.tredi.ecm.cogeaps.XmlReportBuilder;
import it.tredi.ecm.cogeaps.XmlReportValidator;
import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.AnagrafeRegionaleCrediti;
import it.tredi.ecm.dao.entity.AzioneRuoliEventoFSC;
import it.tredi.ecm.dao.entity.DettaglioAttivitaFAD;
import it.tredi.ecm.dao.entity.DettaglioAttivitaRES;
import it.tredi.ecm.dao.entity.Disciplina;
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
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.entity.ProgrammaGiornalieroRES;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.RendicontazioneInviata;
import it.tredi.ecm.dao.entity.RiepilogoFAD;
import it.tredi.ecm.dao.entity.RiepilogoRES;
import it.tredi.ecm.dao.entity.RiepilogoRuoliFSC;
import it.tredi.ecm.dao.entity.RuoloOreFSC;
import it.tredi.ecm.dao.entity.Sponsor;
import it.tredi.ecm.dao.entity.VerificaApprendimentoFAD;
import it.tredi.ecm.dao.enumlist.DestinatariEventoEnum;
import it.tredi.ecm.dao.enumlist.EventoStatoEnum;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.MetodoDiLavoroEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.RendicontazioneInviataResultEnum;
import it.tredi.ecm.dao.enumlist.RendicontazioneInviataStatoEnum;
import it.tredi.ecm.dao.enumlist.RuoloFSCBaseEnum;
import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import it.tredi.ecm.dao.enumlist.TipoMetodologiaEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoRESEnum;
import it.tredi.ecm.dao.enumlist.VerificaApprendimentoFSCEnum;
import it.tredi.ecm.dao.enumlist.VerificaApprendimentoRESEnum;
import it.tredi.ecm.dao.enumlist.VerificaPresenzaPartecipantiEnum;
import it.tredi.ecm.dao.repository.EventoPianoFormativoRepository;
import it.tredi.ecm.dao.repository.EventoRepository;
import it.tredi.ecm.dao.repository.PartnerRepository;
import it.tredi.ecm.dao.repository.PersonaEventoRepository;
import it.tredi.ecm.dao.repository.SponsorRepository;
import it.tredi.ecm.exception.EcmException;
import it.tredi.ecm.service.bean.EcmProperties;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EventoRESProgrammaGiornalieroWrapper;
import it.tredi.ecm.web.bean.EventoWrapper;
import it.tredi.ecm.web.bean.RicercaEventoWrapper;
import it.tredi.ecm.web.bean.ScadenzeEventoWrapper;
import it.tredi.ecm.web.validator.FileValidator;

@Service
public class EventoServiceImpl implements EventoService {
	public static final Logger LOGGER = Logger.getLogger(EventoServiceImpl.class);

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

	@Autowired private AnagrafeRegionaleCreditiService anagrafeRegionaleCreditiService;

	@Autowired private EcmProperties ecmProperties;

	@Autowired private PersonaEventoService personaEventoService;

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

			if(evento.getStato() == EventoStatoEnum.CANCELLATO){
				//TODO al momento non lo faccio....poi lo chiederenno loro...da tenere presente che....se settiamo a flase il flag..l'evento piano formativo sarà eli
				//bisgna gestire tutti i cascade corretti -> eventoPianoFormativo è presente in più piani formativi e nell'evento che lo ha attuato

				//se annullo un evento che è stato attuato da piano formativo...rimuovo il flag in modo tale da poter rieditare l'evento
				//eventoPianoFormativo.setAttuato(false);
			}else{
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
		return account.isSegreteria() || (account.isProvider() && account.getProvider().canInsertEvento());
	}

	//evento rieditabile solo prima del 20/12 dell'anno corrente
	@Override
	public boolean canRieditEvento(Account account) {
		return canCreateEvento(account)
			&& (LocalDate.now().isAfter(LocalDate.of(LocalDate.now().getYear(), 1, 1))
			&& LocalDate.now().isBefore(LocalDate.of(LocalDate.now().getYear(), 12, 20)));
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
			if (eventoWrapper.getDocumentoVerificaRicaduteFormative() != null && eventoWrapper.getDocumentoVerificaRicaduteFormative().getId() != null) {
				eventoRES.setDocumentoVerificaRicaduteFormative(fileService.getFile(eventoWrapper.getDocumentoVerificaRicaduteFormative().getId()));
			}else{
				eventoRES.setDocumentoVerificaRicaduteFormative(null);
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
			if (eventoWrapper.getRequisitiHardwareSoftware() != null && eventoWrapper.getRequisitiHardwareSoftware().getId() != null) {
				((EventoFAD) evento).setRequisitiHardwareSoftware(fileService.getFile(eventoWrapper.getRequisitiHardwareSoftware().getId()));
			}else{
				((EventoFAD) evento).setRequisitiHardwareSoftware(null);
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
		if (eventoWrapper.getBrochure() != null && eventoWrapper.getBrochure().getId() != null) {
			evento.setBrochureEvento(fileService.getFile(eventoWrapper.getBrochure().getId()));
		}else{
			evento.setBrochureEvento(null);
		}

		//Autocertificazione Assenza Finanziamenti
		if (eventoWrapper.getAutocertificazioneAssenzaFinanziamenti() != null && eventoWrapper.getAutocertificazioneAssenzaFinanziamenti().getId() != null) {
			evento.setAutocertificazioneAssenzaFinanziamenti(fileService.getFile(eventoWrapper.getAutocertificazioneAssenzaFinanziamenti().getId()));
		}else{
			evento.setAutocertificazioneAssenzaFinanziamenti(null);
		}

		//Contratti Accordi Convenzioni
		if (eventoWrapper.getContrattiAccordiConvenzioni() != null && eventoWrapper.getContrattiAccordiConvenzioni().getId() != null) {
			evento.setContrattiAccordiConvenzioni(fileService.getFile(eventoWrapper.getContrattiAccordiConvenzioni().getId()));
		}else{
			evento.setContrattiAccordiConvenzioni(null);
		}

		//Dichiarazione Assenza Conflitto Interesse
		if (eventoWrapper.getDichiarazioneAssenzaConflittoInteresse() != null && eventoWrapper.getDichiarazioneAssenzaConflittoInteresse().getId() != null) {
			evento.setDichiarazioneAssenzaConflittoInteresse(fileService.getFile(eventoWrapper.getDichiarazioneAssenzaConflittoInteresse().getId()));
		}else{
			evento.setDichiarazioneAssenzaConflittoInteresse(null);
		}

		//Autocertificazione Assenza Aziende Alimenti Prima Infanzia
		if (eventoWrapper.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia() != null && eventoWrapper.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia().getId() != null) {
			evento.setAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia(fileService.getFile(eventoWrapper.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia().getId()));
		}else{
			evento.setAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia(null);
		}

		//Autocertificazione Autorizzazione Ministero Salute
		if (eventoWrapper.getAutocertificazioneAutorizzazioneMinisteroSalute() != null && eventoWrapper.getAutocertificazioneAutorizzazioneMinisteroSalute().getId() != null) {
			evento.setAutocertificazioneAutorizzazioneMinisteroSalute(fileService.getFile(eventoWrapper.getAutocertificazioneAutorizzazioneMinisteroSalute().getId()));
		}else{
			evento.setAutocertificazioneAutorizzazioneMinisteroSalute(null);
		}

		//non  deve essere possibile caricare gli allegati degli sponsor
		if(evento.getEventoSponsorizzato() != null && !evento.getEventoSponsorizzato().booleanValue())
			evento.setSponsorUploaded(true);

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
	@Transactional
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
				else{
					ultimaRendicontazioneInviata.setResult(RendicontazioneInviataResultEnum.SUCCESS);
					evento.setStato(EventoStatoEnum.RAPPORTATO);
					evento.setAnagrafeRegionaleCrediti(XmlReportValidator.extractAnagrafeRegionaleCreditiPartecipantiFromXml(ultimaRendicontazioneInviata.getFileName(), ultimaRendicontazioneInviata.getFileRendicontazione().getData()));//extract info AnagrafeRegionaleCrediti
					save(evento);
				}
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
		long durataMinuti = 0;

		if(programma != null){
			for(EventoRESProgrammaGiornalieroWrapper progrGior : programma){
				for(DettaglioAttivitaRES dett : progrGior.getProgramma().getProgramma()){
					if(!dett.isExtraType()) {
//						durata += dett.getOreAttivita();
						durataMinuti += dett.getMinutiAttivita();
					}
				}
			}
		}

		durata = (float) durataMinuti / 60;
		durata = Utils.getRoundedFloatValue(durata, 2);
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
			crediti = calcoloCreditiFormativiEventoRES(evento.getTipologiaEventoRES(), evento.getDurata(), eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().values(), evento.getNumeroPartecipanti(), evento.getRiepilogoRES());
			eventoWrapper.setCreditiProposti(crediti);
			LOGGER.info(Utils.getLogMessage("Calcolato crediti per evento RES"));
			return crediti;
		}else if(eventoWrapper.getEvento() instanceof EventoFSC){
			EventoFSC evento = ((EventoFSC)eventoWrapper.getEvento());
			crediti = calcoloCreditiFormativiEventoFSC(evento.getTipologiaEventoFSC(), eventoWrapper);
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
		long minutiFrontale = 0;
		float oreInterattiva = 0f;
		long minutiInterattiva = 0;

		riepilogoRES.clear();

		for(EventoRESProgrammaGiornalieroWrapper progrGio : programma) {
			for(DettaglioAttivitaRES a : progrGio.getProgramma().getProgramma()){
				if(a.getMetodologiaDidattica()!= null && a.getMetodologiaDidattica().getMetodologia() == TipoMetodologiaEnum.FRONTALE){
//					oreFrontale += a.getOreAttivita();
					minutiFrontale += a.getMinutiAttivita();
				}else if(a.getMetodologiaDidattica()!= null && a.getMetodologiaDidattica().getMetodologia() == TipoMetodologiaEnum.INTERATTIVA){
//					oreInterattiva += a.getOreAttivita();
					minutiInterattiva += a.getMinutiAttivita();
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

		oreFrontale = (float) minutiFrontale / 60;
		oreFrontale = Utils.getRoundedFloatValue(oreFrontale, 2);
		oreInterattiva = (float) minutiInterattiva / 60;
		oreInterattiva = Utils.getRoundedFloatValue(oreInterattiva, 2);

		riepilogoRES.setTotaleOreFrontali(oreFrontale);
		riepilogoRES.setTotaleOreInterattive(oreInterattiva);

		//approssimazione per calcolo con HALF_DOWN (2.5 -> 2 || 2.6 -> 3)
		durata = Utils.getRoundedHALFDOWNFloatValue(durata);
		oreFrontale = Utils.getRoundedHALFDOWNFloatValue(oreFrontale);
		oreInterattiva = Utils.getRoundedHALFDOWNFloatValue(oreInterattiva);

		if(tipologiaEvento == TipologiaEventoRESEnum.CONVEGNO_CONGRESSO){
			crediti = (0.20f * (int) durata);
			if(crediti > 5.0f)
				crediti = 5.0f;
		}

		if(tipologiaEvento == TipologiaEventoRESEnum.WORKSHOP_SEMINARIO){
			crediti = 1 * (int) durata;
			if(crediti > 50f)
				crediti = 50f;
		}

		if(tipologiaEvento == TipologiaEventoRESEnum.CORSO_AGGIORNAMENTO){
			float creditiFrontale = 0f;
			float creditiInterattiva = 0f;

			//metodologia frontale
			numeroPartecipanti = numeroPartecipanti!= null ? numeroPartecipanti.intValue() : 0;

			if(numeroPartecipanti >=1 && numeroPartecipanti <=20){
				creditiFrontale = (int) oreFrontale * 1.25f;
			}else if(numeroPartecipanti >=21 && numeroPartecipanti <= 50){
				float creditiDecrescenti = getQuotaFasciaDecrescenteForRES(numeroPartecipanti);
				creditiFrontale = (int) oreFrontale * creditiDecrescenti;
			}else if(numeroPartecipanti >=51 && numeroPartecipanti <=100){
				creditiFrontale = (int) oreFrontale * 1.0f;
			}else if(numeroPartecipanti >= 101 && numeroPartecipanti <= 150){
				creditiFrontale = (int) oreFrontale* 0.75f;
			}else if(numeroPartecipanti >= 151 && numeroPartecipanti <= 200){
				creditiFrontale = (int) oreFrontale * 0.5f;
			}

			//metodologia interattiva
			creditiInterattiva = (int) oreInterattiva * 1.5f;

			crediti = creditiFrontale + creditiInterattiva;

			if(crediti > 50f)
				crediti = 50f;
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
		durata = Utils.getRoundedHALFDOWNFloatValue(durata);

		if(conTutor != null && conTutor)
			crediti = (int) durata * 1.5f;
		else
			crediti = (int) durata * 1.0f;
		if(crediti > 50f)
			crediti = 50f;

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
		//mostra tutti gli eventi del provider non in bozza e già iniziati e che finiscono dopo l'inizio dell'anno corrente
		return eventoRepository.findAllByProviderIdAndStatoNotAndDataInizioBeforeAndDataFineAfter(providerId, EventoStatoEnum.BOZZA, LocalDate.now(), LocalDate.of(LocalDate.now().getYear(), 1, 1));
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
		int edizione = getLastEdizioneEventoByPrefix(eventoPadre.getPrefix()) + 1;
		long eventoPadreId = eventoPadre.getId();
		Evento riedizione = detachEvento(eventoPadre);
		cloneDetachedEvento(riedizione);
		riedizione.setEdizione(edizione);
		riedizione.setEventoPadre(getEvento(eventoPadreId));
		return riedizione;
	}

	//si può fareeeeeee (Iomminstein mode on)... sigh devo proprio andare in vacanza...
	/* funzione di detach ad hoc (detachare veramente tutto ricorsivamente non conviene proprio a
	 * causa di Entity come Provider e Accreditamento presenti in Evento.
	 * */
	@Override
	public Evento detachEvento(Evento eventoPadre) throws Exception{
		LOGGER.debug(Utils.getLogMessage("DETACH evento id: " + eventoPadre.getId()));

		Utils.touchFirstLevelOfEverything(eventoPadre);

		//casi specifici
		if(eventoPadre instanceof EventoFAD) {

			LOGGER.debug(Utils.getLogMessage("Procedura di detach EventoFAD - start"));

			LOGGER.debug(Utils.getLogMessage("Detach Docenti"));
			for(PersonaEvento d : ((EventoFAD) eventoPadre).getDocenti()) {
				LOGGER.debug(Utils.getLogMessage("Detach Docente: " + d.getId()));
				entityManager.detach(d);
			}

			LOGGER.debug(Utils.getLogMessage("Detach DettaglioAttivitaFAD"));
			for(DettaglioAttivitaFAD daf : ((EventoFAD) eventoPadre).getProgrammaFAD()) {
				LOGGER.debug(Utils.getLogMessage("Detach DettaglioAttivitaFAD: " + daf.getId()));
				for(PersonaEvento pe : daf.getDocenti()) {
					LOGGER.debug(Utils.getLogMessage("Detach Docente in DettaglioAttivitaFAD: " + pe.getId()));
					entityManager.detach(pe);
				}
				entityManager.detach(daf);
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
				LOGGER.debug(Utils.getLogMessage("Detach Programma RES: " + pgr.getId()));
				for(DettaglioAttivitaRES dar : pgr.getProgramma()) {
					LOGGER.debug(Utils.getLogMessage("Detach DettaglioAttivitaRES: " + dar.getId()));
					for(PersonaEvento pe : dar.getDocenti()) {
						LOGGER.debug(Utils.getLogMessage("Detach Docente in DettaglioAttivitaRES: " + pe.getId()));
						entityManager.detach(pe);
					}
					entityManager.detach(dar);
				}
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

		LOGGER.debug(Utils.getLogMessage("Detach Anagrafe Regionale Crediti"));
		for(AnagrafeRegionaleCrediti a : eventoPadre.getAnagrafeRegionaleCrediti()) {
			LOGGER.debug(Utils.getLogMessage("Detach Anagrafe Regionale Crediti: " + a.getId()));
			entityManager.detach(a);
		}

		LOGGER.debug(Utils.getLogMessage("Detach Responsabile Segreteria"));
		entityManager.detach(eventoPadre.getResponsabileSegreteria());

		entityManager.detach(eventoPadre);

		LOGGER.debug(Utils.getLogMessage("Procedura di detach Evento - success"));

		return eventoPadre;
	}

	//sistema l'Evento detatchato clonando i campi che devono essere clonati
	private void cloneDetachedEvento(Evento riedizione) throws CloneNotSupportedException {

		//mappa oldId , newId per salvare la lista docenti nel dettaglio attività
		Map<Long, Long> mapIdDocenti = new HashMap<Long, Long>();

		if(riedizione instanceof EventoFAD) {

			LOGGER.debug(Utils.getLogMessage("Procedura di clonazione EventoFAD - start"));

			LOGGER.debug(Utils.getLogMessage("Clonazione e salvataggio Docenti"));
			for(PersonaEvento d : ((EventoFAD) riedizione).getDocenti()) {
				LOGGER.debug(Utils.getLogMessage("Clonazione Docente: " + d.getId()));
				Long oldId = d.getId();
				d.setId(null);
				d.getAnagrafica().setCv(fileService.copyFile(d.getAnagrafica().getCv()));
				personaEventoRepository.save(d);
				Long newId = d.getId();
				mapIdDocenti.put(oldId, newId);
				LOGGER.debug(Utils.getLogMessage("Docente clonato salvato: " + d.getId()));
			}

			LOGGER.debug(Utils.getLogMessage("Clonazione dettaglioAttività FAD"));
			List<DettaglioAttivitaFAD> dettaglioAttivitaFADList = new ArrayList<DettaglioAttivitaFAD>();
			for(DettaglioAttivitaFAD daf : ((EventoFAD) riedizione).getProgrammaFAD()) {
				LOGGER.debug(Utils.getLogMessage("Clonazione DettaglioAttivitaFAD: " + daf.getId()));
				daf.setId(null);
				LOGGER.debug(Utils.getLogMessage("Clonazione dei Docenti del DettaglioAttivitaFAD"));
				for(PersonaEvento pe : daf.getDocenti()) {
					Long newId = mapIdDocenti.get(pe.getId());
					pe.setId(newId);
					personaEventoRepository.save(pe);
				}
				Set<PersonaEvento> docentiSet = new HashSet<PersonaEvento>();
				docentiSet.addAll(Arrays.asList(daf.getDocenti().toArray(new PersonaEvento[daf.getDocenti().size()])));
				daf.setDocenti(docentiSet);
				dettaglioAttivitaFADList.add(daf);
			}
			((EventoFAD) riedizione).setProgrammaFAD(dettaglioAttivitaFADList);

//			((EventoFAD) riedizione).setConfermatiCrediti(null);

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
				Long oldId = d.getId();
				d.setId(null);
				d.getAnagrafica().setCv(fileService.copyFile(d.getAnagrafica().getCv()));
				personaEventoRepository.save(d);
				Long newId = d.getId();
				mapIdDocenti.put(oldId, newId);
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
				for(DettaglioAttivitaRES dar : pgr.getProgramma()) {
					LOGGER.debug(Utils.getLogMessage("Clonazione DettaglioAttivitaRES: " + dar.getId()));
					dar.setId(null);
					LOGGER.debug(Utils.getLogMessage("Clonazione dei Docenti del DettaglioAttivitaRES"));
					for(PersonaEvento pe : dar.getDocenti()) {
						Long newId = mapIdDocenti.get(pe.getId());
						pe.setId(newId);
						personaEventoRepository.save(pe);
					}
					Set<PersonaEvento> docentiSet = new HashSet<PersonaEvento>();
					docentiSet.addAll(Arrays.asList(dar.getDocenti().toArray(new PersonaEvento[dar.getDocenti().size()])));
					dar.setDocenti(docentiSet);
					dettaglioAttivitaRESList.add(dar);
				}
				pgr.setProgramma(dettaglioAttivitaRESList);
				programmaRES.add(pgr);
			}
			((EventoRES) riedizione).setProgramma(programmaRES);

//			((EventoRES) riedizione).setConfermatiCrediti(null);

			LOGGER.debug(Utils.getLogMessage("Clonazione verifica apprendimento"));
			Set<VerificaApprendimentoRESEnum> verificaApprendimento = new HashSet<VerificaApprendimentoRESEnum>();
			verificaApprendimento.addAll(Arrays.asList(((EventoRES) riedizione).getVerificaApprendimento().toArray(new VerificaApprendimentoRESEnum[((EventoRES) riedizione).getVerificaApprendimento().size()])));
			((EventoRES) riedizione).setVerificaApprendimento(verificaApprendimento);

			LOGGER.debug(Utils.getLogMessage("Clonazione verifica presenza partecipanti"));
			Set<VerificaPresenzaPartecipantiEnum> verificaPresenzaPartecipanti = new HashSet<VerificaPresenzaPartecipantiEnum>();
			verificaPresenzaPartecipanti.addAll(Arrays.asList(((EventoRES) riedizione).getVerificaPresenzaPartecipanti().toArray(new VerificaPresenzaPartecipantiEnum[((EventoRES) riedizione).getVerificaPresenzaPartecipanti().size()])));
			((EventoRES) riedizione).setVerificaPresenzaPartecipanti(verificaPresenzaPartecipanti);

			((EventoRES) riedizione).setDocumentoVerificaRicaduteFormative(fileService.copyFile(((EventoRES) riedizione).getDocumentoVerificaRicaduteFormative()));

			//ricalcolato
			((EventoRES) riedizione).setRiepilogoRES(new RiepilogoRES());
		}

		else if(riedizione instanceof EventoFSC) {

			LOGGER.debug(Utils.getLogMessage("Procedura di clonazione EventoFSC - start"));

//			((EventoFSC) riedizione).setOttenutoComitatoEtico(null);

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

			LOGGER.debug(Utils.getLogMessage("Clonazione verifica apprendimento"));
			Set<VerificaApprendimentoFSCEnum> verificaApprendimento = new HashSet<VerificaApprendimentoFSCEnum>();
			verificaApprendimento.addAll(Arrays.asList(((EventoFSC) riedizione).getVerificaApprendimento().toArray(new VerificaApprendimentoFSCEnum[((EventoFSC) riedizione).getVerificaApprendimento().size()])));
			((EventoFSC) riedizione).setVerificaApprendimento(verificaApprendimento);

			LOGGER.debug(Utils.getLogMessage("Clonazione verifica presenza partecipanti"));
			Set<VerificaPresenzaPartecipantiEnum> verificaPresenzaPartecipanti = new HashSet<VerificaPresenzaPartecipantiEnum>();
			verificaPresenzaPartecipanti.addAll(Arrays.asList(((EventoFSC) riedizione).getVerificaPresenzaPartecipanti().toArray(new VerificaPresenzaPartecipantiEnum[((EventoFSC) riedizione).getVerificaPresenzaPartecipanti().size()])));
			((EventoFSC) riedizione).setVerificaPresenzaPartecipanti(verificaPresenzaPartecipanti);

			//ricalcolato
			((EventoFSC) riedizione).setRiepilogoRuoli(new ArrayList<RiepilogoRuoliFSC>());
		}

		//parte in comune
		LOGGER.debug(Utils.getLogMessage("Clonazione destinatari"));
		Set<DestinatariEventoEnum> destinatariEvento = new HashSet<DestinatariEventoEnum>();
		destinatariEvento.addAll(Arrays.asList(riedizione.getDestinatariEvento().toArray(new DestinatariEventoEnum[riedizione.getDestinatariEvento().size()])));
		riedizione.setDestinatariEvento(destinatariEvento);

		LOGGER.debug(Utils.getLogMessage("Clonazione discipline"));
		Set<Disciplina> discipline = new HashSet<Disciplina>();
		discipline.addAll(Arrays.asList(riedizione.getDiscipline().toArray(new Disciplina[riedizione.getDiscipline().size()])));
		riedizione.setDiscipline(discipline);

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
//		riedizione.setCanDoRendicontazione(false);
		riedizione.setValidatorCheck(false);
		riedizione.setReportPartecipantiXML(null);
		riedizione.setReportPartecipantiCSV(null);
		riedizione.setEventoPianoFormativo(null);
		riedizione.setDataScadenzaPagamento(null);
		riedizione.setInviiRendicontazione(new HashSet<RendicontazioneInviata>());
		riedizione.setAnagrafeRegionaleCrediti(null);
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

	@Override
	public List<Evento> cerca(RicercaEventoWrapper wrapper) {

		String query = "";
		HashMap<String, Object> params = new HashMap<String, Object>();
		Set<String> querytipologiaOR = new HashSet<String>();

		if(wrapper.getDenominazioneLegale() != null && !wrapper.getDenominazioneLegale().isEmpty()){
			//devo fare il join con la tabella provider
			query ="SELECT DISTINCT e FROM Evento e LEFT JOIN e.discipline d LEFT JOIN e.provider p WHERE UPPER(p.denominazioneLegale) LIKE :denominazioneLegale";
			params.put("denominazioneLegale", "%" + wrapper.getDenominazioneLegale().toUpperCase() + "%");
		}else{
			//posso cercare direttamente su evento
			query ="SELECT DISTINCT e FROM Evento e LEFT JOIN e.discipline d";
		}

			//PROVIDER ID
			if(wrapper.getCampoIdProvider() != null){
				query = Utils.QUERY_AND(query, "e.provider.id = :providerId");
				params.put("providerId", wrapper.getCampoIdProvider());
			}

			//TIPOLOGIA EVENTO
			if(wrapper.getTipologieSelezionate() != null && !wrapper.getTipologieSelezionate().isEmpty()){
				query = Utils.QUERY_AND(query, "e.proceduraFormativa IN (:tipologieSelezionate)");
				params.put("tipologieSelezionate", wrapper.getTipologieSelezionate());

				if(wrapper.getTipologieRES() != null && !wrapper.getTipologieRES().isEmpty()){
					querytipologiaOR.add("e.tipologiaEventoRES IN (:tipologieRES)");
					params.put("tipologieRES", wrapper.getTipologieRES());
				}else{
					if(wrapper.getTipologieSelezionate().contains(ProceduraFormativa.RES))
						querytipologiaOR.add("Type(e) = EventoRES");
				}

				if(wrapper.getTipologieFSC() != null && !wrapper.getTipologieFSC().isEmpty()){
					querytipologiaOR.add("e.tipologiaEventoFSC IN (:tipologieFSC)");
					params.put("tipologieFSC", wrapper.getTipologieFSC());
				}else{
					if(wrapper.getTipologieSelezionate().contains(ProceduraFormativa.FSC))
						querytipologiaOR.add("Type(e) = EventoFSC");
				}

				if(wrapper.getTipologieFAD() != null && !wrapper.getTipologieFAD().isEmpty()){
					querytipologiaOR.add("e.tipologiaEventoFAD IN (:tipologieFAD)");
					params.put("tipologieFAD", wrapper.getTipologieFAD());
				}else{
					if(wrapper.getTipologieSelezionate().contains(ProceduraFormativa.FAD))
						querytipologiaOR.add("Type(e) = EventoFAD");
				}

				if(!querytipologiaOR.isEmpty()){
					query += " AND (";
					Iterator<String> it = querytipologiaOR.iterator();
					query += it.next();
					while(it.hasNext())
						query += " OR " + it.next();
					query += ")";
				}
			}

			//STATO EVENTO
			if(wrapper.getStatiSelezionati() != null && !wrapper.getStatiSelezionati().isEmpty()){
				query = Utils.QUERY_AND(query, "e.stato IN (:statiSelezionati)");
				params.put("statiSelezionati", wrapper.getStatiSelezionati());
			}

			//EVENTO ID
			if(!wrapper.getCampoIdEvento().isEmpty()){
				query = Utils.QUERY_AND(query, "e.prefix = :eventoId");
				params.put("eventoId", wrapper.getCampoIdEvento());
			}

			//TITOLO EVENTO
			if(!wrapper.getTitoloEvento().isEmpty()){
				query = Utils.QUERY_AND(query, "UPPER(e.titolo) LIKE :titoloEvento");
				params.put("titoloEvento", "%" + wrapper.getTitoloEvento().toUpperCase() + "%");
			}

			//OBIETTIVI NAZIONALI
			if(wrapper.getObiettiviNazionaliSelezionati() != null && !wrapper.getObiettiviNazionaliSelezionati().isEmpty()){
				query = Utils.QUERY_AND(query, "e.obiettivoNazionale IN (:obiettiviNazionaliSelezionati)");
				params.put("obiettiviNazionaliSelezionati", wrapper.getObiettiviNazionaliSelezionati());
			}

			//OBIETTIVI REGIONALI
			if(wrapper.getObiettiviRegionaliSelezionati() != null && !wrapper.getObiettiviRegionaliSelezionati().isEmpty()){
				query = Utils.QUERY_AND(query, "e.obiettivoRegionale IN (:obiettiviRegionaliSelezionati)");
				params.put("obiettiviRegionaliSelezionati", wrapper.getObiettiviRegionaliSelezionati());
			}

			//PROFESSIONI SELEZIONATE
			if(wrapper.getProfessioniSelezionate() != null && !wrapper.getProfessioniSelezionate().isEmpty()){
				Set<Professione> professioniFromDiscipline = new HashSet<Professione>();
				if(wrapper.getDisciplineSelezionate() != null){
					for(Disciplina d : wrapper.getDisciplineSelezionate())
						professioniFromDiscipline.add(d.getProfessione());
				}

				//vedo se ci sono professioni selezionate senza alcuna disciplina specificata
				wrapper.getProfessioniSelezionate().removeAll(professioniFromDiscipline);
				if(!wrapper.getProfessioniSelezionate().isEmpty()){
					for(Disciplina d : wrapper.getDisciplineList()){
						if(wrapper.getProfessioniSelezionate().contains(d.getProfessione()))
							wrapper.getDisciplineSelezionate().add(d);
					}
				}
			}

			//DISCIPLINE SELEZIONATE
			if(wrapper.getDisciplineSelezionate() != null && !wrapper.getDisciplineSelezionate().isEmpty()){
				query = Utils.QUERY_AND(query, "d IN (:disciplineSelezionate)");
				params.put("disciplineSelezionate", wrapper.getDisciplineSelezionate());
			}

			//NUMERO CREDITI
			if(wrapper.getCrediti() != null && wrapper.getCrediti().floatValue() > 0){
				query = Utils.QUERY_AND(query, "e.crediti = :crediti");
				params.put("crediti", wrapper.getCrediti().floatValue());
			}

			//PROVINCIA
			if(wrapper.getProvincia() != null && !wrapper.getProvincia().isEmpty()){
				query = Utils.QUERY_AND(query, "e.sedeEvento.provincia = :provincia");
				params.put("provincia", wrapper.getProvincia());
			}

			//COMUNE
			if(wrapper.getComune() != null && !wrapper.getComune().isEmpty()){
				query = Utils.QUERY_AND(query, "e.sedeEvento.comune = :comune");
				params.put("comune", wrapper.getComune());
			}

			//LUOGO
			if(wrapper.getLuogo() != null && !wrapper.getLuogo().isEmpty()){
				query = Utils.QUERY_AND(query, "UPPER(e.sedeEvento.luogo) LIKE :luogo");
				params.put("luogo", "%" + wrapper.getLuogo().toUpperCase() + "%");
			}

			//DATA INZIO
			if(wrapper.getDataInizioStart() != null){
				query = Utils.QUERY_AND(query, "e.dataInizio >= :dataInizioStart");
				params.put("dataInizioStart", wrapper.getDataInizioStart());
			}

			if(wrapper.getDataInizioEnd() != null){
				query = Utils.QUERY_AND(query, "e.dataInizio <= :dataInizioEnd");
				params.put("dataInizioEnd", wrapper.getDataInizioEnd());
			}


			//DATA FINE
			if(wrapper.getDataFineStart() != null){
				query = Utils.QUERY_AND(query, "e.dataFine >= :dataFineStart");
				params.put("dataFineStart", wrapper.getDataFineStart());
			}

			if(wrapper.getDataFineEnd() != null){
				query = Utils.QUERY_AND(query, "e.dataFine <= :dataFineEnd");
				params.put("dataFineEnd", wrapper.getDataFineEnd());
			}

			//DATA PAGAMENTO
			if(wrapper.getDataScadenzaPagamentoStart() != null){
				query = Utils.QUERY_AND(query, "e.dataScadenzaPagamento >= :dataScadenzaPagamentoStart");
				params.put("dataScadenzaPagamentoStart", wrapper.getDataScadenzaPagamentoStart());
			}

			if(wrapper.getDataScadenzaPagamentoEnd() != null){
				query = Utils.QUERY_AND(query, "e.dataScadenzaPagamento <= :dataScadenzaPagamentoEnd");
				params.put("dataScadenzaPagamentoEnd", wrapper.getDataScadenzaPagamentoEnd());
			}

			//STATO PAGAMENTO
			if(wrapper.getPagato() != null){
				query = Utils.QUERY_AND(query, "e.pagato = :pagato");
				params.put("pagato", wrapper.getPagato().booleanValue());
			}

			//DOCENTI
			if(wrapper.getDocenti() != null && !wrapper.getDocenti().isEmpty()) {
				Set<Long> idEventi = new HashSet<Long>();
				int counter = 0;

				//ATTENZIONE le PersoneEvento nel wrapper non hanno l'id, ma solo il nome e il cognome,
				//ho bisogno di una query che le vada a prendere
				Iterator<PersonaEvento> it = wrapper.getDocenti().values().iterator();
				while(it.hasNext()) {
					PersonaEvento pe = it.next();
					if(counter == 0) {
						idEventi = personaEventoService.getAllEventoIdByNomeAndCognomeDocente(pe.getAnagrafica().getNome(), pe.getAnagrafica().getCognome());
					}
					else {
						if(wrapper.isRicercaEsclusivaDocenti() && idEventi != null) {
							idEventi.retainAll(personaEventoService.getAllEventoIdByNomeAndCognomeDocente(pe.getAnagrafica().getNome(), pe.getAnagrafica().getCognome()));
						}
						else if(!wrapper.isRicercaEsclusivaDocenti()) {
							idEventi.addAll(personaEventoService.getAllEventoIdByNomeAndCognomeDocente(pe.getAnagrafica().getNome(), pe.getAnagrafica().getCognome()));
						}
					}
					counter++;
				}

				//a questo punto idEventi conterrà un Set di Id evento con il quale filtrare la ricerca
				query = Utils.QUERY_AND(query, "e.id IN (:idEventi)");
				params.put("idEventi", idEventi);
			}

		LOGGER.info(Utils.getLogMessage("Cerca Evento: " + query));
		Query q = entityManager.createQuery(query, Evento.class);

		Iterator<Entry<String, Object>> iterator = params.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<String, Object> pairs = iterator.next();
			q.setParameter(pairs.getKey(), pairs.getValue());
			LOGGER.info(Utils.getLogMessage(pairs.getKey() + ": " + pairs.getValue()));
		}

		List<Evento> result = q.getResultList();

		return result;
	}

	/* Funzione che calcola i limiti temporali di editabilità dell'evento */
	/* Editabile solo Docente */
	@Override
	public boolean isEditSemiBloccato(Evento evento) {
		if(evento.getStato() != EventoStatoEnum.BOZZA) {
			//riedizione
			if(evento.isRiedizione()) {
				if(LocalDate.now().isAfter(evento.getDataInizio().minusDays(ecmProperties.getGiorniPrimaBloccoEditRiedizione())))
					return true;
				else
					return false;
			}
			//evento del Provider tipo A
			else if(evento.getProvider().isGruppoA()) {
				if(LocalDate.now().isAfter(evento.getDataInizio().minusDays(ecmProperties.getGiorniPrimaBloccoEditGruppoA())))
					return true;
				else
					return false;
			}
			//evento del Provider tipo B
			else if(evento.getProvider().isGruppoB()) {
				if(LocalDate.now().isAfter(evento.getDataInizio().minusDays(ecmProperties.getGiorniPrimaBloccoEditGruppoB())))
					return true;
				else
					return false;
			}
			return false;
		}
		else return false;
	}

	/* Evento iniziato e completamente bloccato */
	@Override
	public boolean isEventoIniziato(Evento evento) {
		if(evento.getStato() != EventoStatoEnum.BOZZA) {
			if(LocalDate.now().isEqual(evento.getDataInizio()) || LocalDate.now().isAfter(evento.getDataInizio()))
				return true;
			else
				return false;
		}
		else return false;
	}

	/* Ritorna un booleano per il blocco della modifica della data di inizio */
	@Override
	public boolean hasDataInizioRestrictions(Evento evento) {
		if(evento.getStato() != EventoStatoEnum.BOZZA) {
			if(evento.isRiedizione() || evento.getProvider().isGruppoA()) {
				return false;
			}
			//gruppo B
			else {
				if(LocalDate.now().isAfter(evento.getDataInizio().minusDays(ecmProperties.getGiorniMinEventoProviderB())))
					return true;
				else
					return false;
			}
		}
		else
			return false;
	}

	@Override
	public Sponsor getSponsorById(Long sponsorId) {
		LOGGER.debug("Recupero sponsor: " + sponsorId);
		return sponsorRepository.findOne(sponsorId);
	}

	@Override
	public void saveAndCheckContrattoSponsorEvento(File sponsorFile, Sponsor sponsor, Long eventoId, String mode) {
		Evento evento = getEvento(eventoId);
		if(mode.equals("edit")) {
			Long fileId = sponsor.getSponsorFile().getId();

			if(fileId != sponsorFile.getId()){
				sponsor.setSponsorFile(null);
				sponsorRepository.save(sponsor);
				fileService.deleteById(fileId);
			}
		}
		sponsor.setSponsorFile(sponsorFile);
		sponsorRepository.save(sponsor);

		//check se sono stati inseriti tutti i Contratti sponsor
		boolean allSponsorsOk = true;
		for(Sponsor s : evento.getSponsors()) {
			if(s.getSponsorFile() == null || s.getSponsorFile().isNew())
				allSponsorsOk = false;
		}
		if(allSponsorsOk) {
			evento.setSponsorUploaded(true);
			save(evento);
		}
	}

	@Override
	public Set<Evento> getEventiByProviderIdAndStato(Long id, EventoStatoEnum stato) {
		LOGGER.debug("Recupero eventi per il provider: " + id + ", in stato: " + stato);
		return eventoRepository.findAllByProviderIdAndStato(id, stato);
	}

	@Override
	public Integer countAllEventiByProviderIdAndStato(Long id, EventoStatoEnum stato) {
		LOGGER.debug("Conteggio eventi del provider: " + id + ", in stato: " + stato);
		return eventoRepository.countAllByProviderIdAndStato(id, stato);
	}

	@Override
	public Set<Evento> getEventiCreditiNonConfermati() {
		LOGGER.debug("Recupero eventi che non hanno confermato i crediti");
		//prendiamo solo quelli accreditati...quando l'evento viene rendicontato non rientra piu nella vaschetta
		return eventoRepository.findAllByConfermatiCreditiFalseAndStato(EventoStatoEnum.VALIDATO);
	}

	@Override
	public Integer countAllEventiCreditiNonConfermati() {
		LOGGER.debug("Conteggio eventi che non hanno confermato i crediti");
		//prendiamo solo quelli accreditati...quando l'evento viene rendicontato non rientra piu nella vaschetta
		return eventoRepository.countAllByConfermatiCreditiFalseAndStato(EventoStatoEnum.VALIDATO);
	}

	@Override
	public void updateScadenze(Long eventoId, ScadenzeEventoWrapper wrapper) throws Exception {
		LOGGER.info("Update delle scadenze per l'Evento: " + eventoId);

		Evento evento = getEvento(eventoId);
		if(evento == null){
			throw new Exception("Evento non trovato");
		}
		//date scadenza permessi
		evento.setDataScadenzaPagamento(wrapper.getDataScadenzaPagamento());
		evento.setDataScadenzaInvioRendicontazione(wrapper.getDataScadenzaRendicontazione());

		save(evento);
	}

	@Override
	public Evento getEventoByPrefix(String prefix) {
		LOGGER.info("Ricerca dell'Evento con prefisso: " + prefix);
		return eventoRepository.findOneByPrefix(prefix);
	}

	@Override
	public Evento getEventoByPrefixAndEdizione(String prefix, int edizione) {
		LOGGER.info("Ricerca dell'Evento con prefix: " + prefix + " e edizione: " + edizione);
		return eventoRepository.findOneByPrefixAndEdizione(prefix, edizione);
	}

	//funzione che parsa la stringa e divide in prefisso e edizione (se presente) e sulla base di questi
	// cerca l'evento
	@Override
	public Evento getEventoByCodiceIdentificativo(String codiceId) {
		LOGGER.info("Ricerca dell'Evento con codice identificativo: " + codiceId);
		if(codiceId == null || codiceId.isEmpty())
			return null;
		// un solo "-" -> l'evento non è un evento rieditato, si procede con la ricerca by prefix e edizione 1
		else {
			if(StringUtils.countOccurrencesOf(codiceId, "-") < 2)
				return getEventoByPrefixAndEdizione(codiceId, 1);
			// si suppone che l'evento sia una riedizione
			else {
				try {
					int edizione = -1;
					String prefix = "";
					int lastPartIndex = codiceId.lastIndexOf("-");
					if(lastPartIndex != -1) {
						edizione = Integer.parseInt(codiceId.substring(lastPartIndex+1));
						prefix = codiceId.substring(0, lastPartIndex);
					}
					return getEventoByPrefixAndEdizione(prefix, edizione);
				}
				catch (NumberFormatException ex) {
					return null;
				}
			}
		}

	}
}
