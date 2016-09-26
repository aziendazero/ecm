package it.tredi.ecm.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.bean.ProviderRegistrationWrapper;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.validator.ProviderRegistrationWrapperValidator;

@Controller
public class ProviderRegistrationController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProviderRegistrationController.class);
	public final String EDIT = "providerRegistration";

	@Autowired private ProviderService providerService;
	@Autowired private FileService fileService;
	@Autowired private ProviderRegistrationWrapperValidator providerRegistrationValidator;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("providerForm")
	public ProviderRegistrationWrapper getProviderRegistrationWrapperPreRequest(@RequestParam(value="editId",required = false) Long id){
		return providerService.getProviderRegistrationWrapper();
	}

	/** Public provider registration form. */
	@RequestMapping(value = "/providerRegistration", method = RequestMethod.GET)
	public String providerRegistration(Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /providerRegistration"));
		try {
			model.addAttribute("providerForm", providerService.getProviderRegistrationWrapper());
			model.addAttribute("stepToShow", 0);
			LOGGER.info(Utils.getLogMessage("VIEW: /providerRegistration"));
			return "providerRegistration";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /providerRegistration"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	@RequestMapping(value = "/providerRegistration", method = RequestMethod.POST)
	public String registraProvider(@ModelAttribute("providerForm") ProviderRegistrationWrapper providerRegistrationWrapper,
									BindingResult result, RedirectAttributes redirectAttrs, Model model){
		LOGGER.info(Utils.getLogMessage("POST /providerRegistration"));
		try{
			//Delegato consentito solo per alcuni tipi di Provider
			if(providerRegistrationWrapper.isDelegato()){
				File file = providerRegistrationWrapper.getDelega();
				if(file != null && !file.isNew())
					providerRegistrationWrapper.setDelega(fileService.getFile(file.getId()));
			}

			providerRegistrationValidator.validate(providerRegistrationWrapper, result);

			if(result.hasErrors()){
				model.addAttribute("stepToShow", evaluateErrorStep(result));
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
				return EDIT;
			}else{
				providerService.saveProviderRegistrationWrapper(providerRegistrationWrapper);
				LOGGER.info(Utils.getLogMessage("REDIRECT: /registration/confirmed"));
				redirectAttrs.addFlashAttribute("provider", providerRegistrationWrapper.getProvider());
				return "redirect:/confirmRegistration";
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("POST /providerRegistration"),ex);
			model.addAttribute("stepToShow", evaluateErrorStep(result));
			model.addAttribute("message",new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
			return EDIT;
		}

	}

	@RequestMapping(value = "/confirmRegistration", method = RequestMethod.GET)
	public String providerRegistrationCorfirm(Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /confirmRegistration"));
		try {
			LOGGER.info(Utils.getLogMessage("VIEW: /confirmRegistration"));
			return "confirmRegistration";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /confirmRegistration"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /login"));
			return "redirect:/login";
		}
	}

	private int evaluateErrorStep(BindingResult result){
		if(result.hasFieldErrors("provider.account*"))
			return 0;
		if(result.hasFieldErrors("legale*"))
			return 2;
		if(result.hasFieldErrors("provider*"))
			return 1;
		if(result.hasFieldErrors("delega*"))
			return 2;
		return 0;
	}
}
