package it.tredi.ecm.bootstrap;

import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import it.tredi.ecm.dao.entity.Obiettivo;
import it.tredi.ecm.dao.enumlist.CategoriaObiettivoNazionale;
import it.tredi.ecm.service.ObiettivoService;

@Component
@org.springframework.context.annotation.Profile("dev")
public class ObiettiviLoader implements ApplicationListener<ContextRefreshedEvent> {
	private final static Logger LOGGER = LoggerFactory.getLogger(ObiettiviLoader.class);
	
	@Autowired
	private ObiettivoService obiettivoService;
	
	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		LOGGER.info("Bootsrap ECM - Initializing OBIETTIVI EVENTI...");
		
		Set<Obiettivo> obiettivi = obiettivoService.getAllObiettivi();
		
		if(obiettivi.isEmpty()){
			boolean nazionale = true;
			boolean regionale = false;
			
			//Obiettivi Regionali
			obiettivi.add(new Obiettivo("",regionale,null));
			obiettivi.add(new Obiettivo("",regionale,null));
			obiettivi.add(new Obiettivo("",regionale,null));
			obiettivi.add(new Obiettivo("",regionale,null));
			obiettivi.add(new Obiettivo("",regionale,null));
			obiettivi.add(new Obiettivo("",regionale,null));
			
			//Obettivi Nazionali
			obiettivi.add(new Obiettivo("", nazionale, CategoriaObiettivoNazionale.TECNICO_PROFESSIONALI));
			obiettivi.add(new Obiettivo("", nazionale, CategoriaObiettivoNazionale.DI_PROCESSO));
			obiettivi.add(new Obiettivo("", nazionale, CategoriaObiettivoNazionale.DI_SISTEMA));
			
			obiettivoService.save(obiettivi);
		}else{
			LOGGER.info("Bootsrap ECM - OBIETTIVI EVENTI not empty");
		}
	}
}
