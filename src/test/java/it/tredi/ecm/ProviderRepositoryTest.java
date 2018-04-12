package it.tredi.ecm;

import javax.transaction.Transactional;

import org.junit.Ignore;
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
import it.tredi.ecm.dao.repository.ProviderRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("abarducci")
@WithUserDetails("segreteria1")
//@ActiveProfiles("dev")
//@WithUserDetails("LBENEDETTI")
@Rollback(false)
@Ignore
public class ProviderRepositoryTest {

	@Autowired private ProviderRepository providerRepository;

	@Test
	@Transactional
	//@Ignore
	public void getByCodiceFiscale() throws Exception {
		String codiceFiscale = "YYSQCR90A41C774d";
		Provider provider = providerRepository.findOneByCodiceFiscaleIgnoreCase(codiceFiscale);

		if(provider != null)
			System.out.println("provider id: " + provider.getId());
		else
			System.out.println("provider: non trovato");
	}


}
