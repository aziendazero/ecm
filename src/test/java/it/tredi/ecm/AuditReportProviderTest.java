package it.tredi.ecm;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.repository.ProviderRepository;
import it.tredi.ecm.service.AuditReportProviderService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("abarducci")
@WithUserDetails("segreteria1")
@Rollback(false)
//@Ignore
public class AuditReportProviderTest {

	@Autowired private ProviderRepository providerRepository;

	@Autowired private AuditReportProviderService auditReportProviderService;

	@Test
	@Transactional
	public void testAudit() throws Exception {
		//INSERITO
		//Provider provider = providerRepository.findOne(164L);
		//VALIDATO
		//Provider provider = providerRepository.findOne(162L);
		//"ACCREDITATO_PROVVISORIAMENTE"
		//Provider provider = providerRepository.findOne(160L);
		// - Accreditamento provvisoorio "ACCREDITATO_IN_FIRMA"
		Provider provider = providerRepository.findOne(160L);
		System.out.println("Status: " + provider.getStatus());
		provider.setStatus(ProviderStatoEnum.ACCREDITATO_PROVVISORIAMENTE);
		auditReportProviderService.auditAccreditamentoProvider(provider);

	}

}
