package it.tredi.ecm.web.validator;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.repository.ProfileRepository;
import it.tredi.ecm.utils.Utils;

@Component
public class ProfileValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProfileValidator.class);
	
	@Autowired
	private ProfileRepository profileRepository;

	public void validate(Object target, Errors errors) {
		LOGGER.info(Utils.getLogMessage("Validazione Profile"));
		Profile profile = (Profile)target;
		validateProfile(profile, errors);
		Utils.logDebugErrorFields(LOGGER, errors);
	}
	
	private void validateProfile(Profile profile, Errors errors){
		//Presenza e univocità del name
		if(profile.getName().isEmpty()){
			errors.rejectValue("name", "error.empty");
		}else{
			Optional<Profile> profileLoaded = profileRepository.findOneByName(profile.getName());
			if(profileLoaded.isPresent()){
				if(profile.isNew()){
					errors.rejectValue("name", "error.name.duplicated");
				}else{
					if(!profile.getId().equals(profileLoaded.get().getId()))
						errors.rejectValue("name", "error.name.duplicated");
				}
			}
		}

	}
}
