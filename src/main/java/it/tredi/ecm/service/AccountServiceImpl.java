package it.tredi.ecm.service;

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
import it.tredi.ecm.dao.repository.AccountRepository;

@Service
public class AccountServiceImpl implements AccountService{
	private static Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);
	
	@Autowired
	private final AccountRepository accountRepository;
	@Autowired
	private final EmailService emailService;

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
	public Set<Account> getAllUsers() {
		LOGGER.debug("Getting all users");
		return accountRepository.findAll();
	}

	@Override
	@Transactional
	public void save(Account user) {
		if(user.isNew()){
			create(user);
		}else{
			LOGGER.debug("Saving user: " + user.getUsername()); 
			accountRepository.save(user);
		}
	}
	
	@Transactional
	private void create(Account user){
		LOGGER.debug("Creating user: " + user.getUsername());
		
		String firstPassword = RandomStringUtils.random(8, true, true);
		user.setPassword(new BCryptPasswordEncoder().encode(firstPassword));
		accountRepository.save(user);
		sendRegistrationEmail(user, firstPassword);
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

	@Override
	public Account getUserById(Long id) {
		LOGGER.debug("Getting users with id: " + id);
		return accountRepository.findOne(id);
	}
}
