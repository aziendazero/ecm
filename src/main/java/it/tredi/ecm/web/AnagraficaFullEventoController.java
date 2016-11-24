package it.tredi.ecm.web;

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

import it.tredi.ecm.dao.entity.AnagraficaFullEvento;
import it.tredi.ecm.service.AnagraficaFullEventoService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.AnagraficaFullEventoWrapper;
import it.tredi.ecm.web.bean.AnagraficaWrapper;
import it.tredi.ecm.web.bean.Message;

@Controller
public class AnagraficaFullEventoController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AnagraficaFullEventoController.class);

	private static String SHOW = "anagraficaEvento/anagraficaEventoShow";
	private static String EDIT = "anagraficaEvento/anagraficaEventoEdit";

	@Autowired private AnagraficaFullEventoService anagraficaFullEventoService;
	@Autowired private ProviderService providerService;

	@InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

	@ModelAttribute("anagraficaEventoWrapper")
	public AnagraficaFullEventoWrapper getAnagraficaPreRequest(@RequestParam(value="editId",required = false) Long id){
		if(id != null)
			return prepareAnagraficaFullEventoWrapper(anagraficaFullEventoService.getAnagraficaFullEvento(id));
		return new AnagraficaFullEventoWrapper();
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/anagraficaFullEvento/{anagraficaEventoId}/show")
	public String showAnagraficaEventoForProvider(@PathVariable Long providerId, @PathVariable Long anagraficaEventoId, Model model, RedirectAttributes redirectAttrs){
		try {
			LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/anagraficaEvento/" + anagraficaEventoId + "/show"));
			return goToShow(model, prepareAnagraficaFullEventoWrapper(anagraficaFullEventoService.getAnagraficaFullEvento(anagraficaEventoId), providerId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/anagraficaEvento/" + anagraficaEventoId + "/show"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/provider/" + providerId + "/anagraficaEvento/list";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/anagraficaFullEvento/{anagraficaId}/edit")
	public String editAnagraficaEvento(@PathVariable Long providerId, @PathVariable Long anagraficaId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/{providerId}/anagraficaFullEvento/{anagraficaId}/edit"));
		try {
			return goToEdit(model, prepareAnagraficaFullEventoWrapper(anagraficaFullEventoService.getAnagraficaFullEvento(anagraficaId),providerId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/anagraficaFullEvento/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/provider/" + providerId + "/anagraficaEvento/list";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping(value = "/provider/{providerId}/anagraficaFullEvento/save", method = RequestMethod.POST)
	public String saveAnagrafica(@ModelAttribute("anagraficaEventoWrapper") AnagraficaFullEventoWrapper wrapper, BindingResult result,
									@PathVariable Long providerId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("POST /provider/{providerId}/anagraficaFullEvento/save"));
		try {
			//anagraficaFullValidator.validateBase(wrapper.getAnagrafica(), result, "anagrafica.", providerId);
			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
				return EDIT;
			}else{
				anagraficaFullEventoService.save(wrapper.getAnagraficaEvento());
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.anagrafica_salvata", "success"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/anagraficaEvento/list"));
				return "redirect:/provider/" + providerId + "anagraficaEvento/list";
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("POST /provider/{providerId}/anagraficaFullEvento/save"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/provider/" + providerId + "/anagraficaEvento/list";
		}
	}

	private String goToEdit(Model model, AnagraficaFullEventoWrapper wrapper){
		model.addAttribute("anagraficaEventoWrapper", wrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
		return EDIT;
	}

	private String goToShow(Model model, AnagraficaFullEventoWrapper wrapper){
		model.addAttribute("anagraficaEventoWrapper", wrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + SHOW));
		return SHOW;
	}

	//utilizzato save (passa editId come hidden param)
	private AnagraficaFullEventoWrapper prepareAnagraficaFullEventoWrapper(AnagraficaFullEvento anagraficaEvento){
		return prepareAnagraficaFullEventoWrapper(anagraficaEvento, anagraficaEvento.getProvider().getId());
	}

	private AnagraficaFullEventoWrapper prepareAnagraficaFullEventoWrapper(AnagraficaFullEvento anagraficaEvento, Long providerId){
		LOGGER.info(Utils.getLogMessage("prepareAnagraficaWrapper(" + anagraficaEvento.getId() + "," + providerId + ") - entering"));
		AnagraficaFullEventoWrapper wrapper = new AnagraficaFullEventoWrapper();

		wrapper.setAnagraficaEvento(anagraficaEvento);
		wrapper.setProviderId(providerId);

		LOGGER.info(Utils.getLogMessage("prepareAnagraficaWrapper(" + anagraficaEvento.getId() + "," + providerId + ") - exiting"));
		return wrapper;
	}
}
