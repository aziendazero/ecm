package it.tredi.ecm;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoFAD;
import it.tredi.ecm.dao.entity.EventoFSC;
import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.service.EventoService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING) // ordina i test in base al nome crescente
@Rollback(false)
public class RiedizioneEventoTest {

	@Autowired private WebApplicationContext webApplicationContext;
	@Autowired private EventoService eventoService;

	private Evento eventoDaRieditare;
	private MockMvc mockMvc;

	//@BeforeClass
	@Before
	@Transactional
	public void init() {


		eventoDaRieditare = eventoService.getEvento(564L);

	}

	//@AfterClass
	//@After
	public void clean(){
	}

	@Test
	//@Ignore
	public void riedizione() throws Exception{
		System.out.println("Inizio test");

		System.out.println(eventoDaRieditare);

		Evento riedizione;
		switch(eventoDaRieditare.getProceduraFormativa()){
			case FAD: riedizione = new EventoFAD(); break;
			case RES: riedizione = new EventoRES(); break;
			case FSC: riedizione = new EventoFSC(); break;
			default: riedizione = new Evento(); break;
		}
		if(riedizione != null && riedizione.isNew())
			System.out.println("Creazione Evento: success");
		else
			System.out.println("Creazione Evento: fail");


		System.out.println("Fine test");
	}
}