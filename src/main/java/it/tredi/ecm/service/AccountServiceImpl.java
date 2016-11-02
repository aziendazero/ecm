package it.tredi.ecm.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.AspectJAdviceParameterNameDiscoverer.AmbiguousBindingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.enumlist.ProfileEnum;
import it.tredi.ecm.dao.repository.AccountRepository;
import it.tredi.ecm.service.bean.EcmProperties;

@Service
public class AccountServiceImpl implements AccountService{
	private static Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);

	@Autowired private final AccountRepository accountRepository;
	@Autowired private final EmailService emailService;
	@Autowired private ProfileAndRoleService profileAndRoleService;
	@Autowired private EcmProperties ecmProperties;
	@Autowired private SpringTemplateEngine templateEngine;
	
	@Autowired
	private WorkflowService workflowService;

	@Autowired
	public AccountServiceImpl(AccountRepository userRepository, EmailService emailService) {
		this.accountRepository = userRepository;
		this.emailService = emailService;
	}

	@Override
	public Optional<Account> getUserByUsername(String username) {
		LOGGER.debug("Getting users with username: " + username);
		return accountRepository.findOneByUsername(username);
	}

	@Override
	public Optional<Account> getUserByEmail(String email) {
		LOGGER.debug("Getting users with email: " + email);
		return accountRepository.findOneByEmail(email);
	}

	@Override
	public Account getUserById(Long id) {
		LOGGER.debug("Getting users with id: " + id);
		return accountRepository.findOne(id);
	}

	@Override
	public Set<Account> getUserByProfileEnum(ProfileEnum profileEnum) {
		LOGGER.debug("Getting users with profile: " + profileEnum.name());
		Set<Account> users = new HashSet<Account>();
		Optional<Profile> profile = profileAndRoleService.getProfileByProfileEnum(profileEnum);
		if(profile.isPresent())
			users = accountRepository.findAllByProfiles(profile.get());
		return users;
	}

	@Override
	public Set<Account> getAllUsers() {
		LOGGER.debug("Getting all users");
		return accountRepository.findAll();
	}

	@Override
	@Transactional
	public void save(Account user) throws Exception{
		if(user.isNew()){
			create(user);
		}else{
			LOGGER.debug("Saving user: " + user.getUsername());
			accountRepository.save(user);
		}
		workflowService.saveOrUpdateBonitaUserByAccount(user);
	}

	@Override
	@Transactional
	public void resetPassword(String email) throws Exception{
		Account user = getUserByEmail(email).orElseThrow(() -> new Exception());

		String resetPassword = setNewRandomPassword(user);
		sendResetPasswordEmail(user, resetPassword);
	}

	@Override
	@Transactional
	public void changePassword(Long id, String password) throws Exception {
		try{
			Account user = getUserById(id);
			user.setPassword(new BCryptPasswordEncoder().encode(password));
			user.setDataScadenzaPassword(LocalDate.now().plusDays(ecmProperties.getAccountExpiresDay()));
			accountRepository.save(user);
			sendChangePasswordEmail(user, password);
		}catch(Exception ex){
			LOGGER.error("Impossibile cambiare la password per l'utente", ex);
			throw ex;
		}
	}

	@Transactional
	private void create(Account user) throws Exception{
		LOGGER.debug("Creating user: " + user.getUsername());
		try{
			String firstPassword = setNewRandomPassword(user);
			sendRegistrationEmail(user, firstPassword);
			user.setDataScadenzaPassword(LocalDate.now());
		}catch (Exception ex){
			LOGGER.error("Impossibile creare l'utente " + user.getUsername(), ex);
			throw ex;
		}
	}

	@Transactional
	private String setNewRandomPassword(Account user) throws Exception{
		String newPassword = RandomStringUtils.random(8, true, true);
		user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
		accountRepository.save(user);
		return newPassword;
	}

	private void sendRegistrationEmail(Account user, String firstPassword) throws Exception{
		 Context context = new Context();
	     context.setVariable("applicationBaseUrl", ecmProperties.getApplicationBaseUrl());
	     context.setVariable("username", user.getUsername());
	     context.setVariable("password", firstPassword);
	     String message = templateEngine.process("creazioneAccount", context);
	     
	     emailService.send(ecmProperties.getEmailSegreteriaEcm(), user.getEmail(), "Creazione Account ECM", message, true);
	}

	private void sendResetPasswordEmail(Account user, String password) throws Exception{
		 Context context = new Context();
	     context.setVariable("applicationBaseUrl", ecmProperties.getApplicationBaseUrl());
	     context.setVariable("username", user.getUsername());
	     context.setVariable("password", password);
	     String message = templateEngine.process("resetPassword", context);
	    
	     emailService.send(ecmProperties.getEmailSegreteriaEcm(), user.getEmail(), "Cambio Password Account ECM", message, true);
	}

	private void sendChangePasswordEmail(Account user, String password) throws Exception{
		 Context context = new Context();
	     context.setVariable("applicationBaseUrl", ecmProperties.getApplicationBaseUrl());
	     context.setVariable("username", user.getUsername());
	     context.setVariable("password", password);
	     String message = templateEngine.process("cambioPassword", context);
	    
	     emailService.send(ecmProperties.getEmailSegreteriaEcm(), user.getEmail(), "Cambio Password Account ECM", message, true);
	}

	@Override
	public int countAllRefereeWithValutazioniNonDate() {
		return accountRepository.countAllRefereeWithValutazioniNonDate();
	}
	
	@Override
	public Set<String> getEmailByProfileEnum(ProfileEnum profileEnum) {
		LOGGER.debug("Recupero indirizzi email per : " + profileEnum);
		Set<String> emailList = new HashSet<String>();
		
		Optional<Profile> profile = profileAndRoleService.getProfileByProfileEnum(profileEnum);
		if(profile.isPresent()){
			Set<Account> commissione = accountRepository.findAllByProfiles(profile.get());
			for(Account a : commissione)
				emailList.add(a.getEmail());
		}
		return emailList;
	}
	
	@Override
	public Long getProviderIdById(Long accountId) {
		return accountRepository.getProviderIdById(accountId).orElse(null);
	}
	
	@Override
	public Set<Account> findAllByProviderId(Long providerId) {
		return accountRepository.findAllByProviderId(providerId);
	}
}
