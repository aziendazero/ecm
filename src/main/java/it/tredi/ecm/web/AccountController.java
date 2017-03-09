package it.tredi.ecm.web;

import java.util.HashSet;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

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

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.ProfileEnum;
import it.tredi.ecm.service.AccountService;
import it.tredi.ecm.service.CurrentUserDetailsService;
import it.tredi.ecm.service.ProfileAndRoleService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.AccountChangePassword;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.validator.AccountValidator;

@Controller
public class AccountController{
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);
	private final String EDIT = "/user/userEdit";
	private final String LIST = "/user/userList";
	private final String URL_LIST = "/user/list";

	@Autowired private AccountService accountService;
	@Autowired private ProfileAndRoleService profileAndRoleService;
	@Autowired private AccountValidator accountValidator;
	@Autowired private CurrentUserDetailsService currentUserDetailsService;
	@Autowired private ProviderService providerService;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("account")
	public Account getAccountPreRequest(@RequestParam(value="editId",required = false) Long id){
		if(id != null)
			return accountService.getUserById(id);
		return new Account();
	}

	/**
	 * Lista account
	 **/
	@PreAuthorize("@securityAccessServiceImpl.canShowAllUser(principal)")
	@RequestMapping("/user/list")
	public String showAll(Model model, RedirectAttributes redirectAttrs){
		try {
			LOGGER.info(Utils.getLogMessage("GET /user/list"));
			model.addAttribute("accountList", accountService.getAllUsersNotFake());
			LOGGER.info(Utils.getLogMessage("VIEW: " + LIST));
			return LIST;
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /user/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}

	/**
	 * Lista referee
	 */
	@PreAuthorize("@securityAccessServiceImpl.canShowAllUser(principal)")
	@RequestMapping("/referee/list")
	public String showAllReferee(Model model, RedirectAttributes redirectAttrs){
		try {
			LOGGER.info(Utils.getLogMessage("GET /referee/list"));
			model.addAttribute("refereeList", accountService.getUserByProfileEnum(ProfileEnum.REFEREE));
			LOGGER.info(Utils.getLogMessage("VIEW: referee/refereeList"));
			return "referee/refereeList";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /referee/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditUser(principal,#id)")
	@RequestMapping("/user/{id}/edit")
	public String editUser(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs){
		try{
			LOGGER.info(Utils.getLogMessage("GET /user/" + id + "/edit"));
			return goToEdit(model, accountService.getUserById(id));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /user/" + id + "/edit"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:" + URL_LIST;
		}
	}

	/**
	 * Modifica account per provider ridirezione
	 **/
	@RequestMapping("/provider/user/{id}/edit")
	public String editUserForProvider(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/user/" + id + "/edit"));
		try {
			Provider currentProvider = providerService.getProvider();
			if(currentProvider.isNew()){
				throw new Exception("Provider non registrato");
			}else{
				Long providerId = currentProvider.getId();
				LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/user/" + id + "/edit"));
				return "redirect:/provider/"+providerId+"/user/" + id + "/edit";
			}
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/user/" + id + "/edit"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /redirect:/home"));
			return "redirect:/home";
		}
	}

	/**
	 * Modifica account per provider
	 **/
	@PreAuthorize("@securityAccessServiceImpl.canProviderEditUser(principal,#providerId,#id)")
	@RequestMapping("/provider/{providerId}/user/{id}/edit")
	public String editUserForProvider(@PathVariable Long providerId, @PathVariable Long id, Model model, RedirectAttributes redirectAttrs){
		try{
			LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/user/" + id + "/edit"));
			return goToEdit(model, accountService.getUserById(id));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/user/" + id + "/edit"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:" + URL_LIST;
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canCreateUser(principal)")
	@RequestMapping("/user/new")
	public String newUser(Model model, RedirectAttributes redirectAttrs){
		try {
			LOGGER.info(Utils.getLogMessage("GET /user/new"));
			return goToEdit(model, new Account());
		}catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /user/new"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:" + URL_LIST;
		}
	}

	/**
	 * Modifica account per provider
	 **/
	@RequestMapping("provider/user/new")
	public String newUserForProvider(Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/user/new"));
		try {
			Provider currentProvider = providerService.getProvider();
			if(currentProvider.isNew()){
				throw new Exception("Provider non registrato");
			}else{
				Long providerId = currentProvider.getId();
				LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/user/new"));
				return "redirect:/provider/"+providerId+"/user/new";
			}
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/user/new"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /redirect:/home"));
			return "redirect:/home";
		}
	}

	/**
	 * Modifica account per provider
	 **/
	@PreAuthorize("@securityAccessServiceImpl.canProviderCreateUser(principal,#providerId)")
	@RequestMapping("provider/{providerId}/user/new")
	public String newUserForProvider(@PathVariable Long providerId, Model model, RedirectAttributes redirectAttrs){
		try {
			LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/user/new"));
			return goToEdit(model, new Account());
		}catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /user/new"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:" + URL_LIST;
		}
	}

	@RequestMapping(value = "/user/save", method = RequestMethod.POST)
	public String saveUser(@ModelAttribute("account") Account account, BindingResult result, RedirectAttributes redirectAttrs, Model model){
		LOGGER.info(Utils.getLogMessage("POST /user/save"));
		String urlListRedirect = URL_LIST;
		try {
			Provider currentProvider = providerService.getProvider();
			if(currentProvider != null) {
				//si sta salvando un nuovo utente del  provider
				urlListRedirect = "/provider" + URL_LIST;
				account.setProvider(currentProvider);
				Optional<Profile> providerProfile = profileAndRoleService.getProfileByProfileEnum(ProfileEnum.PROVIDER);
				if(account.getProfiles() == null)
					account.setProfiles(new HashSet<Profile>());
				for(Profile profile : account.getProfiles()) {
					account.getProfiles().remove(profile);
				}
				if(providerProfile.isPresent())
					account.getProfiles().add(providerProfile.get());
			}

			accountValidator.validate(account, result);
			if(result.hasErrors()){
				LOGGER.debug(Utils.getLogMessage("Validazione fallita"));
				model.addAttribute("profileList", profileAndRoleService.getAllProfile());
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
				return EDIT;
			}else{
				LOGGER.debug(Utils.getLogMessage("Salvataggio account"));
				try{
					boolean isNew = account.isNew();
					accountService.save(account);
					if (isNew)
						redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.utente_creato", "success"));
					else
						redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.utente_salvato", "success"));
					LOGGER.info(Utils.getLogMessage("REDIRECT:" + urlListRedirect));
					return "redirect:" + urlListRedirect;
				}catch (Exception ex){
					LOGGER.error(Utils.getLogMessage("POST /user/save"),ex);
					model.addAttribute("profileList", profileAndRoleService.getAllProfile());
					LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
					return EDIT;
				}
			}
		}catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /user/save"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT:" + urlListRedirect));
			return "redirect:" + urlListRedirect;
		}
	}

	@RequestMapping("/user/changePassword")
	public String changePassword(Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /user/changePassword"));
		try {
			model.addAttribute("accountChangePassword", new AccountChangePassword());
			LOGGER.info(Utils.getLogMessage("VIEW: /user/changePassword"));
			return "user/changePassword";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /user/changePassword"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}

	@RequestMapping(value = "/user/changePassword", method = RequestMethod.POST)
	public String changePassword(@ModelAttribute("accountChangePassword") AccountChangePassword accountChangePassword,
			BindingResult result, RedirectAttributes redirectAttrs, Model model, HttpServletRequest request){
		LOGGER.info(Utils.getLogMessage("POST /user/changePassword"));
		try {
			Account userAccount = accountService.getUserById(Utils.getAuthenticatedUser().getAccount().getId());
			accountValidator.validateChangePassword(accountChangePassword, result, userAccount);
			if(result.hasErrors()){
				LOGGER.debug(Utils.getLogMessage("Validazione fallita"));
				model.addAttribute("accountChangePassword", accountChangePassword);
				model.addAttribute("message", new Message("message.errore", "message.inserire_campi_required", "error"));
				LOGGER.info(Utils.getLogMessage("VIEW: /user/changePassword"));
				return "/user/changePassword";
			}else{
				LOGGER.debug(Utils.getLogMessage("Salvataggio account"));
				try{
					accountService.changePassword(userAccount.getId(), accountChangePassword.getNewPassword());
					redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.password_cambiata", "success"));
					currentUserDetailsService.authenticateUser(userAccount.getUsername(), request);
				}catch (Exception ex){
					LOGGER.error(Utils.getLogMessage("POST /user/changePassword"),ex);
					redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
				}
				LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
				return "redirect:/home";
			}
		}catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /user/changePassword"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /user/changePassword"));
			return "redirect:/user/changePassword";
		}
	}

	@RequestMapping(value = "/user/resetPassword", method = RequestMethod.POST)
	public String resetPassword(@RequestParam("reset_email") String email, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("POST /user/resetPassword"));
		try{
			if(email != null && !email.isEmpty()){
				accountService.resetPassword(email);
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.password_reset_completato", "success"));
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("POST /user/resetPassword"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
		}
		return "redirect:/login";
	}

	/**
	 * Lista account per provider
	 **/
	@RequestMapping("/provider/user/list")
	public String showAccountForProvider(Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/user/list"));
		try {
			Provider currentProvider = providerService.getProvider();
			if(currentProvider.isNew()){
				throw new Exception("Provider non registrato");
			}else{
				Long providerId = currentProvider.getId();
				LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/user/list"));
				return "redirect:/provider/"+providerId+"/user/list";
			}
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/user/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /redirect:/home"));
			return "redirect:/home";
		}
	}

	/**
	 * Lista account per provider
	 **/
	@PreAuthorize("@securityAccessServiceImpl.canShowAllProviderUser(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/user/list")
	public String showAccountForProvider(@PathVariable Long providerId, Model model, RedirectAttributes redirectAttrs){
		try {
			LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/user/list"));
			model.addAttribute("accountList", accountService.getAllUsersNotFakeByProviderId(providerId));
			model.addAttribute("canInsertUser", !providerService.getProvider(providerId).isBloccato());
			LOGGER.info(Utils.getLogMessage("VIEW: " + LIST));
			return LIST;
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /user/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}


	private String goToEdit(Model model, Account account){
		model.addAttribute("account", account);
		model.addAttribute("profileList", profileAndRoleService.getAllUsableProfile());
		LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
		return EDIT;
	}
}
