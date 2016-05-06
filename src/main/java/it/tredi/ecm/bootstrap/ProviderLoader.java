package it.tredi.ecm.bootstrap;

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
import it.tredi.ecm.dao.repository.AccountRepository;
import it.tredi.ecm.dao.repository.PersonaRepository;
import it.tredi.ecm.dao.repository.ProviderRepository;

@Component
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
		LOGGER.info("Initializing DATABASE...");
		Persona richiedente = new Persona();
		richiedente.getAnagrafica().setCognome("Pranteda");
		richiedente.getAnagrafica().setNome("Domenico");
		richiedente.getAnagrafica().setCodiceFiscale("PRNDNC86H23Z112R");
		richiedente.getAnagrafica().setCellulare("3336580687");
		richiedente.getAnagrafica().setTelefono("098343645");
		richiedente.getAnagrafica().setEmail("dompranteda@gmail.com");
		richiedente.getAnagrafica().setPec("dompranteda@pec.com");
		personaRepository.save(richiedente);
		
		LOGGER.info("Persona " + richiedente.getId() + " created");
		
		Persona legale = new Persona();
		legale.getAnagrafica().setCognome("Bernardini");
		legale.getAnagrafica().setNome("Mirko");
		legale.getAnagrafica().setCodiceFiscale("BCCRSL89H47D969I");
		legale.getAnagrafica().setCellulare("123456789");
		legale.getAnagrafica().setTelefono("098342742");
		legale.getAnagrafica().setEmail("mbernardini@3di.it");
		legale.getAnagrafica().setPec("mbernardini@pec.it");
		personaRepository.save(legale);
		
		LOGGER.info("Persona " + legale.getId() + " created");
		
		richiedente.setRuolo(Ruolo.RICHIEDENTE.getNome());
		legale.setRuolo(Ruolo.LEGALE_RAPPRESENTANTE.getNome());
		Account account = accountRepository.findOneByUsername("admin").orElse(null);
		
		Provider provider = new Provider();
		provider.setDenominazioneLegale("3D Informatica");
		provider.setCfPiva("00578261208");
		provider.setTipoOrganizzatore("Azienda Privata");
		
		//provider.addPersona(richiedente);
		provider.addPersona(legale);
		
		provider.setAccount(account);
		providerRepository.save(provider);
		
		legale.getAnagrafica().setCognome("pippo");
		personaRepository.save(legale);
		
		LOGGER.info("Provider " + provider.getId() + " created");
	}
}