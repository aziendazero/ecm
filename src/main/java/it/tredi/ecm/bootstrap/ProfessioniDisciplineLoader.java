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
			Disciplina d3 = new Disciplina("d3");
			discipline.add(d3);
			Disciplina d4 = new Disciplina("d4");
			discipline.add(d4);
			Disciplina d5 = new Disciplina("d5");
			discipline.add(d5);
			Disciplina d6 = new Disciplina("d6");
			discipline.add(d6);
			Disciplina d7 = new Disciplina("d7");
			discipline.add(d7);
			Disciplina d8 = new Disciplina("d8");
			discipline.add(d8);
			Disciplina d9 = new Disciplina("d9");
			discipline.add(d9);
			Disciplina d10 = new Disciplina("d10");
			discipline.add(d10);
			
			disciplinaService.saveAll(discipline);
			
			Professione p1 = new Professione("p1");
			p1.addDisciplina(d1);
			p1.addDisciplina(d2);
			p1.addDisciplina(d3);
			p1.addDisciplina(d4);
			Professione p2 = new Professione("p2");
			p2.addDisciplina(d5);
			p2.addDisciplina(d6);
			Professione p3 = new Professione("p3");
			p3.addDisciplina(d7);
			Professione p4 = new Professione("p4");
			p4.addDisciplina(d8);
			Professione p5 = new Professione("p5");
			p5.addDisciplina(d9);
			p5.addDisciplina(d10);

			professioneService.save(p1);
			professioneService.save(p2);
			professioneService.save(p3);
			professioneService.save(p4);
			professioneService.save(p5);
		}
	}
}
