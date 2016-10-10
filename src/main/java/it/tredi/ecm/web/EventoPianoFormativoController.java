package it.tredi.ecm.web;

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
import it.tredi.ecm.dao.entity.EventoPianoFormativo;
import it.tredi.ecm.dao.entity.Obiettivo;
import it.tredi.ecm.dao.entity.PianoFormativo;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.CategoriaObiettivoNazionale;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.exception.AccreditamentoNotFoundException;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.EventoPianoFormativoService;
import it.tredi.ecm.service.FieldEditabileAccreditamentoService;
import it.tredi.ecm.service.FieldValutazioneAccreditamentoService;
import it.tredi.ecm.service.IntegrazioneService;
import it.tredi.ecm.service.ObiettivoService;
import it.tredi.ecm.service.PianoFormativoService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.ValutazioneService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EventoPianoFormativoWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.RichiestaIntegrazioneWrapper;
import it.tredi.ecm.web.validator.EventoPianoFormativoValidator;
import it.tredi.ecm.web.validator.ValutazioneValidator;

@Controller
public class EventoPianoFormativoController {
	public static final Logger LOGGER = LoggerFactory.getLogger(EventoPianoFormativoController.class);
	private final String EDIT = "eventoPianoFormativo/eventoPianoFormativoEdit";
	private final String SHOW = "eventoPianoFormativo/eventoPianoFormativoShow";
	private final String VALIDATE  = "eventoPianoFormativo/eventoPianoFormativoValidate";
	private final String ENABLEFIELD  = "eventoPianoFormativo/eventoPianoFormativoEnableField";
	private final String RENDICONTO = "eventoPianoFormativo/eventoPianoFormativoRendiconto";

	@Autowired private EventoPianoFormativoService eventoService;
	@Autowired private ProviderService providerService;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private ObiettivoService obietivoService;
	@Autowired private PianoFormativoService pianoFormativoService;
	@Autowired private FieldEditabileAccreditamentoService fieldEditabileService;
	@Autowired private EventoPianoFormativoValidator eventoValidator;
	@Autowired private ValutazioneValidator valutazioneValidator;
	@Autowired private FieldValutazioneAccreditamentoService fieldValutazioneAccreditamentoService;
	@Autowired private ValutazioneService valutazioneService;
	@Autowired private IntegrazioneService integrazioneService;


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
	public EventoPianoFormativoWrapper getEventoWrapper(@RequestParam(value="editId",required = false) Long id, @RequestParam(value="eventoFrom",required = false) String from){
		if(id != null){
			return prepareEventoWrapperEdit(eventoService.getEvento(id), from);
		}
		return new EventoPianoFormativoWrapper();
	}

	/*
	 * INSERIMENTO EVENTO IN PIANO FORMATIVO (accreditamento)
	 * */
	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/pianoFormativo/{pianoFormativoId}/evento/new")
	public String newEventoAccreditamento(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long pianoFormativoId,
							Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/new"));
		try{
			model.addAttribute("returnLink", "/accreditamento/"+ accreditamentoId + "/edit?tab=tab4");
			model.addAttribute("fromAccreditamento", true);
			return goToEdit(model, prepareEventoAccreditamentoWrapperEdit(new EventoPianoFormativo(), providerId, accreditamentoId, pianoFormativoId), redirectAttrs);
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/new"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	/*
	 * INSERIMENTO EVENTO IN PIANO FORMATIVO (piano formativo)
	 */
	@PreAuthorize("@securityAccessServiceImpl.canEditPianoFormativo(principal,#pianoFormativoId)")
	@RequestMapping("/provider/{providerId}/pianoFormativo/{pianoFormativoId}/evento/new")
	public String newEventoPianoFormativo(@PathVariable Long providerId, @PathVariable Long pianoFormativoId,
			Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/new"));
		try{
			model.addAttribute("returnLink", "/provider/" + providerId + "/pianoFormativo/list?accordion=" + pianoFormativoService.getPianoFormativo(pianoFormativoId).getAnnoPianoFormativo());
			model.addAttribute("fromAccreditamento", false);
			Long accreditamentoId = accreditamentoService.getAccreditamentoAttivoForProvider(providerId).getId();
			return goToEdit(model, prepareEventoPianoFormativoWrapperEdit(new EventoPianoFormativo(), providerId, accreditamentoId, pianoFormativoId), redirectAttrs);
		}
		catch (AccreditamentoNotFoundException ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/new"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.accreditamento_not_trovato", "error"));
			redirectAttrs.addAttribute("providerId", providerId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/pianoFormativo/list"));
			return "redirect:/provider/{providerId}/pianoFormativo/list";
		}
		catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/new"),ex);
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
	public String editEventoAccreditamento(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long pianoFormativoId, @PathVariable Long id,
			Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/edit"));
		try{
			model.addAttribute("returnLink", "/accreditamento/"+ accreditamentoId + "/edit?tab=tab4");
			model.addAttribute("fromAccreditamento", true);
			return goToEdit(model, prepareEventoAccreditamentoWrapperEdit(eventoService.getEvento(id),0L,accreditamentoId,pianoFormativoId),redirectAttrs);
		}
		catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/edit"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	/*
	 * MODIFICA EVENTO IN PIANO FORMATIVO (piano formativo)
	 * */
	@PreAuthorize("@securityAccessServiceImpl.canEditPianoFormativo(principal,#pianoFormativoId)")
	@RequestMapping("/provider/{providerId}/pianoFormativo/{pianoFormativoId}/evento/{id}/edit")
	public String editEventoPianoFormativo(@PathVariable Long providerId, @PathVariable Long pianoFormativoId, @PathVariable Long id,
			Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/edit"));
		try{
			model.addAttribute("returnLink", "/provider/" + providerId + "/pianoFormativo/list?accordion=" + pianoFormativoService.getPianoFormativo(pianoFormativoId).getAnnoPianoFormativo());
			model.addAttribute("fromAccreditamento", false);
			Long accreditamentoId = accreditamentoService.getAccreditamentoAttivoForProvider(providerId).getId();
			return goToEdit(model, prepareEventoPianoFormativoWrapperEdit(eventoService.getEvento(id),0L,accreditamentoId,pianoFormativoId),redirectAttrs);
		}
		catch (AccreditamentoNotFoundException ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/edit"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.accreditamento_not_trovato", "error"));
			redirectAttrs.addAttribute("providerId", providerId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/pianoFormativo/list"));
			return "redirect:/provider/{providerId}/pianoFormativo/list";
		}
		catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/edit"),ex);
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
			@RequestParam(required = false) String from, Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/show"));
		try {
			//parametro per decidere in che modalità di visualizzazione dell'accreditamento dopo un eventoShow
			if(from != null) {
				redirectAttrs.addFlashAttribute("mode", from);
				return "redirect:/accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/show";
			}
			LOGGER.info(Utils.getLogMessage("MODE: " + from));
			String mode = (String) model.asMap().get("mode");
			if(mode != null) {
				model.addAttribute("returnLink", "/accreditamento/" + accreditamentoId + "/" + mode + "?tab=tab4");
			}else {
				model.addAttribute("returnLink", "/accreditamento/" + accreditamentoId + "/show?tab=tab4");
			}
			return goToShow(model, prepareEventoWrapperShow(eventoService.getEvento(id), providerId, accreditamentoId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/show"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/show"));
			return "redirect:/accreditamento/" + accreditamentoId + "/show";
		}
	}

	/*
	 * SHOW EVENTO IN PIANO FORMATIVO (piano formativo)
	 */
	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping(value = "/provider/{providerId}/pianoFormativo/{pianoFormativoId}/evento/{id}/show")
	public String showEventoPianoFormativo(@PathVariable Long providerId, @PathVariable Long pianoFormativoId, @PathVariable Long id,
			@RequestParam(required = false) String from, Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/show"));
		try {
			//parametro per decidere se tornare in edit o in show accreditamento dopo un eventoShow
			if(from != null) {
				redirectAttrs.addFlashAttribute("mode", from);
				return "redirect:/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/show";
			}
			LOGGER.info(Utils.getLogMessage("MODE: " + from));

			//per il momento non c'è nessuna distinzione a seconda del from (tuttavia in thymeleaf è comodo portarselo dietro)
			model.addAttribute("returnLink", "/provider/" + providerId + "/pianoFormativo/list?accordion=" + pianoFormativoService.getPianoFormativo(pianoFormativoId).getAnnoPianoFormativo());

			return goToShow(model, prepareEventoWrapperShow(eventoService.getEvento(id), providerId));
		}
//		catch (AccreditamentoNotFoundException ex){
//			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/show"),ex);
//			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.accreditamento_not_trovato", "error"));
//			redirectAttrs.addAttribute("providerId", providerId);
//			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/pianoFormativo/list"));
//			return "redirect:/provider/{providerId}/pianoFormativo/list";
//		}
		catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/show"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	/*
	 * VALUTAZIONE EVENTO IN PIANO FORMATIVO (accreditamento)
	 */
	//TODO rimuovere se confermato che non serve più
//	@PreAuthorize("@securityAccessServiceImpl.canValidateAccreditamento(principal,#accreditamentoId)")
//	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/{providerId}/pianoFormativo/{pianoFormativoId}/evento/{id}/validate")
//	public String validateEventoAccreditamento(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long pianoFormativoId, @PathVariable Long id,
//			Model model, RedirectAttributes redirectAttrs) {
//		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/validate"));
//		try {
//			//controllo se è possibile modificare la valutazione o meno
//			model.addAttribute("canValutaDomanda", accreditamentoService.canUserValutaDomanda(accreditamentoId, Utils.getAuthenticatedUser()));
//			//tengo traccia di dove ritornare
//			model.addAttribute("returnLink", "/accreditamento/" + accreditamentoId + "/validate?tab=tab4");
//			return goToValidate(model, prepareEventoWrapperValidate(eventoService.getEvento(id), providerId, accreditamentoId, pianoFormativoId));
//		}catch (Exception ex){
//			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/validate"),ex);
//			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
//			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/validate"));
//			return "redirect:/accreditamento/" + accreditamentoId + "/validate";
//		}
//	}

	/*
	 * ENABLE FIELD EVENTO IN PIANO FORMATIVO (accreditamento)
	 */
	@PreAuthorize("@securityAccessServiceImpl.canEnableField(principal,#accreditamentoId)")
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/{providerId}/pianoFormativo/{pianoFormativoId}/evento/{id}/enableField")
	public String enableFieldEventoAccreditamento(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long pianoFormativoId, @PathVariable Long id,
			Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/enableField"));
		try {
			//tengo traccia di dove ritornare
			model.addAttribute("returnLink", "/accreditamento/" + accreditamentoId + "/enableField?tab=tab4");
			return goToEnableField(model, prepareEventoWrapperEnableField(eventoService.getEvento(id), providerId, accreditamentoId, pianoFormativoId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/enableField"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/enableField"));
			return "redirect:/accreditamento/" + accreditamentoId + "/enableField";
		}
	}

	/***	SAVE  VALUTAZIONE EVENTO
	 * @throws Exception ***/
	//TODO rimuovere se confermato che non serve più
//	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/{providerId}/pianoFormativo/{pianoFormativoId}/evento/validate", method=RequestMethod.POST)
//	public String valutaEventoAccreditamento(@ModelAttribute("eventoWrapper") EventoPianoFormativoWrapper wrapper, BindingResult result,
//			Model model, RedirectAttributes redirectAttrs, @PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long pianoFormativoId) throws Exception{
//		LOGGER.info(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/validate"));
//		try {
//			//validazione della persona
//			valutazioneValidator.validateValutazione(wrapper.getMappa(), result);
//
//			if(result.hasErrors()){
//				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
//				model.addAttribute("returnLink", "/accreditamento/" + accreditamentoId + "/validate?tab=tab4");
//				model.addAttribute("canValutaDomanda", accreditamentoService.canUserValutaDomanda(accreditamentoId, Utils.getAuthenticatedUser()));
//				LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
//				return VALIDATE;
//			}else{
//				Accreditamento accreditamento = new Accreditamento();
//				accreditamento.setId(wrapper.getAccreditamentoId());
//				wrapper.getMappa().forEach((k, v) -> {
//					v.setIdField(k);
//					v.setAccreditamento(accreditamento);
//					v.setObjectReference(wrapper.getEvento().getId());
//				});
//
//				Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountId(accreditamento.getId(), Utils.getAuthenticatedUser().getAccount().getId());
//				Set<FieldValutazioneAccreditamento> values = new HashSet<FieldValutazioneAccreditamento>(fieldValutazioneAccreditamentoService.saveMapList(wrapper.getMappa()));
//				valutazione.getValutazioni().addAll(values);
//				valutazioneService.save(valutazione);
//
//				redirectAttrs.addAttribute("accreditamentoId", wrapper.getAccreditamentoId());
//				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.valutazione_salvata", "success"));
//				redirectAttrs.addFlashAttribute("currentTab","tab4");
//				LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/validate"));
//				return "redirect:/accreditamento/{accreditamentoId}/validate";
//			}
//		}catch (Exception ex){
//			LOGGER.error(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId +"/provider/"+ providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/validate"),ex);
//			model.addAttribute("accreditamentoId",wrapper.getAccreditamentoId());
//			model.addAttribute("message",new Message("message.errore", "message.errore_eccezione", "error"));
//			model.addAttribute("returnLink", "/accreditamento/" + accreditamentoId + "/validate?tab=tab4");
//			model.addAttribute("canValutaDomanda", accreditamentoService.canUserValutaDomanda(accreditamentoId, Utils.getAuthenticatedUser()));
//			LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
//			return VALIDATE;
//		}
//	}

	/*
	 * SALVATAGGIO EVENTO IN PIANO FORMATIVO (accreditamento)
	 * */
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/{providerId}/pianoFormativo/{pianoFormativoId}/evento/save", method=RequestMethod.POST)
	public String saveEventoAccreditamento(@ModelAttribute("eventoWrapper") EventoPianoFormativoWrapper wrapper, BindingResult result,
			Model model, RedirectAttributes redirectAttrs, @PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long pianoFormativoId){
		LOGGER.info(Utils.getLogMessage("POST /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/save"));
		return saveEvento(wrapper, accreditamentoId, providerId, pianoFormativoId, result, model, redirectAttrs);
	}

	/*
	 * SALVATAGGIO EVENTO IN PIANO FORMATIVO (piano formativo)
	 * */
	@RequestMapping(value = "/provider/{providerId}/pianoFormativo/{pianoFormativoId}/evento/save", method=RequestMethod.POST)
	public String saveEventoPianoFormativo(@ModelAttribute("eventoWrapper") EventoPianoFormativoWrapper wrapper, BindingResult result,
			Model model, RedirectAttributes redirectAttrs, @PathVariable Long providerId, @PathVariable Long pianoFormativoId){
		LOGGER.info(Utils.getLogMessage("POST /provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/save"));
		return saveEvento(wrapper, null, providerId, pianoFormativoId, result, model, redirectAttrs);
	}

	/*** 	SAVE  ENABLEFIELD   ***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/{providerId}/pianoFormativo/{pianoFormativoId}/evento/enableField", method = RequestMethod.POST)
	public String enableFieldAllegatiAccreditamento(@ModelAttribute("richiestaIntegrazioneWrapper") RichiestaIntegrazioneWrapper richiestaIntegrazioneWrapper, @PathVariable Long accreditamentoId,
												Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("POST /accreditamento/" + accreditamentoId + "/provider/{providerId}/pianoFormativo/{pianoFormativoId}/evento/" + richiestaIntegrazioneWrapper.getObjRef() + "/enableField"));
		try{
			integrazioneService.saveEnableField(richiestaIntegrazioneWrapper);
			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.campi_salvati", "success"));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("POST /accreditamento/" + accreditamentoId + "/provider/{providerId}/pianoFormativo/{pianoFormativoId}/evento/" + richiestaIntegrazioneWrapper.getObjRef() + "/enableField"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/enableField"));
		}
		redirectAttrs.addFlashAttribute("currentTab","tab4");
		return "redirect:/accreditamento/{accreditamentoId}/enableField";
	};

	//Logica in comune tra i salvataggi dell'evento
	private String saveEvento (EventoPianoFormativoWrapper wrapper, Long accreditamentoId, Long providerId, Long pianoFormativoId, BindingResult result, Model model, RedirectAttributes redirectAttrs) {
		try{
			if(wrapper.getEvento().isNew()){
				EventoPianoFormativo evento = wrapper.getEvento();
				evento.setProvider(providerService.getProvider(wrapper.getProviderId()));
				evento.setAccreditamento(accreditamentoService.getAccreditamento(wrapper.getAccreditamentoId()));
			}

			eventoValidator.validate(wrapper.getEvento(), result, "evento.", true);

			if(result.hasErrors()){
				// caso fromAccreditamento
				if (accreditamentoId != null) {
					model.addAttribute("returnLink", "/accreditamento/"+ accreditamentoId + "/edit?tab=tab4");
					model.addAttribute("fromAccreditamento", true);
				}
				// caso inserimento evento dal pianoFormativo
				else {
					model.addAttribute("returnLink", "/provider/" + providerId + "/pianoFormativo/list?accordion=" + pianoFormativoService.getPianoFormativo(pianoFormativoId).getAnnoPianoFormativo());
					model.addAttribute("fromAccreditamento", false);
				}
				model.addAttribute("message", new Message("message.errore", "message.inserire_campi_required", "error"));
				populateListFromAccreditamento(model, wrapper.getAccreditamentoId());
				LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
				return EDIT;
			}else{
				boolean insertFieldEditabile = (wrapper.getEvento().isNew()) ? true : false;
				//non inserisce i field editabili se è la segreteria a fare l'inserimento in uno stato dell'accreditamento che non sia Bozza
				insertFieldEditabile = (Utils.getAuthenticatedUser().getAccount().isSegreteria() && !accreditamentoService.getAccreditamento(wrapper.getAccreditamentoId()).isBozza()) ? false : true;
				eventoService.save(wrapper.getEvento());
				if(wrapper.getEvento().isNew())
					eventoService.buildPrefix(wrapper.getEvento());
				PianoFormativo pianoFormativo = pianoFormativoService.getPianoFormativo(pianoFormativoId);
				pianoFormativo.addEvento(wrapper.getEvento());
				pianoFormativoService.save(pianoFormativo);
				// caso fromAccreditamento
				if (accreditamentoId != null) {
					//inserimento nuovo evento multi-istanza in domanda di Accreditamento, inserisco FieldEditabileAccreditamento
					if(insertFieldEditabile)
						fieldEditabileService.insertFieldEditabileForAccreditamento(accreditamentoId, wrapper.getEvento().getId(), SubSetFieldEnum.EVENTO_PIANO_FORMATIVO, IdFieldEnum.getAllForSubset(SubSetFieldEnum.EVENTO_PIANO_FORMATIVO));

					redirectAttrs.addFlashAttribute("currentTab", "tab4");
					LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/edit"));
					redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.evento_salvato", "success"));
					return "redirect:/accreditamento/{accreditamentoId}/edit";
				}
				// caso inserimento evento dal pianoFormativo
				else {
					redirectAttrs.addFlashAttribute("accordion", pianoFormativo.getAnnoPianoFormativo());
					LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/pianoFormativo/list"));
					redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.evento_salvato", "success"));
					return "redirect:/provider/" + providerId + "/pianoFormativo/list";
				}

			}
		}
		catch (Exception ex){
			if (accreditamentoId != null)
				LOGGER.error(Utils.getLogMessage("POST /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/save"),ex);
			else
				LOGGER.error(Utils.getLogMessage("POST /provider/" + providerId + "/pianoFormativo/"+ pianoFormativoId + "/evento/save"),ex);
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
	public String removeEventoAccreditamento(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Long pianoFormativoId, @PathVariable Long id,
			Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/delete"));
		return removeEvento(accreditamentoId, providerId, pianoFormativoId, id, redirectAttrs);
	}

	/*
	 * ELIMINAZIONE DI UN EVENTO IN PIANO FORMATIVO (piano formativo)
	 * */
	@PreAuthorize("@securityAccessServiceImpl.canEditPianoFormativo(principal,#pianoFormativoId)")
	@RequestMapping("/provider/{providerId}/pianoFormativo/{pianoFormativoId}/evento/{id}/delete")
	public String removeEventoPianoFormativo(@PathVariable Long providerId, @PathVariable Long pianoFormativoId, @PathVariable Long id,
			Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/pianoFormativo/" + pianoFormativoId + "/evento/" + id + "/delete"));
		return removeEvento(null, providerId, pianoFormativoId, id, redirectAttrs);
	}

	private String removeEvento(Long accreditamentoId, Long providerId, Long pianoFormativoId, Long eventoId, RedirectAttributes redirectAttrs) {
		try{
			PianoFormativo pianoFormativo = pianoFormativoService.getPianoFormativo(pianoFormativoId);
			pianoFormativo.removeEvento(eventoId);
			pianoFormativoService.save(pianoFormativo);
			eventoService.delete(eventoId);
			// caso fromAccreditamento
			if (accreditamentoId != null) {
				//eliminazione evento multi-istanza da Domanda di accreditamento, rimuovo FieldEditabileAccreditamento
				fieldEditabileService.removeFieldEditabileForAccreditamento(accreditamentoId, eventoId, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO);
				redirectAttrs.addFlashAttribute("currentTab","tab4");
				LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/edit"));
				return "redirect:/accreditamento/{accreditamentoId}/edit";
			}
			// caso inserimento evento dal pianoFormativo
			else {
				redirectAttrs.addFlashAttribute("accordion", pianoFormativo.getAnnoPianoFormativo());
				LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/pianoFormativo/list"));
				return "redirect:/provider/" + providerId + "/pianoFormativo/list";
			}
		}
		catch (Exception ex){
			if (accreditamentoId != null)
				LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/provider/" + providerId + "/evento/" + eventoId + "/delete"),ex);
			else
				LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId + "/delete"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
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
			model.addAttribute("returnLink", "/provider/"+ providerId + "/evento/list");
			return goToShow(model, prepareEventoWrapperShow(eventoService.getEvento(id), providerId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + id + "/show"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/evento/list"));
			return "redirect:/provider/" + providerId + "/evento/list";
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
				@PathVariable Long eventoId, @ModelAttribute("eventoWrapper") EventoPianoFormativoWrapper wrapper, BindingResult result,
				Model model, RedirectAttributes redirectAttrs) {
			try{
				LOGGER.info(Utils.getLogMessage("POST /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/validate"));
				model.addAttribute("returnLink", "/provider/" + providerId + "/evento/list");
				if(wrapper.getReportPartecipanti().getId() == null)
					model.addAttribute("message", new Message("message.errore", "message.inserire_il_rendiconto", "error"));
				else {
					LOGGER.info(Utils.getLogMessage("Ricevuto File id: " + wrapper.getReportPartecipanti().getId() + " da validare"));
//TODO				eventoService.validaRendiconto(wrapper.getReportPartecipanti());
				}
				return goToRendiconto(model, prepareEventoWrapperRendiconto(eventoService.getEvento(eventoId), providerId));
			}
			catch (Exception ex) {
				LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/validate"),ex);
				redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/validate"));
				return "redirect:/provider/{providerId}/evento/{eventoId}/rendiconto/validate";
			}
		}

	private String goToShow(Model model, EventoPianoFormativoWrapper wrapper) {
		model.addAttribute("eventoWrapper", wrapper);
		model.addAttribute("proceduraFormativaList", wrapper.getEvento().getAccreditamento().getDatiAccreditamento().getProcedureFormative());
		LOGGER.info(Utils.getLogMessage("VIEW: " + SHOW));
		return SHOW;
	}

	private EventoPianoFormativoWrapper prepareEventoWrapperShow(EventoPianoFormativo evento, long providerId){
		return prepareEventoWrapperShow(evento, providerId, 0);
	}

	private EventoPianoFormativoWrapper prepareEventoWrapperShow(EventoPianoFormativo evento, long providerId, long accreditamentoId) {
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperShow(" + evento.getId() + "," + providerId + ") - entering"));
		EventoPianoFormativoWrapper eventoWrapper = new EventoPianoFormativoWrapper();
		eventoWrapper.setEvento(evento);
		eventoWrapper.setProviderId(providerId);
		if (accreditamentoId != 0)
			eventoWrapper.setAccreditamentoId(accreditamentoId);
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperShow(" + evento.getId() + "," + providerId + ") - exiting"));
		return eventoWrapper;
	}

	//TODO rimuovere se non serve più
//	private EventoPianoFormativoWrapper prepareEventoWrapperValidate(EventoPianoFormativo evento, long providerId, long accreditamentoId, long pianoFormativoId) {
//		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperValidate(" + evento.getId() + "," + providerId + ") - entering"));
//		EventoPianoFormativoWrapper eventoWrapper = new EventoPianoFormativoWrapper();
//		eventoWrapper.setEvento(evento);
//		eventoWrapper.setProviderId(providerId);
//		eventoWrapper.setAccreditamentoId(accreditamentoId);
//		eventoWrapper.setPianoFormativoId(pianoFormativoId);
//		eventoWrapper.setEventoFrom("accreditamento");
//
//		//carico la valutazione per l'utente
//		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountId(accreditamentoId, Utils.getAuthenticatedUser().getAccount().getId());
//		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = new HashMap<IdFieldEnum, FieldValutazioneAccreditamento>();
//
//		//cerco tutte le valutazioni dell'oggetto evento per ciascun valutatore dell'accreditamento
//		Map<Account, Map<IdFieldEnum, FieldValutazioneAccreditamento>> mappaValutatoreValutazioni = new HashMap<Account, Map<IdFieldEnum, FieldValutazioneAccreditamento>>();
//
//		//prendo tutti gli id del subset
//		Set<IdFieldEnum> idEditabili = new HashSet<IdFieldEnum>();
//
//		//per distinguere il multistanza degli eventi
//		if(valutazione != null) {
//			mappa = fieldValutazioneAccreditamentoService.filterFieldValutazioneByObjectAsMap(valutazione.getValutazioni(), evento.getId());
//			mappaValutatoreValutazioni = valutazioneService.getMapValutatoreValutazioniByAccreditamentoIdAndObjectId(accreditamentoId, evento.getId());
//			idEditabili = IdFieldEnum.getAllForSubset(SubSetFieldEnum.EVENTO_PIANO_FORMATIVO);
//		}
//
//		eventoWrapper.setMappaValutatoreValutazioni(mappaValutatoreValutazioni);
//		eventoWrapper.setIdEditabili(idEditabili);
//		eventoWrapper.setMappa(mappa);
//
//		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperValidate(" + evento.getId() + "," + providerId + ") - exiting"));
//		return eventoWrapper;
//	}

	private EventoPianoFormativoWrapper prepareEventoWrapperEnableField(EventoPianoFormativo evento, long providerId, long accreditamentoId, long pianoFormativoId) {
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperEnableField(" + evento.getId() + "," + providerId + ") - entering"));
		EventoPianoFormativoWrapper eventoWrapper = prepareEventoWrapperShow(evento, providerId, accreditamentoId);
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperEnableField(" + evento.getId() + "," + providerId + ") - exiting"));
		return eventoWrapper;
	}

	private EventoPianoFormativoWrapper prepareEventoWrapperRendiconto(EventoPianoFormativo evento, long providerId) {
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperRendiconto(" + evento.getId() + "," + providerId + ") - entering"));
		EventoPianoFormativoWrapper eventoWrapper = new EventoPianoFormativoWrapper();
		eventoWrapper.setEvento(evento);
		eventoWrapper.setProviderId(providerId);
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperRendiconto(" + evento.getId() + "," + providerId + ") - exiting"));
		return eventoWrapper;
	}

	//TODO rimuovere se non serve più
//	private String goToValidate(Model model, EventoPianoFormativoWrapper wrapper) {
//		model.addAttribute("eventoWrapper", wrapper);
//		model.addAttribute("proceduraFormativaList", wrapper.getEvento().getAccreditamento().getDatiAccreditamento().getProcedureFormative());
//		LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
//		return VALIDATE;
//	}

	private String goToEnableField(Model model, EventoPianoFormativoWrapper wrapper) {
		model.addAttribute("eventoWrapper", wrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + ENABLEFIELD));
		return ENABLEFIELD;
	}

	private String goToRendiconto(Model model, EventoPianoFormativoWrapper wrapper) {
		model.addAttribute("eventoWrapper", wrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + RENDICONTO));
		return RENDICONTO;
	}

	private void populateListFromAccreditamento(Model model, long accreditamentoId) throws Exception{
		DatiAccreditamento datiAccreditamento = accreditamentoService.getDatiAccreditamentoForAccreditamento(accreditamentoId);
		model.addAttribute("proceduraFormativaList", datiAccreditamento.getProcedureFormative());
		model.addAttribute("professioneList", datiAccreditamento.getProfessioniSelezionate());
		model.addAttribute("disciplinaList", datiAccreditamento.getDiscipline());
	}

	private String goToEdit(Model model, EventoPianoFormativoWrapper wrapper, RedirectAttributes redirectAttrs){
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
	//utilizzato nel caso di save
	private EventoPianoFormativoWrapper prepareEventoWrapperEdit(EventoPianoFormativo evento, String from){
		LOGGER.info(Utils.getLogMessage("Wrapper EventoPianoFormativo trovato, proveniente da: " + from));
		if(from.equals("accreditamento"))
			return prepareEventoAccreditamentoWrapperEdit(evento, 0L, 0L, 0L);
		if(from.equals("pianoFormativo"))
			return prepareEventoPianoFormativoWrapperEdit(evento, 0L, 0L, 0L);
		else
			return new EventoPianoFormativoWrapper();
	}

	//utilizzato nel caso di edit e new
	private EventoPianoFormativoWrapper prepareEventoAccreditamentoWrapperEdit(EventoPianoFormativo evento, long providerId, long accreditamentoId, long pianoFormativoId){
		LOGGER.info(Utils.getLogMessage("prepareEventoAccreditamentoWrapperEdit(" + evento.getId() + "," + providerId + "," + accreditamentoId + "," + pianoFormativoId + ") - entering"));
		EventoPianoFormativoWrapper wrapper = new EventoPianoFormativoWrapper();
		wrapper.setEvento(evento);
		if(evento.isNew()){
			wrapper.setProviderId(providerId);
		}else{
			wrapper.setProviderId(evento.getProvider().getId());
		}
		if(pianoFormativoId != 0){
			wrapper.setPianoFormativoId(pianoFormativoId);
		}

		AccreditamentoStatoEnum statoAccreditamento = accreditamentoService.getStatoAccreditamento(accreditamentoId);

		wrapper.setEventoFrom("accreditamento");
		if(accreditamentoId != 0){
			wrapper.setAccreditamentoId(accreditamentoId);
			if(evento.isNew())
				wrapper.setIdEditabili(IdFieldEnum.getAllForSubset(SubSetFieldEnum.EVENTO_PIANO_FORMATIVO));
			else {
				//la Segreteria se non è in uno stato di integrazione/preavviso rigetto può sempre modificare
				if (Utils.getAuthenticatedUser().getAccount().isSegreteria() && statoAccreditamento != AccreditamentoStatoEnum.INTEGRAZIONE && statoAccreditamento != AccreditamentoStatoEnum.PREAVVISO_RIGETTO)
					wrapper.setIdEditabili(IdFieldEnum.getAllForSubset(SubSetFieldEnum.EVENTO_PIANO_FORMATIVO));
				else
					wrapper.setIdEditabili(Utils.getSubsetOfIdFieldEnum(fieldEditabileService.getAllFieldEditabileForAccreditamentoAndObject(accreditamentoId,evento.getId()), SubSetFieldEnum.EVENTO_PIANO_FORMATIVO));
			}
		}
		else{
			wrapper.setAccreditamentoId(evento.getAccreditamento().getId());
			wrapper.setIdEditabili(IdFieldEnum.getAllForSubset(SubSetFieldEnum.EVENTO_PIANO_FORMATIVO));
		}

		LOGGER.info(Utils.getLogMessage("prepareEventoAccreditamentoWrapperEdit(" + evento.getId() + "," + providerId + "," + accreditamentoId + "," + pianoFormativoId + ") - exiting"));
		return wrapper;
	}

	private EventoPianoFormativoWrapper prepareEventoPianoFormativoWrapperEdit(EventoPianoFormativo evento, long providerId, long accreditamentoId, long pianoFormativoId){
		LOGGER.info(Utils.getLogMessage("prepareEventoPianoFormativoWrapperEdit(" + evento.getId() + "," + providerId + "," + accreditamentoId + "," + pianoFormativoId + ") - entering"));
		EventoPianoFormativoWrapper wrapper = new EventoPianoFormativoWrapper();
		wrapper.setEvento(evento);
		if(evento.isNew()){
			wrapper.setProviderId(providerId);
		}else{
			wrapper.setProviderId(evento.getProvider().getId());
		}
		if(pianoFormativoId != 0){
			wrapper.setPianoFormativoId(pianoFormativoId);
		}

		wrapper.setEventoFrom("pianoFormativo");
		if(accreditamentoId != 0){
			wrapper.setAccreditamentoId(accreditamentoId);
		}
		else{
			wrapper.setAccreditamentoId(evento.getAccreditamento().getId());
		}

		wrapper.setIdEditabili(IdFieldEnum.getAllForSubset(SubSetFieldEnum.EVENTO_PIANO_FORMATIVO));

		LOGGER.info(Utils.getLogMessage("prepareEventoPianoFormativoWrapperEdit(" + evento.getId() + "," + providerId + "," + accreditamentoId + "," + pianoFormativoId + ") - exiting"));
		return wrapper;
	}
}
