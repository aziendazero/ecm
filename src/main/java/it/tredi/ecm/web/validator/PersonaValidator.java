package it.tredi.ecm.web.validator;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.service.PersonaService;

@Component
public class PersonaValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(PersonaValidator.class);
	
	@Autowired
	private AnagraficaValidator anagraficaValidator;
	@Autowired
	private FileValidator fileValidator;
	@Autowired
	private PersonaService personaService;
	
	public void validate(Object target, Errors errors, String prefix, Set<File> files){
		validatePersona(target, errors, prefix);
		validateFiles(files, errors, "", ((Persona)target).getRuolo());
	}
	
	private void validatePersona(Object target, Errors errors, String prefix){
		Persona persona = (Persona)target;
		validateBase(persona, errors, prefix);
		
		//CELLULARE (LR - DLR - CCS - ComCS - RA)
		if(	persona.isLegaleRappresentante() || persona.isDelegatoLegaleRappresentante() || 
			persona.isCoordinatoreComitatoScientifico() || persona.isComponenteComitatoScientifico() || 
			persona.isResponsabileAmministrativo()){
				anagraficaValidator.validateCellulare(persona.getAnagrafica(), errors, prefix + "anagrafica.");
		}
		
		//PEC (LR - DLR)
		if(persona.isLegaleRappresentante() || persona.isDelegatoLegaleRappresentante()){
			anagraficaValidator.validatePEC(persona.getAnagrafica(), errors, prefix + "anagrafica.");
		}
		
		//PROFESSIONE (RSI - RQ - CCS - ComCS)
		if(persona.isResponsabileSistemaInformatico() || persona.isResponsabileQualita() || 
			persona.isCoordinatoreComitatoScientifico() || persona.isComponenteComitatoScientifico()){
			if(persona.getProfessione() == null || persona.getProfessione().getNome().isEmpty())
				errors.rejectValue(prefix + "professione", "error.empty");
		}
		
		//COMPONENTE COMITATO SCIENTIFICO
		boolean cfPresente = false;
		boolean coordinatorePresente = false;
		if(persona.isComponenteComitatoScientifico()){
			//non e' possibile inserire piu' volte la stessa persona nel comitato
			Persona personaCF = personaService.getPersonaByRuoloAndCodiceFiscale(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, persona.getAnagrafica().getCodiceFiscale(), persona.getProvider().getId()); 
			Persona personaCoord = personaService.getCoordinatoreComitatoScientifico(persona.getProvider().getId());
			
			if(personaCF != null){
				if(persona.isNew() || !persona.getId().equals(personaCF.getId()))
					cfPresente = true;
			}
			
			if(personaCoord != null){
				if(persona.isNew() || !persona.getId().equals(personaCoord.getId()))
					coordinatorePresente = true;
			}
			
			if(cfPresente)
				errors.rejectValue(prefix + "anagrafica.codiceFiscale", "error.componente_presente");
			
			if(coordinatorePresente && persona.isCoordinatoreComitatoScientifico())
				errors.rejectValue(prefix + "coordinatoreComitatoScientifico", "error.coordinatore_presente");
		}
	}
	
	private void validateBase(Object target, Errors errors, String prefix){
		LOGGER.debug("VALIDAZIONE PERSONA");
		Persona persona = (Persona)target;
		anagraficaValidator.validateBase(persona.getAnagrafica(), errors, prefix + "anagrafica.");
	}
	
	@SuppressWarnings("unchecked")
	public void validateFiles(Object target, Errors errors, String prefix, Ruolo ruolo){
		LOGGER.debug("VALIDAZIONE ALLEGATI");
		Set<File> files = null;
		if(target != null)
			files = (Set<File>) target;
		else
			files = new HashSet<File>();
		File attoNomina = null;
		File cv = null;
		File delega = null;
		
		for(File file : files){
			if(file != null){
				if(file.isATTONOMINA())
					attoNomina = file;
				else if(file.isCV())
					cv = file;
				else if(file.isDELEGA())
					delega = file;
			}
		}
		
		if(ruolo.equals(Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE)){
			//l'unico che ha l'atto di delega
			fileValidator.validate(delega, errors, prefix + "delega");
		}
		
		if(!ruolo.equals(Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE)){
			//solo il DLR NON ha l'atto di nomina
			fileValidator.validate(attoNomina, errors, prefix + "attoNomina");
		}
		
		if(!ruolo.equals(Ruolo.LEGALE_RAPPRESENTANTE)){
			//solo il LR NON ha il cv
			fileValidator.validate(cv, errors, prefix + "cv");
		}
	}
}