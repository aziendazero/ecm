package it.tredi.ecm.service.controller;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.exception.AccreditamentoNotFoundException;
import it.tredi.ecm.service.AccreditamentoService;

@Component
public class ProviderServiceController {
	private static Logger LOGGER = LoggerFactory.getLogger(ProviderServiceController.class);

	@Autowired private AccreditamentoService accreditamentoService;

	public LocalDate getDataFineAccreditamentoAttivoForProvider(Provider provider) {
		Accreditamento accreditamento;
		try {
			accreditamento = accreditamentoService.getAccreditamentoAttivoForProvider(provider.getId());
			return accreditamento.getDataFineAccreditamento();
		} catch (AccreditamentoNotFoundException e) {
			LOGGER.warn("Il provider: " + provider.getId() + " non ha accreditamenti attivi", e);
		}
		return null;
	}
	
}
