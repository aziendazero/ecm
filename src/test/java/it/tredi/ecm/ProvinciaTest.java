package it.tredi.ecm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.tredi.ecm.dao.entity.AnagraficaEvento;
import it.tredi.ecm.dao.entity.AnagraficaEventoBase;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.dao.entity.JsonViewModel;
//import it.tredi.ecm.dao.entity.AnagraficaFullEvento;
import it.tredi.ecm.dao.entity.PersonaEvento;
//import it.tredi.ecm.dao.entity.PersonaFullEvento;
import it.tredi.ecm.dao.entity.PersonaFullEvento;
import it.tredi.ecm.dao.entity.Provincia;
import it.tredi.ecm.dao.repository.EventoRepository;
import it.tredi.ecm.dao.repository.PersonaEventoRepository;
import it.tredi.ecm.dao.repository.ProvinciaRepository;
import it.tredi.ecm.service.AnagraficaEventoService;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.ProviderService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("abarducci")
//@WithUserDetails("provider")
@Rollback(false)
@Ignore
public class ProvinciaTest {

	@Autowired private ProvinciaRepository provinciaReppository;
	@Autowired private ObjectMapper jacksonObjectMapper;
	
	@Test
	@Transactional
	@Ignore
	public void loadProvincia() throws Exception {
		//List<Provincia> province = provinciaReppository.findAll();
		List<Provincia> province = provinciaReppository.findAllByOrderByNomeAsc();
		
		//Provincia p = provinciaReppository.findOne("001");
		//System.out.println(p.getSigla());
		
		List<Provincia> provinceVeneto = new ArrayList<Provincia>();
		for(Provincia prov : province) {
			if(prov.getCodiceRegione().equals("050")) {
				System.out.println(prov.getNome() + " regione: " + prov.getCodiceRegione() + " - Comuni size: " + prov.getComuni().size());
				provinceVeneto.add(prov);
			}
		}
		
		String json = jacksonObjectMapper.writerWithView(JsonViewModel.Provincia.class).writeValueAsString(provinceVeneto);
		System.out.println(json.replace("'", "\\'"));
	}	
	
}
