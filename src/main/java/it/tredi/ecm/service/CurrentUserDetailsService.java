package it.tredi.ecm.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.entity.Role;
import it.tredi.bonita.api.model.UserDataModel;
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

	@Autowired
	private WorkflowService workflowService;

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
        UserDataModel userDataModel = null;
        if(workflowService.isCreateAccountOnLogin() && (user.getUsernameWorkflow() == null || user.getUsernameWorkflow().isEmpty())) {
        	try {
        		workflowService.saveOrUpdateBonitaUserByAccount(user);
        	} catch( Exception ex) {
            	LOGGER.error("Impossibile creare l'utente su bonita corrispondente all'utente username" + user.getUsername() + ".", ex);
            	throw new UsernameNotFoundException(String.format("Bonita account with usernameWorkflow=%s was not create", user.getUsername()));
        	}
        }
        try {
        	userDataModel = workflowService.getUserByLogin(user.getUsernameWorkflow());
        } catch (Exception ex) {
        	LOGGER.error("Impossibile ricavare l'utente usernameWorkflow: " + user.getUsernameWorkflow() + " da Bonita.", ex);
        	throw new UsernameNotFoundException(String.format("Bonita account with usernameWorkflow=%s was not found", user.getUsernameWorkflow()));
        }
        CurrentUser actor = new CurrentUser(user, authorities, userDataModel);

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

    public void authenticateCasUser(HttpServletRequest request){
    	LOGGER.info(Utils.getLogMessage("Login dell'utente tramite CAS"));
    	String remoteUser = request.getRemoteUser();
    	if(remoteUser != null && !remoteUser.isEmpty())
    		authenticateUser(remoteUser, request);
    	else
    		LOGGER.error(Utils.getLogMessage("RemoteUser non trovato"));
    }

}