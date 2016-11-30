package it.tredi.ecm;

import java.util.Set;

import javax.transaction.Transactional;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.ProfileEnum;
import it.tredi.ecm.service.AccountService;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.ValutazioneService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("tom")
@WithUserDetails("segreteria")
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING) // ordina i test in base al nome crescente
@Rollback(false)
@Ignore
public class StoricoValutazioneTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private ValutazioneService valutazioneService;
	@Autowired private AccountService accountService;

	private Long providerId;
	private Long accreditamentoId;
	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
	}

	//@Before
	@Transactional
	public void init() {
	}

	@After
	public void clean(){
	}

	@Test
	@Transactional
//	@Ignore
	public void provaCopiaValutazione() throws Exception{
		Valutazione valutazione = valutazioneService.getValutazione(1355L);

		copiaInStorico(valutazione);

	}

	private void copiaInStorico(Valutazione valutazione) throws Exception {
		Valutazione valStoricizzata = valutazioneService.detachValutazione(valutazione);
		valutazioneService.cloneDetachedValutazione(valStoricizzata);
		valStoricizzata.setStoricizzato(true);
		valutazioneService.save(valStoricizzata);
	}


}
