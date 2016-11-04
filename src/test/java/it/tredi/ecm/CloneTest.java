package it.tredi.ecm;

import javax.transaction.Transactional;

import org.apache.commons.lang3.SerializationUtils;
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

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.utils.Utils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@Ignore
@ActiveProfiles("abarducci")
@WithUserDetails("segreteria")
@Rollback(false)
public class CloneTest {

	@Autowired
	private EventoService eventoService;
	
	@Test
	@Ignore
	@Transactional
	public void testClone() throws Exception {
		Evento evento = eventoService.getEvento(1910L);
		//Evento eventoClone = SerializationUtils.clone(evento);
		Evento eventoClone = (Evento)Utils.copy(evento);
		
		System.out.println("evento.getProvider().getId(): " + evento.getProvider().getId());
		System.out.println("eventoClone.getProvider().getId(): " + eventoClone.getProvider().getId());
	}
	


}
