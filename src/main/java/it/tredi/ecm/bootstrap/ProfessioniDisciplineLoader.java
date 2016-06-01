package it.tredi.ecm.bootstrap;

import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.service.DisciplinaService;
import it.tredi.ecm.service.ProfessioneService;

@Component
@org.springframework.context.annotation.Profile("dev")
public class ProfessioniDisciplineLoader implements ApplicationListener<ContextRefreshedEvent> {
	private final static Logger LOGGER = LoggerFactory.getLogger(ProfessioniDisciplineLoader.class);
	
	@Autowired
	private ProfessioneService professioneService;
	@Autowired
	private DisciplinaService disciplinaService;
	
	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		LOGGER.info("Initializing PROFESSIONI / DISCIPLINE...");
		
		//Set<Professione> professioni = professioneService.getAllProfessioni();
		Set<Disciplina> discipline = disciplinaService.getAllDiscipline();
			
		if(discipline.isEmpty()){
			Disciplina d1 = new Disciplina("d1");
			discipline.add(d1);
			Disciplina d2 = new Disciplina("d2");
			discipline.add(d2);
			//.....
			
			disciplinaService.saveAll(discipline);
			
			Professione p1 = new Professione();
			p1.addDisciplina(d1);
			p1.addDisciplina(d2);
			professioneService.save(p1);
		}
	}
}
