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

import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.service.AnagraficaService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.AnagraficaWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.validator.AnagraficaValidator;

@Controller
public class AnagraficaController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AnagraficaController.class);
	private final String EDIT = "/anagrafica/anagraficaEdit";
	private final String LIST = "/anagrafica/anagraficaList";
	private final String URL_LIST = "/provider/anagrafica/list";

	@Autowired private AnagraficaService anagraficaService;
	@Autowired private ProviderService providerService;
	@Autowired private AnagraficaValidator anagraficaValidator;

	@InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

	@ModelAttribute("anagraficaWrapper")
	public AnagraficaWrapper getAnagraficaPreRequest(@RequestParam(value="editId",required = false) Long id){
		if(id != null)
			return prepareAnagraficaWrapper(anagraficaService.getAnagrafica(id));
		return new AnagraficaWrapper();
	}

	/**
	 * Lista anagrafiche
	 **/
	@RequestMapping("/provider/anagrafica/list")
	public String showAnagraficaList(Model model, RedirectAttributes redirectAttrs){
		try {
			LOGGER.info(Utils.getLogMessage("GET /provider/anagrafica/list"));
			Provider provider = providerService.getProvider();
			model.addAttribute("anagraficaList", anagraficaService.getAllAnagraficheByProviderId(provider.getId()));
			LOGGER.info(Utils.getLogMessage("VIEW: " + LIST));
			return LIST;
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/anagrafica/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/anagrafica/{anagraficaId}/edit")
	public String editAnagrafica(@PathVariable Long providerId, @PathVariable Long anagraficaId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/{providerId}/anagrafica/{anagraficaId}/edit"));
		try {
			return goToEdit(model, prepareAnagraficaWrapper(anagraficaService.getAnagrafica(anagraficaId),providerId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/anagrafica/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping(value = "/provider/{providerId}/anagrafica/save", method = RequestMethod.POST)
	public String saveAnagrafica(@ModelAttribute("anagraficaWrapper") AnagraficaWrapper wrapper, BindingResult result,
									@PathVariable Long providerId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("POST /provider/{providerId}/anagrafica/save"));
		try {
			anagraficaValidator.validateBase(wrapper.getAnagrafica(), result, "anagrafica.", providerId);
			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
				return EDIT;
			}else{
				anagraficaService.save(wrapper.getAnagrafica());
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.anagrafica_salvata", "success"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/anagrafica/list"));
				return "redirect:" + URL_LIST;
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("POST /provider/{providerId}/anagrafica/save"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}

	private String goToEdit(Model model, AnagraficaWrapper wrapper){
		model.addAttribute("anagraficaWrapper", wrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
		return EDIT;
	}

	//utilizzato save (passa editId come hidden param)
	private AnagraficaWrapper prepareAnagraficaWrapper(Anagrafica anagrafica){
		return prepareAnagraficaWrapper(anagrafica, anagrafica.getProvider().getId());
	}

	//utilizzato per edit e new
	private AnagraficaWrapper prepareAnagraficaWrapper(Anagrafica anagrafica, Long providerId){
		LOGGER.info(Utils.getLogMessage("prepareAnagraficaWrapper(" + anagrafica.getId() + "," + providerId + ") - entering"));
		AnagraficaWrapper wrapper = new AnagraficaWrapper();

		wrapper.setAnagrafica(anagrafica);
		wrapper.setProviderId(providerId);

		LOGGER.info(Utils.getLogMessage("prepareAnagraficaWrapper(" + anagrafica.getId() + "," + providerId + ") - exiting"));
		return wrapper;
	}
}
