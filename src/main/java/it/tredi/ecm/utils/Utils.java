package it.tredi.ecm.utils;

import java.io.IOException;
import java.time.LocalDate;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import it.tredi.ecm.dao.entity.File;
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

		if(authentication instanceof AnonymousAuthenticationToken){
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

	public static void logInfo(org.slf4j.Logger LOGGER, String message){
		String userInfo = "[" + getAuthenticatedUser().getAccount().getUsername() + "] - "; 
		LOGGER.info(userInfo + message);
	}

	public static void logError(org.slf4j.Logger LOGGER, String message, Throwable ex){
		String userInfo = "[" + getAuthenticatedUser().getAccount().getUsername() + "] - "; 
		LOGGER.error(userInfo + message, ex);
	}

	public static void logDebug(org.slf4j.Logger LOGGER, String message){
		String userInfo = "[" + getAuthenticatedUser().getAccount().getUsername() + "] - "; 
		LOGGER.debug(userInfo + message);
	}
}
