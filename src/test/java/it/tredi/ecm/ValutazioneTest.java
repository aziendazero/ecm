package it.tredi.ecm;

import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.repository.ValutazioneRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("abarducci")
@WithUserDetails("segreteria")
@Ignore
public class ValutazioneTest {

	@Autowired
	private ValutazioneRepository valutazioneRepository;

	@Test
	@Ignore
	public void loadValutazioni() throws Exception{
		Set<Valutazione> listValutazioni = valutazioneRepository.findAllByAccreditamentoIdAndStoricizzatoFalseOrderByAccount(1221L);
		System.out.println("listValutazioni.size(): " + listValutazioni.size());
	}
}
