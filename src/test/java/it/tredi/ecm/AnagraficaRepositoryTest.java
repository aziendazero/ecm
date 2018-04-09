package it.tredi.ecm;

import java.util.Optional;

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

import it.tredi.ecm.dao.repository.AnagraficaRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("abarducci")
@WithUserDetails("segreteria1")
//@ActiveProfiles("dev")
//@WithUserDetails("LBENEDETTI")
@Rollback(false)
@Ignore
public class AnagraficaRepositoryTest {

	@Autowired private AnagraficaRepository anagraficaRepository;

	@Test
	@Transactional
	@Ignore
	public void getByCodiceFiscale() throws Exception {
		Long providerId = 160L;
		String codiceFiscale = "mRARSS86H01Z112R";
		Optional<Long> anagId = anagraficaRepository.findOneByCodiceFiscaleAndProviderId(codiceFiscale, providerId);

		if(anagId.isPresent())
			System.out.println("id: " + anagId.get());
		else
			System.out.println("id: non trovato");
	}


}
