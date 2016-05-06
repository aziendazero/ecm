package it.tredi.ecm.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.entity.Role;
import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.repository.ProfileRepository;
import it.tredi.ecm.dao.repository.RoleRepository;
import it.tredi.ecm.service.AccountService;

@Component
public class AccountLoader implements ApplicationListener<ContextRefreshedEvent> {

	private final static Logger LOGGER = LoggerFactory.getLogger(AccountLoader.class);
	
	private final RoleRepository roleRepository;
	private final AccountService accountService;
	private final ProfileRepository profileRepository;
	
	@Autowired
	public AccountLoader(AccountService accountService, RoleRepository roleRepository, ProfileRepository profileRepository) {
		this.accountService = accountService;
		this.roleRepository = roleRepository;
		this.profileRepository = profileRepository;
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOGGER.info("Initializing ACL...");
		Role role_readProvider = new Role();
		role_readProvider.setName("PROVIDER_READ");
		role_readProvider.setDescription("PROVIDER (LETTURA)");
		roleRepository.save(role_readProvider);
		
		Role role_writeProvider = new Role();
		role_writeProvider.setName("PROVIDER_WRITE");
		role_writeProvider.setDescription("PROVIDER (SCRITTURA)");
		roleRepository.save(role_writeProvider);
		
		Role role_readUser = new Role();
		role_readUser.setName("USER_READ");
		role_readUser.setDescription("UTENTI (LETTURA)");
		roleRepository.save(role_readUser);
		
		Role role_writeUser = new Role();
		role_writeUser.setName("USER_WRITE");
		role_writeUser.setDescription("UTENTI (SCRITTURA)");
		roleRepository.save(role_writeUser);
		
		Role role_readUserAll = new Role();
		role_readUserAll.setName("USER_READ_ALL");
		role_readUserAll.setDescription("UTENTI (LETTURA TUTTI)");
		roleRepository.save(role_readUserAll);
		
		Profile profile_provider = new Profile();
		profile_provider.setName("PROVIDER");
		profile_provider.getRoles().add(role_readProvider);
		profile_provider.getRoles().add(role_writeProvider);
		profileRepository.save(profile_provider);
		
		Profile profile_admin = new Profile();
		profile_admin.setName("ADMIN");
		profile_admin.getRoles().add(role_readUser);
		profile_admin.getRoles().add(role_writeUser);
		profile_admin.getRoles().add(role_readUserAll);
		profileRepository.save(profile_admin);
		
		Account provider = new Account();
		provider.setUsername("provider");
		provider.setPassword("provider");
		provider.setEmail("dompranteda@gmail.com");
		provider.setChangePassword(false);
		provider.setEnabled(true);
		provider.setExpiresDate(null);
		provider.setLocked(false);
		provider.getProfiles().add(profile_provider);
		accountService.save(provider);
		
		Account admin = new Account();
		admin.setUsername("admin");
		admin.setPassword("admin");
		admin.setEmail("dpranteda@3di.it");
		admin.setChangePassword(false);
		admin.setEnabled(true);
		admin.setExpiresDate(null);
		admin.setLocked(false);
		admin.getProfiles().add(profile_admin);
		accountService.save(admin);
	}
}
