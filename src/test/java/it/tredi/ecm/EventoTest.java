package it.tredi.ecm;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

//import it.tredi.ecm.dao.entity.AnagraficaFullEvento;
import it.tredi.ecm.dao.entity.PersonaEvento;
//import it.tredi.ecm.dao.entity.PersonaFullEvento;
import it.tredi.ecm.dao.entity.PersonaFullEvento;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
//@WithUserDetails("provider")
@Ignore
public class EventoTest {

	@Test
	public void loadEvento() throws Exception {
		/*
		PersonaEvento personaEvento = new PersonaEvento();
		PersonaFullEvento personaFullEvento = new PersonaFullEvento();
		AnagraficaFullEvento anagFullEvento = new AnagraficaFullEvento();
		//AnagraficaFullEvento anagFullEvento = new AnagraficaFullEvento();
		
		
		System.out.println("loadEvento");
		*/
		PersonaFullEvento pfev = new PersonaFullEvento();
	}	
}
