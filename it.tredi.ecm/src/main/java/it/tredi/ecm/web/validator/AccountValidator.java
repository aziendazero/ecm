package it.tredi.ecm.web.validator;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.service.AccountService;

@Component
public class AccountValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountValidator.class);

	@Autowired
	private AccountService accountService;

	public void validate(Object target, Errors errors) {
		validate(target, errors, "");
	}
	
	public void validate(Object target, Errors errors, String prefix) {
		LOGGER.debug("Validating Account");
		Account account = (Account)target;
		validateAccount(account, errors, prefix);
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
					if(account.getId() != user.get().getId())
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
					if(account.getId() != user.get().getId())
						errors.rejectValue(prefix + "email", "error.email.duplicated");
				}
			}
		}
	}
}
