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
			accountService.save(account);
			return "redirect:/user/list";
		}
	}
	
	@ModelAttribute("account")
	public Account getAccountPreRequest(@RequestParam(value="editId",required = false) Long id){
		if(id != null)
			return accountService.getUserById(id);
		return new Account();
	}
}
