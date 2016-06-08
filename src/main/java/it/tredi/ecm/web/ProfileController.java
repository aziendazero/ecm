package it.tredi.ecm.web;

import org.springframework.beans.factory.annotation.Autowired;
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
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.validator.ProfileValidator;

@Controller
public class ProfileController {
	@Autowired
	private ProfileAndRoleService profileAndRoleService;
	@Autowired
	private ProfileValidator profileValidator;
	
	@InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

	@RequestMapping("profile/list")
	public String showAll(Model model, RedirectAttributes redirectAttrs){
		try {
			model.addAttribute("profileList", profileAndRoleService.getAllProfile());
			return "user/profileList";
		}catch (Exception ex){
			//TODO gestione eccezione
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	@RequestMapping("profile/{id}/edit")
	public String editProfile(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs){
		try {
			model.addAttribute("profile", profileAndRoleService.getProfile(id));
			model.addAttribute("roleList", profileAndRoleService.getAllRole());
			return "user/editProfile";
		}catch (Exception ex){
			//TODO gestione eccezione
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/profile/list";
		}
	}
	
	@RequestMapping("profile/new")
	public String newProfile(Model model, RedirectAttributes redirectAttrs){
		try {
			model.addAttribute("profile", new Profile());
			model.addAttribute("roleList", profileAndRoleService.getAllRole());
			return "user/editProfile";
		}catch (Exception ex) {
			//TODO gestione eccezione
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/profile/list";
		}
	}
	
	@RequestMapping(value = "profile/save", method = RequestMethod.POST)
	public String saveProfile(@ModelAttribute("profile") Profile profile, BindingResult result, RedirectAttributes redirectAttrs, Model model){
		try {
			profileValidator.validate(profile, result);
			if(result.hasErrors()){
				model.addAttribute("roleList", profileAndRoleService.getAllRole());
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				return "user/editProfile";
			}else{
				profileAndRoleService.saveProfile(profile);
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.profilo_salvato", "success"));
				return "redirect:/profile/list";
			}
		}catch (Exception ex){
			//TODO gestione eccezione
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "user/editProfile";
		}
	}
	
	@ModelAttribute("profile")
	public Profile getProfilePreRequest(@RequestParam(value="editId",required = false) Long id){
		if(id != null)
			return profileAndRoleService.getProfile(id);
		return new Profile();
	}
}
