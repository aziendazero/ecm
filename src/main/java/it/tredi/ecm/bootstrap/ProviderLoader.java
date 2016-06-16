package it.tredi.ecm.bootstrap;

import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import it.tredi.ecm.dao.repository.AccountRepository;
import it.tredi.ecm.dao.repository.PersonaRepository;
import it.tredi.ecm.dao.repository.ProviderRepository;

@Component
@org.springframework.context.annotation.Profile("dev")
public class ProviderLoader implements ApplicationListener<ContextRefreshedEvent> {
	private final static Logger LOGGER = LoggerFactory.getLogger(ProviderLoader.class);
	
	private final ProviderRepository providerRepository;
	private final PersonaRepository personaRepository;
	private final AccountRepository accountRepository;
	
	@Autowired
	public ProviderLoader(ProviderRepository providerRepository, PersonaRepository personaRepository, AccountRepository accountRepository) {
		this.providerRepository = providerRepository;
		this.personaRepository = personaRepository;
		this.accountRepository = accountRepository;
	}
	
	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOGGER.info("BOOTSTRAP ECM - Initializing DATABASE...");
		
		Set<Provider> providers = providerRepository.findAll();
		
		if(providers.isEmpty()){
			Persona richiedente = new Persona();
			richiedente.getAnagrafica().setCognome("Rossi");
			richiedente.getAnagrafica().setNome("Mario");
			richiedente.getAnagrafica().setCodiceFiscale("MRARSS86H01Z112R");
			richiedente.getAnagrafica().setCellulare("3331234567");
			richiedente.getAnagrafica().setTelefono("0517654321");
			richiedente.getAnagrafica().setEmail("mrossi@3di.com");
			richiedente.getAnagrafica().setPec("mrossi@pec.com");
			personaRepository.save(richiedente);
			
			LOGGER.info("Persona " + richiedente.getId() + " created");
			
			Persona legale = new Persona();
			legale.getAnagrafica().setCognome("Verdi");
			legale.getAnagrafica().setNome("Giuseppe");
			legale.getAnagrafica().setCodiceFiscale("VRDGPP80H17D969I");
			legale.getAnagrafica().setCellulare("123456789");
			legale.getAnagrafica().setTelefono("051987321");
			legale.getAnagrafica().setEmail("gverdi@3di.it");
			legale.getAnagrafica().setPec("gverdi@pec.it");
			personaRepository.save(legale);
			
			LOGGER.info("Persona " + legale.getId() + " created");
			
			richiedente.setRuolo(Ruolo.RICHIEDENTE);
			legale.setRuolo(Ruolo.LEGALE_RAPPRESENTANTE);
			Account account = accountRepository.findOneByUsername("admin").orElse(null);
			
			Provider provider = new Provider();
			provider.setDenominazioneLegale("3D Informatica");
			provider.setCodiceFiscale("00578261208");
			provider.setTipoOrganizzatore(TipoOrganizzatore.AZIENDE_SANITARIE);
			
			//provider.addPersona(richiedente);
			provider.addPersona(legale);
			
			provider.setAccount(account);
			providerRepository.save(provider);
			
			personaRepository.save(legale);
			
			LOGGER.info("Provider " + provider.getId() + " created");
		}else{
			LOGGER.info("bootstrap ecm - database not empty...");
		}
	}
}