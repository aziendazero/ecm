package it.tredi.ecm.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.transaction.Transactional;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.enumlist.ProfileEnum;
import it.tredi.ecm.dao.repository.AccountRepository;
import it.tredi.ecm.service.bean.EcmProperties;

@Service
public class AccountServiceImpl implements AccountService{
	private static Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);

	@Autowired
	private final AccountRepository accountRepository;
	@Autowired
	private final EmailService emailService;

	@Autowired
	private ProfileAndRoleService profileAndRoleService;
	@Autowired
	private EcmProperties ecmProperties;

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

	private void sendRegistrationEmail(Account user, String firstPassword){
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("segreteria@ecm.it");//TODO definire le property per caricare i dati
		mailMessage.setSubject("Creazione Account ECM");
		mailMessage.setText("Complimenti la registrazione al portale ECM è avvenuta con successo!\n"
							+ "Cliccare sul seguente link per attivare il suo account http://localhost:8080\n"
							+ "Le sue credenziali d'accesso sono: \n"
							+ "username: " + user.getUsername() + "\n"
							+ "password: " + firstPassword);

		emailService.send(mailMessage);
	}

	private void sendResetPasswordEmail(Account user, String password){
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("segreteria@ecm.it");//TODO definire le property per caricare i dati
		mailMessage.setSubject("Reset Password Account ECM");
		mailMessage.setText("Come da sua richiesta è stata rigenerata la password di accesso al sistema ECM!\n"
							+ "Le sue credenziali d'accesso sono: \n"
							+ "username: " + user.getUsername() + "\n"
							+ "password: " + password);

		emailService.send(mailMessage);
	}

	private void sendChangePasswordEmail(Account user, String password){
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("segreteria@ecm.it");//TODO definire le property per caricare i dati
		mailMessage.setSubject("Cambio Password Account ECM");
		mailMessage.setText("Come da sua richiesta è stata cambiata la password di accesso al sistema ECM!\n"
							+ "Le sue credenziali d'accesso sono: \n"
							+ "username: " + user.getUsername() + "\n"
							+ "password: " + password);

		emailService.send(mailMessage);
	}

}
