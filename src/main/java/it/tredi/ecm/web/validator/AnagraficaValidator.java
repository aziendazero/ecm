package it.tredi.ecm.web.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.AnagraficaEvento;
import it.tredi.ecm.dao.entity.AnagraficaEventoBase;
import it.tredi.ecm.dao.entity.AnagraficaFullEvento;
import it.tredi.ecm.dao.entity.AnagraficaFullEventoBase;
import it.tredi.ecm.service.AnagraficaEventoService;
import it.tredi.ecm.service.AnagraficaFullEventoService;
import it.tredi.ecm.service.AnagraficaService;
import it.tredi.ecm.utils.Utils;

@Component
public class AnagraficaValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(AnagraficaValidator.class);
	private static final String PATTERN_CODICE_FISCALE = "[a-zA-Z]{6}[0-9]{2}[a-zA-Z][0-9]{2}[a-zA-Z][0-9]{3}[a-zA-Z]";
	private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	@Autowired private AnagraficaService anagraficaService;
	@Autowired private AnagraficaFullEventoService anagraficaFullEventoService;
	@Autowired private AnagraficaEventoService anagraficaEventoService;
	@Autowired private FileValidator fileValidator;

	public void validateBase(Object target, Errors errors, String prefix, Long providerId){
		LOGGER.info(Utils.getLogMessage("Validazione Anagrafica Base"));
		Anagrafica anagrafica = (Anagrafica)target;
		if(anagrafica == null)
			anagrafica = new Anagrafica();
		if(anagrafica.getCognome() == null || anagrafica.getCognome().isEmpty())
			errors.rejectValue(prefix + "cognome", "error.empty");
		if(anagrafica.getNome() == null || anagrafica.getNome().isEmpty())
			errors.rejectValue(prefix + "nome", "error.empty");
		if(anagrafica.getTelefono() == null || anagrafica.getTelefono().isEmpty())
			errors.rejectValue(prefix + "telefono", "error.empty");
		if(anagrafica.getEmail() == null || anagrafica.getEmail().isEmpty())
			errors.rejectValue(prefix + "email", "error.empty");
		else if(!Pattern.matches(PATTERN_EMAIL, anagrafica.getEmail()))
			errors.rejectValue(prefix + "email", "error.invalid");

		if(anagrafica.getCodiceFiscale() == null || anagrafica.getCodiceFiscale().isEmpty()){
			errors.rejectValue(prefix + "codiceFiscale", "error.empty");
		}else{
			Optional<Long> anagraficaId = anagraficaService.getAnagraficaIdWithCodiceFiscaleForProvider(anagrafica.getCodiceFiscale(), providerId);
			if(anagraficaId.isPresent()){
				if(anagrafica.isNew()){
					errors.rejectValue(prefix + "codiceFiscale", "error.codiceFiscale.duplicated");
				}else{
					if(!anagrafica.getId().equals(anagraficaId.get()))
						errors.rejectValue(prefix + "codiceFiscale", "error.codiceFiscale.duplicated");
				}
			}else{
				if(!Pattern.matches(PATTERN_CODICE_FISCALE, anagrafica.getCodiceFiscale()))
					errors.rejectValue(prefix + "codiceFiscale", "error.invalid");
			}
		}
	}

	public void validateCellulare(Object target, Errors errors, String prefix){
		Anagrafica anagrafica = (Anagrafica)target;
		if(anagrafica.getCellulare() == null || anagrafica.getCellulare().isEmpty())
			errors.rejectValue(prefix + "cellulare", "error.empty");
	}

	public void validatePEC(Object target, Errors errors, String prefix){
		Anagrafica anagrafica = (Anagrafica)target;
		if(anagrafica.getPec() == null || anagrafica.getPec().isEmpty())
			errors.rejectValue(prefix + "pec", "error.empty");
		else if(!Pattern.matches(PATTERN_EMAIL, anagrafica.getPec()))
			errors.rejectValue(prefix + "pec", "error.invalid");
	}

	//validator anagrafica docenti/tutor e responsabili evento (Gestione Anagrafiche Ruoli Evento)
	//non controllo univocità del cf perchè non è modificabile
	public void validateAnagraficaEvento(Object target, Errors errors, String prefix, Long providerId) throws Exception{
		LOGGER.info(Utils.getLogMessage("Validazione AnagraficaEvento Base"));
		AnagraficaEvento anagraficaEvento = (AnagraficaEvento) target;

		if(anagraficaEvento == null)
			anagraficaEvento = new AnagraficaEvento();
		if(anagraficaEvento.getAnagrafica().getCognome() == null || anagraficaEvento.getAnagrafica().getCognome().isEmpty())
			errors.rejectValue(prefix + "cognome", "error.empty");
		if(anagraficaEvento.getAnagrafica().getNome() == null || anagraficaEvento.getAnagrafica().getNome().isEmpty())
			errors.rejectValue(prefix + "nome", "error.empty");
		if(anagraficaEvento.getAnagrafica().getCodiceFiscale() == null || anagraficaEvento.getAnagrafica().getCodiceFiscale().isEmpty()){
			errors.rejectValue(prefix + "codiceFiscale", "error.empty");
		}

		if(!fileValidator.validateFirmaCF(anagraficaEvento.getAnagrafica().getCv(), providerId))
			errors.rejectValue(prefix + "cv", "error.codiceFiscale.firmatario");
	}

	//validator anagrafica docenti/tutor e responsabili evento (Gestione Anagrafiche Ruoli Evento)
	//non controllo univocità del cf perchè non è modificabile
	public void validateAnagraficaFullEvento(Object target, Errors errors, String prefix, Long providerId) throws Exception{
		LOGGER.info(Utils.getLogMessage("Validazione AnagraficaEvento Base"));
		AnagraficaFullEvento anagraficaEvento = (AnagraficaFullEvento) target;

		if(anagraficaEvento == null)
			anagraficaEvento = new AnagraficaFullEvento();
		if(anagraficaEvento.getAnagrafica().getCognome() == null || anagraficaEvento.getAnagrafica().getCognome().isEmpty())
			errors.rejectValue(prefix + "cognome", "error.empty");
		if(anagraficaEvento.getAnagrafica().getNome() == null || anagraficaEvento.getAnagrafica().getNome().isEmpty())
			errors.rejectValue(prefix + "nome", "error.empty");
		if(anagraficaEvento.getAnagrafica().getCodiceFiscale() == null || anagraficaEvento.getAnagrafica().getCodiceFiscale().isEmpty())
			errors.rejectValue(prefix + "codiceFiscale", "error.empty");
		if(anagraficaEvento.getAnagrafica().getEmail() == null || anagraficaEvento.getAnagrafica().getEmail().isEmpty())
			errors.rejectValue(prefix + "email", "error.empty");
		if(anagraficaEvento.getAnagrafica().getTelefono() == null || anagraficaEvento.getAnagrafica().getTelefono().isEmpty())
			errors.rejectValue(prefix + "telefono", "error.empty");
		if(anagraficaEvento.getAnagrafica().getCellulare() == null || anagraficaEvento.getAnagrafica().getCellulare().isEmpty())
			errors.rejectValue(prefix + "cellulare", "error.empty");
	}

	//validator anagrafica responsabile di segreteria evento (modale)
	public Map<String, String> validateAnagraficaBaseEvento(AnagraficaEventoBase anagrafica, Long providerId, String prefix) throws Exception {
		Map<String, String> errMap = new HashMap<String, String>();
		if(anagrafica.getNome() == null || anagrafica.getNome().isEmpty())
			errMap.put(prefix + "nome", "error.empty");
		if(anagrafica.getCognome() ==  null || anagrafica.getCognome().isEmpty())
			errMap.put(prefix + "cognome", "error.empty");
		if(anagrafica.getCv() == null || anagrafica.getCv().isNew())
			errMap.put("file_cv_button", "error.empty");
		else if(!fileValidator.validateFirmaCF(anagrafica.getCv(), providerId))
			errMap.put("file_cv_button", "error.codiceFiscale.firmatario");
		if(anagrafica.getCodiceFiscale() == null || anagrafica.getCodiceFiscale().isEmpty())
			errMap.put(prefix + "codice_fiscale", "error.empty");
		else if(!Pattern.matches(PATTERN_CODICE_FISCALE, anagrafica.getCodiceFiscale()) && !anagrafica.getStraniero())
			errMap.put(prefix + "codice_fiscale", "error.invalid");
		else if(anagraficaEventoService.getAnagraficaEventoByCodiceFiscaleForProvider(anagrafica.getCodiceFiscale(), providerId) != null)
			errMap.put(prefix + "codice_fiscale", "error.cf_duplicated");

		return errMap;
	}

	//validator anagrafica responsabile di segreteria evento (modale)
	public Map<String, String> validateAnagraficaFullEvento(AnagraficaFullEventoBase anagrafica, Long providerId, String prefix) {
		Map<String, String> errMap = new HashMap<String, String>();
		if(anagrafica.getNome() == null || anagrafica.getNome().isEmpty())
			errMap.put(prefix + "nome", "error.empty");
		if(anagrafica.getCognome() == null || anagrafica.getCognome().isEmpty())
			errMap.put(prefix + "cognome", "error.empty");
		if(anagrafica.getCodiceFiscale() == null || anagrafica.getCodiceFiscale().isEmpty())
			errMap.put(prefix + "codice_fiscale", "error.empty");
		else if(!Pattern.matches(PATTERN_CODICE_FISCALE, anagrafica.getCodiceFiscale()))
			errMap.put(prefix + "codice_fiscale", "error.invalid");
		else if(anagraficaFullEventoService.getAnagraficaFullEventoByCodiceFiscaleForProvider(anagrafica.getCodiceFiscale(), providerId) != null)
			errMap.put(prefix + "codice_fiscale", "error.cf_duplicated");
		if(anagrafica.getEmail() == null || anagrafica.getEmail().isEmpty())
			errMap.put(prefix + "email", "error.empty");
		else if(!Pattern.matches(PATTERN_EMAIL, anagrafica.getEmail()))
			errMap.put(prefix + "email", "error.invalid");
		if(anagrafica.getTelefono() == null || anagrafica.getTelefono().isEmpty())
			errMap.put(prefix + "telefono", "error.empty");
		if(anagrafica.getCellulare() == null || anagrafica.getCellulare().isEmpty())
			errMap.put(prefix + "cellulare", "error.empty");

		return errMap;
	}

}
