package it.tredi.ecm.web;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.AnagraficaEvento;
import it.tredi.ecm.dao.entity.AnagraficaEventoBase;
import it.tredi.ecm.dao.entity.AnagraficaFullEvento;
import it.tredi.ecm.dao.entity.AnagraficaFullEventoBase;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.DettaglioAttivitaRES;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoFAD;
import it.tredi.ecm.dao.entity.EventoFSC;
import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.entity.PersonaFullEvento;
import it.tredi.ecm.dao.entity.ProgrammaGiornalieroRES;
import it.tredi.ecm.dao.entity.Provider;

import it.tredi.ecm.dao.enumlist.EventoWrapperModeEnum;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.MetodologiaDidatticaRESEnum;
import it.tredi.ecm.dao.enumlist.ObiettiviFormativiRESEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.repository.PersonaEventoRepository;
import it.tredi.ecm.exception.AccreditamentoNotFoundException;
import it.tredi.ecm.exception.EcmException;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.AnagraficaEventoService;
import it.tredi.ecm.service.AnagraficaFullEventoService;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.ObiettivoService;

import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EventoWrapper;
import it.tredi.ecm.web.bean.Message;

@Controller
@SessionAttributes("eventoWrapper")
public class EventoController {
	public static final Logger LOGGER = LoggerFactory.getLogger(EventoController.class);

	@Autowired private EventoService eventoService;
	@Autowired private ProviderService providerService;
	@Autowired private ObiettivoService obiettivoService;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private FileService fileService;
	
	@Autowired private AnagraficaEventoService anagraficaEventoService;
	@Autowired private AnagraficaFullEventoService anagraficaFullEventoService;
	@Autowired private PersonaEventoRepository personaEventoRepo;
	
	private final String LIST = "evento/eventoList";
	private final String EDIT = "evento/eventoEdit";
	private final String RENDICONTO = "evento/eventoRendiconto";
	private final String EDITRES = "evento/eventoRESEdit";
	private final String EDITFSC = "evento/eventoFSCEdit";
	private final String EDITFAD = "evento/eventoFADEdit";
	
	
	@ModelAttribute("elencoProvince")
	public List<String> getElencoProvince(){
		List<String> elencoProvince = new ArrayList<String>();

		elencoProvince.add("Venezia");
		elencoProvince.add("Padova");
		elencoProvince.add("Verona");

		return elencoProvince;
	}

	@InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }
	
	//@ModelAttribute("eventoWrapper")
	public EventoWrapper getEvento(@RequestParam(name = "editId", required = false) Long id,
			@RequestParam(value="providerId",required = false) Long providerId,
			@RequestParam(value="proceduraFormativa",required = false) ProceduraFormativa proceduraFormativa,
			@RequestParam(value="wrapperMode",required = false) EventoWrapperModeEnum wrapperMode) throws Exception{
		if(id != null){
			if (wrapperMode == EventoWrapperModeEnum.RENDICONTO)
				return prepareEventoWrapperRendiconto(eventoService.getEvento(id), providerId);
			else
				return prepareEventoWrapperEdit(eventoService.getEvento(id), false);
		}
		if(providerId != null && proceduraFormativa != null)
			return prepareEventoWrapperNew(proceduraFormativa, providerId);
		return new EventoWrapper();
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowAllEventi(principal)")
	@RequestMapping("/evento/list")
	public String getListEventi(Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /evento/list"));
		try {
			model.addAttribute("eventoList", eventoService.getAllEventi());
			LOGGER.info(Utils.getLogMessage("VIEW: evento/eventoList"));
			return LIST;
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /evento/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /redirect:/home"));
			return "redirect:/home";
		}
	}

	@RequestMapping("/provider/evento/list")
	public String getListEventiCurrentUserProvider(Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/evento/list"));
		try {
			Provider currentProvider = providerService.getProvider();
			if(currentProvider.isNew()){
				throw new Exception("Provider non registrato");
			}else{
				Long providerId = currentProvider.getId();
				LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/evento/list"));
				return "redirect:/provider/"+providerId+"/evento/list";
			}
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/evento/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /redirect:/home"));
			return "redirect:/home";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowAllEventiProvider(principal, #providerId)")
	@RequestMapping("/provider/{providerId}/evento/list")
	public String getListEventiProvider(@PathVariable Long providerId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/evento/list"));
		try {
			String denominazioneProvider = providerService.getProvider(providerId).getDenominazioneLegale();
			model.addAttribute("eventoList", eventoService.getAllEventiForProviderId(providerId));
			model.addAttribute("denominazioneProvider", denominazioneProvider);
			model.addAttribute("providerId", providerId);
			model.addAttribute("canCreateEvento", eventoService.canCreateEvento(Utils.getAuthenticatedUser().getAccount()));
			LOGGER.info(Utils.getLogMessage("VIEW: evento/eventoList"));
			return LIST;
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /redirect:/home"));
			return "redirect:/home";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canCreateEvento(principal, #providerId)")
	@RequestMapping(value= "/provider/{providerId}/evento/new", method = RequestMethod.POST)
	public String createNewEvento(@RequestParam(name = "proceduraFormativa", required = false) ProceduraFormativa proceduraFormativa,
			@PathVariable Long providerId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("POST /provider/" + providerId + "/evento/new, proceduraFormativa = " + proceduraFormativa));
		try {
			if(proceduraFormativa == null) {
				redirectAttrs.addFlashAttribute("error", true);
				redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.inserire_campi_required", "error"));
				return "redirect:/provider/{providerId}/evento/list";
			}
			else {
				EventoWrapper wrapper = prepareEventoWrapperNew(proceduraFormativa, providerId);
				return goToNew(model, wrapper);
			}
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /provider/" + providerId + "/evento/new"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/"+providerId+"/evento/list"));
			return "redirect:/provider/{providerId}/evento/list";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canCreateEvento(principal, #providerId)")
	@RequestMapping(value= "/provider/{providerId}/evento/save", method = RequestMethod.POST)
	public String saveEvento(@ModelAttribute EventoWrapper eventoWrapper, @PathVariable Long providerId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("POST /provider/" + providerId + "/evento/save"));
		try {
			//salvataggio temporaneo senza validatore (in stato di bozza)
			//gestione dei campi ripetibili
			Evento evento = eventoService.handleRipetibiliAndAllegati(eventoWrapper);
			eventoService.save(evento);
			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.evento_salvato_in_bozza_success", "success"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/{providerId}/evento/list"));
			return "redirect:/provider/{providerId}/evento/list";
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /provider/" + providerId + "/evento/save"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/{providerId}/evento/list"));
			return "redirect:/provider/{providerId}/evento/list";
		}
	}

//TODO	@PreAuthorize("@securityAccessServiceImpl.canEditEvento(principal, #providerId")
	@RequestMapping("/provider/{providerId}/evento/{eventoId}/edit")
	public String editEvento(@PathVariable Long providerId, @PathVariable Long eventoId,
			Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/evento/"+ eventoId + "/edit"));
		try {
			//edit dell'evento
			EventoWrapper wrapper = prepareEventoWrapperEdit(eventoService.getEvento(eventoId), true);
			return goToEdit(model, wrapper);
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /provider/" + providerId + "/evento/"+ eventoId + "/edit"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/"+providerId+"/evento/list"));
			return "redirect:/provider/{providerId}/evento/list";
		}
	}

//TODO	@PreAuthorize("@securityAccessServiceImpl.canSendRendiconto(principal)")
	@RequestMapping("/provider/{providerId}/evento/{eventoId}/rendiconto")
	public String rendicontoEvento(@PathVariable Long providerId,
			@PathVariable Long eventoId, Model model, RedirectAttributes redirectAttrs) {
		try{
			LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId + "/rendiconto"));
			model.addAttribute("returnLink", "/provider/" + providerId + "/evento/list");
			return goToRendiconto(model, prepareEventoWrapperRendiconto(eventoService.getEvento(eventoId), providerId));
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId + "/rendiconto"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/evento/list"));
			return "redirect:/provider/" + providerId + "/evento/list";
		}
	}

//TODO	@PreAuthorize("@securityAccessServiceImpl.canSendRendiconto(principal)")
		@RequestMapping(value = "/provider/{providerId}/evento/{eventoId}/rendiconto/validate", method = RequestMethod.POST)
		public String rendicontoEventoValidate(@PathVariable Long providerId,
				@PathVariable Long eventoId, @ModelAttribute("eventoWrapper") EventoWrapper wrapper, BindingResult result,
				Model model, RedirectAttributes redirectAttrs) {
			try{
				LOGGER.info(Utils.getLogMessage("POST /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/validate"));
				model.addAttribute("returnLink", "/provider/" + providerId + "/evento/list");
				if(wrapper.getReportPartecipanti().getId() == null)
					model.addAttribute("message", new Message("message.errore", "message.inserire_il_rendiconto", "error"));
				else {
					LOGGER.info(Utils.getLogMessage("Ricevuto File id: " + wrapper.getReportPartecipanti().getId() + " da validare"));
					File file = wrapper.getReportPartecipanti();
					if(file != null && !file.isNew()){
						if(file.isREPORTPARTECIPANTI()) {
							String fileName = wrapper.getReportPartecipanti().getNomeFile().trim().toUpperCase();
							if (fileName.endsWith(".XML") || fileName.endsWith(".XML.P7M") || fileName.endsWith(".XML.ZIP.P7M") || fileName.endsWith(".CSV")) {
								wrapper.setReportPartecipanti(fileService.getFile(file.getId()));
								eventoService.validaRendiconto(eventoId, wrapper.getReportPartecipanti());
							}
							else {
								model.addAttribute("message", new Message("message.errore", "error.formatNonAcceptedXML", "error"));
							}
						}
					}
			}
			return goToRendiconto(model, prepareEventoWrapperRendiconto(eventoService.getEvento(eventoId), providerId));
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/validate"),ex);
				if (ex instanceof EcmException) //errore gestito
//TODO - l'idea era quella di utilizzare error._free_msg={0} ma non funziona!!!!
					redirectAttrs.addFlashAttribute("message", new Message(((EcmException) ex).getMessageTitle(), ((EcmException) ex).getMessageDetail(), "error"));
				else
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/validate"));
				return "redirect:/provider/{providerId}/evento/{eventoId}/rendiconto";
		}
	}

//	//metodo per chiamate AJAX sulle date ripetibili
//	@RequestMapping("/add/dataIntermedia")
//	public String addDataIntermedia(@RequestParam (name="dataIntermedia", required = false) LocalDate dataIntermedia, Model model) {
//		try{
//			LOGGER.info(Utils.getLogMessage("AJAX /add/dataIntermedia"));
//			EventoWrapper wrapper = (EventoWrapper) model.asMap().get("eventoWrapper");
//			EventoRES evento = (EventoRES) wrapper.getEvento();
//			Set<LocalDate> dateIntermedie = evento.getDateIntermedie();
//			if(dataIntermedia != null) {
//				dateIntermedie.add(dataIntermedia);
//				wrapper.setEvento(evento);
//				model.addAttribute("eventoWrapper", wrapper);
//			}
//			else model.addAttribute("message", new Message("message.errore", "message.non_possibile_salvare_data", "error"));
//			return EDIT;
//		}
//		catch (Exception ex) {
//			LOGGER.error(Utils.getLogMessage("POST /add/dataIntermedia"),ex);
//			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
//			return EDIT;
//		}
//	}

	//metodi privati di supporto

	private EventoWrapper prepareEventoWrapperNew(ProceduraFormativa proceduraFormativa, Long providerId) throws AccreditamentoNotFoundException, Exception {
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperNew(" + proceduraFormativa + ") - entering"));
		EventoWrapper eventoWrapper = prepareCommonEditWrapper(proceduraFormativa, providerId);
		Evento evento;
		switch(proceduraFormativa){
			case FAD: evento = new EventoFAD(); break;
			case RES: evento = new EventoRES(); break;
			case FSC: evento = new EventoFSC(); break;
			default: evento = new Evento(); break;
		}
		evento.setProvider(providerService.getProvider(providerId));
		evento.setProceduraFormativa(proceduraFormativa);
		eventoWrapper.setEvento(evento);
		
		if(evento instanceof EventoRES){
			//Lista attività singolo programma giornaliero
			List<DettaglioAttivitaRES> programmaGiorno1 = new ArrayList<DettaglioAttivitaRES>();
			programmaGiorno1.add(new DettaglioAttivitaRES());
			
			ProgrammaGiornalieroRES p = new ProgrammaGiornalieroRES();
			p.setProgramma(programmaGiorno1);
			//p.setEventoRES((EventoRES) evento);
			
			//Lista programmi giornalieri dell'evento
			List<ProgrammaGiornalieroRES> programmaEvento = new ArrayList<ProgrammaGiornalieroRES>();
			programmaEvento.add(p);
			
			eventoWrapper.setProgrammaEventoRES(programmaEvento);
		}
		
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperNew(" + proceduraFormativa + ") - exiting"));
		return eventoWrapper;
	}

	private EventoWrapper prepareEventoWrapperEdit(Evento evento, boolean reloadWrapperFromDB) throws Exception {
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperEdit(" + evento.getId() + ") - entering"));
		EventoWrapper eventoWrapper = prepareCommonEditWrapper(evento.getProceduraFormativa(), evento.getProvider().getId());
		eventoWrapper.setEvento(evento);
		if(reloadWrapperFromDB)
			eventoWrapper = eventoService.prepareRipetibiliAndAllegati(eventoWrapper);
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperEdit(" + evento.getId() + ") - exiting"));
		return eventoWrapper;
	}

	private EventoWrapper prepareCommonEditWrapper(ProceduraFormativa proceduraFormativa, Long providerId) throws AccreditamentoNotFoundException, Exception {
		EventoWrapper eventoWrapper = new EventoWrapper();
		eventoWrapper.setProceduraFormativa(proceduraFormativa);
		eventoWrapper.setProviderId(providerId);
		eventoWrapper.setObiettiviNazionali(obiettivoService.getObiettiviNazionali());
		eventoWrapper.setObiettiviRegionali(obiettivoService.getObiettiviNazionali());
		DatiAccreditamento datiAccreditamento = accreditamentoService.getDatiAccreditamentoForAccreditamento(accreditamentoService.getAccreditamentoAttivoForProvider(providerId).getId());
		eventoWrapper.setProfessioneList(datiAccreditamento.getProfessioniSelezionate());
		eventoWrapper.setDisciplinaList(datiAccreditamento.getDiscipline());
		eventoWrapper.setWrapperMode(EventoWrapperModeEnum.EDIT);
		return eventoWrapper;
	}

	private EventoWrapper prepareEventoWrapperRendiconto(Evento evento, long providerId) {
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperRendiconto(" + evento.getId() + "," + providerId + ") - entering"));
		EventoWrapper eventoWrapper = new EventoWrapper();
		eventoWrapper.setEvento(evento);
		eventoWrapper.setProviderId(providerId);
		eventoWrapper.setReportPartecipanti(new File(FileEnum.FILE_REPORT_PARTECIPANTI));
		eventoWrapper.setWrapperMode(EventoWrapperModeEnum.RENDICONTO);
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperRendiconto(" + evento.getId() + "," + providerId + ") - exiting"));
		return eventoWrapper;
	}

	private String goToNew(Model model, EventoWrapper eventoWrapper) {
		model.addAttribute("eventoWrapper", eventoWrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
		return EDIT;
	}

	private String goToEdit(Model model, EventoWrapper eventoWrapper) {
		model.addAttribute("eventoWrapper", eventoWrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
		return EDIT;
	}

	private String goToRendiconto(Model model, EventoWrapper wrapper) {
		model.addAttribute("eventoWrapper", wrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + RENDICONTO));
		return RENDICONTO;
	}

	@RequestMapping("/listaMetodologie")
	@ResponseBody
	public List<MetodologiaDidatticaRESEnum>getListaMetodologie(@RequestParam ObiettiviFormativiRESEnum obiettivo){
		return obiettivo.getMetodologieDidattiche();
	}
	
//	@RequestMapping(value = "/provider/{providerId}/evento/save", method=RequestMethod.POST, params={"addAttivitaToProgramma"})
//	public String addElement(@RequestParam("addAttivitaToProgramma") String programma,
//								@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
//		try{
//			int programmaIndex = Integer.valueOf(programma).intValue();
//			eventoWrapper.getProgrammaEvento().get(programmaIndex).getProgramma().add(new DettaglioAttivitaRES());
//			eventoWrapper.setGotoLink("#programma");
//			return EDIT;
//		}catch (Exception ex){
//			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
//			return "redirect:/home";
//		}
//	}
//	
//	@RequestMapping(value = "/provider/{providerId}/evento/removeAttivita/{programmaIndex}/{attivitaIndex}", method=RequestMethod.GET)
//	public String removeAttivitaFromProgramma(@PathVariable("programmaIndex") String progIndex, @PathVariable("attivitaIndex") String attIndex,
//												@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
//		try{
//			int programmaIndex = Integer.valueOf(progIndex).intValue();
//			int attivitaIndex = Integer.valueOf(attIndex).intValue();
//			eventoWrapper.getProgrammaEvento().get(programmaIndex).getProgramma().remove(attivitaIndex);
//			eventoWrapper.setGotoLink("#programma");
//			return EDIT;
//		}catch (Exception ex){
//			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
//			return "redirect:/home";
//		}
//	}
	
	@RequestMapping(value="/provider/{providerId}/createAnagraficaFullEvento", method=RequestMethod.POST)
	@ResponseBody
	public String saveAnagraficaFullEvento(@PathVariable("providerId") Long providerId, AnagraficaEvento anagrafica){
		//TODO
		anagraficaEventoService.save(anagrafica);
		return "OK";
	}
	
	@RequestMapping(value = "/provider/{providerId}/evento/addPersonaTo", method=RequestMethod.POST, params={"addPersonaTo"})
	public String addPersonaTo(@RequestParam("addPersonaTo") String target, 
								@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			//TODO da fare solo se rispetta il validator
			AnagraficaEventoBase anagraficaBase = eventoWrapper.getTempPersonaEvento().getAnagrafica();
			//check se non esiste -> si registra l'anagrafica per il provider
			if(anagraficaBase != null && !anagraficaBase.getCodiceFiscale().isEmpty()){
				if(anagraficaEventoService.getAnagraficaEventoByCodiceFiscaleForProvider(anagraficaBase.getCodiceFiscale(), eventoWrapper.getEvento().getProvider().getId()) == null){
					if(eventoWrapper.getCv() != null && !eventoWrapper.getCv().isNew())
						anagraficaBase.setCv(fileService.getFile(eventoWrapper.getCv().getId()));
					AnagraficaEvento anagraficaEventoToSave = new AnagraficaEvento();
					anagraficaEventoToSave.setAnagrafica(anagraficaBase);
					anagraficaEventoToSave.setProvider(eventoWrapper.getEvento().getProvider());
					anagraficaEventoService.save(anagraficaEventoToSave);
				}
			}
			PersonaEvento p = (PersonaEvento) Utils.copy(eventoWrapper.getTempPersonaEvento());
			if(target.equalsIgnoreCase("responsabiliScientifici")){
				p.setEventoResponsabile(eventoWrapper.getEvento());
				personaEventoRepo.save(p);
				eventoWrapper.getResponsabiliScientifici().add(p);
			}else if(target.equalsIgnoreCase("docenti")){
				p.setEventoDocente(eventoWrapper.getEvento());
				personaEventoRepo.save(p);//TODO trovare soluzione per settare docente senza id in AddAttivitaRES
				eventoWrapper.getDocenti().add(p);
			}
			eventoWrapper.setTempPersonaEvento(new PersonaEvento());
			return EDIT + " :: " + target;
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	@RequestMapping(value = "/provider/{providerId}/evento/addPersonaFullTo", method=RequestMethod.POST, params={"addPersonaFullTo"})
	public String addPersonaFullTo(@RequestParam("addPersonaFullTo") String target, 
								@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			//TODO da fare solo se rispetta il validator
			AnagraficaFullEventoBase anagraficaFull = eventoWrapper.getTempPersonaFullEvento().getAnagrafica();
			//check se non esiste -> si registra l'anagrafica per il provider
			if(anagraficaFull != null && !anagraficaFull.getCodiceFiscale().isEmpty()){
				if(anagraficaFullEventoService.getAnagraficaFullEventoByCodiceFiscaleForProvider(anagraficaFull.getCodiceFiscale(), eventoWrapper.getEvento().getProvider().getId()) == null){
					AnagraficaFullEvento anagraficaFullEventoToSave = new AnagraficaFullEvento();
					anagraficaFullEventoToSave.setAnagrafica(anagraficaFull);
					anagraficaFullEventoToSave.setProvider(eventoWrapper.getEvento().getProvider());
					anagraficaFullEventoService.save(anagraficaFullEventoToSave);
				}
			}
			
			PersonaFullEvento p = (PersonaFullEvento) Utils.copy(eventoWrapper.getTempPersonaFullEvento());
			if(target.equalsIgnoreCase("responsabileSegreteria")){
				p.setEventoResponsabileSegreteriaOrganizzativa(eventoWrapper.getEvento());
				eventoWrapper.getEvento().setResponsabileSegreteria(p);
			}
			eventoWrapper.setTempPersonaFullEvento(new PersonaFullEvento());
			return EDIT + " :: " + target;
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	@RequestMapping(value = "/provider/{providerId}/evento/removePersonaFrom/{removePersonaFrom}/{rowIndex}", method=RequestMethod.GET)
	public String removePersonaFrom(@PathVariable("removePersonaFrom") String target, @PathVariable("rowIndex") String rowIndex,
												@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			int responsabileIndex;
			if(target.equalsIgnoreCase("responsabiliScientifici")){
				responsabileIndex = Integer.valueOf(rowIndex).intValue();
				eventoWrapper.getResponsabiliScientifici().remove(responsabileIndex);
			}else if(target.equalsIgnoreCase("docenti")){
				responsabileIndex = Integer.valueOf(rowIndex).intValue();
				eventoWrapper.getDocenti().remove(responsabileIndex);
			}else if(target.equalsIgnoreCase("responsabileSegreteria")){
				eventoWrapper.getEvento().setResponsabileSegreteria(new PersonaFullEvento());
			}
			return EDIT + " :: " + target;
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	@RequestMapping(value = "/provider/{providerId}/evento/setLookupAnagraficaEvento/{type}/{angraficaEventoId}", method=RequestMethod.GET)
	public String lookupPersona(@PathVariable("type") String type,
									@PathVariable("angraficaEventoId") Long angraficaEventoId,
												@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			if(type.equalsIgnoreCase("Full")){
				eventoWrapper.setTempPersonaFullEvento(new PersonaFullEvento(anagraficaFullEventoService.getAnagraficaFullEvento(angraficaEventoId)));
				return EDIT + " :: #addPersonaFullTo";
			}else{
				eventoWrapper.setTempPersonaEvento(new PersonaEvento(anagraficaEventoService.getAnagraficaEvento(angraficaEventoId)));
				return EDIT + " :: #addPersonaTo";
			}
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	@RequestMapping(value = "/provider/{providerId}/evento/addAttivitaTo", method=RequestMethod.POST)
	public String addAttivitaTo(@RequestParam("target") String target, 
								@RequestParam("addAttivitaTo") String addAttivitaTo,
								@RequestParam(name = "pausa",required=false) Boolean pausa,
								@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			int programmaIndex = Integer.valueOf(addAttivitaTo).intValue();
			if(target.equalsIgnoreCase("attivitaRES")){
				//DettaglioAttivitaRES attivitaRES = (DettaglioAttivitaRES) Utils.copy(eventoWrapper.getTempAttivitaRES());
				DettaglioAttivitaRES attivitaRES =  SerializationUtils.clone(eventoWrapper.getTempAttivitaRES());
				eventoWrapper.getProgrammaEventoRES().get(programmaIndex).getProgramma().add(attivitaRES);
				if(pausa.booleanValue())
					attivitaRES.setAsPausa();
				eventoWrapper.setTempAttivitaRES(new DettaglioAttivitaRES());
			}
			return EDIT + " :: " + target;
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()));
			return "redirect:/home";
		}
	}
	
	@RequestMapping(value = "/provider/{providerId}/evento/removeAttivitaFrom/{target}/{removeAttivitaFrom}/{rowIndex}", method=RequestMethod.GET)
	public String removeAttivitaFrom(@PathVariable("target") String target,
										@PathVariable("removeAttivitaFrom") String removeAttivitaFrom, 
											@PathVariable("rowIndex") String rowIndex,
												@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			int programmaIndex;
			int attivitaRow;
			if(target.equalsIgnoreCase("attivitaRES")){
				programmaIndex = Integer.valueOf(removeAttivitaFrom).intValue();
				attivitaRow = Integer.valueOf(rowIndex).intValue();
				eventoWrapper.getProgrammaEventoRES().get(programmaIndex).getProgramma().remove(attivitaRow);
			}
			return EDIT + " :: " + target;
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	@RequestMapping(value = "/provider/{providerId}/evento/addProgramma/{target}", method=RequestMethod.GET)
	public String addProgramma(@PathVariable("target") String target,
										@RequestParam("programmaDate") String programmaDate, 
												@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			LocalDate data = LocalDate.parse(programmaDate, dtf);
			if(target.equalsIgnoreCase("attivitaRES")){
				ProgrammaGiornalieroRES programma = new ProgrammaGiornalieroRES();
				programma.setGiorno(data);
				programma.setSede(((EventoRES)eventoWrapper.getEvento()).getSedeEvento());
				eventoWrapper.getProgrammaEventoRES().add(programma);
			}
			return EDIT + " :: " + target;
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	@RequestMapping(value = "/provider/{providerId}/evento/showSection/{sectionIndex}", method=RequestMethod.POST)
	public String showSection(@PathVariable("sectionIndex") String sIndex, 
								@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			int sectionIndex = Integer.valueOf(sIndex).intValue();
			if(sectionIndex == 2){
				//sezione programma evento
			}else if(sectionIndex == 3){
				//sezione finale
				//ricalcolo durata e crediti
				eventoWrapper.getEvento().calcoloDurata();
				if(eventoWrapper.getEvento() instanceof EventoRES){
					if(!((EventoRES)eventoWrapper.getEvento()).isConfermatiCrediti()){
						eventoWrapper.getEvento().calcoloCreditiFormativi();
					}
				}
			}
			
			if(eventoWrapper.getEvento() instanceof EventoRES){
				return EDITRES + " :: " + "section-" + sectionIndex;
			}else if(eventoWrapper.getEvento() instanceof EventoFSC){
				return EDITFSC + " :: " + "section-" + sectionIndex;
			}else{
				return EDITFAD + " :: " + "section-" + sectionIndex;
			}
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()));
			return "redirect:/home";
		}
	}
	
	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/evento/listaDocentiAttivitaRES")
	@ResponseBody
	public List<PersonaEvento>getListaDocentiAttivitaRES(@PathVariable Long providerId, @ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		List<PersonaEvento> lista = new ArrayList<PersonaEvento>();
		if(eventoWrapper.getEvento() instanceof EventoRES){
			lista = ((EventoRES)eventoWrapper.getEvento()).getDocenti();
		}
		return lista;
	}
	
}
