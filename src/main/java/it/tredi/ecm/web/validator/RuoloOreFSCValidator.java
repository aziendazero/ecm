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
import it.tredi.ecm.dao.entity.RuoloOreFSC;
import it.tredi.ecm.service.AccountService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.AccountChangePassword;

@Component
public class RuoloOreFSCValidator{
	private static final Logger LOGGER = LoggerFactory.getLogger(RuoloOreFSCValidator.class);

	public void validate(Object target, Errors errors) {
		validate(target, errors, "");
	}

	public void validate(Object target, Errors errors, String prefix) {
		LOGGER.info(Utils.getLogMessage("Validazione RuoloOreFSC"));
		RuoloOreFSC ruoloOre = (RuoloOreFSC)target;
		if(ruoloOre.getRuolo() == null)
			errors.rejectValue(prefix + "ruolo", "error.min");
		
		if(ruoloOre.getTempoDedicato() == null)
			errors.rejectValue(prefix + "tempoDedicato", "error.min");
		
		Utils.logDebugErrorFields(LOGGER, errors);
	}

}
