package it.tredi.ecm.web.validator;

import java.util.Optional;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.service.AccountService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.AccountChangePassword;

@Component
public class AccountValidator{
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountValidator.class);

	private static final String PATTERN_PASSWORD = "(?=.*[0-9])(?=.*[a-zA-Z])([a-zA-Z0-9]+)";

	@Autowired private AccountService accountService;

	public void validate(Object target, Errors errors) {
		validate(target, errors, "");
	}

	public void validate(Object target, Errors errors, String prefix) {
		LOGGER.info(Utils.getLogMessage("Validazione Account"));
		Account account = (Account)target;
		validateAccount(account, errors, prefix);
		Utils.logDebugErrorFields(LOGGER, errors);
	}

	public void validateChangePassword(Object target, Errors errors, Account account){
		LOGGER.info(Utils.getLogMessage("Validzione Account per ChangePassword"));
		AccountChangePassword accountChangePassword = (AccountChangePassword)target;
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		if(accountChangePassword.getOldPassword().isEmpty())
			errors.rejectValue("oldPassword", "error.empty");
		else{
			if(!bcrypt.matches(accountChangePassword.getOldPassword(), account.getPassword()))
				errors.rejectValue("oldPassword", "error.password.incorrect");
		}

		if(accountChangePassword.getNewPassword().isEmpty()){
			errors.rejectValue("newPassword", "error.empty");
		}else{
			if(accountChangePassword.getNewPassword().length() < 8){
				errors.rejectValue("newPassword", "error.min");
			}else if(accountChangePassword.getNewPassword().length() > 12){
				errors.rejectValue("newPassword", "error.max");
			}else if(!Pattern.matches(PATTERN_PASSWORD, accountChangePassword.getNewPassword())){
				errors.rejectValue("newPassword", "error.invalid");
			}
			if(bcrypt.matches(accountChangePassword.getNewPassword(), account.getPassword()))
				errors.rejectValue("newPassword", "error.same_password");
		}

		if(accountChangePassword.getConfirmNewPassword().isEmpty()){
			errors.rejectValue("confirmNewPassword", "error.empty");
		}else{
			if(!accountChangePassword.getNewPassword().equals(accountChangePassword.getConfirmNewPassword())){
				errors.rejectValue("confirmNewPassword", "error.password_repeat");
			}
		}
		Utils.logDebugErrorFields(LOGGER, errors);
	}

	private void validateAccount(Account account, Errors errors, String prefix){
		//Presenza e univocità dello username
		if(account.getUsername().isEmpty()){
			errors.rejectValue(prefix + "username", "error.empty");
		}else{
			Optional<Account> user = accountService.getUserByUsername(account.getUsername());
			if(user.isPresent()){
				if(account.isNew()){
					errors.rejectValue(prefix + "username", "error.username.duplicated");
				}else{
					if(!account.getId().equals(user.get().getId()))
						errors.rejectValue(prefix + "username", "error.username.duplicated");
				}
			}
		}

		//Presenza e univocità dell'email di registrazione
		if(account.getEmail().isEmpty()){
			errors.rejectValue(prefix + "email", "error.empty");
		}else{
			Optional<Account> user = accountService.getUserByEmail(account.getEmail());
			if(user.isPresent()){
				if(account.isNew()){
					errors.rejectValue(prefix + "email", "error.email.duplicated");
				}else{
					if(!account.getId().equals(user.get().getId()))
						errors.rejectValue(prefix + "email", "error.email.duplicated");
				}
			}
		}
	}
}
