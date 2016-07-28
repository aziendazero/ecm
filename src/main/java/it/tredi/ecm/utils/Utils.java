package it.tredi.ecm.utils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import it.tredi.ecm.dao.entity.FieldEditabile;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.service.bean.CurrentUser;

public class Utils {
	private final static Logger LOGGER = Logger.getLogger(Utils.class);

	/**
	 * Recupero dell'utente loggato
	 * @return utente loggato oppure <code>NULL</code> se l'utente non ha fatto login
	 */
	public static CurrentUser getAuthenticatedUser(){
		CurrentUser currentUser = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if(authentication == null){
			LOGGER.info("AnonymousAuthentication found");
			return null;
		}else if(authentication instanceof AnonymousAuthenticationToken){
			LOGGER.info("AnonymousAuthentication found");
			return null;
		}

		currentUser = (CurrentUser) authentication.getPrincipal();
		return currentUser;
	}

	public static File convertFromMultiPart(MultipartFile multiPartFile){
		try{
			if(multiPartFile != null){
				File file = new File();
				file.setNomeFile(multiPartFile.getOriginalFilename());
				file.setData(multiPartFile.getBytes());
				file.setDataCreazione(LocalDate.now());
				return file;
			}
		}catch (IOException ioException){
			LOGGER.error("Errore durante lettura contenuto del file caricato oppure nessun file passato",ioException);		
		}

		return null;
	}
	
	public static String getLogMessage(String message){
		CurrentUser currentUser = getAuthenticatedUser();
		return "[" + ((currentUser != null) ? currentUser.getAccount().getUsername() : "AnonymousAuthentication") + "] - " + message; 
	}
	
	public static void logDebugErrorFields(org.slf4j.Logger LOGGER, Errors errors){
		if(LOGGER.isDebugEnabled())
			errors.getFieldErrors().forEach( fieldError -> LOGGER.debug(fieldError.getField() + ": '" + fieldError.getRejectedValue() + "' [" + fieldError.getCode()+ "]"));
	}
	
	/*
	 * Mi restituisce la lista di ENUM per legare le checkbox lato thymeleafe
	 * */
	public static Set<IdFieldEnum> getSubsetOfIdFieldEnum(Set<FieldEditabile> src, SubSetFieldEnum type){
		Set<IdFieldEnum> dst = new HashSet<IdFieldEnum>();
		
		src.forEach(f -> {
			if(f.getIdField().getSubSetField() == type)
				dst.add(f.getIdField());
		});
		
		return dst;
	}
	
	/*
	 * Mi restituisce la sublist di record presenti su db per valutare nel save del controller quali eliminare perchè deselezionate
	 * */
	public static Set<FieldEditabile> getSubset(Set<FieldEditabile> src, SubSetFieldEnum type){
		Set<FieldEditabile> dst = new HashSet<FieldEditabile>();
		
		src.forEach(f -> {
			if(f.getIdField().getSubSetField() == type)
				dst.add(f);
		});		
		
		return dst;
	}
	
	/*
	 * Controllo se un determinato IdFieldEnum è presente nella lista di record
	 * */
	public static FieldEditabile getField(Set<FieldEditabile> src, IdFieldEnum idEnum){
		for(FieldEditabile f : src)
			if(f.getIdField() == idEnum)
				return f;
		return null;
	}
	
	public static SubSetFieldEnum getSubsetFromRuolo(Ruolo ruolo){
		switch (ruolo){
			case RESPONSABILE_SEGRETERIA: return SubSetFieldEnum.RESPONSABILE_SEGRETERIA;
			case RESPONSABILE_AMMINISTRATIVO: return SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO;
			case RESPONSABILE_SISTEMA_INFORMATICO: return SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO;
			case RESPONSABILE_QUALITA: return SubSetFieldEnum.RESPONSABILE_QUALITA;
			case COMPONENTE_COMITATO_SCIENTIFICO: return SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO;
			default: return null;
		}
	}
}
