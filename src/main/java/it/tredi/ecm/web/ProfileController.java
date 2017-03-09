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

import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.service.ProfileAndRoleService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.validator.ProfileValidator;

@Controller
public class ProfileController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProfileController.class);

	private final String EDIT = "user/profileEdit";

	@Autowired private ProfileAndRoleService profileAndRoleService;
	@Autowired private ProfileValidator profileValidator;

	@InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

	@PreAuthorize("@securityAccessServiceImpl.canShowAllUser(principal)")
	@RequestMapping("/profile/list")
	public String showAll(Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /profile/list"));
		try {
			model.addAttribute("profileList", profileAndRoleService.getAllUsableProfile());
			LOGGER.info(Utils.getLogMessage("VIEW: /user/profileList"));
			return "user/profileList";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /profile/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canCreateUser(principal)")
	@RequestMapping("/profile/{id}/edit")
	public String editProfile(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /profile/" + id +"/edit"));
		try {
			model.addAttribute("profile", profileAndRoleService.getProfile(id));
			model.addAttribute("roleList", profileAndRoleService.getAllRole());
			LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
			return EDIT;
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /profile/" + id +"/edit"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /profile/list"));
			return "redirect:/profile/list";
		}
	}

//	@PreAuthorize("@securityAccessServiceImpl.canCreateUser(principal)")
//	@RequestMapping("/profile/new")
//	public String newProfile(Model model, RedirectAttributes redirectAttrs){
//		LOGGER.info(Utils.getLogMessage("GET /profile/new"));
//		try {
//			model.addAttribute("profile", new Profile());
//			model.addAttribute("roleList", profileAndRoleService.getAllRole());
//			LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
//			return EDIT;
//		}catch (Exception ex) {
//			LOGGER.error(Utils.getLogMessage("GET /profile/new"),ex);
//			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
//			LOGGER.info(Utils.getLogMessage("REDIRECT: /profile/list"));
//			return "redirect:/profile/list";
//		}
//	}

	@RequestMapping(value = "profile/save", method = RequestMethod.POST)
	public String saveProfile(@ModelAttribute("profile") Profile profile, BindingResult result, RedirectAttributes redirectAttrs, Model model){
		LOGGER.info(Utils.getLogMessage("POST /profile/save"));
		try {
			profileValidator.validate(profile, result);
			if(result.hasErrors()){
				model.addAttribute("roleList", profileAndRoleService.getAllRole());
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
				return EDIT;
			}else{
				profileAndRoleService.saveProfile(profile);
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.profilo_salvato", "success"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /profile/list"));
				return "redirect:/profile/list";
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("POST /profile/save"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
			return EDIT;
		}
	}

	@ModelAttribute("profile")
	public Profile getProfilePreRequest(@RequestParam(value="editId",required = false) Long id){
		if(id != null)
			return profileAndRoleService.getProfile(id);
		return new Profile();
	}
}
