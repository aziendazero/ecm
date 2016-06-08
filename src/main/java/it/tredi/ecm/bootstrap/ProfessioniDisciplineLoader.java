package it.tredi.ecm.bootstrap;

import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

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
			
			Disciplina d1 = new Disciplina("Allergologia ed immunologia clinica");
			discipline.add(d1);
			Disciplina d2 = new Disciplina("Angiologia");
			discipline.add(d2);
			Disciplina d3 = new Disciplina("Cardiologia");   
			discipline.add(d3);
			Disciplina d4 = new Disciplina("Dermatologia e venereologia");   
			discipline.add(d4);
			Disciplina d5 = new Disciplina("Ematologia");   
			discipline.add(d5);
			Disciplina d6 = new Disciplina("Endocrinologia");   
			discipline.add(d6);
			Disciplina d7 = new Disciplina("Gastroenterologia");   
			discipline.add(d7);
			Disciplina d8 = new Disciplina("Genetica medica");   
			discipline.add(d8);
			Disciplina d9 = new Disciplina("Geriatria");   
			discipline.add(d9);
			Disciplina d10 = new Disciplina("Malattie metaboliche e diabetologia");   
			discipline.add(d10);
			Disciplina d11 = new Disciplina("Malattie dell'apparato respiratorio");   
			discipline.add(d11);
			Disciplina d12 = new Disciplina("Malattie infettive");   
			discipline.add(d12);
			Disciplina d13 = new Disciplina("Medicina e chirurgia di accettazione e di urgenza");   
			discipline.add(d13);
			Disciplina d14 = new Disciplina("Medicina fisica e riabilitazione");   
			discipline.add(d14);
			Disciplina d15 = new Disciplina("Medicina interna");   
			discipline.add(d15);
			Disciplina d16 = new Disciplina("Medicina termale");   
			discipline.add(d16);
			Disciplina d17 = new Disciplina("Medicina aeronautica e spaziale");   
			discipline.add(d17);
			Disciplina d18 = new Disciplina("Medicina dello sport");   
			discipline.add(d18);
			Disciplina d19 = new Disciplina("Nefrologia");   
			discipline.add(d19);
			Disciplina d20 = new Disciplina("Neonatologia");   
			discipline.add(d20);
			Disciplina d21 = new Disciplina("Neurologia");   
			discipline.add(d21);
			Disciplina d22 = new Disciplina("Neuropsichiatria infantile");   
			discipline.add(d22);
			Disciplina d23 = new Disciplina("Oncologia");   
			discipline.add(d23);
			Disciplina d24 = new Disciplina("Pediatria");   
			discipline.add(d24);
			Disciplina d25 = new Disciplina("Psichiatria");   
			discipline.add(d25);
			Disciplina d26 = new Disciplina("Radioterapia");   
			discipline.add(d26);
			Disciplina d27 = new Disciplina("Reumatologia");   
			discipline.add(d27);
			Disciplina d29 = new Disciplina("Cardiochirurgia");   
			discipline.add(d29);
			Disciplina d30 = new Disciplina("Chirurgia generale");   
			discipline.add(d30);
			Disciplina d31 = new Disciplina("Chirurgia maxillo-facciale");   
			discipline.add(d31);
			Disciplina d32 = new Disciplina("Chirurgia pediatrica");   
			discipline.add(d32);
			Disciplina d33 = new Disciplina("Chirurgia plastica e ricostruttiva");   
			discipline.add(d33);
			Disciplina d34 = new Disciplina("Chirurgia toracica");   
			discipline.add(d34);
			Disciplina d35 = new Disciplina("Chirurgia vascolare");   
			discipline.add(d35);
			Disciplina d36 = new Disciplina("Ginecologia e ostetricia");   
			discipline.add(d36);
			Disciplina d37 = new Disciplina("Neurochirurgia");   
			discipline.add(d37);
			Disciplina d38 = new Disciplina("Oftalmologia");   
			discipline.add(d38);
			Disciplina d39 = new Disciplina("Ortopedia e traumatologia");  
			discipline.add(d39);
			Disciplina d40 = new Disciplina("Otorinolaringoiatria");   
			discipline.add(d40);
			Disciplina d41 = new Disciplina("Urologia");  
			discipline.add(d41);
			Disciplina d42 = new Disciplina("Anatomia patologica");  
			discipline.add(d42);
			Disciplina d43 = new Disciplina("Anestesia e rianimazione"); 
			discipline.add(d43);
			Disciplina d44 = new Disciplina("Biochimica clinica");  
			discipline.add(d44);
			Disciplina d45 = new Disciplina("Farmacologia e tossicologia clinica");   
			discipline.add(d45);
			Disciplina d46 = new Disciplina("Laboratorio di genetica medica");
			discipline.add(d46);
			Disciplina d47 = new Disciplina("Medicina trasfusionale"); 
			discipline.add(d47);
			Disciplina d48 = new Disciplina("Medicina legale"); 
			discipline.add(d48);
			Disciplina d49 = new Disciplina("Medicina nucleare"); 
			discipline.add(d49);
			Disciplina d50 = new Disciplina("Microbiologia e virologia");  
			discipline.add(d50);
			Disciplina d51 = new Disciplina("Neurofisiopatologia");  
			discipline.add(d51);
			Disciplina d52 = new Disciplina("Neuroradiologia");
			discipline.add(d52); 
			Disciplina d53 = new Disciplina("Patologia clinica (laboratorio di analisi chimico-cliniche e microbiologia)");   
			discipline.add(d53);
			Disciplina d54 = new Disciplina("Radiodiagnostica");  
			discipline.add(d54);
			Disciplina d55 = new Disciplina("Igiene, epidemiologia e sanità pubblica");  
			discipline.add(d55);
			Disciplina d56 = new Disciplina("Igiene degli alimenti e della nutrizione");  
			discipline.add(d56);
			Disciplina d57 = new Disciplina("Medicina del lavoro e sicurezza degli ambienti di lavoro");  
			discipline.add(d57);
			Disciplina d58 = new Disciplina("Medicina generale (medici di famiglia)");  
			discipline.add(d58);
			Disciplina d59 = new Disciplina("Continuità assistenziale");  
			discipline.add(d59);
			Disciplina d60 = new Disciplina("Pediatria (pediatri di libera scelta)");  
			discipline.add(d60);
			Disciplina d106 = new Disciplina("Scienza dell'alimentazione e dietetica");  
			discipline.add(d106);
			Disciplina d107 = new Disciplina("Direzione medica di presidio ospedaliero");  
			discipline.add(d107);
			Disciplina d108 = new Disciplina("Organizzazione dei servizi sanitari di	base"); 
			discipline.add(d108);
			Disciplina d111 = new Disciplina("Audiologia e foniatria");  
			discipline.add(d111);
			Disciplina d112 = new Disciplina("Psicoterapia"); 
			discipline.add(d112);
			Disciplina d113 = new Disciplina("Privo di specializzazione");  
			discipline.add(d113);
			Disciplina d114 = new Disciplina("Cure palliative");  
			discipline.add(d114);
			Disciplina d115 = new Disciplina("Epidemiologia");  
			discipline.add(d115);
			Disciplina d116 = new Disciplina("Medicina di comunità"); 
			discipline.add(d116);
		
			Disciplina d64 = new Disciplina("Odontoiatria");  
			discipline.add(d64);
		
			Disciplina d66 = new Disciplina("Farmacia ospedaliera");  
			discipline.add(d66);
			Disciplina d67 = new Disciplina("Farmacia territoriale");  
			discipline.add(d67);
		
			Disciplina d61 = new Disciplina("Igiene degli allevamenti e delle produzioni zootecniche"); 
			discipline.add(d61);
			Disciplina d62 = new Disciplina("Igiene prod., trasf., commercial., conserv. E tras. Alimenti di origine animale e derivati");  
			discipline.add(d62);
			Disciplina d63 = new Disciplina("Sanità animale"); 
			discipline.add(d63);
		
			Disciplina d77 = new Disciplina("Psicoterapia"); 
			discipline.add(d77);
			Disciplina d78 = new Disciplina("Psicologia"); 
			discipline.add(d78);
		
			Disciplina d68 = new Disciplina("Biologo");  
			discipline.add(d68);
		
			Disciplina d76 = new Disciplina("Chimica analitica");  
			discipline.add(d76);
		
			Disciplina d79 = new Disciplina("Fisica sanitaria"); 
			discipline.add(d79);
		
			Disciplina d80 = new Disciplina("Assistente sanitario");  
			discipline.add(d80);
		
			Disciplina d81 = new Disciplina("Dietista");  
			discipline.add(d81);
		
			Disciplina d83 = new Disciplina("Educatore professionale");
			discipline.add(d83);
		
			Disciplina d82 = new Disciplina("Fisioterapista"); 
			discipline.add(d82);
		
			Disciplina d84 = new Disciplina("Igienista dentale");
			discipline.add(d84);
		
			Disciplina d85 = new Disciplina("Infermiere"); 
			discipline.add(d85);
		
			Disciplina d86 = new Disciplina("Infermiere pediatrico"); 
			discipline.add(d86);
		
			Disciplina d87 = new Disciplina("Logopedista");  
			discipline.add(d87);
		
			Disciplina d88 = new Disciplina("Ortottista/Assistente di oftalmologia");  
			discipline.add(d88);
		
			Disciplina d89 = new Disciplina("Ostetrica/o"); 
			discipline.add(d89);
		
			Disciplina d90 = new Disciplina("Podologo");  
			discipline.add(d90);
		
			Disciplina d95 = new Disciplina("Tecnico audiometrista");  
			discipline.add(d95);
		
			Disciplina d96 = new Disciplina("Tecnico audioprotesista");  
			discipline.add(d96);
		
			Disciplina d92 = new Disciplina("Tecnico della fisiopatologia cardiocircolatoria e perfusione cardiovascolare"); 
			discipline.add(d92);
		
			Disciplina d105 = new Disciplina("Tecnico della prevenzione nell'ambiente e nei luoghi di lavoro");  
			discipline.add(d105);
		
			Disciplina d91 = new Disciplina("Tecnico della riabilitazione psichiatrica");   
			discipline.add(d91);
		
			Disciplina d98 = new Disciplina("Tecnico di neurofisiopatologia");  
			discipline.add(d98);
		
			Disciplina d99 = new Disciplina("Tecnico ortopedico");  
			discipline.add(d99);
		
			Disciplina d94 = new Disciplina("Tecnico sanitario di radiologia medica");  
			discipline.add(d94);
		
			Disciplina d93 = new Disciplina("Tecnico sanitario laboratorio biomedico"); 
			discipline.add(d93);
		
			Disciplina d100 = new Disciplina("Terapista della neuro e psicomotricità dell'età evolutiva"); 
			discipline.add(d100);
		
			Disciplina d101 = new Disciplina("Terapista occupazionale"); 
			discipline.add(d101);
				
			disciplinaService.saveAll(discipline);
			
			Professione p1 = new Professione("Medico chirurgo");
			p1.addDisciplina(d1);
			p1.addDisciplina(d2);	
			p1.addDisciplina(d3);	
			p1.addDisciplina(d4);	
			p1.addDisciplina(d5);	
			p1.addDisciplina(d6);	
			p1.addDisciplina(d7);	
			p1.addDisciplina(d8);
			p1.addDisciplina(d9);
			p1.addDisciplina(d10);
			p1.addDisciplina(d11);
			p1.addDisciplina(d12);
			p1.addDisciplina(d13);
			p1.addDisciplina(d14);
			p1.addDisciplina(d15);
			p1.addDisciplina(d16);
			p1.addDisciplina(d17);
			p1.addDisciplina(d18);
			p1.addDisciplina(d19);
			p1.addDisciplina(d20);
			p1.addDisciplina(d21);
			p1.addDisciplina(d22);
			p1.addDisciplina(d23);
			p1.addDisciplina(d24);
			p1.addDisciplina(d25);
			p1.addDisciplina(d26);
			p1.addDisciplina(d27);
			p1.addDisciplina(d29);
			p1.addDisciplina(d30);
			p1.addDisciplina(d31);
			p1.addDisciplina(d32);
			p1.addDisciplina(d33);
			p1.addDisciplina(d34);
			p1.addDisciplina(d35);
			p1.addDisciplina(d36);
			p1.addDisciplina(d37);
			p1.addDisciplina(d38);
			p1.addDisciplina(d39);
			p1.addDisciplina(d40);
			p1.addDisciplina(d41);
			p1.addDisciplina(d42);
			p1.addDisciplina(d43);
			p1.addDisciplina(d44);
			p1.addDisciplina(d45);
			p1.addDisciplina(d46);
			p1.addDisciplina(d47);
			p1.addDisciplina(d48);
			p1.addDisciplina(d49);
			p1.addDisciplina(d50);
			p1.addDisciplina(d51);
			p1.addDisciplina(d52); 
			p1.addDisciplina(d53);
			p1.addDisciplina(d54);
			p1.addDisciplina(d55);
			p1.addDisciplina(d56);
			p1.addDisciplina(d57);
			p1.addDisciplina(d58);
			p1.addDisciplina(d59);
			p1.addDisciplina(d60);
			p1.addDisciplina(d106);
			p1.addDisciplina(d107);
			p1.addDisciplina(d108);
			p1.addDisciplina(d111);
			p1.addDisciplina(d112);
			p1.addDisciplina(d113);
			p1.addDisciplina(d114);
			p1.addDisciplina(d115);
			p1.addDisciplina(d116);
			professioneService.save(p1);
			Professione p2 = new Professione("Odontoiatra");
			p2.addDisciplina(d64);	
			professioneService.save(p2);
			Professione p3 = new Professione("Farmacista");
			p3.addDisciplina(d66);	
			p3.addDisciplina(d67);	
			professioneService.save(p3);
			Professione p4 = new Professione("Veterinario");
			p4.addDisciplina(d61);	
			p4.addDisciplina(d62);	
			p4.addDisciplina(d63);	
			professioneService.save(p4);
			Professione p5 = new Professione("Psicologo");
			p5.addDisciplina(d77);	
			p5.addDisciplina(d78);	
			professioneService.save(p5);
			Professione p6 = new Professione("Biologo");
			p6.addDisciplina(d68);	
			professioneService.save(p6);
			Professione p7 = new Professione("Chimico");
			p7.addDisciplina(d76);	
			professioneService.save(p7);
			Professione p8 = new Professione("Fisico");
			p8.addDisciplina(d79);	
			professioneService.save(p8);
			Professione p9 = new Professione("Assistente sanitario");
			p9.addDisciplina(d80);	
			professioneService.save(p9);
			Professione p10 = new Professione("Dietista");
			p10.addDisciplina(d81);	
			professioneService.save(p10);
			Professione p11 = new Professione("Educatore professionale");
			p11.addDisciplina(d83);	
			professioneService.save(p11);
			Professione p12 = new Professione("Fisioterapista");
			p12.addDisciplina(d82);	
			professioneService.save(p12);
			Professione p13 = new Professione("Igienista dentale");
			p13.addDisciplina(d84);	
			professioneService.save(p13);
			Professione p14 = new Professione("Infermiere");
			p14.addDisciplina(d85);	
			professioneService.save(p14);
			Professione p15 = new Professione("Infermiere pediatrico");
			p15.addDisciplina(d86);	
			professioneService.save(p15);
			Professione p16 = new Professione("Logopedista");
			p16.addDisciplina(d87);	
			professioneService.save(p16);
			Professione p17 = new Professione("Ortottista/Assistente di oftalmologia");
			p17.addDisciplina(d88);	
			professioneService.save(p17);
			Professione p18 = new Professione("Ostetrica/o");
			p18.addDisciplina(d89);	
			professioneService.save(p18);
			Professione p19 = new Professione("Podologo");
			p19.addDisciplina(d90);	
			professioneService.save(p19);
			Professione p20 = new Professione("Tecnico audiometrista");
			p20.addDisciplina(d95);	
			professioneService.save(p20);
			Professione p21 = new Professione("Tecnico audioprotesista");
			p21.addDisciplina(d96);	
			professioneService.save(p21);
			Professione p22 = new Professione("Tecnico della fisiopatologia cardiocircolatoria e perfusione cardiovascolare");
			p22.addDisciplina(d92);	
			professioneService.save(p22);
			Professione p23 = new Professione("Tecnico della prevenzione nell'ambiente e nei luoghi di lavoro");
			p23.addDisciplina(d105);	
			professioneService.save(p23);
			Professione p24 = new Professione("Tecnico della riabilitazione psichiatrica");
			p24.addDisciplina(d91);	
			professioneService.save(p24);
			Professione p25 = new Professione("Tecnico di neurofisiopatologia");
			p25.addDisciplina(d98);	
			professioneService.save(p25);
			Professione p26 = new Professione("Tecnico ortopedico");
			p26.addDisciplina(d99);	
			professioneService.save(p26);
			Professione p27 = new Professione("Tecnico sanitario di radiologia medica");
			p27.addDisciplina(d94);	
			professioneService.save(p27);
			Professione p28 = new Professione("Tecnico sanitario laboratorio biomedico");
			p28.addDisciplina(d93);	
			professioneService.save(p28);
			Professione p29 = new Professione("Terapista della neuro e psicomotricità dell'età evolutiva");
			p29.addDisciplina(d100);	
			professioneService.save(p29);
			Professione p30 = new Professione("Terapista occupazionale");
			p30.addDisciplina(d101);	
			professioneService.save(p30);
		}
	}
}
