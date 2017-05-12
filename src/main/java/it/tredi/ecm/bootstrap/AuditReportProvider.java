package it.tredi.ecm.bootstrap;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.enumlist.RagioneSocialeEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import it.tredi.ecm.dao.repository.AccountRepository;
import it.tredi.ecm.dao.repository.AuditReportProviderRepository;
import it.tredi.ecm.dao.repository.PersonaRepository;
import it.tredi.ecm.dao.repository.ProviderRepository;
import it.tredi.ecm.dao.repository.SedeRepository;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.AuditReportProviderService;
import it.tredi.ecm.service.DatiAccreditamentoService;
import it.tredi.ecm.service.DisciplinaService;
import it.tredi.ecm.service.WorkflowService;

@Component
//@org.springframework.context.annotation.Profile({"simone","abarducci", "tom", "joe19","dev"})
public class AuditReportProvider implements ApplicationListener<ContextRefreshedEvent> {
	private final static Logger LOGGER = LoggerFactory.getLogger(AuditReportProvider.class);

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