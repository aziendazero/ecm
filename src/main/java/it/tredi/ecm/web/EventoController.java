package it.tredi.ecm.web;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.AnagraficaEvento;
import it.tredi.ecm.dao.entity.AnagraficaEventoBase;
import it.tredi.ecm.dao.entity.AnagraficaFullEvento;
import it.tredi.ecm.dao.entity.AnagraficaFullEventoBase;
import it.tredi.ecm.dao.entity.AzioneRuoliEventoFSC;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.DettaglioAttivitaFAD;
import it.tredi.ecm.dao.entity.DettaglioAttivitaRES;
import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoFAD;
import it.tredi.ecm.dao.entity.EventoFSC;
import it.tredi.ecm.dao.entity.EventoPianoFormativo;
import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Partner;
import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.entity.PersonaFullEvento;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.RuoloOreFSC;
import it.tredi.ecm.dao.entity.Sponsor;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import it.tredi.ecm.dao.enumlist.EventoSearchEnum;
import it.tredi.ecm.dao.enumlist.EventoStatoEnum;
import it.tredi.ecm.dao.enumlist.EventoWrapperModeEnum;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.MetodologiaDidatticaFADEnum;
import it.tredi.ecm.dao.enumlist.MetodologiaDidatticaRESEnum;
import it.tredi.ecm.dao.enumlist.ObiettiviFormativiFADEnum;
import it.tredi.ecm.dao.enumlist.ObiettiviFormativiRESEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import it.tredi.ecm.dao.repository.PersonaEventoRepository;
import it.tredi.ecm.exception.AccreditamentoNotFoundException;
import it.tredi.ecm.exception.EcmException;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.AnagraficaEventoService;
import it.tredi.ecm.service.AnagraficaFullEventoService;
import it.tredi.ecm.service.EngineeringService;
import it.tredi.ecm.service.EventoPianoFormativoService;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.ObiettivoService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.bean.CurrentUser;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EventoWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.validator.EventoValidator;
import it.tredi.ecm.web.validator.RuoloOreFSCValidator;

@Controller
@SessionAttributes("eventoWrapper")
public class EventoController {
	public static final Logger LOGGER = LoggerFactory.getLogger(EventoController.class);

	@Autowired private EventoService eventoService;
	@Autowired private EventoPianoFormativoService eventoPianoFormativoService;
	@Autowired private ProviderService providerService;
	@Autowired private ObiettivoService obiettivoService;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private FileService fileService;

	@Autowired private AnagraficaEventoService anagraficaEventoService;
	@Autowired private AnagraficaFullEventoService anagraficaFullEventoService;
	@Autowired private PersonaEventoRepository personaEventoRepository;

	@Autowired private RuoloOreFSCValidator ruoloOreFSCValidator;
	@Autowired private EventoValidator eventoValidator;

	@Autowired private EngineeringService engineeringService;

	private final String LIST = "evento/eventoList";
	private final String EDIT = "evento/eventoEdit";
	private final String SHOW = "evento/eventoShow";
	private final String RENDICONTO = "evento/eventoRendiconto";
	private final String EDITRES = "evento/eventoRESEdit";
	private final String EDITFSC = "evento/eventoFSCEdit";
	private final String EDITFAD = "evento/eventoFADEdit";

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
			return "";
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
			model.addAttribute("eventoAttuazioneList", eventoPianoFormativoService.getAllEventiAttuabiliForProviderId(providerId));
			model.addAttribute("eventoRiedizioneList", eventoService.getAllEventiRieditabiliForProviderId(providerId));
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
	@RequestMapping("/provider/{providerId}/eventoPianoFormativo/{eventoPianoFormativoId}/fulfill")
	public String attuaEvento(@PathVariable Long providerId, @PathVariable Long eventoPianoFormativoId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/eventoPianoFormativo/" + eventoPianoFormativoId +"/fulfill"));
		try {
			EventoPianoFormativo eventoPianoFormativo = eventoPianoFormativoService.getEvento(eventoPianoFormativoId);
			EventoWrapper wrapper = prepareEventoWrapperAttuazione(eventoPianoFormativo, providerId);
			return goToEdit(model, wrapper);
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/eventoPianoFormativo/" + eventoPianoFormativoId +"/fulfill"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/"+providerId+"/evento/list"));
			return "redirect:/provider/{providerId}/evento/list";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canCreateEvento(principal, #providerId)")
	@RequestMapping("/provider/{providerId}/evento/{eventoId}/re-edit")
	public String rieditaEvento(@PathVariable Long providerId, @PathVariable Long eventoId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId +"/re-edit"));
		try {
			Evento evento = eventoService.getEvento(eventoId);
			EventoWrapper wrapper = prepareEventoWrapperRiedizione(evento, providerId);
			return goToEdit(model, wrapper);
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId +"/re-edit"),ex);
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

	@PreAuthorize("@securityAccessServiceImpl.canCreateEvento(principal, #providerId)")
	@RequestMapping(value= "/provider/{providerId}/evento/validate", method = RequestMethod.POST)
	public String validaEvento(@ModelAttribute EventoWrapper eventoWrapper, BindingResult result, @PathVariable Long providerId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("POST /provider/" + providerId + "/evento/validate"));
		try {
			//gestione dei campi ripetibili
			Evento evento = eventoService.handleRipetibiliAndAllegati(eventoWrapper);

			eventoValidator.validate(evento, eventoWrapper, result, "evento.");

			if(result.hasErrors()){
				model.addAttribute("message", new Message("message.errore", "message.inserire_campi_required", "error"));
				//gestione adHoc degli errori per evitare di perdere i dati dopo i refresh delle tab eventi
				eventoWrapper.setMappaErroriValidazione(prepareMappaErroriValutazione(result));
				LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
				return EDIT;
			}else{
				evento.setStato(EventoStatoEnum.VALIDATO);
				evento.setValidatorCheck(true);
				evento.setDataScadenzaInvioRendicontazione(evento.getDataFine().plusDays(90));
				eventoService.save(evento);
				LOGGER.info(Utils.getLogMessage("Evento validato e salvato!"));
			}

			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.evento_validato_e_salvato_success", "success"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/{providerId}/evento/list"));
			return "redirect:/provider/{providerId}/evento/list";
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /provider/" + providerId + "/evento/validate"),ex);
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
			Evento evento = eventoService.getEvento(eventoId);
			EventoWrapper wrapper = prepareEventoWrapperEdit(evento, true);
			return goToEdit(model, wrapper);
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /provider/" + providerId + "/evento/"+ eventoId + "/edit"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/"+providerId+"/evento/list"));
			return "redirect:/provider/{providerId}/evento/list";
		}
	}

//TODO	@PreAuthorize("@securityAccessServiceImpl.canShowEvento(principal, #providerId")
	@RequestMapping("/provider/{providerId}/evento/{eventoId}/show")
	public String showEvento(@PathVariable Long providerId, @PathVariable Long eventoId,
			Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/evento/"+ eventoId + "/show"));
		try {
			//show dell'evento
			EventoWrapper wrapper = prepareEventoWrapperShow(eventoService.getEvento(eventoId));
			return goToShow(model, wrapper);
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /provider/" + providerId + "/evento/"+ eventoId + "/show"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/"+providerId+"/evento/list"));
			return "redirect:/provider/{providerId}/evento/list";
		}
	}

		//TODO	@PreAuthorize("@securityAccessServiceImpl.canDeleteEvento(principal, #providerId")
		@RequestMapping("/provider/{providerId}/evento/{eventoId}/delete")
		public String deleteEvento(@PathVariable Long providerId, @PathVariable Long eventoId,
				Model model, RedirectAttributes redirectAttrs) {
			LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/evento/"+ eventoId + "/delete"));
			try {
				//delete dell'evento
				Evento evento = eventoService.getEvento(eventoId);
				if(evento.getStato() == EventoStatoEnum.BOZZA){
					eventoService.delete(eventoId);
				}else{
					evento.setStato(EventoStatoEnum.CANCELLATO);
					eventoService.save(evento);
				}
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.evento_elimitato", "success"));
				return "redirect:/provider/{providerId}/evento/list";
			}
			catch (Exception ex) {
				LOGGER.error(Utils.getLogMessage("POST /provider/" + providerId + "/evento/"+ eventoId + "/delete"),ex);
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
					redirectAttrs.addFlashAttribute("message", new Message("message.warning", "message.inserire_il_rendiconto", "alert"));
				else {
					LOGGER.info(Utils.getLogMessage("Ricevuto File id: " + wrapper.getReportPartecipanti().getId() + " da validare"));
					File file = wrapper.getReportPartecipanti();
					if(file != null && !file.isNew()){
						if(file.isREPORTPARTECIPANTI()) {
							String fileName = wrapper.getReportPartecipanti().getNomeFile().trim().toUpperCase();
							if (fileName.endsWith(".XML") || fileName.endsWith(".XML.P7M") || fileName.endsWith(".XML.ZIP.P7M") || fileName.endsWith(".CSV")) {
								wrapper.setReportPartecipanti(fileService.getFile(file.getId()));
								eventoService.validaRendiconto(eventoId, wrapper.getReportPartecipanti());
								redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.xml_evento_validation_ok", "success"));
							}
							else {
								redirectAttrs.addFlashAttribute("message", new Message("message.warning", "error.formatNonAcceptedXML", "alert"));
							}
						}
					}
			}
			return "redirect:/provider/{providerId}/evento/{eventoId}/rendiconto";
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/validate"),ex);
				if (ex instanceof EcmException) //errore gestito
//TODO - l'idea era quella di utilizzare error._free_msg={0} ma non funziona!!!!
					redirectAttrs.addFlashAttribute("message", new Message(((EcmException) ex).getMessageTitle(), ((EcmException) ex).getMessageDetail(), "alert"));
				else
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/validate"));
				return "redirect:/provider/{providerId}/evento/{eventoId}/rendiconto";
		}
	}

	//TODO	@PreAuthorize("@securityAccessServiceImpl.canSendRendiconto(principal)")
		@RequestMapping(value = "/provider/{providerId}/evento/{eventoId}/rendiconto/inviaACogeaps", method = RequestMethod.GET)
		public String rendicontoEventoIviaACogeaps(@PathVariable Long providerId,
				@PathVariable Long eventoId, @ModelAttribute("eventoWrapper") EventoWrapper wrapper, BindingResult result,
				Model model, RedirectAttributes redirectAttrs) {
			try{
				LOGGER.info(Utils.getLogMessage("POST /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/inviaACogeaps"));
				model.addAttribute("returnLink", "/provider/" + providerId + "/evento/list");
				eventoService.inviaRendicontoACogeaps(eventoId);
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.invio_cogeaps_ok", "success"));
				return "redirect:/provider/{providerId}/evento/{eventoId}/rendiconto";
			}
			catch (Exception ex) {
				LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/inviaACogeaps"),ex);
				if (ex instanceof EcmException) //errore gestito
	//TODO - l'idea era quella di utilizzare error._free_msg={0} ma non funziona!!!!
					redirectAttrs.addFlashAttribute("message", new Message(((EcmException) ex).getMessageTitle(), ((EcmException) ex).getMessageDetail(), "alert"));
				else
					redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/inviaACogeaps"));
				return "redirect:/provider/{providerId}/evento/{eventoId}/rendiconto";
			}
		}

		//TODO	@PreAuthorize("@securityAccessServiceImpl.canSendRendiconto(principal)")
		@RequestMapping(value = "/provider/{providerId}/evento/{eventoId}/rendiconto/statoElaborazioneCogeaps", method = RequestMethod.GET)
		public String rendicontoEventoStatoElaborazioneCogeaps(@PathVariable Long providerId,
				@PathVariable Long eventoId, @ModelAttribute("eventoWrapper") EventoWrapper wrapper, BindingResult result,
				Model model, RedirectAttributes redirectAttrs) {
			try{
				LOGGER.info(Utils.getLogMessage("POST /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/statoElaborazioneCogeaps"));
				model.addAttribute("returnLink", "/provider/" + providerId + "/evento/list");
				eventoService.statoElaborazioneCogeaps(eventoId);
				return "redirect:/provider/{providerId}/evento/{eventoId}/rendiconto";
			}
			catch (Exception ex) {
				LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/statoElaborazioneCogeaps"),ex);
				if (ex instanceof EcmException) //errore gestito
	//TODO - l'idea era quella di utilizzare error._free_msg={0} ma non funziona!!!!
					redirectAttrs.addFlashAttribute("message", new Message(((EcmException) ex).getMessageTitle(), ((EcmException) ex).getMessageDetail(), "alert"));
				else
					redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/statoElaborazioneCogeaps"));
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
		evento.setAccreditamento(accreditamentoService.getAccreditamentoAttivoForProvider(providerId));
		evento.setProceduraFormativa(proceduraFormativa);
		evento.setStato(EventoStatoEnum.BOZZA);
		eventoWrapper.setEvento(evento);
		eventoWrapper.initProgrammi();
//		eventoWrapper = eventoService.prepareRipetibiliAndAllegati(eventoWrapper);
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperNew(" + proceduraFormativa + ") - exiting"));
		return eventoWrapper;
	}

	private EventoWrapper prepareEventoWrapperEdit(Evento evento, boolean reloadWrapperFromDB) throws Exception {
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperEdit(" + evento.getId() + ") - entering"));
		EventoWrapper eventoWrapper = prepareCommonEditWrapper(evento.getProceduraFormativa(), evento.getProvider().getId());
		eventoWrapper.setEvento(evento);
		eventoWrapper.initProgrammi();
		if(reloadWrapperFromDB)
			eventoWrapper = eventoService.prepareRipetibiliAndAllegati(eventoWrapper);
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperEdit(" + evento.getId() + ") - exiting"));
		//editabilità
		eventoWrapper.setEditSemiBloccato(eventoService.isEditSemiBloccato(evento));
		eventoWrapper.setEventoIniziato(eventoService.isEventoIniziato(evento));
		eventoWrapper.setDataInizioEditabile(eventoService.canEditDataInizio(evento));
		return eventoWrapper;
	}

	private EventoWrapper prepareEventoWrapperShow(Evento evento) throws Exception {
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperShow(" + evento.getId() + ") - entering"));
		EventoWrapper eventoWrapper = new EventoWrapper();
		eventoWrapper.setProceduraFormativa(evento.getProceduraFormativa());
		eventoWrapper.setProviderId(evento.getProvider().getId());
		eventoWrapper.setEvento(evento);
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperShow(" + evento.getId() + ") - exiting"));
		return eventoWrapper;
	}

	private EventoWrapper prepareCommonEditWrapper(ProceduraFormativa proceduraFormativa, Long providerId) throws AccreditamentoNotFoundException, Exception {
		EventoWrapper eventoWrapper = new EventoWrapper();
		eventoWrapper.setProceduraFormativa(proceduraFormativa);
		eventoWrapper.setProviderId(providerId);
		eventoWrapper.setObiettiviNazionali(obiettivoService.getObiettiviNazionali());
		eventoWrapper.setObiettiviRegionali(obiettivoService.getObiettiviRegionali());
		DatiAccreditamento datiAccreditamento = accreditamentoService.getDatiAccreditamentoForAccreditamentoId(accreditamentoService.getAccreditamentoAttivoForProvider(providerId).getId());
		List<Professione> professioneList = new ArrayList<Professione>();
		professioneList.addAll(datiAccreditamento.getProfessioniSelezionate());
		professioneList.sort(new Comparator<Professione>() {
			 public int compare(Professione p1, Professione p2) {
				 return (p1.getNome().compareTo(p2.getNome()));
			 }
		});
		eventoWrapper.setProfessioneList(professioneList);
		eventoWrapper.setDisciplinaList(datiAccreditamento.getDiscipline());
		eventoWrapper.setWrapperMode(EventoWrapperModeEnum.EDIT);
		eventoWrapper.setMappaErroriValidazione(new HashMap<String, String>());
		return eventoWrapper;
	}

	private EventoWrapper prepareEventoWrapperRendiconto(Evento evento, long providerId) {
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperRendiconto(" + evento.getId() + "," + providerId + ") - entering"));
		EventoWrapper eventoWrapper = new EventoWrapper();
		eventoWrapper.setEvento(evento);
		eventoWrapper.setProviderId(providerId);
		eventoWrapper.setProceduraFormativa(evento.getProceduraFormativa());
		eventoWrapper.setReportPartecipanti(new File(FileEnum.FILE_REPORT_PARTECIPANTI));
		eventoWrapper.setWrapperMode(EventoWrapperModeEnum.RENDICONTO);
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperRendiconto(" + evento.getId() + "," + providerId + ") - exiting"));
		return eventoWrapper;
	}

	private EventoWrapper prepareEventoWrapperAttuazione(EventoPianoFormativo eventoPianoFormativo, long providerId) throws AccreditamentoNotFoundException, Exception {
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperAttuazione(" + eventoPianoFormativo.getId() + ") - entering"));
		EventoWrapper eventoWrapper = prepareCommonEditWrapper(eventoPianoFormativo.getProceduraFormativa(), providerId);
		Evento evento;
		switch(eventoPianoFormativo.getProceduraFormativa()){
			case FAD: evento = new EventoFAD(); break;
			case RES: evento = new EventoRES(); break;
			case FSC: evento = new EventoFSC(); break;
			default: evento = new Evento(); break;
		}
		evento.setFromEventoPianoFormativo(eventoPianoFormativo);
		evento.setStato(EventoStatoEnum.BOZZA);
		eventoWrapper.setEvento(evento);
		eventoWrapper.initProgrammi();
//		eventoWrapper = eventoService.prepareRipetibiliAndAllegati(eventoWrapper);

		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperAttuazione(" + eventoPianoFormativo.getId() + ") - exiting"));
		return eventoWrapper;
	}

	private EventoWrapper prepareEventoWrapperRiedizione(Evento evento, long providerId) throws AccreditamentoNotFoundException, Exception {
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperRiedizione(" + evento.getId() + ") - entering"));
		EventoWrapper eventoWrapper = prepareCommonEditWrapper(evento.getProceduraFormativa(), providerId);
		Evento riedizioneEvento = eventoService.prepareRiedizioneEvento(evento);
		eventoWrapper.setEvento(riedizioneEvento);
		eventoWrapper.initProgrammi();
		eventoWrapper = eventoService.prepareRipetibiliAndAllegati(eventoWrapper);
		//editabilità
		eventoWrapper.setEditSemiBloccato(eventoService.isEditSemiBloccato(evento));
		eventoWrapper.setEventoIniziato(eventoService.isEventoIniziato(evento));
		eventoWrapper.setDataInizioEditabile(eventoService.canEditDataInizio(evento));
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperRiedizione(" + evento.getId() + ") - exiting"));
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

	private String goToShow(Model model, EventoWrapper eventoWrapper) {
		model.addAttribute("eventoWrapper", eventoWrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
		return SHOW;
	}

	private String goToRendiconto(Model model, EventoWrapper wrapper) {
		model.addAttribute("eventoWrapper", wrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + RENDICONTO));
		return RENDICONTO;
	}

	private Map<String, String> prepareMappaErroriValutazione(BindingResult result){
		Map<String, String> mappa = new HashMap<String, String>();
		List<FieldError> errori = result.getFieldErrors();
		for (FieldError e : errori) {
			mappa.put(e.getField(), e.getCode());
		}
		return mappa;
	}

	@RequestMapping("/listaMetodologieRES")
	@ResponseBody
	public List<MetodologiaDidatticaRESEnum>getListaMetodologieRES(@RequestParam ObiettiviFormativiRESEnum obiettivo){
		return obiettivo.getMetodologieDidattiche();
	}

	@RequestMapping("/listaMetodologieFAD")
	@ResponseBody
	public List<MetodologiaDidatticaFADEnum>getListaMetodologieFAD(@RequestParam ObiettiviFormativiFADEnum obiettivo){
		return obiettivo.getMetodologieDidattiche();
	}

	@RequestMapping(value="/provider/{providerId}/createAnagraficaFullEvento", method=RequestMethod.POST)
	@ResponseBody
	public String saveAnagraficaFullEvento(@PathVariable("providerId") Long providerId, AnagraficaEvento anagrafica){
		//TODO
		anagraficaEventoService.save(anagrafica);
		return "OK";
	}

	@RequestMapping(value = "/provider/{providerId}/evento/addPersonaTo", method=RequestMethod.POST, params={"addPersonaTo"})
	public String addPersonaTo(@RequestParam("addPersonaTo") String target,
								@RequestParam("fromLookUp") String fromLookUp,
								@RequestParam(name = "modificaElemento",required=false) String modificaElemento,
								@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			if(modificaElemento == null || modificaElemento.isEmpty()){
				//INSERIMENTO NUOVA PERSONA

				//TODO da fare solo se rispetta il validator
				AnagraficaEventoBase anagraficaBase = eventoWrapper.getTempPersonaEvento().getAnagrafica();
				//check se non esiste -> si registra l'anagrafica per il provider
				if(anagraficaBase != null && !anagraficaBase.getCodiceFiscale().isEmpty()){
					if(anagraficaEventoService.getAnagraficaEventoByCodiceFiscaleForProvider(anagraficaBase.getCodiceFiscale(), eventoWrapper.getEvento().getProvider().getId()) == null){
						if(eventoWrapper.getCv() != null && !eventoWrapper.getCv().isNew()){
							File cv = fileService.getFile(eventoWrapper.getCv().getId());
							cv.getData();
							if(fromLookUp != null && Boolean.valueOf(fromLookUp)){
								File f = (File) cv.clone();
								fileService.save(f);
								anagraficaBase.setCv(f);
							}else{
								anagraficaBase.setCv(cv);
							}
						}
						AnagraficaEvento anagraficaEventoToSave = new AnagraficaEvento();
						anagraficaEventoToSave.setAnagrafica(anagraficaBase);
						anagraficaEventoToSave.setProvider(eventoWrapper.getEvento().getProvider());
						anagraficaEventoService.save(anagraficaEventoToSave);
					}
				}

				PersonaEvento p = SerializationUtils.clone(eventoWrapper.getTempPersonaEvento());
				if(target.equalsIgnoreCase("responsabiliScientifici")){
					//TODO sono obbligato a salvarlo perchè altrimenti non riesco a fare il binding in in AddAttivitaRES (select si basa su id della entity)
					//questo comporta anche che prima di salvare l'evento devo fare il reload della persona altrimenti hibernate mi da detached object e non mi fa salvare

					File cv = p.getAnagrafica().getCv();
					if(cv != null) {
						cv.getData();
						File f = (File) cv.clone();
						fileService.save(f);
						p.getAnagrafica().setCv(f);
					}

					personaEventoRepository.save(p);
					eventoWrapper.getResponsabiliScientifici().add(p);
				}else if(target.equalsIgnoreCase("docenti")){

					File cv = p.getAnagrafica().getCv();
					if(cv != null) {
						cv.getData();
						File f = (File) cv.clone();
						fileService.save(f);
						p.getAnagrafica().setCv(f);
					}

					personaEventoRepository.save(p);
					eventoWrapper.getDocenti().add(p);
				}
			}else{
				//MODIFICA
				int index = Integer.parseInt(modificaElemento);
				if(target.equalsIgnoreCase("responsabiliScientifici")){
					personaEventoRepository.save(eventoWrapper.getTempPersonaEvento());
					eventoWrapper.getResponsabiliScientifici().set(index, eventoWrapper.getTempPersonaEvento());
				}else if(target.equalsIgnoreCase("docenti")){
					personaEventoRepository.save(eventoWrapper.getTempPersonaEvento());
					eventoWrapper.getDocenti().set(index, eventoWrapper.getTempPersonaEvento());
				}
			}
			eventoWrapper.setTempPersonaEvento(new PersonaEvento());
			return EDIT + " :: " + target;
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDIT + " :: " + target;
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/addPersonaFullTo", method=RequestMethod.POST, params={"addPersonaFullTo"})
	public String addPersonaFullTo(@RequestParam("addPersonaFullTo") String target,
									@RequestParam(name = "modificaElementoFull",required=false) String modificaElemento,
										@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			if(modificaElemento == null || modificaElemento.isEmpty()){
				//INSERIMENTO NUOVA PERSONA

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

				//PersonaFullEvento p = (PersonaFullEvento) Utils.copy(eventoWrapper.getTempPersonaFullEvento());
				PersonaFullEvento p = SerializationUtils.clone(eventoWrapper.getTempPersonaFullEvento());
				if(target.equalsIgnoreCase("responsabileSegreteria")){
					eventoWrapper.getEvento().setResponsabileSegreteria(p);
				}
			}else{
				//MODIFICA
				eventoWrapper.getEvento().setResponsabileSegreteria(eventoWrapper.getTempPersonaFullEvento());
			}

			eventoWrapper.setTempPersonaFullEvento(new PersonaFullEvento());
			return EDIT + " :: " + target;
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDIT + " :: " + target;
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/addSponsorTo", method=RequestMethod.POST)
	public String addSponsorTo(@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			Sponsor s = SerializationUtils.clone(eventoWrapper.getTempSponsorEvento());
			eventoWrapper.getSponsors().add(s);
			eventoWrapper.setTempSponsorEvento(new Sponsor());
			if(eventoWrapper.getSponsorFile() != null && !eventoWrapper.getSponsorFile().isNew())
				s.setSponsorFile(fileService.getFile(eventoWrapper.getSponsorFile().getId()));
			return EDIT + " :: sponsors";
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDIT + " :: sponsors";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/addPartnerTo", method=RequestMethod.POST)
	public String addPartnerTo(@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			Partner p = SerializationUtils.clone(eventoWrapper.getTempPartnerEvento());
			eventoWrapper.getPartners().add(p);
			eventoWrapper.setTempPartnerEvento(new Partner());
			if(eventoWrapper.getPartnerFile() != null && !eventoWrapper.getPartnerFile().isNew())
				p.setPartnerFile(fileService.getFile(eventoWrapper.getPartnerFile().getId()));
			return EDIT + " :: partners";
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDIT + " :: partners";
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
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDIT + " :: " + target;
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/removeSponsor/{rowIndex}", method=RequestMethod.GET)
	public String removeSponsorFrom(@PathVariable("rowIndex") String rowIndex,
			@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			int sponsorIndex = Integer.valueOf(rowIndex).intValue();
				eventoWrapper.getSponsors().remove(sponsorIndex);
			return EDIT + " :: sponsors";
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDIT + " :: sponsors";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/removePartner/{rowIndex}", method=RequestMethod.GET)
	public String removePartnerFrom(@PathVariable("rowIndex") String rowIndex,
			@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			int partnerIndex = Integer.valueOf(rowIndex).intValue();
				eventoWrapper.getPartners().remove(partnerIndex);
			return EDIT + " :: partners";
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDIT + " :: partners";
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
				PersonaEvento p = new PersonaEvento(anagraficaEventoService.getAnagraficaEvento(angraficaEventoId));
				eventoWrapper.setTempPersonaEvento(p);
				eventoWrapper.setCv(p.getAnagrafica().getCv());
				return EDIT + " :: #addPersonaTo";
			}
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return "redirect:/home";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/addAttivitaTo", method=RequestMethod.POST)
	public String addAttivitaTo(@RequestParam("target") String target,
								@RequestParam("addAttivitaTo") String addAttivitaTo,
								@RequestParam("modificaElemento") Integer modificaElemento,
								@RequestParam(name = "extraType",required=false) String extraType,
								@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			if(modificaElemento == null){
				//INSERIMENTO
				int programmaIndex = Integer.valueOf(addAttivitaTo).intValue();
				if(target.equalsIgnoreCase("attivitaRES")){
					DettaglioAttivitaRES attivitaRES =  SerializationUtils.clone(eventoWrapper.getTempAttivitaRES());
					attivitaRES.calcolaOreAttivita();
					Long programmaIndexLong = Long.valueOf(programmaIndex);
					LOGGER.debug("EventoRES - evento/addAttivitaTo programmaIndexLong: " + programmaIndexLong);
					eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().get(programmaIndexLong).getProgramma().getProgramma().add(attivitaRES);

					if(extraType != null){
						attivitaRES.setExtraType(extraType);
					}

					eventoWrapper.setTempAttivitaRES(new DettaglioAttivitaRES());
				}else if(target.equalsIgnoreCase("attivitaFSC")){
					AzioneRuoliEventoFSC azioniRuoli = SerializationUtils.clone(eventoWrapper.getTempAttivitaFSC());
					eventoWrapper.getProgrammaEventoFSC().get(programmaIndex).getAzioniRuoli().add(azioniRuoli);
					eventoWrapper.setTempAttivitaFSC(new AzioneRuoliEventoFSC());
				}else if(target.equalsIgnoreCase("attivitaFAD")){
					DettaglioAttivitaFAD attivitaFAD =  SerializationUtils.clone(eventoWrapper.getTempAttivitaFAD());
					eventoWrapper.getProgrammaEventoFAD().add(attivitaFAD);
					eventoWrapper.setTempAttivitaFAD(new DettaglioAttivitaFAD());
				}
			}else{
				//MODIFICA
				int programmaIndex = Integer.valueOf(addAttivitaTo).intValue();
				int elementoIndex = Integer.valueOf(modificaElemento).intValue();
				if(target.equalsIgnoreCase("attivitaRES")){
					DettaglioAttivitaRES attivitaRES =  eventoWrapper.getTempAttivitaRES();
					attivitaRES.calcolaOreAttivita();
					Long programmaIndexLong = Long.valueOf(programmaIndex);
					eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().get(programmaIndexLong).getProgramma().getProgramma().set(elementoIndex, attivitaRES);
					eventoWrapper.setTempAttivitaRES(new DettaglioAttivitaRES());
				}else if(target.equalsIgnoreCase("attivitaFSC")){
					AzioneRuoliEventoFSC azioniRuoli = eventoWrapper.getTempAttivitaFSC();
					eventoWrapper.getProgrammaEventoFSC().get(programmaIndex).getAzioniRuoli().set(elementoIndex, azioniRuoli);
					eventoWrapper.setTempAttivitaFSC(new AzioneRuoliEventoFSC());
				}else if(target.equalsIgnoreCase("attivitaFAD")){
					DettaglioAttivitaFAD attivitaFAD =  eventoWrapper.getTempAttivitaFAD();
					eventoWrapper.getProgrammaEventoFAD().set(elementoIndex, attivitaFAD);
					eventoWrapper.setTempAttivitaFAD(new DettaglioAttivitaFAD());
				}
			}
			return EDIT + " :: " + target;
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDIT + " :: " + target;
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
				Long programmaIndexLong = Long.valueOf(programmaIndex);
				LOGGER.debug("EventoRES - evento/removeAttivitaFrom programmaIndexLong: " + programmaIndexLong + "; attivitaRow: " + attivitaRow);
				eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().get(programmaIndexLong).getProgramma().getProgramma().remove(attivitaRow);
			}else if(target.equalsIgnoreCase("attivitaFSC")){
				programmaIndex = Integer.valueOf(removeAttivitaFrom).intValue();
				attivitaRow = Integer.valueOf(rowIndex).intValue();
				eventoWrapper.getProgrammaEventoFSC().get(programmaIndex).getAzioniRuoli().remove(attivitaRow);
			}else if(target.equalsIgnoreCase("attivitaFAD")){
				programmaIndex = Integer.valueOf(removeAttivitaFrom).intValue();
				attivitaRow = Integer.valueOf(rowIndex).intValue();
				eventoWrapper.getProgrammaEventoFAD().remove(attivitaRow);
			}
			return EDIT + " :: " + target;
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDIT + " :: " + target;
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/showSection/{sectionIndex}", method=RequestMethod.POST)
	public String showSection(@PathVariable("sectionIndex") String sIndex, @ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper,
			Model model, RedirectAttributes redirectAttrs){
		int sectionIndex = 1;
		try{
			sectionIndex = Integer.valueOf(sIndex).intValue();
			if(sectionIndex == 2){
				//sezione programma evento
				//Eseguo questo aggiornamento perche' le date dataInizioe dataFine potrebbero essere state modificate
				eventoService.aggiornaDati(eventoWrapper);
			}else if(sectionIndex == 3){
				//sezione finale - ricalcolo durata e crediti
				eventoService.calculateAutoCompilingData(eventoWrapper);
			}
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
		}

		if(eventoWrapper.getEvento() instanceof EventoRES){
			return EDITRES + " :: " + "section-" + sectionIndex;
		}else if(eventoWrapper.getEvento() instanceof EventoFSC){
			return EDITFSC + " :: " + "section-" + sectionIndex;
		}else{
			return EDITFAD + " :: " + "section-" + sectionIndex;
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/addDataIntermedia/{sectionToRefresh}", method=RequestMethod.POST)
	public String addDataIntermedia(@PathVariable("sectionToRefresh") String sectionToRefresh,
								@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			if(eventoWrapper.getEvento() instanceof EventoRES){
				eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().addProgrammaGiornalieroIntermedio(null);
				//return EDITRES + " :: " + sectionToRefresh;
				return EDITRES + " :: eventoRESEdit";
			} else {
				throw new Exception("Metodo chiamato dalla pagina errata aspettatto EventoRES.");
			}
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return "redirect:/home";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/removeDataIntermedia/{key}/{sectionToRefresh}", method=RequestMethod.POST)
	public String removeDataIntermedia(@PathVariable("key") String key, @PathVariable("sectionToRefresh") String sectionToRefresh,
								@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			Long k = Long.valueOf(key);
			if(eventoWrapper.getEvento() instanceof EventoRES){
				eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().removeProgrammaGiornalieroIntermedio(k);
				//return EDITRES + " :: " + sectionToRefresh;
				return EDITRES + " :: eventoRESEdit";
			} else {
				throw new Exception("Metodo chiamato dalla pagina errata aspettatto EventoRES.");
			}
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return "redirect:/home";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/addRisultatoAtteso/{sectionToRefresh}", method=RequestMethod.POST)
	public String addRisultatoAtteso(@PathVariable("sectionToRefresh") String sectionToRefresh,
			@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			if(eventoWrapper.getEvento() instanceof EventoRES || eventoWrapper.getEvento() instanceof EventoFAD){
				if(eventoWrapper.getRisultatiAttesiMapTemp() == null) {
					Map<Long, String> val = new LinkedHashMap<Long, String>();
					val.put(1L, null);
					eventoWrapper.setRisultatiAttesiMapTemp(val);
				} else {
					Long max = 1L;
					if(eventoWrapper.getRisultatiAttesiMapTemp().size() != 0)
						max = Collections.max(eventoWrapper.getRisultatiAttesiMapTemp().keySet()) + 1;
					eventoWrapper.getRisultatiAttesiMapTemp().put(max, null);
				}
				String evType = eventoWrapper.getEvento() instanceof EventoRES ? EDITRES : EDITFAD;
				return evType + " :: " + sectionToRefresh;
			} else {
				throw new Exception("Metodo chiamato dalla pagina errata aspettatto EventoRES o EventoFAD.");
			}
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return "redirect:/home";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/removeRisultatoAtteso/{key}/{sectionToRefresh}", method=RequestMethod.POST)
	public String removeRisultatoAtteso(@PathVariable("key") String key, @PathVariable("sectionToRefresh") String sectionToRefresh,
			@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			Long k = Long.valueOf(key);
			if(eventoWrapper.getEvento() instanceof EventoRES || eventoWrapper.getEvento() instanceof EventoFAD){
				eventoWrapper.getRisultatiAttesiMapTemp().remove(k);
				String evType = eventoWrapper.getEvento() instanceof EventoRES ? EDITRES : EDITFAD;
				return evType + " :: " + sectionToRefresh;
			} else {
				throw new Exception("Metodo chiamato dalla pagina errata aspettatto EventoRES o EventoFAD.");
			}
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return "redirect:/home";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/evento/listaDocentiAttivita")
	@ResponseBody
	public List<PersonaEvento>getListaDocentiAttivitaRES(@PathVariable Long providerId, @ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		List<PersonaEvento> lista = new ArrayList<PersonaEvento>();
		if(eventoWrapper.getEvento() instanceof EventoRES || eventoWrapper.getEvento() instanceof EventoFAD){
			lista = eventoWrapper.getDocenti();
		}
		return lista;
	}

	@RequestMapping("/listaRuoliCoinvolti")
	@ResponseBody
	public List<RuoloFSCEnum>getListaRuoliCoinvolti(@RequestParam TipologiaEventoFSCEnum tipologiaEvento){
		if(tipologiaEvento != null)
			return tipologiaEvento.getRuoliCoinvolti();
		else
			return null;
	}

	@RequestMapping(value = "/provider/{providerId}/evento/addRuoloOreToTemp", method=RequestMethod.POST)
	public String addRuoloOreToTemp(@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, BindingResult result, Model model, RedirectAttributes redirectAttrs){
		try{
			eventoWrapper.getTempAttivitaFSC().getRuoli().add(new RuoloOreFSC(eventoWrapper.getTempRuoloOreFSC().getRuolo(), eventoWrapper.getTempRuoloOreFSC().getTempoDedicato()));
			return EDITFSC + " :: ruoloOreFSC";
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDITFSC + " :: ruoloOreFSC";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/removeRuoloOreToTemp/{rowIndex}", method=RequestMethod.GET)
	public String removeRuoloOreToTemp(@PathVariable("rowIndex") String rowIndex,
										@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			int index = Integer.valueOf(rowIndex).intValue();
			eventoWrapper.getTempAttivitaFSC().getRuoli().remove(index);
			return EDITFSC + " :: ruoloOreFSC";
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDITFSC + " :: ruoloOreFSC";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/modifica/{target}/{modificaElemento}", method=RequestMethod.GET)
	public String modificaPersona(@PathVariable("target") String target,
									@PathVariable("modificaElemento") Long modificaElemento,
												@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			if(target.equalsIgnoreCase("responsabiliScientifici")){
				PersonaEvento p = eventoWrapper.getResponsabiliScientifici().get(modificaElemento.intValue());
				eventoWrapper.setTempPersonaEvento(p);
				eventoWrapper.setCv(p.getAnagrafica().getCv());
				return EDIT + " :: #addPersonaTo";
			}else if(target.equalsIgnoreCase("docenti")){
				PersonaEvento p = eventoWrapper.getDocenti().get(modificaElemento.intValue());
				eventoWrapper.setTempPersonaEvento(p);
				eventoWrapper.setCv(p.getAnagrafica().getCv());
				return EDIT + " :: #addPersonaTo";
			}else if(target.equalsIgnoreCase("responsabileSegreteria")){
				PersonaFullEvento p = eventoWrapper.getEvento().getResponsabileSegreteria();
				eventoWrapper.setTempPersonaFullEvento(p);
				return EDIT + " :: #addPersonaFullTo";
			}

			return "redirect:/home";
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return "redirect:/home";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/modificaAttivita/{target}/{addAttivitaTo}/{modificaElemento}", method=RequestMethod.GET)
	public String modificaAttivita(@PathVariable("target") String target,
									@PathVariable("addAttivitaTo") String addAttivitaTo,
									@PathVariable("modificaElemento") Integer modificaElemento,
											@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			int programmaIndex = Integer.valueOf(addAttivitaTo).intValue();
			int elementoIndex = Integer.valueOf(modificaElemento).intValue();
			if(target.equalsIgnoreCase("attivitaRES")){
				Long programmaIndexLong = Long.valueOf(programmaIndex);
				DettaglioAttivitaRES attivita = eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().get(programmaIndexLong).getProgramma().getProgramma().get(elementoIndex);
				eventoWrapper.setTempAttivitaRES(attivita);
				return EDITRES + " :: #addAttivitaRES";
			}else if(target.equalsIgnoreCase("attivitaFSC")){
				AzioneRuoliEventoFSC azione = eventoWrapper.getProgrammaEventoFSC().get(programmaIndex).getAzioniRuoli().get(elementoIndex);
				eventoWrapper.setTempAttivitaFSC(azione);
				return EDITFSC + " :: #addAttivitaFSC";
			}else if(target.equalsIgnoreCase("attivitaFAD")){
				DettaglioAttivitaFAD attivitaFAD = eventoWrapper.getProgrammaEventoFAD().get(elementoIndex);
				eventoWrapper.setTempAttivitaFAD(attivitaFAD);
				return EDITFAD + " :: #addAttivitaFAD";
			}
			return "redirect:/home";
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return "redirect:/home";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/{eventoId}/paga", method=RequestMethod.GET)
	public String pagaEvento(@PathVariable("providerId") Long providerId, @PathVariable("eventoId") Long eventoId,
			 					HttpServletRequest request, Model model, RedirectAttributes redirectAttrs){
		try{
			String rootUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
			String url = engineeringService.pagaEvento(eventoId, rootUrl + request.getContextPath() + "/provider/" + providerId + "/evento/list");

			if (StringUtils.hasText(url)) {
				return "redirect:" + url;
			}

			return "redirect:/provider/{providerId}/evento/list";
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return "redirect:/provider/{providerId}/evento/list";
		}
	}

	@RequestMapping("/provider/eventi/{search}/list")
	public String getAllEventiByProviderIdForGruppo(@PathVariable("search") EventoSearchEnum search, Model model,
			RedirectAttributes redirectAttrs) throws Exception {
		LOGGER.info(Utils.getLogMessage("GET /provider/eventi/" + search + "/list"));
		try {

			Set<Evento> listaEventi = new HashSet<Evento>();
			CurrentUser currentUser = Utils.getAuthenticatedUser();

			switch(search){
				case SCADENZA_PAGAMENTO : 	listaEventi = eventoService.getEventiForProviderIdInScadenzaDiPagamento(currentUser.getAccount().getProvider().getId());
											break;

				case NON_RAPPORTATI : listaEventi = eventoService.getEventiForProviderIdPagamentoScaduti(currentUser.getAccount().getProvider().getId());
										break;

				default: break;
			}

			model.addAttribute("label", search.getNome());
			model.addAttribute("eventoList", listaEventi);
			model.addAttribute("canCreateEvento", false);
			LOGGER.info(Utils.getLogMessage("VIEW: accreditamento/accreditamentoList"));
			return LIST;
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/eventi/" + search + "/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

}
