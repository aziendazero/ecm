package it.tredi.ecm.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Authenticate a user from CAS
 */
@Service
public class CasUserDetailService implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

	private static final Logger LOGGER = Logger.getLogger(CasUserDetailService.class);

	@Autowired private CurrentUserDetailsService currentUserDetailsService;

	@Override
	public UserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
		String login = token.getPrincipal().toString();
		String lowercaseLogin = login.toLowerCase();

		LOGGER.info("Authenticating CAS '{" + login + "}'");
		return currentUserDetailsService.loadUserByUsername(login);
	}

}
