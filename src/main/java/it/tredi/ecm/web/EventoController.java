package it.tredi.ecm.web;

import java.util.LinkedList;
import java.util.Set;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.Obiettivo;
import it.tredi.ecm.dao.entity.PianoFormativo;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.CategoriaObiettivoNazionale;
import it.tredi.ecm.dao.enumlist.Costanti;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.ObiettivoService;
import it.tredi.ecm.service.PianoFormativoService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EventoWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.validator.EventoValidator;

@Controller
public class EventoController {
	public static final Logger LOGGER = LoggerFactory.getLogger(EventoController.class);
	private final String EDIT = "evento/eventoEdit";

	@Autowired private EventoService eventoService;
	@Autowired private ProviderService providerService;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private ObiettivoService obietivoService;
	@Autowired private PianoFormativoService pianoFormativoService;
	@Autowired private EventoValidator eventoValidator;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("categoriaObiettivoNazionaleList")
	public CategoriaObiettivoNazionale[] getCategoriaObiettivoNazionaleList(){
		return CategoriaObiettivoNazionale.values();
	}


	@ModelAttribute("obiettivoNazionaleList")
	public Set<Obiettivo> getObiettiviNazionali(){
		return obietivoService.getObiettiviNazionali();
	}

	@ModelAttribute("obiettivoRegionaleList")
	public Set<Obiettivo> getObiettiviRegionali(){
		return obietivoService.getObiettiviRegionali();
	}

	@ModelAttribute("eventoWrapper")
	public EventoWrapper getEventoWrapper(@RequestParam(value="editId",required = false) Long id){
		if(id != null){
			return prepareEventoWrapperEdit(eventoService.getEvento(id));
		}
		return new EventoWrapper();
	}

	/*
	 * INSERIMENTO EVENTO IN PIANO FORMATIVO (accreditamento)
	 * */
	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/pianoFormativo/{pianoFormativoId}/evento/new")
	public String newEvento(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long pianoFormativoId,
							Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/new"));
		try{
			return goToEdit(model, prepareEventoWrapperEdit(new Evento(), providerId, accreditamentoId, pianoFormativoId), redirectAttrs);
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/new"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	/*
	 * MODIFICA EVENTO IN PIANO FORMATIVO (accreditamento)
	 * */
	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/pianoFormativo/{pianoFormativoId}/evento/{id}/edit")
	public String editEvento(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long pianoFormativoId, @PathVariable Long id,
			Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/edit"));
		try{
			return goToEdit(model, prepareEventoWrapperEdit(eventoService.getEvento(id),0L,accreditamentoId,pianoFormativoId),redirectAttrs);
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/edit"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	/*
	 * SHOW EVENTO IN PIANO FORMATIVO (accreditamento)
	 */
	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/{providerId}/pianoFormativo/{pianoFormativoId}/evento/{id}/show")
	public String showEventoAccreditamento(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long pianoFormativoId, @PathVariable Long id,
												Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/show"));
		try {
			return goToShow(model, prepareEventoWrapperShow(eventoService.getEvento(id), providerId, accreditamentoId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/show"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/show"));
			return "redirect:/accreditamento/" + accreditamentoId + "/show";
		}
	}

	/*
	 * SALVATAGGIO EVENTO IN PIANO FORMATIVO (accreditamento)
	 * */
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/{providerId}/pianoFormativo/{pianoFormativoId}/evento/save", method=RequestMethod.POST)
	public String saveEvento(@ModelAttribute("eventoWrapper") EventoWrapper wrapper, BindingResult result,
			Model model, RedirectAttributes redirectAttrs, @PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long pianoFormativoId){
		LOGGER.info(Utils.getLogMessage("POST /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/save"));
		try{
			if(wrapper.getEvento().isNew()){
				Evento evento = wrapper.getEvento();
				evento.setProvider(providerService.getProvider(wrapper.getProviderId()));
				evento.setAccreditamento(accreditamentoService.getAccreditamento(wrapper.getAccreditamentoId()));
			}

			eventoValidator.validate(wrapper.getEvento(), result, "evento.", true);

			if(result.hasErrors()){
				model.addAttribute("message", new Message("message.errore", "message.inserire_campi_required", "error"));
				populateListFromAccreditamento(model, wrapper.getAccreditamentoId());
				LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
				return EDIT;
			}else{
				eventoService.save(wrapper.getEvento());
				PianoFormativo pianoFormativo = pianoFormativoService.getPianoFormativo(pianoFormativoId);
				pianoFormativo.addEvento(wrapper.getEvento());
				pianoFormativoService.save(pianoFormativo);
				redirectAttrs.addAttribute("accreditamentoId", wrapper.getAccreditamentoId());
				redirectAttrs.addAttribute("providerId", wrapper.getProviderId());
				redirectAttrs.addAttribute("pianoFormativo", wrapper.getEvento().getPianoFormativo());
				redirectAttrs.addFlashAttribute("currentTab", "tab4");
				LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/edit"));
				return "redirect:/accreditamento/{accreditamentoId}/edit";
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("POST /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/evento/save"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
			return EDIT;
		}
	}


	/*
	 * ELIMINAZIONE DI UN EVENTO IN PIANO FORMATIVO (accreditamento)
	 * */
	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/pianoFormativo/{pianoFormativoId}/evento/{id}/delete")
	public String removeEvento(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long pianoFormativoId, @PathVariable Long id,
			Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/delete"));
		try{
			PianoFormativo pianoFormativo = pianoFormativoService.getPianoFormativo(pianoFormativoId);
			pianoFormativo.removeEvento(id);
			pianoFormativoService.save(pianoFormativo);
			eventoService.delete(id);
			redirectAttrs.addFlashAttribute("currentTab","tab4");
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/edit"));
			return "redirect:/accreditamento/{accreditamentoId}/edit";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/evento/" + id + "/delete"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}


	//TODO domenico (check se fa la query di tutto il provider)
	/*** LIST EVENTO ***/
	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/evento/list")
	public String listPersona(@PathVariable Long providerId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/evento/list"));
		try {
			Provider provider = providerService.getProvider(providerId);
			model.addAttribute("eventoList", eventoService.getAllEventiFromProvider(providerId));
			model.addAttribute("titolo", provider.getDenominazioneLegale());
			LOGGER.info(Utils.getLogMessage("VIEW: evento/eventoList"));
			return "evento/eventoList";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/show"));
			return "redirect:/provider/show";
		}
	}

	/*
	 * SHOW EVENTO PROVIDER
	 */
	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping(value = "/provider/{providerId}/evento/{id}/show")
	public String showEvento(@PathVariable Long providerId, @PathVariable Long id, Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + id + "/show"));
		try {
			return goToShow(model, prepareEventoWrapperShow(eventoService.getEvento(id), providerId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + id + "/show"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/evento/list"));
			return "redirect:/provider/" + providerId + "/evento/list";
		}
	}

	private String goToShow(Model model, EventoWrapper wrapper) {
		model.addAttribute("eventoWrapper", wrapper);
		model.addAttribute("proceduraFormativaList", wrapper.getEvento().getAccreditamento().getDatiAccreditamento().getProcedureFormative());
		LOGGER.info(Utils.getLogMessage("VIEW: evento/eventoShow"));
		return "evento/eventoShow";
	}

	private EventoWrapper prepareEventoWrapperShow(Evento evento, Long providerId){
		return prepareEventoWrapperShow(evento, providerId, null);
	}

	private EventoWrapper prepareEventoWrapperShow(Evento evento, Long providerId, Long accreditamentoId) {
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperShow(" + evento.getId() + "," + providerId + ") - entering"));
		EventoWrapper eventoWrapper = new EventoWrapper();
		eventoWrapper.setEvento(evento);
		eventoWrapper.setProviderId(providerId);
		eventoWrapper.setAccreditamentoId(accreditamentoId);
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperShow(" + evento.getId() + "," + providerId + ") - exiting"));
		return eventoWrapper;
	}





	private String goToEdit(Model model, EventoWrapper wrapper, RedirectAttributes redirectAttrs){
		try {
			model.addAttribute("eventoWrapper", wrapper);
			populateListFromAccreditamento(model, wrapper.getAccreditamentoId());
			LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
			return EDIT;
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("goToEdit"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	private void populateListFromAccreditamento(Model model, Long accreditamentoId) throws Exception{
		DatiAccreditamento datiAccreditamento = accreditamentoService.getDatiAccreditamentoForAccreditamento(accreditamentoId);
		model.addAttribute("proceduraFormativaList", datiAccreditamento.getProcedureFormative());
		model.addAttribute("professioneList", datiAccreditamento.getProfessioniSelezionate());
		model.addAttribute("disciplinaList", datiAccreditamento.getDiscipline());
	}

	//utilizzato nel caso di save
	private EventoWrapper prepareEventoWrapperEdit(Evento evento){
		return prepareEventoWrapperEdit(evento, 0L, 0L, 0L);
	}

	//utilizzato nel caso di edit e new
	private EventoWrapper prepareEventoWrapperEdit(Evento evento, long providerId, long accreditamentoId, long pianoFormativoId){
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperEdit(" + evento.getId() + "," + providerId + "," + accreditamentoId + "," + pianoFormativoId + ") - entering"));
		EventoWrapper wrapper = new EventoWrapper();
		wrapper.setEvento(evento);

		if(evento.isNew()){
			wrapper.setProviderId(providerId);
		}else{
			wrapper.setProviderId(evento.getProvider().getId());
		}

		if(accreditamentoId != 0){
			wrapper.setAccreditamentoId(accreditamentoId);
			wrapper.setOffsetAndIds(new LinkedList<Integer>(Costanti.IDS_EVENTO_PIANO_FORMATIVO), evento.getIdEditabili());
		}
		else{
			wrapper.setAccreditamentoId(evento.getAccreditamento().getId());
			wrapper.setOffsetAndIds(new LinkedList<Integer>(Costanti.IDS_EVENTO), evento.getIdEditabili());
		}

		if(pianoFormativoId != 0){
			wrapper.setPianoFormativoId(pianoFormativoId);
		}

		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperEdit(" + evento.getId() + "," + providerId + "," + accreditamentoId + "," + pianoFormativoId + ") - exiting"));
		return wrapper;
	}

}
