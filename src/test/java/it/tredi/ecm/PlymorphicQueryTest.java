package it.tredi.ecm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
import it.tredi.ecm.dao.enumlist.TipologiaEventoFADEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoRESEnum;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.utils.Utils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@WithUserDetails("LBENEDETTI")
@Rollback(false)
@Ignore
public class PlymorphicQueryTest {

	@Autowired private EventoService eventoService;
	@PersistenceContext EntityManager entityManager;
	
	@Test
	@Transactional
	public void query() throws Exception {
		
		Set<TipologiaEventoRESEnum> tipologieRES = new HashSet<TipologiaEventoRESEnum>();
		Set<TipologiaEventoFSCEnum> tipologieFSC = new HashSet<TipologiaEventoFSCEnum>();
		Set<TipologiaEventoFADEnum> tipologieFAD = new HashSet<TipologiaEventoFADEnum>();
		
		tipologieRES.addAll(Arrays.asList(TipologiaEventoRESEnum.values()));
		tipologieFSC.addAll(Arrays.asList(TipologiaEventoFSCEnum.values()));
		tipologieFAD.addAll(Arrays.asList(TipologiaEventoFADEnum.values()));
		
		String query = "SELECT e FROM Evento e WHERE e.tipologiaEventoRES IN :tipologieRES";

		Query q = entityManager.createQuery(query, Evento.class);
		q.setParameter("tipologieRES", tipologieRES);
		//q.setParameter("titoloConvegno", "titoloTest");

		List<Evento> result = q.getResultList(); 
		
		System.out.println(result.size());
	}
}
