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

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.service.AccountService;
import it.tredi.ecm.service.ProfileAndRoleService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.AccountChangePassword;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.validator.AccountValidator;

@Controller
public class AccountController {
	@Autowired
	private AccountService accountService;
	@Autowired
	private ProfileAndRoleService profileAndRoleService;
	
	@Autowired
	private AccountValidator accountValidator;
	
	@InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

	@RequestMapping("user/list")
	public String showAll(Model model, RedirectAttributes redirectAttrs){
		try {
			model.addAttribute("accountList", accountService.getAllUsers());
			return "user/userList";
		}catch (Exception ex){
			//TODO gestione eccezione
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	@RequestMapping("user/{id}/edit")
	public String editUser(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs){
		try{
			model.addAttribute("account", accountService.getUserById(id));
			model.addAttribute("profileList", profileAndRoleService.getAllProfile());
			return "user/editUser";
		}catch (Exception ex){
			//TODO gestione eccezione
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/user/list";
		}
	}
	
	@RequestMapping("user/new")
	public String newUser(Model model, RedirectAttributes redirectAttrs){
		try {
			model.addAttribute("account", new Account());
			model.addAttribute("profileList", profileAndRoleService.getAllProfile());
			return "user/editUser";
		}catch (Exception ex) {
			//TODO gestione eccezione
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/user/list";
		}
	}
	
	@RequestMapping(value = "user/save", method = RequestMethod.POST)
	public String saveUser(@ModelAttribute("account") Account account, BindingResult result, RedirectAttributes redirectAttrs, Model model){
		try {
			accountValidator.validate(account, result);
			if(result.hasErrors()){
				model.addAttribute("profileList", profileAndRoleService.getAllProfile());
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				return "user/editUser";
			}else{
				try{
					accountService.save(account);
					redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.inserito", "success"));
					return "redirect:/user/list";
				}catch (Exception ex){
					model.addAttribute("errore",ex.getMessage());
					model.addAttribute("profileList", profileAndRoleService.getAllProfile());
					return "user/editUser"; 
				}
			}
		}catch (Exception ex) {
			//TODO gestione eccezione
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/user/list";
		}
	}
	
	@RequestMapping(value = "user/resetPassword", method = RequestMethod.POST)
	public String resetPassword(@RequestParam("reset_email") String email){
		if(!email.isEmpty())
			try{
				accountService.resetPassword(email);
			}catch (Exception ex){
				//TODO gestire exception nei controller
			}
		return "redirect:/home";
	}
	
	@RequestMapping("/user/changePassword")
	public String changePassword(Model model, RedirectAttributes redirectAttrs){
		try {
			model.addAttribute("accountChangePassword", new AccountChangePassword());
			return "user/changePassword";
		}catch (Exception ex){
			//TODO gestione eccezione
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	@RequestMapping(value = "/user/changePassword", method = RequestMethod.POST)
	public String changePassword(@ModelAttribute("accountChangePassword") AccountChangePassword accountChangePassword, 
									BindingResult result, RedirectAttributes redirectAttrs, Model model){
		
		try {
			Account userAccount = accountService.getUserById(Utils.getAuthenticatedUser().getAccount().getId());
			accountValidator.validateChangePassword(accountChangePassword, result, userAccount);
			if(result.hasErrors()){
				model.addAttribute("accountChangePassword", accountChangePassword);
				model.addAttribute("message", new Message("message.errore", "message.inserire_campi_required", "error"));
				return "user/changePassword";
			}else{
				try{
					accountService.changePassword(userAccount.getId(), accountChangePassword.getNewPassword());
					redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.password_cambiata", "success"));
				}catch (Exception ex){
					//TODO gestione eccezione
					redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
				}
				return "redirect:/home";
			}
		}catch (Exception ex) {
			//TODO gestione eccezione
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/user/changePassword/";
		}
	}
	
	@ModelAttribute("account")
	public Account getAccountPreRequest(@RequestParam(value="editId",required = false) Long id){
		if(id != null)
			return accountService.getUserById(id);
		return new Account();
	}
}
