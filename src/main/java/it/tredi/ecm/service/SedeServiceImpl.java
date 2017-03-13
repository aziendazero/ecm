package it.tredi.ecm.service;

import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.audit.entity.ProviderAudit;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.repository.SedeRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class SedeServiceImpl implements SedeService{
	private static Logger LOGGER = LoggerFactory.getLogger(SedeServiceImpl.class);

	@Autowired private SedeRepository sedeRepository;
	@Autowired private ProviderService providerService;
	@Autowired private AuditService auditService;

	@Override
	public Sede getSede(Long id) {
		LOGGER.debug("Recupero Sede: " + id);
		return sedeRepository.findOne(id);
	}

	@Override
	@Transactional
	public void save(Sede sede) {
		LOGGER.debug("Salvataggio Sede");
		sedeRepository.saveAndFlush(sede);
		saveAuditProvider(sede);
	}

	private void saveAuditProvider(Sede sede) {
		if(!sede.isDirty() && sede.getProvider() != null)
			auditService.commitForCurrrentUser(new ProviderAudit(sede.getProvider()));
	}


	@Override
	@Transactional
	public void save(Sede sede, Provider provider) {
		save(sede);
		provider.addSede(sede);
		providerService.save(provider);
	}

	@Override
	public void delete(Long sedeId) {
		LOGGER.debug("Eliminazione Sede");
		Sede sede = sedeRepository.findOne(sedeId);
		sedeRepository.delete(sedeId);
		sedeRepository.flush();
		saveAuditProvider(sede);
	}

	@Transactional
	@Override
	public void saveFromIntegrazione(Sede sede){
		LOGGER.debug(Utils.getLogMessage("Salvataggio Sede da Integrazione"));
		sede.setDirty(false);
		save(sede);
	}

	@Transactional
	@Override
	public void deleteFromIntegrazione(Long id){
		LOGGER.debug("Eliminazione Sede da Integrazione" + id);
		delete(id);
	}

	@Override
	public Set<Sede> getSediFromIntegrazione(Long providerId) {
		LOGGER.debug("Recupero sedi per approvazione integrazione per il provider:" + providerId);
		return sedeRepository.findAllByProviderId(providerId);
	}
}
