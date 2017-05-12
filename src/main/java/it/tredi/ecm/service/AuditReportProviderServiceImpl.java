package it.tredi.ecm.service;

import java.time.LocalDateTime;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.AuditReportProvider;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.repository.AuditReportProviderRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class AuditReportProviderServiceImpl implements AuditReportProviderService {

	private static Logger LOGGER = LoggerFactory.getLogger(AuditReportProviderServiceImpl.class);

	@Autowired private AuditReportProviderRepository auditReportProviderStatusRepository;
	//@Autowired private ProviderService providerService;
	@Autowired private AccreditamentoService acreditamentoService;

	@Override
	public void auditAccreditamentoProvider(Provider provider) {
		LOGGER.debug(Utils.getLogMessage("Audit reportistica Provider"));
		//in caso di stato:
		//		INSERITO (1, "Inserito - Domanda in stato di bozza"),
		//		VALIDATO (2, "Domanda inviata"),
		//non faccio nulla
		if(checkProviderAuditReport(provider)) {
			DatiAccreditamento datiAccreditamento = null;
			try {
				Accreditamento accreditamento = acreditamentoService.getAccreditamentoAttivoOppureUltimoForProvider(provider.getId());
				datiAccreditamento = accreditamento.getDatiAccreditamento();
			} catch(Exception e) {
				//potrei non aver un accreditamento attivo o ultimo non dovrebbe capitare
			}

			//Carico l'ultimo audit
			AuditReportProvider auditReportProvider = auditReportProviderStatusRepository.findOneByProviderAndDataFine(provider, null);
//			AuditReportProviderStatus auditReportProvider = null;
//			try {
//				auditReportProvider = auditReportProviderStatusRepository.findOneByProviderAndDataFine(provider, null);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			LocalDateTime currentDateTime = LocalDateTime.now();
			if(auditReportProvider == null) {
				//lo inserisco
				auditReportProvider = new AuditReportProvider(provider, datiAccreditamento);
				auditReportProvider.setDataInizio(currentDateTime);
				auditReportProviderStatusRepository.save(auditReportProvider);
			} else {
				if( isProviderDatiAccreditamentoChanged(auditReportProvider, provider, datiAccreditamento)) {
					auditReportProvider.setDataFine(currentDateTime);
					auditReportProviderStatusRepository.save(auditReportProvider);

					auditReportProvider = new AuditReportProvider(provider, datiAccreditamento);
					auditReportProvider.setDataInizio(currentDateTime);
					auditReportProviderStatusRepository.save(auditReportProvider);
				}
			}
		}
	}

	private boolean isProviderDatiAccreditamentoChanged(AuditReportProvider auditReportProvider, Provider provider, DatiAccreditamento datiAccreditamento) {
		return auditReportProvider.getStatus() != provider.getStatus() ||
			stringDiff(auditReportProvider.getNaturaOrganizzazione(), provider.getNaturaOrganizzazione()) ||
			auditReportProvider.getTipoOrganizzatore() != provider.getTipoOrganizzatore() ||

			stringDiff(auditReportProvider.getSedeLegaleAltroTelefono(), provider.getSedeLegale() == null ? null : provider.getSedeLegale().getAltroTelefono()) ||
			stringDiff(auditReportProvider.getSedeLegaleCap(), provider.getSedeLegale() == null ? null : provider.getSedeLegale().getCap()) ||
			stringDiff(auditReportProvider.getSedeLegaleComune(), provider.getSedeLegale() == null ? null : provider.getSedeLegale().getComune()) ||
			stringDiff(auditReportProvider.getSedeLegaleEmail(), provider.getSedeLegale() == null ? null : provider.getSedeLegale().getEmail()) ||
			stringDiff(auditReportProvider.getSedeLegaleFax(), provider.getSedeLegale() == null ? null : provider.getSedeLegale().getFax()) ||
			stringDiff(auditReportProvider.getSedeLegaleIndirizzo(), provider.getSedeLegale() == null ? null : provider.getSedeLegale().getIndirizzo()) ||
			stringDiff(auditReportProvider.getSedeLegaleProvincia(), provider.getSedeLegale() == null ? null : provider.getSedeLegale().getProvincia()) ||
			stringDiff(auditReportProvider.getSedeLegaleTelefono(), provider.getSedeLegale() == null ? null : provider.getSedeLegale().getTelefono()) ||

			!setEquals(auditReportProvider.getProcedureFormative(), datiAccreditamento == null ? null : datiAccreditamento.getProcedureFormative()) ||
			!setEquals(auditReportProvider.getDiscipline(), datiAccreditamento == null ? null : datiAccreditamento.getDiscipline());
	}

	private boolean stringDiff(String str1, String str2) {
        if(str1 == null) {
        	if(str2 == null || str2.isEmpty())
        		return false;
        	else
    			return true;
        }
        return !str1.equals(str2);
    }

	private boolean setEquals(Set<?> set1, Set<?> set2) {
        if(set1 == null || set1.isEmpty()) {
        	if(set2 == null || set2.isEmpty())
        		return true;
        	else
    			return false;
        }
        if(set2 == null || set2.isEmpty()) {
    		return false;
        }

        if(set1.size() != set2.size()) {
            return false;
        }
        return set1.containsAll(set2);
    }

	private boolean checkProviderAuditReport(Provider provider) {
		//return provider.getStatus() != ProviderStatoEnum.INSERITO && provider.getStatus() != ProviderStatoEnum.VALIDATO;
		return provider.getStatus() != ProviderStatoEnum.INSERITO;
	}

}
