package it.tredi.ecm.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.entity.Role;
import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.service.bean.CurrentUser;
import it.tredi.ecm.utils.Utils;

@Service
public class CurrentUserDetailsService implements UserDetailsService {
	
	private static final Logger LOGGER = Logger.getLogger(CurrentUserDetailsService.class);
	
    private final AccountService accountService;

    @Autowired
    public CurrentUserDetailsService(AccountService userService) {
        this.accountService = userService;
    }

    @Override
    public CurrentUser loadUserByUsername(String username) throws UsernameNotFoundException {
        Account user = accountService.getUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Account with username=%s was not found", username)));
        
        List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        for(Profile profile : user.getProfiles()){
        	for(Role role : profile.getRoles()) {
                authorities.add(new SimpleGrantedAuthority(role.getName()));
            }
        }
        CurrentUser actor = new CurrentUser(user, authorities);
        
        LOGGER.info("Account: " + actor.getUsername() + " is logged with roles: " + actor.getAuthorities().toString());
        
        return actor;
    }
    
    public void authenticateUser(String username, HttpServletRequest request){
		LOGGER.info(Utils.getLogMessage("Auto login dell'utente: " + username));
    	CurrentUser user = loadUserByUsername(username);
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		token.setDetails(new WebAuthenticationDetails(request));
		SecurityContextHolder.getContext().setAuthentication(token);		
		
//    		HttpSession session = request.getSession(true);
//    	    session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }
    
}