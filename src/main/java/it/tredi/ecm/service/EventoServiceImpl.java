package it.tredi.ecm.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
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
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.AnagrafeRegionaleCrediti;
import it.tredi.ecm.dao.entity.AzioneRuoliEventoFSC;
import it.tredi.ecm.dao.entity.BaseEntityDefaultId;
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
import it.tredi.ecm.dao.entity.Obiettivo;
import it.tredi.ecm.dao.entity.Pagamento;
import it.tredi.ecm.dao.entity.Partner;
import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.entity.PianoFormativo;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.entity.ProgrammaGiornalieroRES;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.QuotaAnnuale;
import it.tredi.ecm.dao.entity.RendicontazioneInviata;
import it.tredi.ecm.dao.entity.RiepilogoFAD;
import it.tredi.ecm.dao.entity.RiepilogoRES;
import it.tredi.ecm.dao.entity.RiepilogoRuoliFSC;
import it.tredi.ecm.dao.entity.RuoloOreFSC;
import it.tredi.ecm.dao.entity.Sponsor;
import it.tredi.ecm.dao.entity.VerificaApprendimentoFAD;
import it.tredi.ecm.dao.enumlist.ContenutiEventoEnum;
import it.tredi.ecm.dao.enumlist.DestinatariEventoEnum;
import it.tredi.ecm.dao.enumlist.EventoStatoEnum;
import it.tredi.ecm.dao.enumlist.EventoVersioneEnum;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.MetodoDiLavoroEnum;
import it.tredi.ecm.dao.enumlist.MotivazioneProrogaEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.ProgettiDiMiglioramentoFasiDaInserireFSCEnum;
import it.tredi.ecm.dao.enumlist.RendicontazioneInviataResultEnum;
import it.tredi.ecm.dao.enumlist.RendicontazioneInviataStatoEnum;
import it.tredi.ecm.dao.enumlist.RuoloFSCBaseEnum;
import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import it.tredi.ecm.dao.enumlist.VerificaApprendimentoFSCEnum;
import it.tredi.ecm.dao.enumlist.VerificaApprendimentoRESEnum;
import it.tredi.ecm.dao.enumlist.VerificaPresenzaPartecipantiEnum;
import it.tredi.ecm.dao.repository.EventoPianoFormativoRepository;
import it.tredi.ecm.dao.repository.EventoRepository;
import it.tredi.ecm.dao.repository.PartnerRepository;
import it.tredi.ecm.dao.repository.PersonaEventoRepository;
import it.tredi.ecm.dao.repository.SponsorRepository;
import it.tredi.ecm.exception.AccreditamentoNotFoundException;
import it.tredi.ecm.exception.EcmException;
import it.tredi.ecm.service.bean.EcmProperties;
import it.tredi.ecm.service.component.EventoCrediti;
import it.tredi.ecm.service.controller.EventoServiceController;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EventoRESProgrammaGiornalieroWrapper;
import it.tredi.ecm.web.bean.EventoWrapper;
import it.tredi.ecm.web.bean.ModificaOrarioAttivitaWrapper;
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

	@Autowired private ObiettivoService obiettivoService;

	@Autowired private ReportRitardiService reportRitardiService;

	@Autowired private AccreditamentoService accreditamentoService;

	@Autowired private PagamentoService pagamentoService;
	@Autowired private EngineeringService engineeringService;
	
	@Autowired private EventoCrediti eventoCrediti;

	@Autowired private EventoServiceController eventoServiceController;
	
	@Override
	public Evento getEvento(Long id) {
		LOGGER.debug("Recupero evento: " + id);
		return eventoRepository.findOne(id);
	}

	@Override
	@Transactional
	public void save(Evento evento) throws Exception {
		LOGGER.debug("Salvataggio evento");
		Evento eventoDB = null;
		Map<String, Object> diffMap = new HashMap<String, Object>();
		if(evento.isNew()) {
			LOGGER.info(Utils.getLogMessage("provider/" + evento.getProvider().getId() + "/evento - Creazione"));
			evento.handleDateScadenza();
			eventoRepository.saveAndFlush(evento);
			evento.buildPrefix();
		}else{
			LOGGER.info(Utils.getLogMessage("provider/" + evento.getProvider().getId() + "/evento/" + evento.getId() + " - Salvataggio"));
			eventoDB = eventoRepository.getOne(evento.getId());
			if(!Objects.equals(evento.getDataFine(), eventoDB.getDataFine()))
				evento.handleDateScadenza();
			if(existRiedizioniOfEventoId(evento.getId()))
				diffMap = populateDiffMap(evento, eventoDB);
		}
		evento.setDataUltimaModifica(LocalDateTime.now());
		evento = eventoRepository.saveAndFlush(evento);
		//guai a chi rimuovere questo save, serve per l'audit
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
				//TODO al momento non lo faccio....poi lo chiederenno loro...da tenere presente che....se settiamo a false il flag..l'evento piano formativo sarà eli
				//bisgna gestire tutti i cascade corretti -> eventoPianoFormativo è presente in più piani formativi e nell'evento che lo ha attuato
				//se annullo un evento che è stato attuato da piano formativo...rimuovo il flag in modo tale da poter rieditare l'evento
				//eventoPianoFormativo.setAttuato(false);
			}else if(evento.getStato() == EventoStatoEnum.VALIDATO){
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

		//devo farlo alla fine per contrasti con Hibernate
		//lista degli eventi da sincronizzare
		Set<Evento> eventiDaAggiornare = getRiedizioniOfEventoId(evento.getId());
		//aggiornamento degli eventi a seconda del diff (utilizza la reflection)
		for(Evento ev : eventiDaAggiornare) {
			syncEventoByDiffMap(ev, diffMap);
		}
	}

	//metodo per l'individuazioni dei campi/valori modificati nell'ultimo salvataggio, messi in una mappa
	//e utilizzati poi la reflection per l'aggiornamento delle altre riedizioni.
	//ATTENZIONE: i campi presi in considerazione sono solo un piccolo subset dei campi (solo quelli da tenere sincronizzati)
	private Map<String, Object> populateDiffMap(Evento eventoToSave, Evento eventoDB) {
		LOGGER.info(Utils.getLogMessage("Creazione della mappa delle modifiche dall'ultimo salvataggio"));
		Map<String, Object> diffMap = new HashMap<String, Object>();

		//hibernate unproxy
		if(eventoToSave instanceof	HibernateProxy){
			eventoToSave = (Evento) entityManager.unwrap(SessionImplementor.class).getPersistenceContext().unproxy(eventoToSave);
		}
		if(eventoDB instanceof	HibernateProxy){
			eventoDB = (Evento) entityManager.unwrap(SessionImplementor.class).getPersistenceContext().unproxy(eventoDB);
		}

		//PARTE IN COMUNE A TUTTI GLI EVENTI
		if(!Objects.equals(eventoToSave.getCosto(), eventoDB.getCosto())) {
			diffMap.put("costo", eventoToSave.getCosto());
		}
		if(!Objects.equals(eventoToSave.getTitolo(), eventoDB.getTitolo())) {
			diffMap.put("titolo", eventoToSave.getTitolo());
		}
		if(!Objects.equals(eventoToSave.getObiettivoNazionale(), eventoDB.getObiettivoNazionale())) {
			diffMap.put("obiettivoNazionale", eventoToSave.getObiettivoNazionale());
		}
		if(!Objects.equals(eventoToSave.getObiettivoRegionale(), eventoDB.getObiettivoRegionale())) {
			diffMap.put("obiettivoRegionale", eventoToSave.getObiettivoRegionale());
		}
		if(!Objects.equals(eventoToSave.getDiscipline(), eventoDB.getDiscipline())) {
			diffMap.put("discipline", eventoToSave.getDiscipline());
		}
		//le professioni sono aggiornate automaticamente con la modifica delle discipline
		if(!Objects.equals(eventoToSave.getDestinatariEvento(), eventoDB.getDestinatariEvento())) {
			diffMap.put("destinatariEvento", eventoToSave.getDestinatariEvento());
		}
		if(!Objects.equals(eventoToSave.getContenutiEvento(), eventoDB.getContenutiEvento())) {
			diffMap.put("contenutiEvento", eventoToSave.getContenutiEvento());
		}
		if(!Objects.equals(eventoToSave.getNumeroPartecipanti(), eventoDB.getNumeroPartecipanti())) {
			diffMap.put("numeroPartecipanti", eventoToSave.getNumeroPartecipanti());
		}
		if(!Objects.equals(eventoToSave.getResponsabili(), eventoDB.getResponsabili())) {
			diffMap.put("responsabili", eventoToSave.getResponsabili());
		}
		if(!Objects.equals(eventoToSave.getConfermatiCrediti(), eventoDB.getConfermatiCrediti())) {
			diffMap.put("confermatiCrediti", eventoToSave.getConfermatiCrediti());
		}
		if(!Objects.equals(eventoToSave.getCrediti(), eventoDB.getCrediti())) {
			diffMap.put("crediti", eventoToSave.getCrediti());
		}
		if(!Objects.equals(eventoToSave.getQuotaPartecipazione(), eventoDB.getQuotaPartecipazione())) {
			diffMap.put("quotaPartecipazione", eventoToSave.getQuotaPartecipazione());
		}
		if(!Objects.equals(eventoToSave.getEventoSponsorizzato(), eventoDB.getEventoSponsorizzato())) {
			diffMap.put("eventoSponsorizzato", eventoToSave.getEventoSponsorizzato());
		}
		if(!Objects.equals(eventoToSave.getSponsors(), eventoDB.getSponsors())) {
			diffMap.put("sponsors", eventoToSave.getSponsors());
		}
		if(!Objects.equals(eventoToSave.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia(), eventoDB.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia())) {
			diffMap.put("eventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia", eventoToSave.getEventoSponsorizzatoDaAziendeAlimentiPrimaInfanzia());
		}
		if(!File.equals(eventoToSave.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia(), eventoDB.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia())) {
			diffMap.put("autocertificazioneAssenzaAziendeAlimentiPrimaInfanzia", eventoToSave.getAutocertificazioneAssenzaAziendeAlimentiPrimaInfanzia());
		}
		if(!File.equals(eventoToSave.getAutocertificazioneAutorizzazioneMinisteroSalute(), eventoDB.getAutocertificazioneAutorizzazioneMinisteroSalute())) {
			diffMap.put("autocertificazioneAutorizzazioneMinisteroSalute", eventoToSave.getAutocertificazioneAutorizzazioneMinisteroSalute());
		}
		if(!Objects.equals(eventoToSave.getAltreFormeFinanziamento(), eventoDB.getAltreFormeFinanziamento())) {
			diffMap.put("altreFormeFinanziamento", eventoToSave.getAltreFormeFinanziamento());
		}
		if(!File.equals(eventoToSave.getAutocertificazioneAssenzaFinanziamenti(), eventoDB.getAutocertificazioneAssenzaFinanziamenti())) {
			diffMap.put("autocertificazioneAssenzaFinanziamenti", eventoToSave.getAutocertificazioneAssenzaFinanziamenti());
		}
		if(!File.equals(eventoToSave.getContrattiAccordiConvenzioni(), eventoDB.getContrattiAccordiConvenzioni())) {
			diffMap.put("contrattiAccordiConvenzioni", eventoToSave.getContrattiAccordiConvenzioni());
		}
		if(!Objects.equals(eventoToSave.getEventoAvvalePartner(), eventoDB.getEventoAvvalePartner())) {
			diffMap.put("eventoAvvalePartner", eventoToSave.getEventoAvvalePartner());
		}
		if(!Objects.equals(eventoToSave.getPartners(), eventoDB.getPartners())) {
			diffMap.put("partners", eventoToSave.getPartners());
		}
		if(!File.equals(eventoToSave.getDichiarazioneAssenzaConflittoInteresse(), eventoDB.getDichiarazioneAssenzaConflittoInteresse())) {
			diffMap.put("dichiarazioneAssenzaConflittoInteresse", eventoToSave.getDichiarazioneAssenzaConflittoInteresse());
		}
		if(!Objects.equals(eventoToSave.getProceduraVerificaQualitaPercepita(), eventoDB.getProceduraVerificaQualitaPercepita())) {
			diffMap.put("proceduraVerificaQualitaPercepita", eventoToSave.getProceduraVerificaQualitaPercepita());
		}
		if(!Objects.equals(eventoToSave.getAutorizzazionePrivacy(), eventoDB.getAutorizzazionePrivacy())) {
			diffMap.put("autorizzazionePrivacy", eventoToSave.getAutorizzazionePrivacy());
		}
		//PARTE SPECIFICA RES
		if(eventoToSave instanceof EventoRES) {
			if(!Objects.equals(((EventoRES) eventoToSave).getWorkshopSeminariEcm(), ((EventoRES) eventoDB).getWorkshopSeminariEcm())) {
				diffMap.put("workshopSeminariEcm", ((EventoRES) eventoToSave).getWorkshopSeminariEcm());
			}
			if(!Objects.equals(((EventoRES) eventoToSave).getTitoloConvegno(), ((EventoRES) eventoDB).getTitoloConvegno())) {
				diffMap.put("titoloConvegno", ((EventoRES) eventoToSave).getTitoloConvegno());
			}
			if(!Objects.equals(((EventoRES) eventoToSave).getRazionale(), ((EventoRES) eventoDB).getRazionale())) {
				diffMap.put("razionale", ((EventoRES) eventoToSave).getRazionale());
			}
			if(!Objects.equals(((EventoRES) eventoToSave).getRisultatiAttesi(), ((EventoRES) eventoDB).getRisultatiAttesi())) {
				diffMap.put("risultatiAttesi", ((EventoRES) eventoToSave).getRisultatiAttesi());
			}
			if(!Objects.equals(((EventoRES) eventoToSave).getVerificaApprendimento(), ((EventoRES) eventoDB).getVerificaApprendimento())) {
				diffMap.put("verificaApprendimento", ((EventoRES) eventoToSave).getVerificaApprendimento());
			}
			if(!Objects.equals(((EventoRES) eventoToSave).getVerificaPresenzaPartecipanti(), ((EventoRES) eventoDB).getVerificaPresenzaPartecipanti())) {
				diffMap.put("verificaPresenzaPartecipanti", ((EventoRES) eventoToSave).getVerificaPresenzaPartecipanti());
			}
			if(!Objects.equals(((EventoRES) eventoToSave).getMaterialeDurevoleRilasciatoAiPratecipanti(), ((EventoRES) eventoDB).getMaterialeDurevoleRilasciatoAiPratecipanti())) {
				diffMap.put("materialeDurevoleRilasciatoAiPratecipanti", ((EventoRES) eventoToSave).getMaterialeDurevoleRilasciatoAiPratecipanti());
			}
			if(!Objects.equals(((EventoRES) eventoToSave).getSoloLinguaItaliana(), ((EventoRES) eventoDB).getSoloLinguaItaliana())) {
				diffMap.put("soloLinguaItaliana", ((EventoRES) eventoToSave).getSoloLinguaItaliana());
			}
			if(!Objects.equals(((EventoRES) eventoToSave).getLinguaStranieraUtilizzata(), ((EventoRES) eventoDB).getLinguaStranieraUtilizzata())) {
				diffMap.put("linguaStranieraUtilizzata", ((EventoRES) eventoToSave).getLinguaStranieraUtilizzata());
			}
			if(!Objects.equals(((EventoRES) eventoToSave).getEsisteTraduzioneSimultanea(), ((EventoRES) eventoDB).getEsisteTraduzioneSimultanea())) {
				diffMap.put("esisteTraduzioneSimultanea", ((EventoRES) eventoToSave).getEsisteTraduzioneSimultanea());
			}
			if(!Objects.equals(((EventoRES) eventoToSave).getVerificaRicaduteFormative(), ((EventoRES) eventoDB).getVerificaRicaduteFormative())) {
				diffMap.put("verificaRicaduteFormative", ((EventoRES) eventoToSave).getVerificaRicaduteFormative());
			}
			if(!Objects.equals(((EventoRES) eventoToSave).getDescrizioneVerificaRicaduteFormative(), ((EventoRES) eventoDB).getDescrizioneVerificaRicaduteFormative())) {
				diffMap.put("descrizioneVerificaRicaduteFormative", ((EventoRES) eventoToSave).getDescrizioneVerificaRicaduteFormative());
			}
			if(!File.equals(((EventoRES) eventoToSave).getDocumentoVerificaRicaduteFormative(), ((EventoRES) eventoDB).getDocumentoVerificaRicaduteFormative())) {
				diffMap.put("documentoVerificaRicaduteFormative", ((EventoRES) eventoToSave).getDocumentoVerificaRicaduteFormative());
			}
			//gestione del programma
			diffMap.put("programmaRES", populateDiffMapProgrammaRES(((EventoRES) eventoToSave).getProgramma(), ((EventoRES) eventoDB).getProgramma()));
		}
		//PARTE SPECIFICA FSC
		else if (eventoToSave instanceof EventoFSC) {
			if(!Objects.equals(((EventoFSC) eventoToSave).getTipologiaGruppo(), ((EventoFSC) eventoDB).getTipologiaGruppo())) {
				diffMap.put("tipologiaGruppo", ((EventoFSC) eventoToSave).getTipologiaGruppo());
			}
			if(!Objects.equals(((EventoFSC) eventoToSave).getSperimentazioneClinica(), ((EventoFSC) eventoDB).getSperimentazioneClinica())) {
				diffMap.put("sperimentazioneClinica", ((EventoFSC) eventoToSave).getSperimentazioneClinica());
			}
			if(!Objects.equals(((EventoFSC) eventoToSave).getOttenutoComitatoEtico(), ((EventoFSC) eventoDB).getOttenutoComitatoEtico())) {
				diffMap.put("ottenutoComitatoEtico", ((EventoFSC) eventoToSave).getOttenutoComitatoEtico());
			}
			if(!Objects.equals(((EventoFSC) eventoToSave).getDescrizioneProgetto(), ((EventoFSC) eventoDB).getDescrizioneProgetto())) {
				diffMap.put("descrizioneProgetto", ((EventoFSC) eventoToSave).getDescrizioneProgetto());
			}
			if(!Objects.equals(((EventoFSC) eventoToSave).getVerificaApprendimento(), ((EventoFSC) eventoDB).getVerificaApprendimento())) {
				diffMap.put("verificaApprendimento", ((EventoFSC) eventoToSave).getVerificaApprendimento());
			}
			if(!Objects.equals(((EventoFSC) eventoToSave).getVerificaPresenzaPartecipanti(), ((EventoFSC) eventoDB).getVerificaPresenzaPartecipanti())) {
				diffMap.put("verificaPresenzaPartecipanti", ((EventoFSC) eventoToSave).getVerificaPresenzaPartecipanti());
			}
			if(!Objects.equals(((EventoFSC) eventoToSave).getIndicatoreEfficaciaFormativa(), ((EventoFSC) eventoDB).getIndicatoreEfficaciaFormativa())) {
				diffMap.put("indicatoreEfficaciaFormativa", ((EventoFSC) eventoToSave).getIndicatoreEfficaciaFormativa());
			}
			//gestione del programma
			diffMap.put("fasiAzioniRuoliFSC", populateDiffMapProgrammaFSC(((EventoFSC) eventoToSave).getFasiAzioniRuoli(), ((EventoFSC) eventoDB).getFasiAzioniRuoli()));
		}
		//I FAD NON SONO RIEDITABILI
		return diffMap;
	}

	//metodo che controlla le differenze tra il programma res da salvare e quello già salvato
	//DEVE essere garantito lato inserimento che il numero e l'ordinamento dei programmi e dei dettagli attività non cambi durante le riedizioni
	//new guinness world record di mappe annidate map<string, map<int, map<int, map<string, object>>>> (._.)
	private Map<Integer, Map<Integer, Map<String, Object>>> populateDiffMapProgrammaRES(List<ProgrammaGiornalieroRES> programmaToSaveList, List<ProgrammaGiornalieroRES> programmaDBList) {
		LOGGER.info(Utils.getLogMessage("Gestione ad-hoc delle differenze nel Programma RES"));
		//mappa <index, modifiche programma>
		Map<Integer, Map<Integer, Map<String, Object>>> diffProgMap = new HashMap<Integer, Map<Integer, Map<String, Object>>>();
		//ordino i programmi per giorno
		Collections.sort(programmaToSaveList, (a,b) -> a.getGiorno().compareTo(b.getGiorno()));
		Collections.sort(programmaDBList, (a,b) -> a.getGiorno().compareTo(b.getGiorno()));
		for(int i = 0; i < programmaToSaveList.size(); i++) {
			List<DettaglioAttivitaRES> dettaglioToSaveList = programmaToSaveList.get(i).getProgramma();
			List<DettaglioAttivitaRES> dettaglioDBList = programmaDBList.get(i).getProgramma();
			//ordino i dettagli attività per orario di inizio
			Collections.sort(dettaglioToSaveList, (a,b) -> a.getOrarioInizio().compareTo(b.getOrarioInizio()));
			Collections.sort(dettaglioDBList, (a,b) -> a.getOrarioInizio().compareTo(b.getOrarioInizio()));
			boolean someChangesProgramma = false;
			//mappa <index, modifiche dettaglio>
			Map<Integer, Map<String, Object>> indexDiffDettaglioMap = new HashMap<Integer, Map<String, Object>>();
			for(int k = 0; k < dettaglioToSaveList.size(); k++) {
				DettaglioAttivitaRES dettaglioToSave = dettaglioToSaveList.get(k);
				DettaglioAttivitaRES dettaglioDB = dettaglioDBList.get(k);
				boolean someChangesDettaglio = false;
				//mappa <setter, value>
				Map<String, Object> diffDettaglioMap = new HashMap<String, Object>();
				if(!Objects.equals(dettaglioToSave.getArgomento(), dettaglioDB.getArgomento())) {
					diffDettaglioMap.put("argomento", dettaglioToSave.getArgomento());
					someChangesDettaglio = true;
				}
				if(!Objects.equals(dettaglioToSave.getRisultatoAtteso(), dettaglioDB.getRisultatoAtteso())) {
					diffDettaglioMap.put("risultatoAtteso", dettaglioToSave.getRisultatoAtteso());
					someChangesDettaglio = true;
				}
				if(!Objects.equals(dettaglioToSave.getObiettivoFormativo(), dettaglioDB.getObiettivoFormativo())) {
					diffDettaglioMap.put("obiettivoFormativo", dettaglioToSave.getObiettivoFormativo());
					someChangesDettaglio = true;
				}
				if(!Objects.equals(dettaglioToSave.getMetodologiaDidattica(), dettaglioDB.getMetodologiaDidattica())) {
					diffDettaglioMap.put("metodologiaDidattica", dettaglioToSave.getMetodologiaDidattica());
					someChangesDettaglio = true;
				}
				//se ci sono stati cambiamenti aggiungo alla mappa con l'indice del dettaglio
				if(someChangesDettaglio) {
					someChangesProgramma = true;
					indexDiffDettaglioMap.put(k, diffDettaglioMap);
				}
			}
			//se ci sono stati cambiamenti aggiungo alla mappa con l'indice del programma
			if(someChangesProgramma) {
				diffProgMap.put(i, indexDiffDettaglioMap);
			}
		}
		return diffProgMap;
	}

	//metodo che controlla le differenze tra il programma fsc da salvare e quello già salvato
	//DEVE essere garantito lato inserimento che il numero e l'ordinamento dei programmi e dei dettagli attività non cambi durante le riedizioni
	//new guinness world record di mappe annidate map<string, map<int, map<int, map<string, object>>>> (._.)
	private Map<Integer, Map<Integer, Map<String, Object>>> populateDiffMapProgrammaFSC(List<FaseAzioniRuoliEventoFSCTypeA> fasiAzioniRuoliToSave, List<FaseAzioniRuoliEventoFSCTypeA> fasiAzioniRuoliDB) {
		LOGGER.info(Utils.getLogMessage("Gestione ad-hoc delle differenze nel Programma FSC"));
		//mappa <index, modifiche fasi>
		Map<Integer, Map<Integer, Map<String, Object>>> diffProgMap = new HashMap<Integer, Map<Integer, Map<String, Object>>>();
		//N.B. CRITICO SE NON DOVESSE FUNZIONARE trovare un modo univoco per ordinare sempre nello stesso modo le list nella entity
		for(int i = 0; i < fasiAzioniRuoliToSave.size(); i++) {
			List<AzioneRuoliEventoFSC> azioneToSaveList = fasiAzioniRuoliToSave.get(i).getAzioniRuoli();
			List<AzioneRuoliEventoFSC> azioneDBList = fasiAzioniRuoliDB.get(i).getAzioniRuoli();
			//N.B. stesso problema ordinamento!!!
			boolean someChangesFase = false;
			//mappa <index, azioni>
			Map<Integer, Map<String, Object>> indexDiffAzioneMap = new HashMap<Integer, Map<String, Object>>();
			for(int k = 0; k < azioneToSaveList.size(); k++) {
				AzioneRuoliEventoFSC azioneToSave = azioneToSaveList.get(k);
				AzioneRuoliEventoFSC azioneDB = azioneDBList.get(k);
				boolean someChangesAzione = false;
				//mappa <setter, value>
				Map<String, Object> diffAzioneMap = new HashMap<String, Object>();
				if(!Objects.equals(azioneToSave.getAzione(), azioneDB.getAzione())) {
					diffAzioneMap.put("azione", azioneToSave.getAzione());
					someChangesAzione = true;
				}
				if(!Objects.equals(azioneToSave.getObiettivoFormativo(), azioneDB.getObiettivoFormativo())) {
					diffAzioneMap.put("obiettivoFormativo", azioneToSave.getObiettivoFormativo());
					someChangesAzione = true;
				}
				if(!Objects.equals(azioneToSave.getRisultatiAttesi(), azioneDB.getRisultatiAttesi())) {
					diffAzioneMap.put("risultatiAttesi", azioneToSave.getRisultatiAttesi());
					someChangesAzione = true;
				}
				if(!Objects.equals(azioneToSave.getMetodiDiLavoro(), azioneDB.getMetodiDiLavoro())) {
					diffAzioneMap.put("metodiDiLavoro", azioneToSave.getMetodiDiLavoro());
					someChangesAzione = true;
				}
				//se ci sono stati cambiamenti aggiungo alla mappa con l'indice dell'azione
				if(someChangesAzione) {
					someChangesFase = true;
					indexDiffAzioneMap.put(k, diffAzioneMap);
				}
			}
			//se ci sono stati cambiamenti aggiungo al programma con l'indice della fase
			if(someChangesFase) {
				diffProgMap.put(i, indexDiffAzioneMap);
			}
		}
		return diffProgMap;
	}

	private void syncEventoByDiffMap(Evento evento, Map<String, Object> diffMap) throws Exception {
		LOGGER.info(Utils.getLogMessage("Sincronizzazione dell'evento " + evento.getCodiceIdentificativo()));
		for(String key : diffMap.keySet()) {
			//gestione ad-hoc per i programmi (non vado a sostituire tutto il programma, ma entro dentro)
			if(key == "programmaRES" || key == "fasiAzioniRuoliFSC") {
				handleProgrammaDiffMap(evento, diffMap);
			}
			//gestione di tutti gli altri campi
			else {
				Object value = diffMap.get(key);
				handleCollectionsThenSetValue(evento, key, value);
			}
		}
		//ricontrollo se i file degli sponsor sono stati caricati!
		boolean allSponsorsOk = true;
		for(Sponsor s : evento.getSponsors()) {
			if(s.getSponsorFile() == null || s.getSponsorFile().isNew())
				allSponsorsOk = false;
		}
		evento.setSponsorUploaded(allSponsorsOk);

		evento = eventoRepository.saveAndFlush(evento);
		//non togliere il doppio salvataggio, serve per l'audit
		eventoRepository.save(evento);
	}

	//salvataggio delle modifiche ai programmi segnate nella diffMap nell'evento passato come argomento
	private void handleProgrammaDiffMap(Evento evento, Map<String, Object> diffMap) throws Exception {
		if(evento instanceof EventoRES) {
			@SuppressWarnings("unchecked")
			Map<Integer, Map<Integer, Map<String, Object>>> mappaProgramma = (Map<Integer, Map<Integer, Map<String, Object>>>) diffMap.get("programmaRES");
			for(int i : mappaProgramma.keySet()) {
				Map<Integer, Map<String, Object>> mappaDettaglio = mappaProgramma.get(i);
				for(int k : mappaDettaglio.keySet()) {
					Map<String, Object> mappaModifiche = mappaDettaglio.get(k);
					DettaglioAttivitaRES dettaglioRES = ((EventoRES) evento).getProgramma().get(i).getProgramma().get(k);
					for(String key : mappaModifiche.keySet()) {
						Object value = mappaModifiche.get(key);
						handleCollectionsThenSetValue(dettaglioRES, key, value);
					}
				}
			}
		}
		else if(evento instanceof EventoFSC) {
			@SuppressWarnings("unchecked")
			Map<Integer, Map<Integer, Map<String, Object>>> mappaFasi = (Map<Integer, Map<Integer, Map<String, Object>>>) diffMap.get("fasiAzioniRuoliFSC");
			for(int i : mappaFasi.keySet()) {
				Map<Integer, Map<String, Object>> mappaAzioni = mappaFasi.get(i);
				for(int k : mappaAzioni.keySet()) {
					Map<String, Object> mappaModifiche = mappaAzioni.get(k);
					AzioneRuoliEventoFSC azioniRuoliFSC = ((EventoFSC) evento).getFasiAzioniRuoli().get(i).getAzioniRuoli().get(k);
					for(String key : mappaModifiche.keySet()) {
						Object value = mappaModifiche.get(key);
						handleCollectionsThenSetValue(azioniRuoliFSC, key, value);
					}
				}
			}
		}
	}

	//gestisce le collection dai campi che si possono settare direttamente
	private void handleCollectionsThenSetValue(Object object, String key, Object value) throws Exception {
		//se è una collection devo ciclare
		if(value instanceof Collection) {
			Collection<Object> collection = null;
			if(value instanceof HashSet || value instanceof PersistentSet) {
				collection = new HashSet<Object>();
			}
			else if (value instanceof ArrayList) {
				collection = new ArrayList<Object>();
			}
			for(Object iter : (Collection<?>) value) {
				collection.add(handleIdOggetto(iter));
			}
			invokeSetterForCollections(object, key, collection);
		}
		else {
			invokeSetter(object, key, handleIdOggetto(value));
		}
	}

	//gestisce la clonazione se necessario
	private Object handleIdOggetto(Object value) throws Exception {
		if(value != null) {
			//gestione adhoc per i file (che contengono la parte Data)
			if(value instanceof File) {
				return fileService.copyFile((File) value);
			}
			//BaseEntityDefaultId da settare direttamente
			else if(value instanceof Disciplina || value instanceof Obiettivo) {
				return value;
			}
			//se l'oggetto in questione estende la classe delle entity devo clonarlo
			else if(value instanceof BaseEntityDefaultId) {
				Utils.touchFirstLevelOfEverything(value);
				LOGGER.debug(Utils.getLogMessage("DETACH dell'oggetto id: " + ((BaseEntityDefaultId)value).getId() + " di classe: " + value.getClass()));
				entityManager.detach(value);
				((BaseEntityDefaultId)value).setId(null);
				//FIXME probabilmente chiamato solo per i casi sponsor e partner
				if(value instanceof Sponsor) {
					((Sponsor) value).setSponsorFile(fileService.copyFile(((Sponsor) value).getSponsorFile()));
				}
				else if(value instanceof Partner) {
					((Partner) value).setPartnerFile(fileService.copyFile(((Partner) value).getPartnerFile()));
				}
				else if(value instanceof PersonaEvento) {
					((PersonaEvento)value).getAnagrafica().setCv(fileService.copyFile(((PersonaEvento)value).getAnagrafica().getCv()));
				}
				//N.B. si suppone che non ci siano altre BaseEntityDefaultId dentro l'oggetto prima di detacharlo / clonarlo
				//o si crerebbero dei reference non voluti
				entityManager.persist(value);
			}
		}
		//altrimenti posso settarlo direttamente
		return value;
	}

	//invoka il setter dell'object per settare il value usando reflection
	private void invokeSetter(Object object, String property, Object value) throws Exception {
		//reflection by spring framework
		PropertyAccessor myAccessor = PropertyAccessorFactory.forDirectFieldAccess(object);
		myAccessor.setPropertyValue(property, value);
	}

	//invoka il get della collection, fa un clear e riaggiunge gli elementi (per evitare l'ex all-deleted-orphans di hibernate)
	private void invokeSetterForCollections(Object object, String property, Collection<Object> collectionToSave) {
		PropertyAccessor myAccessor = PropertyAccessorFactory.forDirectFieldAccess(object);
		((Collection<Object>)myAccessor.getPropertyValue(property)).clear();
		((Collection<Object>)myAccessor.getPropertyValue(property)).addAll(collectionToSave);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		LOGGER.debug("Eliminazione evento: " + id);

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
				LOGGER.error("Errore processando il file csv: " + fileName, e);
				throw new EcmException("error.csv_to_xml_report_error", e.getMessage(), e);
			}

			//xsd validation
			try {
				XmlReportValidator.validateXmlWithXsd(rendiconto.getNomeFile(), xml_b, Helper.getSchemaEvento_1_16_XSD(), evento);
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
				XmlReportValidator.validateXmlWithXsd(rendiconto.getNomeFile(), rendiconto.getData(), Helper.getSchemaEvento_1_16_XSD(), evento);
			}
			catch (Exception e) {
				throw new EcmException("error.xml_validation", e.getMessage(), e);
			}
		}
		save(evento);
	}

	//Get all events of events for segretaria.  ordering is based on column number
	@Override
	public Page<Evento> getAllEventi(Integer pageNumber, Integer columnNumber, String order, Integer numOfPages) {
		LOGGER.debug("Recupero tutti gli eventi");
		PageRequest request = null;
		switch (columnNumber) {
		case 0:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "prefix", "edizione"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "prefix", "edizione"));
			break;
		
		case 1:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "provider.denominazioneLegale"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "provider.denominazioneLegale"));
			break;
			
		case 2:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "edizione"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "edizione"));
			break;
			
		case 3:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "proceduraFormativa"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "proceduraFormativa"));
			break;
			
		case 4:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "titolo"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "titolo"));
			break;
			
		case 6:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "dataInizio"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "dataInizio"));
			break;
			
		case 7:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "dataFine"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "dataFine"));
			break;
			
		case 8:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "stato"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "stato"));
			break;
			
		case 9:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "numeroPartecipanti"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "numeroPartecipanti"));
			break;
			
		case 10:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "durata"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "durata"));
			break;
			
		case 11:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "dataScadenzaInvioRendicontazione"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "dataScadenzaInvioRendicontazione"));
			break;
			
		case 12:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "confermatiCrediti"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "confermatiCrediti"));
			break;
			
		default:
			request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "dataUltimaModifica"));
			break;
		}
		return eventoRepository.findAll(request);
	}
	
	
	//Get all events of events per provider id.  ordering is based on column number
	@Override
	public Page<Evento> getAllEventiForProviderId(Long providerId, Integer pageNumber, Integer columnNumber, String order, Integer numOfPages) {
		LOGGER.debug("Recupero tutti gli eventi");
		PageRequest request = null;
		switch (columnNumber) {
		case 0:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "prefix", "edizione"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "prefix", "edizione"));
			break;
			
		case 1:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "edizione"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "edizione"));
			break;
			
		case 2:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "proceduraFormativa"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "proceduraFormativa"));
			break;
			
		case 3:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "titolo"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "titolo"));
			break;
			
		case 5:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "dataInizio"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "dataInizio"));
			break;
			
		case 6:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "dataFine"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "dataFine"));
			break;
			
		case 7:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "stato"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "stato"));
			break;
			
		case 8:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "numeroPartecipanti"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "numeroPartecipanti"));
			break;
			
		case 9:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "durata"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "durata"));
			break;
			
		case 10:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "dataScadenzaInvioRendicontazione"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "dataScadenzaInvioRendicontazione"));
			break;
			
		case 11:
			if(order.equals("asc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "confermatiCrediti"));
			else if(order.equals("desc"))
				request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.DESC, "confermatiCrediti"));
			break;
			
		default:
			request = new PageRequest(pageNumber, numOfPages, new Sort(Direction.ASC, "dataUltimaModifica"));
			break;
		}
		return eventoRepository.findAllByProviderId(providerId, request);
	}
	
	@Override
	public List<Evento> getAllEventi() {
		LOGGER.debug("Recupero tutti gli eventi");
		return eventoRepository.findAll();
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
		if(ecmProperties.isDebugTestMode()) {
			return canCreateEvento(account);
		}
		
		return canCreateEvento(account)
			&& (LocalDate.now().isAfter(LocalDate.of(LocalDate.now().getYear(), 1, 1))
			&& LocalDate.now().isBefore(LocalDate.of(LocalDate.now().getYear(), 12, 20)));
	}

	/*	SALVATAGGIO	*/
	@Override
	public Evento handleRipetibiliAndAllegati(EventoWrapper eventoWrapper) throws Exception{
		Evento evento = eventoWrapper.getEvento();

		if(evento instanceof EventoFSC) {
			if(versioneEvento(evento) == EventoVersioneEnum.UNO_PRIMA_2018) {
				//cancello eventuali esperti coordinatori e investigatori inseriti
				if(eventoWrapper.getEsperti() != null)
					eventoWrapper.getEsperti().clear();
				if(eventoWrapper.getCoordinatori() != null)
					eventoWrapper.getCoordinatori().clear();
				if(eventoWrapper.getInvestigatori() != null)
					eventoWrapper.getInvestigatori().clear();
			} else {
				//cancello investigatori inseriti se la TipologiaEventoFSC non è ATTIVITA_DI_RICERCA
				if(((EventoFSC) evento).getTipologiaEventoFSC() != TipologiaEventoFSCEnum.ATTIVITA_DI_RICERCA) {
					if(eventoWrapper.getInvestigatori() != null)
						eventoWrapper.getInvestigatori().clear();
				}
			}
		}
		
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
//			int pos = 0;
//			for(PersonaEvento pers : eventoRES.getDocenti()) {
//				//Se inserita durante la modifica dell'evento ricarico l'entity per evitare il detached object di hibernate e la sostituisco nella lista
//				if(eventoWrapper.getPersoneEventoInserite().contains(pers)) {
//					PersonaEvento p = personaEventoRepository.findOne(pers.getId());
//					eventoRES.getDocenti().set(pos, p);
//				}
//				pos++;
//			}
			Iterator<PersonaEvento> it = eventoRES.getDocenti().iterator();
			List<PersonaEvento> attachedList = new ArrayList<PersonaEvento>();
			while(it.hasNext()){
				PersonaEvento p = it.next();
				if(eventoWrapper.getPersoneEventoInserite().contains(p))
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
			EventoFSC eventoFSC = (EventoFSC) evento;
			retrieveProgrammaAndAddJoin(eventoWrapper);

			if(eventoWrapper.getRiepilogoRuoliFSC() != null) {
				eventoFSC.getRiepilogoRuoli().clear();
				eventoFSC.getRiepilogoRuoli().addAll(eventoWrapper.getRiepilogoRuoliFSC().values());
			}
			
			//Esperti
//			int pos = 0;
//			for(PersonaEvento pers : eventoFSC.getEsperti()) {
//				//Se inserita durante la modifica dell'evento ricarico l'entity per evitare il detached object di hibernate e la sostituisco nella lista
//				if(eventoWrapper.getPersoneEventoInserite().contains(pers)) {
//					PersonaEvento p = personaEventoRepository.findOne(pers.getId());
//					eventoFSC.getEsperti().set(pos, p);
//				}
//				pos++;
//			}
			Iterator<PersonaEvento> itPersona = eventoFSC.getEsperti().iterator();
			List<PersonaEvento> attachedListPersona = new ArrayList<PersonaEvento>();
			while(itPersona.hasNext()){
				PersonaEvento p = itPersona.next();
				if(eventoWrapper.getPersoneEventoInserite().contains(p))
					p = personaEventoRepository.findOne(p.getId());
				attachedListPersona.add(p);
			}
			eventoFSC.setEsperti(attachedListPersona);
			

			//Coordinatori
//			pos = 0;
//			for(PersonaEvento pers : eventoFSC.getCoordinatori()) {
//				//Se inserita durante la modifica dell'evento ricarico l'entity per evitare il detached object di hibernate e la sostituisco nella lista
//				if(eventoWrapper.getPersoneEventoInserite().contains(pers)) {
//					PersonaEvento p = personaEventoRepository.findOne(pers.getId());
//					eventoFSC.getCoordinatori().set(pos, p);
//				}
//				pos++;
//			}
			itPersona = eventoWrapper.getCoordinatori().iterator();
			attachedListPersona = new ArrayList<PersonaEvento>();
			while(itPersona.hasNext()){
				PersonaEvento p = itPersona.next();
				if(eventoWrapper.getPersoneEventoInserite().contains(p))
					p = personaEventoRepository.findOne(p.getId());
				attachedListPersona.add(p);
			}
			eventoFSC.setCoordinatori(attachedListPersona);

			//Investigatori
//			pos = 0;
//			for(PersonaEvento pers : eventoFSC.getInvestigatori()) {
//				//Se inserita durante la modifica dell'evento ricarico l'entity per evitare il detached object di hibernate e la sostituisco nella lista
//				if(eventoWrapper.getPersoneEventoInserite().contains(pers)) {
//					PersonaEvento p = personaEventoRepository.findOne(pers.getId());
//					eventoFSC.getInvestigatori().set(pos, p);
//				}
//				pos++;
//			}
			itPersona = eventoWrapper.getInvestigatori().iterator();
			attachedListPersona = new ArrayList<PersonaEvento>();
			while(itPersona.hasNext()){
				PersonaEvento p = itPersona.next();
				if(eventoWrapper.getPersoneEventoInserite().contains(p))
					p = personaEventoRepository.findOne(p.getId());
				attachedListPersona.add(p);
			}
			eventoFSC.setInvestigatori(attachedListPersona);
		}else if(evento instanceof EventoFAD){
			EventoFAD eventoFAD = (EventoFAD)evento;
			//Docenti
//			int pos = 0;
//			for(PersonaEvento pers : eventoFAD.getDocenti()) {
//				if(eventoWrapper.getPersoneEventoInserite().contains(pers)) {
//					PersonaEvento p = personaEventoRepository.findOne(pers.getId());
//					eventoFAD.getDocenti().set(pos, p);
//				}
//				pos++;
//			}
			Iterator<PersonaEvento> it = eventoFAD.getDocenti().iterator();
			List<PersonaEvento> attachedList = new ArrayList<PersonaEvento>();
			while(it.hasNext()){
				PersonaEvento p = it.next();
				if(eventoWrapper.getPersoneEventoInserite().contains(p))
				p = personaEventoRepository.findOne(p.getId());
				attachedList.add(p);
			}
			eventoFAD.setDocenti(attachedList);

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
//		int pos = 0;
//		for(PersonaEvento pers : evento.getResponsabili()) {
//			//Se inserita durante la modifica dell'evento ricarico l'entity per evitare il detached object di hibernate e la sostituisco nella lista
//			if(eventoWrapper.getPersoneEventoInserite().contains(pers)) {
//				PersonaEvento p = personaEventoRepository.findOne(pers.getId());
//				evento.getResponsabili().set(pos, p);
//			}
//			pos++;
//		}
		Iterator<PersonaEvento> itPersona = eventoWrapper.getResponsabiliScientifici().iterator();
		List<PersonaEvento> attachedListPersona = new ArrayList<PersonaEvento>();
		while(itPersona.hasNext()){
			PersonaEvento p = itPersona.next();
			if(eventoWrapper.getPersoneEventoInserite().contains(p))
			p = personaEventoRepository.findOne(p.getId());
			attachedListPersona.add(p);
		}
		evento.setResponsabili(attachedListPersona);

		//Sponsor
		List<Sponsor> attachedSetSponsor = eventoWrapper.getSponsors();
		evento.setSponsors(attachedSetSponsor);

		//Partner
		List<Partner> attachedSetPartner = eventoWrapper.getPartners();
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

//		//non  deve essere possibile caricare gli allegati degli sponsor
//		if(evento.getEventoSponsorizzato() != null && !evento.getEventoSponsorizzato().booleanValue())
//			evento.setSponsorUploaded(true);

		//check se sono stati inseriti tutti i Contratti sponsor
		//tiommi 28/06/2017
		//FIXME spostare nel salvataggio??
		boolean allSponsorsOk = true;
		for(Sponsor s : evento.getSponsors()) {
			if(s.getSponsorFile() == null || s.getSponsorFile().isNew())
				allSponsorsOk = false;
		}
		evento.setSponsorUploaded(allSponsorsOk);

		return evento;
	}

	@Override
	public void inviaRendicontoACogeaps(Long id) throws Exception {
		Evento evento = getEvento(id);
		try {
			RendicontazioneInviata ultimaRendicontazioneInviata = evento.getUltimaRendicontazioneInviata();
			if (ultimaRendicontazioneInviata != null && ultimaRendicontazioneInviata.getStato().equals(RendicontazioneInviataStatoEnum.PENDING)) //se ultima elaborazione pendente -> invio non concesso
				throw new Exception("error.elaborazione_pendente");

			String reportFileName = evento.getReportPartecipantiXML().getNomeFile().trim(); //il cogeaps non accetta spazi
			if (!reportFileName.toUpperCase().endsWith(".P7M")) { //file non firmato -> invio non concesso
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
					evento.setAnagrafeRegionaleCrediti(anagrafeRegionaleCreditiService.extractAnagrafeRegionaleCreditiPartecipantiFromXml(ultimaRendicontazioneInviata.getFileName(), ultimaRendicontazioneInviata.getFileRendicontazione().getData()));//extract info AnagrafeRegionaleCrediti
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
//			//date intermedie
//			Long key = 1L;
//			Map<Long, String> dateIntermedieTemp = new LinkedHashMap<Long, String>();
//			for (LocalDate d : ((EventoRES) evento).getDateIntermedie()) {
//				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//				String dataToString = d.format(dtf);
//				dateIntermedieTemp.put(key++, dataToString);
//			}
//			if(dateIntermedieTemp.size() == 0)
//				dateIntermedieTemp.put(key, null);

			//risultati attesi
			Long key = 1L;
			Map<Long, String> risultatiAttesiTemp = new LinkedHashMap<Long, String>();
			for (String s : ((EventoRES) evento).getRisultatiAttesi()) {
				risultatiAttesiTemp.put(key++, s);
			}
			if(risultatiAttesiTemp.size() == 0)
				risultatiAttesiTemp.put(key, null);

			eventoWrapper.setRisultatiAttesiMapTemp(risultatiAttesiTemp);

			//Docenti
			//eventoWrapper.setDocenti(((EventoRES) evento).getDocenti());

			//Documento Verifica Ricadute Formative
			if (((EventoRES) evento).getDocumentoVerificaRicaduteFormative() != null) {
				eventoWrapper.setDocumentoVerificaRicaduteFormative(((EventoRES) evento).getDocumentoVerificaRicaduteFormative());
			}
		}else if(evento instanceof EventoFSC){
			if(evento.getResponsabili() != null) {
				//setto l'IdentificativoPersonaRuoloEvento su tutti i primi 3 responsabili scientifici
				//faccio questo per i dati salvati prima dell'aggiunta del campo
				personaEventoService.setIdentificativoPersonaRuoloEvento(evento.getResponsabili());
			}

			if(((EventoFSC) evento).getEsperti() != null) {
				//setto l'IdentificativoPersonaRuoloEvento su tutti i primi 3 responsabili scientifici
				//faccio questo per i dati salvati prima dell'aggiunta del campo
				personaEventoService.setIdentificativoPersonaRuoloEvento(((EventoFSC) evento).getEsperti());
			}
			if(((EventoFSC) evento).getCoordinatori() != null) {
				//setto l'IdentificativoPersonaRuoloEvento su tutti i primi 3 responsabili scientifici
				//faccio questo per i dati salvati prima dell'aggiunta del campo
				personaEventoService.setIdentificativoPersonaRuoloEvento(((EventoFSC) evento).getCoordinatori());
			}

			
			//Programma
			eventoWrapper.setProgrammaEventoFSC(((EventoFSC) evento).getFasiAzioniRuoli());

			//mappa ruoli ore
			eventoWrapper.initMappaRuoloOreFSC();

			//Riepilogo RuoloOreFSC
			eventoWrapper.initRiepilogoRuoliFSC();
			for(RiepilogoRuoliFSC r : ((EventoFSC) evento).getRiepilogoRuoli())
				eventoWrapper.getRiepilogoRuoliFSC().put(r.getRuolo(), r);

//			//esperti
//			eventoWrapper.setEsperti(((EventoFSC) evento).getEsperti());
//			//coordinatori
//			eventoWrapper.setCoordinatori(((EventoFSC) evento).getCoordinatori());
//			//investigatori
//			eventoWrapper.setInvestigatori(((EventoFSC) evento).getInvestigatori());
		}else if(evento instanceof EventoFAD){
			//Docenti
			//eventoWrapper.setDocenti(((EventoFAD) evento).getDocenti());

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

//		//responsabili scientifici
//		eventoWrapper.setResponsabiliScientifici(evento.getResponsabili());

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
		eventoCrediti.calcoloCreditiEvento(eventoWrapper);
		eventoWrapper.getEvento().calcolaCosto();
	}

	private float calcoloDurataEvento(EventoWrapper eventoWrapper) {
		float durata = 0;

		if(eventoWrapper.getEvento() instanceof EventoRES){
			//durata = calcoloDurataEventoRES(eventoWrapper.getProgrammaEventoRES());
			durata = calcoloDurataEventoRES(eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().values());
			((EventoRES)eventoWrapper.getEvento()).setDurata(durata);
		}else if(eventoWrapper.getEvento() instanceof EventoFSC){
			durata = calcoloDurataEventoFSC(eventoWrapper);
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

	private float calcoloDurataEventoFSC(EventoWrapper eventoWrapper){
		float durata = 0;

		prepareRiepilogoRuoli(eventoWrapper);
		durata = getMaxDurataPatecipanti(eventoWrapper.getRiepilogoRuoliFSC());

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
				if(rrf.getRuolo() != null && rrf.getRuolo().getRuoloBase() == ruolo) {
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


	/*
	 * Ragruppo i Ruoli coinvolti in una mappa <Ruolo,RiepilogoRuoloOreFSC>
	 * dove il RiepilogoRuoloOreFSC avra la somma delle ore dei ruoli
	 * */
	private void prepareRiepilogoRuoli(EventoWrapper eventoWrapper){
		if(eventoWrapper.getRiepilogoRuoliFSC() != null)
		{
			Set<RuoloFSCEnum> ruoliUsati = new HashSet<RuoloFSCEnum>();

			Iterator<Entry<RuoloFSCEnum, RiepilogoRuoliFSC>> iterator = eventoWrapper.getRiepilogoRuoliFSC().entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry<RuoloFSCEnum,RiepilogoRuoliFSC> pairs = iterator.next();
				pairs.getValue().setTempoDedicato(0f);
				pairs.getValue().setCrediti(0f);
				if(pairs.getValue().getRuolo() == null)
					iterator.remove();
			}

			//dpranteda - 17/01/2018: bugfix risolto, nel caso di modifica alle fasi attive non si aggiornavano tutti i calcoli
			EventoFSC eventoFSC = ((EventoFSC)eventoWrapper.getEvento());
			TipologiaEventoFSCEnum tipologiaEventoFSC = eventoFSC.getTipologiaEventoFSC();
			ProgettiDiMiglioramentoFasiDaInserireFSCEnum fasiDaInserire = eventoFSC.getFasiDaInserire();
			
			for(FaseAzioniRuoliEventoFSCTypeA fase : eventoWrapper.getProgrammaEventoFSC()){
				if(tipologiaEventoFSC != null && (tipologiaEventoFSC != TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO || ProgettiDiMiglioramentoFasiDaInserireFSCEnum.faseAbilitata(fasiDaInserire, fase.getFaseDiLavoro()))) {
					for(AzioneRuoliEventoFSC azione : fase.getAzioniRuoli()){
						for(RuoloOreFSC ruolo : azione.getRuoli())
						{
							ruoliUsati.add(ruolo.getRuolo());
	
							if(eventoWrapper.getRiepilogoRuoliFSC().containsKey(ruolo.getRuolo())){
								RiepilogoRuoliFSC r = eventoWrapper.getRiepilogoRuoliFSC().get(ruolo.getRuolo());
								float tempoDedicato = ruolo.getTempoDedicato() != null ? ruolo.getTempoDedicato() : 0.0f;
								r.addTempo(tempoDedicato);
							}else{
								float tempoDedicato = ruolo.getTempoDedicato() != null ? ruolo.getTempoDedicato() : 0.0f;
								RiepilogoRuoliFSC r = new RiepilogoRuoliFSC(ruolo.getRuolo(), tempoDedicato, 0.0f);
								eventoWrapper.getRiepilogoRuoliFSC().put(ruolo.getRuolo(), r);
							}
						}
					}
				}
			}

			iterator = eventoWrapper.getRiepilogoRuoliFSC().entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry<RuoloFSCEnum,RiepilogoRuoliFSC> pairs = iterator.next();
				if(!ruoliUsati.contains(pairs.getValue().getRuolo()))
					iterator.remove();
			}
		}
	}

	public EventoVersioneEnum versioneEvento(Evento evento) {
		return eventoServiceController.versioneEvento(evento);
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
	public Set<Evento> getAllEventiRieditabiliForProviderId(Long providerId) throws AccreditamentoNotFoundException {
		LOGGER.debug(Utils.getLogMessage("Recupero tutti gli eventi del piano formativo rieditabili per il provider: " + providerId));
		//mostra tutti gli eventi del provider non in bozza, non cancellati,
		//non FAD, selezionabili secondo le procedureFormative del provider,
		//che finiscono dopo l'inizio dell'anno corrente
		Accreditamento accreditamento = accreditamentoService.getAccreditamentoAttivoForProvider(providerId);
		Set<ProceduraFormativa> procedureFormative = new HashSet<ProceduraFormativa>();
		procedureFormative.addAll(accreditamento.getDatiAccreditamento().getProcedureFormative());
		procedureFormative.remove(ProceduraFormativa.FAD);

		if(ecmProperties.getEventoVersioniRieditabili() != null && !ecmProperties.getEventoVersioniRieditabili().isEmpty()) {
			return eventoRepository.findAllByProviderIdAndStatoNotAndStatoNotAndProceduraFormativaInAndDataFineAfterAndVersioneIn(providerId, EventoStatoEnum.BOZZA, EventoStatoEnum.CANCELLATO, procedureFormative, LocalDate.of(LocalDate.now().getYear(), 1, 1).minusDays(1), ecmProperties.getEventoVersioniRieditabili());			
		} else {
		return eventoRepository.findAllByProviderIdAndStatoNotAndStatoNotAndProceduraFormativaInAndDataFineAfter(providerId, EventoStatoEnum.BOZZA, EventoStatoEnum.CANCELLATO, procedureFormative, LocalDate.of(LocalDate.now().getYear(), 1, 1).minusDays(1));
	}
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

	//sigh devo proprio andare in vacanza...
	/* funzione di detach ad hoc (detachare veramente tutto ricorsivamente non conviene proprio a
	 * causa di Entity come Provider e Accreditamento presenti in Evento.che innesterebbero un loop di detach)
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

			for(PersonaEvento r : ((EventoFSC) eventoPadre).getEsperti()) {
				LOGGER.debug(Utils.getLogMessage("Detach Esperti: " + r.getId()));
				entityManager.detach(r);
			}
			for(PersonaEvento r : ((EventoFSC) eventoPadre).getCoordinatori()) {
				LOGGER.debug(Utils.getLogMessage("Detach Coordinatori: " + r.getId()));
				entityManager.detach(r);
		}
			for(PersonaEvento r : ((EventoFSC) eventoPadre).getInvestigatori()) {
				LOGGER.debug(Utils.getLogMessage("Detach Investigatori: " + r.getId()));
				entityManager.detach(r);
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
				Set<PersonaEvento> docentiSet = new HashSet<PersonaEvento>();
				for(PersonaEvento pe : daf.getDocenti()) {
					Long newId = mapIdDocenti.get(pe.getId());
					PersonaEvento nuovaPersona = personaEventoRepository.findOne(newId);
					docentiSet.add(nuovaPersona);
				}
//				Set<PersonaEvento> docentiSet = new HashSet<PersonaEvento>();
//				docentiSet.addAll(Arrays.asList(dar.getDocenti().toArray(new PersonaEvento[dar.getDocenti().size()])));
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
					Set<PersonaEvento> docentiSet = new HashSet<PersonaEvento>();
					for(PersonaEvento pe : dar.getDocenti()) {
						Long newId = mapIdDocenti.get(pe.getId());
						PersonaEvento nuovaPersona = personaEventoRepository.findOne(newId);
						docentiSet.add(nuovaPersona);
					}
//					Set<PersonaEvento> docentiSet = new HashSet<PersonaEvento>();
//					docentiSet.addAll(Arrays.asList(dar.getDocenti().toArray(new PersonaEvento[dar.getDocenti().size()])));
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

			LOGGER.debug(Utils.getLogMessage("Clonazione riepilogo ruoli (in realtà solo dei partecipanti)"));
			List<RiepilogoRuoliFSC> riepilogoRuoli = new ArrayList<RiepilogoRuoliFSC>();
			riepilogoRuoli.addAll(Arrays.asList(((EventoFSC) riedizione).getRiepilogoRuoli().toArray(new RiepilogoRuoliFSC[((EventoFSC) riedizione).getRiepilogoRuoli().size()])));
			((EventoFSC) riedizione).setRiepilogoRuoli(riepilogoRuoli);
			
			LOGGER.debug(Utils.getLogMessage("Clonazione e salvataggio esperti"));
			for(PersonaEvento r : ((EventoFSC) riedizione).getEsperti()) {
				LOGGER.debug(Utils.getLogMessage("Clonazione Esperto: " + r.getId()));
				r.setId(null);
				r.getAnagrafica().setCv(fileService.copyFile(r.getAnagrafica().getCv()));
				personaEventoRepository.save(r);
				LOGGER.debug(Utils.getLogMessage("Esperto clonato salvato: " + r.getId()));
			}

			LOGGER.debug(Utils.getLogMessage("Clonazione e salvataggio coordinatori"));
			for(PersonaEvento r : ((EventoFSC) riedizione).getCoordinatori()) {
				LOGGER.debug(Utils.getLogMessage("Clonazione Coordinatore: " + r.getId()));
				r.setId(null);
				r.getAnagrafica().setCv(fileService.copyFile(r.getAnagrafica().getCv()));
				personaEventoRepository.save(r);
				LOGGER.debug(Utils.getLogMessage("Coordinatore clonato salvato: " + r.getId()));
			}
			
			LOGGER.debug(Utils.getLogMessage("Clonazione e salvataggio investigatori"));
			for(PersonaEvento r : ((EventoFSC) riedizione).getInvestigatori()) {
				LOGGER.debug(Utils.getLogMessage("Clonazione Investigatore: " + r.getId()));
				r.setId(null);
				r.getAnagrafica().setCv(fileService.copyFile(r.getAnagrafica().getCv()));
				personaEventoRepository.save(r);
				LOGGER.debug(Utils.getLogMessage("Investigatore clonato salvato: " + r.getId()));
		}
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
		riedizione.setPagato(false);
		riedizione.setPagInCorso(null);
		riedizione.setPagatoQuietanza(false);
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
		return eventoRepository.findAllByProviderIdAndDataScadenzaPagamentoBetweenAndPagatoFalseAndStatoNot(providerId, LocalDate.now(), LocalDate.now().plusDays(30), EventoStatoEnum.CANCELLATO);
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
	public Set<Evento> getEventiForProviderIdInScadenzaDiRendicontazione(Long providerId) {
		return eventoRepository.findAllByProviderIdAndDataScadenzaInvioRendicontazioneBetweenAndStato(providerId, LocalDate.now(), LocalDate.now().plusDays(30), EventoStatoEnum.VALIDATO);
	}

	@Override
	public int countEventiForProviderIdInScadenzaDiRendicontazione(Long providerId) {
		Set<Evento> listaEventi = getEventiForProviderIdInScadenzaDiRendicontazione(providerId);
		if(listaEventi != null)
			return listaEventi.size();
		return 0;
	}

	/* Vaschetta provider */
	@Override
	public Set<Evento> getEventiForProviderIdScadutiENonPagati(Long providerId) {
		return eventoRepository.findAllByProviderIdAndPagatoFalseAndDataScadenzaPagamentoBeforeAndStatoNot(providerId, LocalDate.now(), EventoStatoEnum.CANCELLATO);
	}

	@Override
	public int countEventiForProviderIdScadutiENonPagati(Long providerId) {
		Set<Evento> listaEventi = getEventiForProviderIdScadutiENonPagati(providerId);
		if(listaEventi != null)
			return listaEventi.size();
		return 0;
	}
	
	@Override
	public Set<Evento> getEventiForProviderIdScadutiENonRendicontati(Long providerId) {
		return eventoRepository.findAllByProviderIdAndDataScadenzaInvioRendicontazioneBeforeAndStato(providerId, LocalDate.now(), EventoStatoEnum.VALIDATO);
	}

	@Override
	public int countEventiForProviderIdScadutiENonRendicontati(Long providerId) {
		Set<Evento> listaEventi = getEventiForProviderIdScadutiENonRendicontati(providerId);
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

			//SPONSOR
			if(wrapper.getSponsorizzato() != null){
				query = Utils.QUERY_AND(query, "e.eventoSponsorizzato = :sponsorizzato");
				params.put("sponsorizzato", wrapper.getSponsorizzato().booleanValue());
			}
			
			//ALTRE FORME DI FINANZIAMENTO
			if(wrapper.getAltreFormeFinanziamento() != null) {
				query = Utils.QUERY_AND(query, "e.altreFormeFinanziamento = :altreFormeFinanziamento");
				params.put("altreFormeFinanziamento", wrapper.getAltreFormeFinanziamento().booleanValue());
			}
			
			//VERSIONE
			if(wrapper.getVersione() != null) {
				query = Utils.QUERY_AND(query, "e.versione = :versione");
				params.put("versione", EventoVersioneEnum.getByNumeroVersione(wrapper.getVersione()));
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
				// se il set è vuoto si inserisce un valore dummy -1 per far fallire la ricerca
				if(idEventi == null || idEventi.isEmpty()) {
					idEventi.add(-1L);
				}
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

	/* Evento iniziato e completamente bloccato N.B. prima aveva anche la condizione che now() == dataInizio, ma è stata fatta togliere */
	@Override
	public boolean isEventoIniziato(Evento evento) {
		if(evento.getStato() != EventoStatoEnum.BOZZA) {
			if(LocalDate.now().isAfter(evento.getDataInizio()))
				return true;
			else
				return false;
		}
		else return false;
	}

//	/* Ritorna un booleano per il blocco della modifica della data di inizio */
//	@Override
//	public boolean hasDataInizioRestrictions(Evento evento) {
//		if(evento.getStato() != EventoStatoEnum.BOZZA) {
//			if(evento.isRiedizione() || evento.getProvider().isGruppoA()) {
//				return false;
//			}
//			//gruppo B
//			else {
//				if(LocalDate.now().isAfter(evento.getDataInizio().minusDays(ecmProperties.getGiorniMinEventoProviderB())))
//					return true;
//				else
//					return false;
//			}
//		}
//		else
//			return false;
//	}

	@Override
	public Sponsor getSponsorById(Long sponsorId) {
		LOGGER.debug("Recupero sponsor: " + sponsorId);
		return sponsorRepository.findOne(sponsorId);
	}

	@Override
	public void saveAndCheckContrattoSponsorEvento(File sponsorFile, Sponsor sponsor, Long eventoId, String mode) throws Exception {
		Evento evento = getEvento(eventoId);
		if(mode.equals("edit")) {
			Long fileId = sponsor.getSponsorFile().getId();

			if(fileId != sponsorFile.getId()){
				sponsor.setSponsorFile(null);
				sponsorRepository.save(sponsor);
				//fileService.deleteById(fileId);
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
		if(evento.getDataScadenzaPagamento() != null &&
				!evento.getDataScadenzaPagamento().isEqual(wrapper.getDataScadenzaPagamento())) {
			reportRitardiService.createReport(
					MotivazioneProrogaEnum.PAGAMENTO_EVENTO,
					evento.getId(),
					evento.getDataScadenzaPagamento(),
					wrapper.getDataScadenzaPagamento(),
					LocalDate.now(),
					(evento.getPagato() == null || !evento.getPagato().booleanValue()),
					evento.getProvider().getId());
		}
		evento.setDataScadenzaPagamento(wrapper.getDataScadenzaPagamento());
		if(evento.getDataScadenzaInvioRendicontazione() != null &&
				!evento.getDataScadenzaInvioRendicontazione().isEqual(wrapper.getDataScadenzaRendicontazione())) {
			reportRitardiService.createReport(
					MotivazioneProrogaEnum.RENDICONTAZIONE_EVENTO,
					evento.getId(),
					evento.getDataScadenzaInvioRendicontazione(),
					wrapper.getDataScadenzaRendicontazione(),
					LocalDate.now(),
					(evento.getUltimaRendicontazioneInviata() == null || evento.getUltimaRendicontazioneInviata().getResult() == RendicontazioneInviataResultEnum.ERROR),
					evento.getProvider().getId());
		}
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

	@Override
	public Set<Evento> getEventiAlimentazionePrimaInfanzia(){
		LOGGER.debug("Recupero eventi con alimenti prima infanzia");
		return eventoRepository.findAllByContenutiEventoAndArchivatoPrimaInfanziaFalse(ContenutiEventoEnum.ALIMENTAZIONE_PRIMA_INFANZIA);
	}

	@Override
	public Integer countAllEventiAlimentazionePrimaInfanzia(){
		LOGGER.debug("Conteggio eventi con alimenti prima infanzia");
		return eventoRepository.countAllByContenutiEventoAndArchivatoPrimaInfanziaFalse(ContenutiEventoEnum.ALIMENTAZIONE_PRIMA_INFANZIA);
	}

	@Override
	public Set<Evento> getEventiMedicineNonConvenzionali(){
		LOGGER.debug("Recupero eventi con medicine non convenzionali");
		//l'obiettivo 1042 è un obiettivo nazionale (medicine non convenzionali)
		Obiettivo nonConvenzionale = obiettivoService.getObiettivo(1042L);
		return eventoRepository.findAllByArchiviatoMedicinaliFalseAndContenutiEventoOrObiettivoNazionale(ContenutiEventoEnum.MEDICINE_NON_CONVENZIONALE, nonConvenzionale);
	}

	@Override
	public Integer countAllEventiMedicineNonConvenzionali(){
		LOGGER.debug("Conteggio eventi con medicine non convenzionali");
		//l'obiettivo 1042 è un obiettivo nazionale (medicine non convenzionali)
		Obiettivo nonConvenzionale = obiettivoService.getObiettivo(1042L);
		return eventoRepository.countAllByArchiviatoMedicinaliFalseAndContenutiEventoOrObiettivoNazionale(ContenutiEventoEnum.MEDICINE_NON_CONVENZIONALE, nonConvenzionale);
	}

//	@Override
//	public boolean checkIfRESAndWorkshopOrCorsoAggiornamentoAndInterettivoSelected(Evento evento) {
//		if(evento instanceof EventoRES) {
//			EventoRES eventoRes = (EventoRES) evento;
//			if(eventoRes.getTipologiaEventoRES() == TipologiaEventoRESEnum.WORKSHOP_SEMINARIO
//					|| eventoRes.getTipologiaEventoRES() == TipologiaEventoRESEnum.CORSO_AGGIORNAMENTO) {
//				for(ProgrammaGiornalieroRES prog : eventoRes.getProgramma()) {
//					for(DettaglioAttivitaRES dettaglio : prog.getProgramma()) {
//						if(!dettaglio.isExtraType() && dettaglio.getMetodologiaDidattica().getMetodologia() == TipoMetodologiaEnum.INTERATTIVA)
//							return true;
//					}
//				}
//			}
//		}
//		return false;
//	}

	@Override
	public boolean checkIfFSCAndTrainingAndTutorPartecipanteRatioAlert(Evento evento) {
		if(evento instanceof EventoFSC) {
			EventoFSC eventoFsc = (EventoFSC) evento;
			if(eventoFsc.getTipologiaEventoFSC() == TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO
					&& eventoFsc.getNumeroPartecipanti() > (eventoFsc.getNumeroTutor() * 5))
				return true;
		}
		return false;
	}

	//metodo che aggiorna gli orari delle attività nel wrapper (non nell'evento poichè questi orari devono ancora passare per il validatore)
	@Override
	public boolean updateOrariAttivita(ModificaOrarioAttivitaWrapper orariDescriptor, EventoWrapper eventoWrapper) {
		//formatto la data
		LocalTime ora = LocalTime.parse(orariDescriptor.getOra());
		List<DettaglioAttivitaRES> listaAttivita = eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().get(orariDescriptor.getProgrammaId()).getProgramma().getProgramma();
		LocalTime primaAttivitaStart = LocalTime.parse("23:59");
		//trovo la prima (in distanza temporale) attività
		for(Long pos : orariDescriptor.getListaRowId()) {
			DettaglioAttivitaRES da = listaAttivita.get((int) (long) pos);
			if(primaAttivitaStart.isAfter(da.getOrarioInizio()))
				primaAttivitaStart = da.getOrarioInizio();
		}
		//calcolo i minuti da aggiungere/sottrarre
		long minutes = Duration.between(primaAttivitaStart, ora).toMinutes();
		//controllo se posso spostare tutte le date o se fanno il giro (scavalcano al giorno dopo)
		for(Long pos : orariDescriptor.getListaRowId()) {
			DettaglioAttivitaRES da = listaAttivita.get((int) (long) pos);
			if(da.getOrarioFine().plusMinutes(minutes).isBefore(da.getOrarioFine()) && minutes > 0)
				return false;
		}
		//setto le date
		for(Long pos : orariDescriptor.getListaRowId()) {
			DettaglioAttivitaRES da = listaAttivita.get((int) (long) pos);
			da.setOrarioInizio(da.getOrarioInizio().plusMinutes(minutes));
			da.setOrarioFine(da.getOrarioFine().plusMinutes(minutes));
		}
		return true;
	}

	@Override
	public boolean existRiedizioniOfEventoId(Long eventoId) {
		int riedizioniCounter = eventoRepository.countRiedizioniOfEventoId(eventoId);
		return riedizioniCounter > 0;
	}

	@Override
	//metodo ricorsivo sui figli per prendere anche i nipoti
	public Set<Evento> getRiedizioniOfEventoId(Long eventoId) {
		Set<Evento> riedizioniToAdd = new HashSet<Evento>();

		Set<Evento> riedizioni = eventoRepository.getRiedizioniOfEventoId(eventoId);
		for(Evento riedizione : riedizioni) {
			riedizioniToAdd.addAll(getRiedizioniOfEventoId(riedizione.getId()));
		}

		riedizioni.addAll(riedizioniToAdd);
		return riedizioni;
	}

	@Override
	public void salvaQuietanzaPagamento(File quietanzaPagamento, Long eventoId) throws Exception {
		LOGGER.info("Salvataggio della Quietanza di Pagamento per evento: " + eventoId);

		engineeringService.createPagamentoForEvento(eventoId);
		Evento evento = eventoRepository.findOne(eventoId);

		Pagamento pagamento = pagamentoService.getPagamentoByEvento(evento);
		if (pagamento == null) {
			throw new Exception("Pagamento non presente");
		}

		pagamento.setQuietanza(quietanzaPagamento);
		pagamento.setDataPagamento(LocalDate.now());
		pagamentoService.save(pagamento);

		evento.setPagato(true);
		evento.setPagInCorso(false);
		evento.setPagatoQuietanza(true);
		save(evento);
	}

	@Override
	public Pagamento getPagamentoForQuietanza(Evento evento) throws Exception{
		LOGGER.info("Creazione Pagamento per evento: " + evento.getId());
		Pagamento pagamento = pagamentoService.getPagamentoByEvento(evento);

		if (pagamento == null) {
			pagamento = engineeringService.createPagamentoForEvento(evento.getId());
			pagamento.setDataScadenzaPagamento(evento.getDataScadenzaPagamento());
			pagamento.setImporto(evento.getCosto());
			pagamentoService.save(pagamento);
		}

		return pagamento;
	}

	@Override
	public Long getFileQuietanzaId(Long eventoId) {
		LOGGER.info("Recupero ID file di Quietanza di Pagamento per evento: " + eventoId);
		Evento e = eventoRepository.findOne(eventoId);
		Pagamento p = pagamentoService.getPagamentoByEvento(e);
		if(p != null && p.getQuietanza() != null){
			LOGGER.info("Trovato pagamento con ID file di Quietanza: " + p.getQuietanza().getId() + " per evento: " + eventoId);
			return p.getQuietanza().getId();
		}

		LOGGER.info("Pagamento o file di Quietanza NON TROVATO per evento: " + eventoId);
		return null;
	}

	private List<RuoloFSCEnum> getListRuoloFSCEnumPerResponsabiliScientifici(List<PersonaEvento> personeEvento) {
		List<RuoloFSCEnum> toRet = new ArrayList<RuoloFSCEnum>();
		if(personeEvento != null) {
			for(PersonaEvento pEv : personeEvento) {
				if(pEv.isSvolgeAttivitaDiDocenza() && pEv.getIdentificativoPersonaRuoloEvento() != null)
					toRet.add(pEv.getIdentificativoPersonaRuoloEvento().getRuoloFSCResponsabileSCientifico());
			}
		}
		return toRet;
	}
	
	@Override
	public List<RuoloFSCEnum> getListRuoloFSCEnumPerResponsabiliScientifici(EventoFSC evento) {
		return getListRuoloFSCEnumPerResponsabiliScientifici(evento.getResponsabili());
	}

	private List<RuoloFSCEnum> getListRuoloFSCEnumPerEsperti(List<PersonaEvento> personeEvento) {
		List<RuoloFSCEnum> toRet = new ArrayList<RuoloFSCEnum>();
		if(personeEvento != null) {
			for(PersonaEvento pEv : personeEvento) {
				if(pEv.isSvolgeAttivitaDiDocenza() && pEv.getIdentificativoPersonaRuoloEvento() != null)
					toRet.add(pEv.getIdentificativoPersonaRuoloEvento().getRuoloFSCEsperto());
			}
		}
		return toRet;
	}

	@Override
	public List<RuoloFSCEnum> getListRuoloFSCEnumPerEsperti(EventoFSC evento) {
		return getListRuoloFSCEnumPerEsperti(evento.getEsperti());
	}

	private List<RuoloFSCEnum> getListRuoloFSCEnumPerCoordinatori(List<PersonaEvento> personeEvento) {
		List<RuoloFSCEnum> toRet = new ArrayList<RuoloFSCEnum>();
		if(personeEvento != null) {
			for(PersonaEvento pEv : personeEvento) {
				if(pEv.isSvolgeAttivitaDiDocenza() && pEv.getIdentificativoPersonaRuoloEvento() != null)
					toRet.add(pEv.getIdentificativoPersonaRuoloEvento().getRuoloFSCCoordinatore());
			}
		}
		return toRet;
	}

	@Override
	public List<RuoloFSCEnum> getListRuoloFSCEnumPerCoordinatori(EventoFSC evento) {
		return getListRuoloFSCEnumPerCoordinatori(evento.getCoordinatori());
	}
	
		@Override
	public void archiveEventoInPrimaInfanziaOrMedNonConv(List<Long> ids) {
			List<Evento> events = eventoRepository.findAll(ids);
			
			for(Evento event : events) {
				if(event.getContenutiEvento().equals(ContenutiEventoEnum.ALIMENTAZIONE_PRIMA_INFANZIA)) {
					event.setArchivatoPrimaInfanzia(true);
					eventoRepository.save(event);
				} else {
					event.setArchiviatoMedicinali(true);
					eventoRepository.save(event);
				}
			}
		
	}
}
