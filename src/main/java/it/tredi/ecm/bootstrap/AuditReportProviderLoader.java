package it.tredi.ecm.bootstrap;

import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.repository.AuditReportProviderRepository;
import it.tredi.ecm.dao.repository.ProviderRepository;
import it.tredi.ecm.service.AuditReportProviderService;

@Component
/**
 * Bootstrap utilizzato per aggiornare il pregresso delle AuditReportProvider di tutti i provider presenti
 *
 */
public class AuditReportProviderLoader implements ApplicationListener<ContextRefreshedEvent> {
	private final static Logger LOGGER = LoggerFactory.getLogger(AuditReportProviderLoader.class);

	@Autowired private ProviderRepository providerRepository;
	@Autowired private AuditReportProviderRepository auditReportProviderRepository;
	@Autowired private AuditReportProviderService auditReportProviderService;

	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event){
		String intesta = "BOOTSTRAP ECM - Inizializzazione AUDIT REPORT PROVIDER";
		LOGGER.info(intesta + "...");

		if(auditReportProviderRepository.count() == 0) {
			//Non ci sono AuditReportProvider aggiorno l'eventuale pregresso
			LOGGER.info(intesta + " Nessun AuditReportProvider trovato - Aggiorno eventuale pregresso");
			List<Provider> providers = providerRepository.findAll();
			if(providers != null && !providers.isEmpty()) {
				for(Provider provider : providers) {
					LOGGER.info(intesta + " Aggiorno AuditReportProvider per provider id: " + provider.getId() + "; status: " + provider.getStatus());
					auditReportProviderService.auditAccreditamentoProvider(provider);
				}
			}
			LOGGER.info(intesta + " COMNPLETATO");
		} else {
			LOGGER.info(intesta + " già inizializzato");
		}

	}
}