package it.tredi.ecm.web;

import java.time.LocalDate;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Seduta;
import it.tredi.ecm.dao.repository.SedutaRepository;
import it.tredi.ecm.service.SedutaService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.validator.SedutaValidator;

@Controller
public class SedutaController {
	private static Logger LOGGER = LoggerFactory.getLogger(SedutaController.class);

	private final String EDIT = "seduta/sedutaEdit";
	private final String SHOW = "seduta/sedutaShow";
	private final String LIST = "seduta/sedutaList";

	@Autowired SedutaService sedutaService;
	@Autowired SedutaRepository sedutaRepository;
	@Autowired SedutaValidator sedutaValidator;

	@ModelAttribute("seduta")
	public Seduta getSedutaPreRequest(@RequestParam(value="editId",required = false) Long id){
		if(id != null)
			return sedutaService.getSedutaById(id);
		return new Seduta();
	}

	@ModelAttribute("canEdit")
	public boolean getCanEditPreRequest(@RequestParam(value="flag", required = false) Boolean flag){
		if(flag != null)
			return flag;
		else return false;
	}
	/***	Get Lista Sedute ***/

//	@PreAuthorize("@securityAccessServiceImpl.canShowSedute(principal)") TODO
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

//	@PreAuthorize("@securityAccessServiceImpl.canEditSeduta(principal)") TODO
	@RequestMapping("/seduta/new")
	public String insertSeduta(@RequestParam(name = "date", required = false) String date, Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /seduta/new"));
		try {
			if(date != null) {
				LocalDate localDate = LocalDate.parse(date);
				return goToEdit(model, new Seduta(localDate));
			}
			else return goToEdit(model, new Seduta());
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /seduta/new"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /seduta/list"));
			return "redirect:/seduta/list";
		}
	}

//	@PreAuthorize("@securityAccessServiceImpl.canEditSeduta(principal)") TODO
	@RequestMapping(value = "/seduta/save", method = RequestMethod.POST)
	public String salvaSeduta(@ModelAttribute("canEdit") boolean canEdit, @ModelAttribute("seduta") Seduta seduta, BindingResult result, Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("POST /seduta/save"));
		try{
			sedutaValidator.validate(seduta, result, "");
			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				model.addAttribute("canEdit", canEdit);
				LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
				return EDIT;
			}else{
				sedutaRepository.save(seduta);
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.seduta_salvata", "success"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /seduta/list"));
				return "redirect:/seduta/list";
			}
		}catch(Exception ex){
			LOGGER.error(Utils.getLogMessage("POST /seduta/save"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
			return EDIT;
		}
	}

//	@PreAuthorize("@securityAccessServiceImpl.canShowSeduta(principal)") TODO
	@RequestMapping("/seduta/{sedutaId}/show")
	public String visualizzaSede(@PathVariable Long sedutaId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /seduta/" + sedutaId + "/show"));
		try {
			Seduta seduta = sedutaService.getSedutaById(sedutaId);
			return goToShow(model, seduta);
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /seduta/" + sedutaId + "/show"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + LIST));
			return LIST;
		}
	}

//	@PreAuthorize("@securityAccessServiceImpl.canEditSeduta(principal)") TODO
	@RequestMapping("/seduta/{sedutaId}/edit")
	public String modificaSede(@PathVariable Long sedutaId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /seduta/" + sedutaId + "/edit"));
		try {
			Seduta seduta = sedutaService.getSedutaById(sedutaId);
			return goToEdit(model, seduta);
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /seduta/" + sedutaId + "/edit"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + SHOW));
			return SHOW;
		}
	}

//	@PreAuthorize("@securityAccessServiceImpl.canEditSeduta(principal)") TODO
	@RequestMapping("/seduta/{sedutaId}/remove")
	public String rimuoviSede(@PathVariable Long sedutaId, Model model, RedirectAttributes redirectAttrs){
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

	/** metodi privati di support **/
	private String goToEdit(Model model, Seduta seduta) {
		model.addAttribute("seduta", seduta);
		model.addAttribute("canEdit", sedutaService.canEditSeduta(seduta));
		LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
		return EDIT;
	}

	private String goToShow(Model model, Seduta seduta) {
		model.addAttribute("seduta", seduta);
		model.addAttribute("canEdit", sedutaService.canEditSeduta(seduta));
		LOGGER.info(Utils.getLogMessage("VIEW: " + SHOW));
		return SHOW;
	}

	private JSONArray seduteToJSON(Set<Seduta> sedute) {
		JSONArray result = new JSONArray();
		for(Seduta s : sedute) {
			JSONObject temp = new JSONObject();
			temp.put("title", "ODG delle: " + s.getOra().toString());
			temp.put("start", s.getData().toString());
			temp.put("id", s.getId());
			result.put(temp);
		}
		return result;
	}

}
