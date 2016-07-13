package it.tredi.ecm.service;

import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.Costanti;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.StatusProvider;
import it.tredi.ecm.dao.repository.ProviderRepository;
import it.tredi.ecm.service.bean.CurrentUser;
import it.tredi.ecm.service.bean.ProviderRegistrationWrapper;
import it.tredi.ecm.utils.Utils;

@Service
public class ProviderServiceImpl implements ProviderService {

	private final Logger LOGGER = Logger.getLogger(ProviderService.class);

	@Autowired
	private ProviderRepository providerRepository;
	@Autowired
	private PersonaService personaService;
	@Autowired
	private ProfileAndRoleService profileAndRoleService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private FileService fileService;

	@Override
	public Provider getProvider() {
		LOGGER.info("Retrieving Provider for current user...");
		CurrentUser currentUser = Utils.getAuthenticatedUser();
		if(currentUser != null){
			Provider provider = providerRepository.findOneByAccountId(currentUser.getAccount().getId());
			if(provider != null){
				LOGGER.info("Found Provider (" + provider.getId() +")");
				return provider;
			}
			LOGGER.info("Provider not found");
		}else{
			LOGGER.info("User not sign in");
			return new Provider();
		}
		return null;
	}

	@Override
	public Provider getProvider(Long id){
		LOGGER.info("Retrieving Provider (" + id +")");
		return providerRepository.findOne(id);
	}

	@Override
	public Provider getProviderByCodiceFiscale(String codiceFiscale) {
		LOGGER.info("Retrieving Provider (" + codiceFiscale +")");
		return providerRepository.findOneByCodiceFiscale(codiceFiscale);
	}
	
	@Override
	public Provider getProviderByPartitaIva(String partitaIva) {
		LOGGER.info("Retrieving Provider (" + partitaIva +")");
		return providerRepository.findOneByPartitaIva(partitaIva);
	}

	@Override
	public Set<Provider> getAll(){
		LOGGER.info("Retrieving all Providers");
		return providerRepository.findAll();
	}

	@Override
	@Transactional
	public void save(Provider provider) {
		LOGGER.info("Saving Provider");
		if(provider.getAccount().isNew()){
			try{
				accountService.save(provider.getAccount());
			}catch (Exception ex){
				LOGGER.error("Impossibile salvare il Provider. Errore durante creazione Account",ex);
			}
		}
		providerRepository.save(provider);
	}
	
	@Override
	public Set<String> getFileTypeUploadedByProviderId(Long id) {
		LOGGER.debug("Recupero i tipi di file presenti per il provider: " + id);
		return providerRepository.findAllFileTipoByProviderId(id);
	}
	
	@Override
	public ProviderRegistrationWrapper getProviderRegistrationWrapper() {
		ProviderRegistrationWrapper providerRegistrationWrapper = new ProviderRegistrationWrapper();
		Provider provider = new Provider();
		providerRegistrationWrapper.setProvider(provider);
		
		if(provider.isNew()){
			File delega = new File();
			providerRegistrationWrapper.setDelega(delega);
			providerRegistrationWrapper.setRichiedente(new Persona(Ruolo.RICHIEDENTE));
			providerRegistrationWrapper.setLegale(new Persona(Ruolo.LEGALE_RAPPRESENTANTE));
		}else{
			Persona richiedente = personaService.getPersonaByRuolo(Ruolo.RICHIEDENTE, provider.getId());
			if(richiedente == null){
				richiedente = new Persona(Ruolo.RICHIEDENTE);
				provider.addPersona(richiedente);
			}
			
			Persona legale = personaService.getPersonaByRuolo(Ruolo.LEGALE_RAPPRESENTANTE, provider.getId());
			if(legale == null){
				legale = new Persona(Ruolo.LEGALE_RAPPRESENTANTE);
				provider.addPersona(legale);
			}
			
			File delega = new File();
			providerRegistrationWrapper.setDelega(delega);
			providerRegistrationWrapper.setRichiedente(richiedente);
			providerRegistrationWrapper.setLegale(legale);
		}
		
		return providerRegistrationWrapper;
	}

	@Override
	@Transactional
	public void saveProviderRegistrationWrapper(ProviderRegistrationWrapper providerRegistrationWrapper) {
		Provider provider = providerRegistrationWrapper.getProvider();
		Persona richiedente = providerRegistrationWrapper.getRichiedente();
		if(providerRegistrationWrapper.isDelegato())
			richiedente.setRuolo(Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE);
		Persona legale = providerRegistrationWrapper.getLegale();
		File delegaRichiedente = providerRegistrationWrapper.getDelega();
		
		if(provider.getAccount().getProfiles().isEmpty()){
			//assegno profilo PROVIDER TODO 
			Optional<Profile> providerProfile = profileAndRoleService.getProfileByName(Costanti.PROFILO_PROVIDER);
			if(providerProfile.isPresent())
				provider.getAccount().getProfiles().add(providerProfile.get());
		}
		
		provider.setStatus(StatusProvider.INSERITO);
		save(provider);
		
		//TODO Delegato consentito solo per alcuni tipi di Provider
		if(providerRegistrationWrapper.isDelegato()){
			delegaRichiedente.setTipo(FileEnum.FILE_DELEGA);
			fileService.save(delegaRichiedente);
			richiedente.addFile(delegaRichiedente);
		}
		
		provider.addPersona(richiedente);
		personaService.save(richiedente);
		provider.addPersona(legale);
		personaService.save(legale);
	}
	
	@Override
	public Long getAccountIdForProvider(Long providerId) {
		return providerRepository.getAccountIdById(providerId);
	}
}
