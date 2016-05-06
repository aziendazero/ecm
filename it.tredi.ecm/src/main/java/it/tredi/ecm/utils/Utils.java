package it.tredi.ecm.utils;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
}
