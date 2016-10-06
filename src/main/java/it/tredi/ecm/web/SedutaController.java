package it.tredi.ecm.web;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Seduta;
import it.tredi.ecm.dao.entity.ValutazioneCommissione;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.repository.SedutaRepository;
import it.tredi.ecm.dao.repository.ValutazioneCommissioneRepository;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.EmailService;
import it.tredi.ecm.service.SedutaService;
import it.tredi.ecm.service.bean.EcmProperties;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.SedutaWrapper;
import it.tredi.ecm.web.validator.SedutaValidator;

@Controller
public class SedutaController {
	private static Logger LOGGER = LoggerFactory.getLogger(SedutaController.class);

	private final String EDIT = "seduta/sedutaEdit";
	private final String SHOW = "seduta/sedutaShow";
	private final String LIST = "seduta/sedutaList";
	private final String HANDLE =  "seduta/sedutaHandle";
	private final String VALIDATE = "seduta/sedutaValidate";

	@Autowired private SedutaService sedutaService;
	@Autowired private SedutaValidator sedutaValidator;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private ValutazioneCommissioneRepository valutazioneCommissioneRepository;
	@Autowired private EcmProperties ecmProperties;

	@ModelAttribute("sedutaWrapper")
	public SedutaWrapper getSedutaWrapperPreRequest(@RequestParam(value="editId", required = false) Long id){
		if(id != null){
			SedutaWrapper sedutaWrapper = new SedutaWrapper();
			sedutaWrapper.setSeduta(sedutaService.getSedutaById(id));
			return sedutaWrapper;
		}
		return new SedutaWrapper();
	}

	/***	Get Lista Sedute ***/

	@PreAuthorize("@securityAccessServiceImpl.canShowSeduta(principal)")
	@RequestMapping("/seduta/list")
	public String getAllSedute(Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /seduta/list"));
		try {
			Set<Seduta> sedute = sedutaService.getAllSedute();
			model.addAttribute("sedutaList", sedute);
			model.addAttribute("sedutaListJSON", seduteToJSON(sedute).toString());
			LOGGER.info(Utils.getLogMessage("VIEW: /seduta/list"));
			return LIST;
		}
		catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /seduta/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	/*** Inserisce nuova Seduta ***/

	@PreAuthorize("@securityAccessServiceImpl.canEditSeduta(principal)")
	@RequestMapping("/seduta/new")
	public String insertSeduta(@RequestParam(name = "date", required = false) String date, Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /seduta/new"));
		try {
			if(date != null) {
				LocalDate localDate = LocalDate.parse(date);
				return goToEdit(model, prepareWrapper(new Seduta(localDate)));
			}
			else return goToEdit(model, prepareWrapper(new Seduta()));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /seduta/new"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /seduta/list"));
			return "redirect:/seduta/list";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditSeduta(principal)")
	@RequestMapping(value = "/seduta/save", method = RequestMethod.POST)
	public String salvaSeduta(@ModelAttribute("sedutaWrapper") SedutaWrapper sedutaWrapper, BindingResult result, Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("POST /seduta/save"));
		try{
			sedutaValidator.validate(sedutaWrapper.getSeduta(), result, "seduta.");
			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
				return EDIT;
			}else{
				sedutaService.save(sedutaWrapper.getSeduta());
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.seduta_salvata", "success"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /seduta/list"));
				redirectAttrs.addFlashAttribute("dataSedutaInserita", sedutaWrapper.getSeduta().getData());
				return "redirect:/seduta/list";
			}
		}catch(Exception ex){
			LOGGER.error(Utils.getLogMessage("POST /seduta/save"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
			return EDIT;
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowSeduta(principal)")
	@RequestMapping("/seduta/{sedutaId}/show")
	public String visualizzaSeduta(@PathVariable Long sedutaId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /seduta/" + sedutaId + "/show"));
		try {
			Seduta seduta = sedutaService.getSedutaById(sedutaId);
			return goToShow(model, prepareWrapper(seduta));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /seduta/" + sedutaId + "/show"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + LIST));
			return LIST;
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditSeduta(principal)")
	@RequestMapping("/seduta/{sedutaId}/edit")
	public String modificaSeduta(@PathVariable Long sedutaId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /seduta/" + sedutaId + "/edit"));
		try {
			Seduta seduta = sedutaService.getSedutaById(sedutaId);
			return goToEdit(model, prepareWrapper(seduta));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /seduta/" + sedutaId + "/edit"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT:/seduta/{sedutaId}/show"));
			return "redirect:/seduta/{sedutaId}/show";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditSeduta(principal)")
	@RequestMapping("/seduta/{sedutaId}/handle")
	public String modificaValutazioniCommissioneSeduta(@PathVariable Long sedutaId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /seduta/" + sedutaId + "/handle"));
		try {
			Seduta seduta = sedutaService.getSedutaById(sedutaId);
			return goToHandle(model, prepareWrapper(seduta));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /seduta/" + sedutaId + "/handle"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT:/seduta/{sedutaId}/show"));
			return "redirect:/seduta/{sedutaId}/show";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditSeduta(principal)")
	@RequestMapping("/seduta/{sedutaId}/validate")
	public String inserisciValutazioneValutazioniCommissioneSeduta(@RequestParam (name = "seduta.numeroVerbale", required = false) String numeroVerbale,
			@PathVariable Long sedutaId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /seduta/" + sedutaId + "/validate"));
		try {
			Seduta seduta = sedutaService.getSedutaById(sedutaId);
			if((numeroVerbale == null || numeroVerbale.isEmpty()) &&
					(seduta.getNumeroVerbale() == null || seduta.getNumeroVerbale().isEmpty())) {
				redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_inserimento_numero_verbale", "error"));
				redirectAttrs.addFlashAttribute("errorNumeroVerbale", true);
				return "redirect:/seduta/{sedutaId}/show";
			}
			else {
				if ((numeroVerbale == null || numeroVerbale.isEmpty()) &&
					(seduta.getNumeroVerbale() != null && !seduta.getNumeroVerbale().isEmpty())) {
					return goToValidate(model, prepareWrapper(seduta));
				}
				else {
					seduta.setNumeroVerbale(numeroVerbale);
					sedutaService.save(seduta);
					return goToValidate(model, prepareWrapper(seduta));
				}
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /seduta/" + sedutaId + "/validate"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT:/seduta/{sedutaId}/show"));
			return "redirect:/seduta/{sedutaId}/show";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditSeduta(principal)")
	@RequestMapping("/seduta/{sedutaId}/remove")
	public String rimuoviSeduta(@PathVariable Long sedutaId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /seduta/" + sedutaId + "/remove"));
		try {
			if(sedutaService.canBeRemoved(sedutaId)) {
				sedutaService.removeSedutaById(sedutaId);
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.seduta_eliminata", "success"));
			}
			else {
				redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eliminazione_valutazioni_presenti", "error"));
				return "redirect:/seduta/{sedutaId}/show";
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /seduta/" + sedutaId + "/remove"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
		}
		LOGGER.info(Utils.getLogMessage("REDIRECT: /seduta/list"));
		return "redirect:/seduta/list";

	}

	@PreAuthorize("@securityAccessServiceImpl.canEditSeduta(principal)")
	@RequestMapping(value= "/seduta/{sedutaId}/valutazioneCommissione/save", method = RequestMethod.POST)
	public String aggiungiValutazioneCommissione(@ModelAttribute("sedutaWrapper") SedutaWrapper sedutaWrapper, BindingResult result,
			Model model, RedirectAttributes redirectAttrs){
		try {
			sedutaValidator.validateValutazioneCommissione(sedutaWrapper, result, "");
			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				model.addAttribute("modalError", true);
				LOGGER.info(Utils.getLogMessage("VIEW: " + HANDLE));
				return HANDLE;
			}else{
				//aggiungo la valutazioni commissione
				Seduta seduta = sedutaWrapper.getSeduta();
				sedutaService.addValutazioneCommissioneToSeduta(sedutaWrapper.getMotivazioneDaInserire(), sedutaWrapper.getIdAccreditamentoDaInserire(), seduta);
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.domanda_aggiunta", "success"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /seduta/{sedutaId}/handle"));
				return "redirect:/seduta/{sedutaId}/handle";
			}
		}catch(Exception ex){
			LOGGER.error(Utils.getLogMessage("POST /seduta/{sedutaId}/valutazioneCommissione/save"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + HANDLE));
			return HANDLE;
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditSeduta(principal)")
	@RequestMapping("/seduta/{sedutaId}/valutazioneCommissione/{valutazioneCommissioneId}/remove")
	public String rimuoviValutazioneCommissione(@ModelAttribute("sedutaWrapper") SedutaWrapper sedutaWrapper, BindingResult result, @PathVariable Long sedutaId,
			@PathVariable Long valutazioneCommissioneId, Model model, RedirectAttributes redirectAttrs){
		try {
			sedutaService.removeValutazioneCommissioneFromSeduta(valutazioneCommissioneId);
			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.domanda_rimossa", "success"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /seduta/{sedutaId}/edit"));
			return "redirect:/seduta/{sedutaId}/handle";
		}catch(Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /seduta/{sedutaId}/valutazioneCommissione/{valutazioneCommissioneId}/remove"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + HANDLE));
			return HANDLE;
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditSeduta(principal)")
	@RequestMapping(value = "/seduta/{sedutaId}/valutazioneCommissione/{valutazioneCommissioneId}/move", method = RequestMethod.POST)
	public String spostaValutazioneCommissione(@ModelAttribute("sedutaWrapper") SedutaWrapper sedutaWrapper, BindingResult result, @PathVariable Long sedutaId,
			@PathVariable Long valutazioneCommissioneId, @RequestParam(required = false) String caller, Model model, RedirectAttributes redirectAttrs){
		try {
			sedutaValidator.validateSpostamentoValutazioneCommissione(sedutaWrapper, result, "");
			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				model.addAttribute("modalErrorMove", valutazioneCommissioneId);
				model.addAttribute("sedutaWrapper", sedutaWrapper);
				if(caller != null && caller.equals("validate")) {
					LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
					return VALIDATE;
				}
				else {
					LOGGER.info(Utils.getLogMessage("VIEW: " + HANDLE));
					return HANDLE;
				}
			}else{
				//sposto la valutazione commissione dalla seduta corrente a quella target
				ValutazioneCommissione val = valutazioneCommissioneRepository.findOne(valutazioneCommissioneId);
				Seduta from =  sedutaWrapper.getSeduta();
				Seduta to = sedutaWrapper.getSedutaTarget();
				sedutaService.moveValutazioneCommissione(val, from, to);
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.domanda_spostata", "success"));
				if(caller != null && caller.equals("validate")) {
					LOGGER.info(Utils.getLogMessage("REDIRECT: /seduta/{sedutaId}/validate"));
					return "redirect:/seduta/{sedutaId}/validate";
				}
				else {
					LOGGER.info(Utils.getLogMessage("REDIRECT: /seduta/{sedutaId}/handle"));
					return "redirect:/seduta/{sedutaId}/handle";
				}

			}
		}catch(Exception ex){
			LOGGER.error(Utils.getLogMessage("POST /seduta/{sedutaId}/valutazioneCommissione/{valutazioneCommissioneId}/move"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			if(caller != null && caller.equals("validate")) {
				LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
				return VALIDATE;
			}
			else {
				LOGGER.info(Utils.getLogMessage("VIEW: " + HANDLE));
				return HANDLE;
			}
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditSeduta(principal)")
	@RequestMapping(value = "/seduta/{sedutaId}/valutazioneCommissione/{valutazioneCommissioneId}/validate", method = RequestMethod.POST)
	public String valutaValutazioneCommissione(@ModelAttribute("sedutaWrapper") SedutaWrapper sedutaWrapper, BindingResult result, @PathVariable Long sedutaId,
			@PathVariable Long valutazioneCommissioneId, Model model, RedirectAttributes redirectAttrs) throws Exception{
		try {
			sedutaValidator.validateCompletamentoValutazioneCommissione(sedutaWrapper.getValutazioneTarget(), result, "valutazioneTarget.");
			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				model.addAttribute("modalErrorValidate", valutazioneCommissioneId);
				sedutaWrapper.setMappaStatiValutazione(sedutaService.prepareMappaStatiValutazione(sedutaWrapper.getSeduta()));
				LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
				return VALIDATE;
			}else{
				//salvo i valori nella valutazione commissione
				ValutazioneCommissione val = valutazioneCommissioneRepository.findOne(valutazioneCommissioneId);
				val.setValutazioneCommissione(sedutaWrapper.getValutazioneTarget().getValutazioneCommissione());
				val.setStato(sedutaWrapper.getValutazioneTarget().getStato());
				valutazioneCommissioneRepository.save(val);

				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.valutazione_commissione_salvata", "success"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /seduta/{sedutaId}/validate"));
				return "redirect:/seduta/{sedutaId}/validate";
			}
		}catch(Exception ex){
			LOGGER.error(Utils.getLogMessage("POST /seduta/{sedutaId}/valutazioneCommissione/{valutazioneCommissioneId}/validate"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			sedutaWrapper.setMappaStatiValutazione(sedutaService.prepareMappaStatiValutazione(sedutaWrapper.getSeduta()));
			LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
			return VALIDATE;
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditSeduta(principal)")
	@RequestMapping("/seduta/{sedutaId}/chiudiSeduta")
	public String chiudiSeduta(@PathVariable Long sedutaId, Model model, RedirectAttributes redirectAttrs){
		try {
			//Conferma della valutazione della seduta
			sedutaService.chiudiSeduta(sedutaId);
			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.seduta_valutata", "success"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /seduta/{sedutaId}/show"));
			return "redirect:/seduta/{sedutaId}/show";
		}catch(Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /seduta/{sedutaId}/lock"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: redirect:/seduta/{sedutaId}/validate"));
			return "redirect:/seduta/{sedutaId}/validate";
		}
	}

	/** metodi privati di support **/
	private String goToEdit(Model model, SedutaWrapper sedutaWrapper) {
		model.addAttribute("sedutaWrapper", sedutaWrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
		return EDIT;
	}

	private String goToShow(Model model, SedutaWrapper sedutaWrapper) {
		model.addAttribute("sedutaWrapper", sedutaWrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + SHOW));
		return SHOW;
	}

	private String goToHandle(Model model, SedutaWrapper sedutaWrapper) {
		Set<Accreditamento> accreditamentiInODG = accreditamentoService.getAllAccreditamentiInseribiliInODG();
		//cerca le sedute disponibili per un eventuale spostamento di valutazione commissione (almeno 30 min da adesso)
		Set<Seduta> seduteDisponibili = sedutaService.getAllSeduteAfter(LocalDate.now(), LocalTime.now().plusMinutes(ecmProperties.getSedutaValidationMinutes()));
		//rimuove anche la seduta corrente (per evitare spostamenti da a la stessa seduta)
		seduteDisponibili.remove(sedutaWrapper.getSeduta());
		sedutaWrapper.setSeduteSelezionabili(seduteDisponibili);
		sedutaWrapper.setDomandeSelezionabili(accreditamentiInODG);
		model.addAttribute("sedutaWrapper", sedutaWrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + HANDLE));
		return HANDLE;
	}

	private String goToValidate(Model model, SedutaWrapper sedutaWrapper) throws Exception {
		//cerca le sedute disponibili per un eventuale spostamento di valutazione commissione
		Set<Seduta> seduteDisponibili = sedutaService.getAllSeduteAfter(LocalDate.now(), LocalTime.now().plusMinutes(ecmProperties.getSedutaValidationMinutes()));
		//rimuove la seduta corrente (per evitare spostamenti da a la stessa seduta)
		seduteDisponibili.remove(sedutaWrapper.getSeduta());
		sedutaWrapper.setSeduteSelezionabili(seduteDisponibili);
		sedutaWrapper.setCanConfirmEvaluation(sedutaService.canBeLocked(sedutaWrapper.getSeduta()));
		sedutaWrapper.setMappaStatiValutazione(sedutaService.prepareMappaStatiValutazione(sedutaWrapper.getSeduta()));
		model.addAttribute("sedutaWrapper", sedutaWrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
		return VALIDATE;
	}

	private SedutaWrapper prepareWrapper(Seduta seduta) {
		SedutaWrapper wrapper = new SedutaWrapper();
		wrapper.setSeduta(seduta);
		wrapper.setCanEdit(sedutaService.canEditSeduta(seduta));
		wrapper.setCanValidate(sedutaService.canBeEvaluated(seduta));
		wrapper.setCanBloccaSeduta(true);

		if(!seduta.isNew()){
			Set<Accreditamento> listaAccreditamentiInSeduta = sedutaService.getAccreditamentiInSeduta(seduta.getId());
			for(Accreditamento a : listaAccreditamentiInSeduta){
				if(a.isValutazioneCommissione()){
					wrapper.setCanBloccaSeduta(false);
				}
			}
		}

		return wrapper;
	}

	private JSONArray seduteToJSON(Set<Seduta> sedute) {
		JSONArray result = new JSONArray();
		for(Seduta s : sedute) {
			JSONObject temp = new JSONObject();
			temp.put("title", "ODG delle: " + s.getOra().toString());
			temp.put("start", s.getData().toString());
			temp.put("id", s.getId());
			if (s.isLocked())
				temp.put("color", "#1abc9c");
			else temp.put("color", "#337ab7");
			result.put(temp);
		}
		return result;
	}

	@RequestMapping("/seduta/{sedutaId}/bloccaSeduta")
	public String bloccaSeduta(@PathVariable Long sedutaId) throws Exception{
		Set<Accreditamento> listaInOdg = sedutaService.getAccreditamentiInSeduta(sedutaId);
		for(Accreditamento a : listaInOdg){
			accreditamentoService.inserisciInValutazioneCommissione(a.getId(), Utils.getAuthenticatedUser());
		}
		return "redirect:/seduta/list";
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditSeduta(principal)")
	@RequestMapping("/seduta/{sedutaId}/convocazioneCommissione")
	public String convocazioneCommissione(@PathVariable Long sedutaId, RedirectAttributes redirectAttrs){
		try {
			sedutaService.inviaMailACommissioneEcm();
			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.mail_inviata", "success"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /seduta/{sedutaId}/show"));
		}catch(Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /seduta/{sedutaId}/lock"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: redirect:/seduta/{sedutaId}/validate"));
		}

		return "redirect:/seduta/{sedutaId}/show";
	}

}
