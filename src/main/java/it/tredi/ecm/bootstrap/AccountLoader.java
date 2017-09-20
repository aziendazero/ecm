package it.tredi.ecm.bootstrap;

import java.time.LocalDate;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import it.tredi.ecm.dao.entity.Role;
import it.tredi.ecm.dao.enumlist.ProfileEnum;
import it.tredi.ecm.dao.enumlist.RoleEnum;
import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.repository.AccountRepository;
import it.tredi.ecm.dao.repository.ProfileRepository;
import it.tredi.ecm.dao.repository.RoleRepository;
import it.tredi.ecm.service.AccountService;
import it.tredi.ecm.service.WorkflowService;

@Component
@org.springframework.context.annotation.Profile({"demo","simone","abarducci", "tom", "joe19"})
public class AccountLoader implements ApplicationListener<ContextRefreshedEvent> {

	private final static Logger LOGGER = LoggerFactory.getLogger(AccountLoader.class);
	private final String defaultDataScadenzaPassword = "2099-12-31";

	private final RoleRepository roleRepository;
	private final AccountService accountService;
	private final AccountRepository accountRepository;
	private final ProfileRepository profileRepository;

	@Autowired
	private WorkflowService workflowService;

	@Autowired
	public AccountLoader(AccountService accountService, AccountRepository accountRepository, RoleRepository roleRepository, ProfileRepository profileRepository) {
		this.accountService= accountService;
		this.accountRepository = accountRepository;
		this.roleRepository = roleRepository;
		this.profileRepository = profileRepository;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOGGER.info("BOOTSTRAP ECM - Inizializzazione ACL...");

		Set<Account> accounts = accountRepository.findAll();

		//tiommi 20170307: cambiato il check sugli account che devono avere l'account fake della segreteria per la gestione delle comunicazioni
		if(accounts.isEmpty() || accounts.size() == 1){
			/* ACCREDITAMENTO */
//			Role role_readAccreditamento = new Role();
//			role_readAccreditamento.setName(RoleEnum.ACCREDITAMENTO_SHOW.name());
//			role_readAccreditamento.setDescription("ACCREDITAMENTO (LETTURA)");
//			roleRepository.save(role_readAccreditamento);
//
//			Role role_writeAccreditamento = new Role();
//			role_writeAccreditamento.setName(RoleEnum.ACCREDITAMENTO_EDIT.name());
//			role_writeAccreditamento.setDescription("ACCREDITAMENTO (SCRITTURA)");
//			roleRepository.save(role_writeAccreditamento);
//
//			Role role_readAllAccreditamento = new Role();
//			role_readAllAccreditamento.setName(RoleEnum.ACCREDITAMENTO_SHOW_ALL.name());
//			role_readAllAccreditamento.setDescription("ACCREDITAMENTO (LETTURA TUTTI)");
//			roleRepository.save(role_readAllAccreditamento);
//
//			Role role_writeAllAccreditamento = new Role();
//			role_writeAllAccreditamento.setName(RoleEnum.ACCREDITAMENTO_EDIT_ALL.name());
//			role_writeAllAccreditamento.setDescription("ACCREDITAMENTO (SCRITTURA TUTTI)");
//			roleRepository.save(role_writeAllAccreditamento);

			/* PROVIDER */
//			Role role_readProvider = new Role();
//			role_readProvider.setName(RoleEnum.PROVIDER_SHOW.name());
//			role_readProvider.setDescription("PROVIDER (LETTURA)");
//			roleRepository.save(role_readProvider);
//
//			Role role_writeProvider = new Role();
//			role_writeProvider.setName(RoleEnum.PROVIDER_EDIT.name());
//			role_writeProvider.setDescription("PROVIDER (SCRITTURA)");
//			roleRepository.save(role_writeProvider);
//
//			Role role_readAllProvider = new Role();
//			role_readAllProvider.setName(RoleEnum.PROVIDER_SHOW_ALL.name());
//			role_readAllProvider.setDescription("PROVIDER (LETTURA TUTTI)");
//			roleRepository.save(role_readAllProvider);
//
//			Role role_writeAllProvider = new Role();
//			role_writeAllProvider.setName(RoleEnum.PROVIDER_EDIT_ALL.name());
//			role_writeAllProvider.setDescription("PROVIDER (SCRITTURA TUTTI)");
//			roleRepository.save(role_writeAllProvider);

			//PROVIDER_USER_SHOW, PROVIDER_USER_EDIT, PROVIDER_USER_CREATE,
//			Role role_providerUserShow = new Role();
//			role_providerUserShow.setName(RoleEnum.PROVIDER_USER_SHOW.name());
//			role_providerUserShow.setDescription("ò");
//			roleRepository.save(role_providerUserShow);
//
//			Role role_providerUserEdit = new Role();
//			role_providerUserEdit.setName(RoleEnum.PROVIDER_USER_EDIT.name());
//			role_providerUserEdit.setDescription("PROVIDER (MODIFICA UTENTI DEL PROVIDER)");
//			roleRepository.save(role_providerUserEdit);
//
//			Role role_providerUserCreate = new Role();
//			role_providerUserCreate.setName(RoleEnum.PROVIDER_USER_CREATE.name());
//			role_providerUserCreate.setDescription("PROVIDER (CREAZIONE UTENTI DEL PROVIDER)");
//			roleRepository.save(role_providerUserCreate);

			/* USER */
//			Role role_readUser = new Role();
//			role_readUser.setName(RoleEnum.USER_SHOW.name());
//			role_readUser.setDescription("UTENTI (LETTURA)");
//			roleRepository.save(role_readUser);
//
//			Role role_writeUser = new Role();
//			role_writeUser.setName(RoleEnum.USER_EDIT.name());
//			role_writeUser.setDescription("UTENTI (SCRITTURA)");
//			roleRepository.save(role_writeUser);
//
//			Role role_readAllUser = new Role();
//			role_readAllUser.setName(RoleEnum.USER_SHOW_ALL.name());
//			role_readAllUser.setDescription("UTENTI (LETTURA TUTTI)");
//			roleRepository.save(role_readAllUser);
//
//			Role role_writeAllUser = new Role();
//			role_writeAllUser.setName(RoleEnum.USER_EDIT_ALL.name());
//			role_writeAllUser.setDescription("UTENTI (SCRITTURA TUTTI)");
//			roleRepository.save(role_writeAllUser);
//
//			Role role_createUser = new Role();
//			role_createUser.setName(RoleEnum.USER_CREATE.name());
//			role_createUser.setDescription("UTENTI (CREAZIONE)");
//			roleRepository.save(role_createUser);

			/* PROFILE PROVIDER */
			Profile profile_provider = profileRepository.findOneByProfileEnum(ProfileEnum.PROVIDER).orElse(null);

			/* PROFILE PROVIDERUSERADMIN */
			Profile profile_providerUserAdmin = profileRepository.findOneByProfileEnum(ProfileEnum.PROVIDERUSERADMIN).orElse(null);

			/* PROFILE PROVIDER VISUALIZZATORE */
			Profile profile_providerVisualizzatore = profileRepository.findOneByProfileEnum(ProfileEnum.PROVIDER_VISUALIZZATORE).orElse(null);

			/* PROFILE SEGRETERIA */
			Profile profile_admin = profileRepository.findOneByProfileEnum(ProfileEnum.SEGRETERIA).orElse(null);

			/* PROFILE RESPONSABILE SEGRETERIA ECM */
			Profile profile_superadmin = profileRepository.findOneByProfileEnum(ProfileEnum.RESPONSABILE_SEGRETERIA_ECM).orElse(null);

			/* PROFILE REFEREE */
			Profile profile_referee = profileRepository.findOneByProfileEnum(ProfileEnum.REFEREE).orElse(null);

			/* PROFILE COMMISSIONE */
			Profile profile_commissione = profileRepository.findOneByProfileEnum(ProfileEnum.COMMISSIONE).orElse(null);

			/* PROFILE OSSERVATORE */
			Profile profile_osservatore = profileRepository.findOneByProfileEnum(ProfileEnum.COMPONENTE_OSSERVATORIO).orElse(null);

			/* PROFILE REFERENTE INFORMATICO */
			Profile profile_referente_informatico = profileRepository.findOneByProfileEnum(ProfileEnum.REFERENTE_INFORMATICO).orElse(null);

			/* PROFILE FAKE ACCOUNT COMUNICAZIONI PROVIDER */
			Profile profile_account_comunicazioni_provider = profileRepository.findOneByProfileEnum(ProfileEnum.PROVIDER_ACCOUNT_COMUNICAZIONI).orElse(null);

			createAccountProviderWithUserNameAndEmail("provider1", "abarducci@3di.it", profile_provider, profile_providerUserAdmin);
			createAccountProviderWithUserNameAndEmail("provider2", "dpranteda@3di.it", profile_provider, profile_providerUserAdmin);
			createAccountProviderWithUserNameAndEmail("provider3", "eluconi@3di.it", profile_provider, profile_providerUserAdmin);
			createAccountProviderWithUserNameAndEmail("provider4", "sstagni@3di.it", profile_provider, profile_providerUserAdmin);
			createAccountProviderWithUserNameAndEmail("provider5", "tiommi@3di.it", profile_provider, profile_providerUserAdmin);

			//creazione fake account per comunicazioni i provider
			createAccountComunicazioniProvider(5, profile_account_comunicazioni_provider);

			Account admin1 = new Account();
			admin1.setUsername("segreteria1");
			admin1.setPassword("$2a$10$JCx8DPs0l0VNFotVGkfW/uRyJzFfc8HkTi5FQy0kpHSpq7W4iP69.");
			//admin.setPassword("admin");
			admin1.setEmail("dpranteda@3di.it");
			admin1.setChangePassword(false);
			admin1.setEnabled(true);
			admin1.setExpiresDate(null);
			admin1.setLocked(false);
			admin1.setNome("Tizio");
			admin1.setCognome("Caio");
			admin1.getProfiles().add(profile_admin);
			admin1.setDataScadenzaPassword(LocalDate.parse(defaultDataScadenzaPassword));

			try {
				//accountService.save(admin);
				accountRepository.save(admin1);
				workflowService.saveOrUpdateBonitaUserByAccount(admin1);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Account admin2 = new Account();
			admin2.setUsername("segreteria2");
			admin2.setPassword("$2a$10$JCx8DPs0l0VNFotVGkfW/uRyJzFfc8HkTi5FQy0kpHSpq7W4iP69.");
			//admin.setPassword("admin");
			admin2.setEmail("segreteriaECM1@ecm.it");
			admin2.setChangePassword(false);
			admin2.setEnabled(true);
			admin2.setExpiresDate(null);
			admin2.setLocked(false);
			admin2.setNome("Segretario");
			admin2.setCognome("DiRiserva");
			admin2.getProfiles().add(profile_admin);
			admin2.setDataScadenzaPassword(LocalDate.parse(defaultDataScadenzaPassword));

			try {
				//accountService.save(admin);
				accountRepository.save(admin2);
				workflowService.saveOrUpdateBonitaUserByAccount(admin2);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Account referee1 = new Account();
			referee1.setUsername("referee1");
			referee1.setPassword("$2a$10$JCx8DPs0l0VNFotVGkfW/uRyJzFfc8HkTi5FQy0kpHSpq7W4iP69.");
			//referee1.setPassword("admin");
			referee1.setEmail("referee1@ecm.it");
			referee1.setChangePassword(false);
			referee1.setEnabled(true);
			referee1.setExpiresDate(null);
			referee1.setLocked(false);
			referee1.setNome("John");
			referee1.setCognome("Doe");
			referee1.getProfiles().add(profile_referee);
			referee1.setDataScadenzaPassword(LocalDate.parse(defaultDataScadenzaPassword));
			try {
				//accountService.save(referee1);
				accountRepository.save(referee1);
				workflowService.saveOrUpdateBonitaUserByAccount(referee1);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Account referee2 = new Account();
			referee2.setUsername("referee2");
			referee2.setPassword("$2a$10$JCx8DPs0l0VNFotVGkfW/uRyJzFfc8HkTi5FQy0kpHSpq7W4iP69.");
			//referee2.setPassword("admin");
			referee2.setEmail("referee2@ecm.it");
			referee2.setChangePassword(false);
			referee2.setEnabled(true);
			referee2.setExpiresDate(null);
			referee2.setLocked(false);
			referee2.setNome("Mario");
			referee2.setCognome("Rossi");
			referee2.getProfiles().add(profile_referee);
			referee2.setDataScadenzaPassword(LocalDate.parse(defaultDataScadenzaPassword));
			try {
				//accountService.save(referee2);
				accountRepository.save(referee2);
				workflowService.saveOrUpdateBonitaUserByAccount(referee2);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Account referee3 = new Account();
			referee3.setUsername("referee3");
			referee3.setPassword("$2a$10$JCx8DPs0l0VNFotVGkfW/uRyJzFfc8HkTi5FQy0kpHSpq7W4iP69.");
			//referee3.setPassword("admin");
			referee3.setEmail("referee3@ecm.it");
			referee3.setChangePassword(false);
			referee3.setEnabled(true);
			referee3.setExpiresDate(null);
			referee3.setLocked(false);
			referee3.setNome("Marco");
			referee3.setCognome("Marroni");
			referee3.getProfiles().add(profile_referee);
			referee3.setDataScadenzaPassword(LocalDate.parse(defaultDataScadenzaPassword));
			try {
				//accountService.save(referee3);
				accountRepository.save(referee3);
				workflowService.saveOrUpdateBonitaUserByAccount(referee3);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Account osservatore = new Account();
			osservatore.setUsername("osservatore");
			osservatore.setPassword("$2a$10$JCx8DPs0l0VNFotVGkfW/uRyJzFfc8HkTi5FQy0kpHSpq7W4iP69.");
			//referee1.setPassword("admin");
			osservatore.setEmail("osservatore@ecm.it");
			osservatore.setChangePassword(false);
			osservatore.setEnabled(true);
			osservatore.setExpiresDate(null);
			osservatore.setLocked(false);
			osservatore.setNome("Osservatore");
			osservatore.setCognome("Osservante");
			osservatore.getProfiles().add(profile_osservatore);
			osservatore.setDataScadenzaPassword(LocalDate.parse(defaultDataScadenzaPassword));
			try {
				//accountService.save(referee1);
				accountRepository.save(osservatore);
				workflowService.saveOrUpdateBonitaUserByAccount(osservatore);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Account commissione = new Account();
			commissione.setUsername("commissione");
			commissione.setPassword("$2a$10$JCx8DPs0l0VNFotVGkfW/uRyJzFfc8HkTi5FQy0kpHSpq7W4iP69.");
			//commissione.setPassword("admin");
			commissione.setEmail("commissione@ecm.it");
			commissione.setChangePassword(false);
			commissione.setEnabled(true);
			commissione.setExpiresDate(null);
			commissione.setLocked(false);
			commissione.setNome("Commissario");
			commissione.setCognome("Basettoni");
			commissione.getProfiles().add(profile_commissione);
			commissione.setDataScadenzaPassword(LocalDate.parse(defaultDataScadenzaPassword));
			try {
				//accountService.save(commissione);
				accountRepository.save(commissione);
				workflowService.saveOrUpdateBonitaUserByAccount(commissione);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Account referente = new Account();
			referente.setUsername("referente");
			referente.setPassword("$2a$10$JCx8DPs0l0VNFotVGkfW/uRyJzFfc8HkTi5FQy0kpHSpq7W4iP69.");
			//commissione.setPassword("admin");
			referente.setEmail("referenteInformatico@ecm.it");
			referente.setChangePassword(false);
			referente.setEnabled(true);
			referente.setExpiresDate(null);
			referente.setLocked(false);
			referente.setNome("Referente");
			referente.setCognome("Informatico");
			referente.getProfiles().add(profile_referente_informatico);
			referente.setDataScadenzaPassword(LocalDate.parse(defaultDataScadenzaPassword));
			try {
				//accountService.save(referente);
				accountRepository.save(referente);
				workflowService.saveOrUpdateBonitaUserByAccount(referente);
			} catch (Exception e) {
				e.printStackTrace();
			}

			LOGGER.info("BOOTSTRAP ECM - ACL creata");
		}else{
			LOGGER.info("BOOTSTRAP ECM - ACL trovata (" + accounts.size() + ")");
		}
	}

	private void createAccountComunicazioniProvider(int numeroProvider, Profile profilo) {
		for(int i = 1; i <= numeroProvider; i++) {
			Account accountComunicazioni = new Account();
			accountComunicazioni.setPassword("$2a$10$JCx8DPs0l0VNFotVGkfW/uRyJzFfc8HkTi5FQy0kpHSpq7W4iP69.");
			accountComunicazioni.setChangePassword(false);
			accountComunicazioni.setEnabled(true);
			accountComunicazioni.setExpiresDate(null);
			accountComunicazioni.setLocked(false);
			accountComunicazioni.setNome("Provider");
			accountComunicazioni.setCognome("Comunicazioni");
			accountComunicazioni.getProfiles().add(profilo);
			accountComunicazioni.setDataScadenzaPassword(null);
			accountComunicazioni.setFakeAccountComunicazioni(true);
			try {
				//accountService.save(referente);
				accountRepository.save(accountComunicazioni);
				workflowService.saveOrUpdateBonitaUserByAccount(accountComunicazioni);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void createAccountProviderWithUserNameAndEmail(String userName, String email, Profile profile_provider, Profile profile_providerUserAdmin) {
		Account account = getAccountProviderWithUserNameAndEmail(userName, email, profile_provider, profile_providerUserAdmin);
		try {
			//accountService.save(provider);
			accountRepository.save(account);
			workflowService.saveOrUpdateBonitaUserByAccount(account);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Account getAccountProviderWithUserNameAndEmail(String userName, String email, Profile profile_provider, Profile profile_providerUserAdmin) {
		Account provider = new Account();
		provider.setUsername(userName);
		provider.setPassword("$2a$10$JCx8DPs0l0VNFotVGkfW/uRyJzFfc8HkTi5FQy0kpHSpq7W4iP69.");
		//provider.setPassword("admin");
		provider.setEmail(email);
		provider.setChangePassword(false);
		provider.setEnabled(true);
		provider.setExpiresDate(null);
		provider.setLocked(false);
		provider.getProfiles().add(profile_provider);
		provider.getProfiles().add(profile_providerUserAdmin);
		provider.setDataScadenzaPassword(LocalDate.parse(defaultDataScadenzaPassword));
		return provider;
	}
}
