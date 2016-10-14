package it.tredi.ecm.web;

import java.util.ArrayList;
import java.util.List;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoFAD;
import it.tredi.ecm.dao.entity.EventoFSC;
import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Provider;

import it.tredi.ecm.dao.enumlist.EventoWrapperModeEnum;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.exception.AccreditamentoNotFoundException;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.exception.EcmException;
import it.tredi.ecm.exception.EcmException;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.ObiettivoService;

import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoWrapperModeEnum;
import it.tredi.ecm.dao.enumlist.EventoWrapperModeEnum;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.exception.EcmException;
import it.tredi.ecm.service.EventoService;

import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EventoWrapper;
import it.tredi.ecm.web.bean.Message;

@Controller
public class EventoController {
	public static final Logger LOGGER = LoggerFactory.getLogger(EventoController.class);

	@Autowired private EventoService eventoService;
	@Autowired private ProviderService providerService;
	@Autowired private ObiettivoService obiettivoService;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private FileService fileService;

	private final String LIST = "evento/eventoList";
	private final String EDIT = "evento/eventoEdit";
	private final String RENDICONTO = "evento/eventoRendiconto";

	@ModelAttribute("elencoProvince")
	public List<String> getElencoProvince(){
		List<String> elencoProvince = new ArrayList<String>();

		elencoProvince.add("Venezia");
		elencoProvince.add("Padova");
		elencoProvince.add("Verona");

		return elencoProvince;
	}

	@ModelAttribute("eventoWrapper")
	public EventoWrapper getEvento(@RequestParam(name = "editId", required = false) Long id,
			@RequestParam(value="providerId",required = false) Long providerId,
			@RequestParam(value="proceduraFormativa",required = false) ProceduraFormativa proceduraFormativa,
			@RequestParam(value="wrapperMode",required = false) EventoWrapperModeEnum wrapperMode) throws Exception{
		if(id != null){
			if (wrapperMode == EventoWrapperModeEnum.RENDICONTO)
				return prepareEventoWrapperRendiconto(eventoService.getEvento(id), providerId);
			else
				return prepareEventoWrapperEdit(eventoService.getEvento(id));
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
			LOGGER.info(Utils.getLogMessage("REDIRECT: /redirect:/home"));
			return "redirect:/home";
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
				//TODO - fare la gestione con la mia eccezione
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
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperNew(" + proceduraFormativa + ") - exiting"));
		return eventoWrapper;
	}

	private EventoWrapper prepareEventoWrapperEdit(Evento evento) throws Exception {
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperEdit(" + evento.getId() + ") - entering"));
		EventoWrapper eventoWrapper = prepareCommonEditWrapper(evento.getProceduraFormativa(), evento.getProvider().getId());
		eventoWrapper.setEvento(evento);
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

	private String goToRendiconto(Model model, EventoWrapper wrapper) {
		model.addAttribute("eventoWrapper", wrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + RENDICONTO));
		return RENDICONTO;
	}


}
