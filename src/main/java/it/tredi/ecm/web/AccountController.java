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
import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.service.AccountService;
import it.tredi.ecm.service.ProfileAndRoleService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.AccountChangePassword;
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
	public String showAll(Model model){
		model.addAttribute("accountList", accountService.getAllUsers());
		return "user/userList";
	}
	
	@RequestMapping("user/{id}/edit")
	public String editUser(@PathVariable Long id, Model model){
		model.addAttribute("account", accountService.getUserById(id));
		model.addAttribute("profileList", profileAndRoleService.getAllProfile());
		return "user/editUser";
	}
	
	@RequestMapping("user/new")
	public String newUser(Model model){
		model.addAttribute("account", new Account());
		model.addAttribute("profileList", profileAndRoleService.getAllProfile());
		return "user/editUser";
	}
	
	@RequestMapping(value = "user/save", method = RequestMethod.POST)
	public String saveUser(@ModelAttribute("account") Account account, BindingResult result, Model model){
		accountValidator.validate(account, result);
		if(result.hasErrors()){
			model.addAttribute("profileList", profileAndRoleService.getAllProfile());
			return "user/editUser";
		}else{
				try{
				accountService.save(account);
				}catch (Exception ex){
					model.addAttribute("errore",ex.getMessage());
					model.addAttribute("profileList", profileAndRoleService.getAllProfile());
					return "user/editUser"; 
				}
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
	public String changePassword(Model model){
		model.addAttribute("accountChangePassword", new AccountChangePassword());
		return "user/changePassword";
	}
	
	@RequestMapping(value = "/user/changePassword", method = RequestMethod.POST)
	public String changePassword(@ModelAttribute("accountChangePassword") AccountChangePassword accountChangePassword, 
									BindingResult result, Model model){
		
		Account userAccount = accountService.getUserById(Utils.getAuthenticatedUser().getAccount().getId());
		accountValidator.validateChangePassword(accountChangePassword, result, userAccount);
		if(result.hasErrors()){
			model.addAttribute("accountChangePassword", accountChangePassword);
			return "user/changePassword";
		}else{
			try{
				accountService.changePassword(userAccount.getId(), accountChangePassword.getNewPassword());
			}catch (Exception ex){
				//TODO gestione exception controller
			}
			return "redirect:/home";
		}
	}
	
	@ModelAttribute("account")
	public Account getAccountPreRequest(@RequestParam(value="editId",required = false) Long id){
		if(id != null)
			return accountService.getUserById(id);
		return new Account();
	}
}
