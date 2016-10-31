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
@org.springframework.context.annotation.Profile({"dev","demo","prod","simone","abarducci"})
public class ProfessioniDisciplineLoader implements ApplicationListener<ContextRefreshedEvent> {
	private final static Logger LOGGER = LoggerFactory.getLogger(ProfessioniDisciplineLoader.class);

	@Autowired
	private ProfessioneService professioneService;
	@Autowired
	private DisciplinaService disciplinaService;

	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		LOGGER.info("BOOTSTRAP ECM - Inizializzazione PROFESSIONI/DISCIPLINE...");

		//Set<Professione> professioni = professioneService.getAllProfessioni();
		Set<Disciplina> disciplinea = disciplinaService.getAllDiscipline();

		if(disciplinea.isEmpty()){
			Disciplina d1 = new Disciplina("Allergologia ed immunologia clinica", "1");
			disciplinaService.save(d1);

			Disciplina d2 = new Disciplina("Angiologia", "2");
			disciplinaService.save(d2);

			Disciplina d3 = new Disciplina("Cardiologia", "3");
			disciplinaService.save(d3);

			Disciplina d4 = new Disciplina("Dermatologia e venereologia", "4");
			disciplinaService.save(d4);

			Disciplina d5 = new Disciplina("Ematologia", "5");
			disciplinaService.save(d5);

			Disciplina d6 = new Disciplina("Endocrinologia", "6");
			disciplinaService.save(d6);

			Disciplina d7 = new Disciplina("Gastroenterologia", "7");
			disciplinaService.save(d7);

			Disciplina d8 = new Disciplina("Genetica medica", "8");
			disciplinaService.save(d8);

			Disciplina d9 = new Disciplina("Geriatria", "9");
			disciplinaService.save(d9);

			Disciplina d10 = new Disciplina("Malattie metaboliche e diabetologia", "10");
			disciplinaService.save(d10);

			Disciplina d11 = new Disciplina("Malattie dell'apparato respiratorio", "11");
			disciplinaService.save(d11);

			Disciplina d12 = new Disciplina("Malattie infettive", "12");
			disciplinaService.save(d12);

			Disciplina d13 = new Disciplina("Medicina e chirurgia di accettazione e di urgenza", "13");
			disciplinaService.save(d13);

			Disciplina d14 = new Disciplina("Medicina fisica e riabilitazione", "14");
			disciplinaService.save(d14);

			Disciplina d15 = new Disciplina("Medicina interna", "15");
			disciplinaService.save(d15);

			Disciplina d16 = new Disciplina("Medicina termale", "16");
			disciplinaService.save(d16);

			Disciplina d17 = new Disciplina("Medicina aeronautica e spaziale", "17");
			disciplinaService.save(d17);

			Disciplina d18 = new Disciplina("Medicina dello sport", "18");
			disciplinaService.save(d18);

			Disciplina d19 = new Disciplina("Nefrologia", "19");
			disciplinaService.save(d19);

			Disciplina d20 = new Disciplina("Neonatologia", "20");
			disciplinaService.save(d20);

			Disciplina d21 = new Disciplina("Neurologia", "21");
			disciplinaService.save(d21);

			Disciplina d22 = new Disciplina("Neuropsichiatria infantile", "22");
			disciplinaService.save(d22);

			Disciplina d23 = new Disciplina("Oncologia", "23");
			disciplinaService.save(d23);

			Disciplina d24 = new Disciplina("Pediatria", "24");
			disciplinaService.save(d24);

			Disciplina d25 = new Disciplina("Psichiatria", "25");
			disciplinaService.save(d25);

			Disciplina d26 = new Disciplina("Radioterapia", "26");
			disciplinaService.save(d26);

			Disciplina d27 = new Disciplina("Reumatologia", "27");
			disciplinaService.save(d27);

			Disciplina d29 = new Disciplina("Cardiochirurgia", "29");
			disciplinaService.save(d29);

			Disciplina d30 = new Disciplina("Chirurgia generale", "30");
			disciplinaService.save(d30);

			Disciplina d31 = new Disciplina("Chirurgia maxillo-facciale", "31");
			disciplinaService.save(d31);

			Disciplina d32 = new Disciplina("Chirurgia pediatrica", "32");
			disciplinaService.save(d32);

			Disciplina d33 = new Disciplina("Chirurgia plastica e ricostruttiva", "33");
			disciplinaService.save(d33);

			Disciplina d34 = new Disciplina("Chirurgia toracica", "34");
			disciplinaService.save(d34);

			Disciplina d35 = new Disciplina("Chirurgia vascolare", "35");
			disciplinaService.save(d35);

			Disciplina d36 = new Disciplina("Ginecologia e ostetricia", "36");
			disciplinaService.save(d36);

			Disciplina d37 = new Disciplina("Neurochirurgia", "37");
			disciplinaService.save(d37);

			Disciplina d38 = new Disciplina("Oftalmologia", "38");
			disciplinaService.save(d38);

			Disciplina d39 = new Disciplina("Ortopedia e traumatologia", "39");
			disciplinaService.save(d39);

			Disciplina d40 = new Disciplina("Otorinolaringoiatria", "40");
			disciplinaService.save(d40);

			Disciplina d41 = new Disciplina("Urologia", "41");
			disciplinaService.save(d41);

			Disciplina d42 = new Disciplina("Anatomia patologica", "42");
			disciplinaService.save(d42);

			Disciplina d43 = new Disciplina("Anestesia e rianimazione", "43");
			disciplinaService.save(d43);

			Disciplina d44 = new Disciplina("Biochimica clinica", "44");
			disciplinaService.save(d44);

			Disciplina d45 = new Disciplina("Farmacologia e tossicologia clinica", "45");
			disciplinaService.save(d45);

			Disciplina d46 = new Disciplina("Laboratorio di genetica medica", "46");
			disciplinaService.save(d46);

			Disciplina d47 = new Disciplina("Medicina trasfusionale", "47");
			disciplinaService.save(d47);

			Disciplina d48 = new Disciplina("Medicina legale", "48");
			disciplinaService.save(d48);

			Disciplina d49 = new Disciplina("Medicina nucleare", "49");
			disciplinaService.save(d49);

			Disciplina d50 = new Disciplina("Microbiologia e virologia", "50");
			disciplinaService.save(d50);

			Disciplina d51 = new Disciplina("Neurofisiopatologia", "51");
			disciplinaService.save(d51);

			Disciplina d52 = new Disciplina("Neuroradiologia", "52");
			disciplinaService.save(d52);

			Disciplina d53 = new Disciplina("Patologia clinica (laboratorio di analisi chimico-cliniche e microbiologia)", "53");
			disciplinaService.save(d53);

			Disciplina d54 = new Disciplina("Radiodiagnostica", "54");
			disciplinaService.save(d54);

			Disciplina d55 = new Disciplina("Igiene, epidemiologia e sanità pubblica", "55");
			disciplinaService.save(d55);

			Disciplina d56 = new Disciplina("Igiene degli alimenti e della nutrizione", "56");
			disciplinaService.save(d56);

			Disciplina d57 = new Disciplina("Medicina del lavoro e sicurezza degli ambienti di lavoro", "57");
			disciplinaService.save(d57);

			Disciplina d58 = new Disciplina("Medicina generale (medici di famiglia)", "58");
			disciplinaService.save(d58);

			Disciplina d59 = new Disciplina("Continuità assistenziale", "59");
			disciplinaService.save(d59);

			Disciplina d60 = new Disciplina("Pediatria (pediatri di libera scelta)", "60");
			disciplinaService.save(d60);

			Disciplina d106 = new Disciplina("Scienza dell'alimentazione e dietetica", "106");
			disciplinaService.save(d106);

			Disciplina d107 = new Disciplina("Direzione medica di presidio ospedaliero", "107");
			disciplinaService.save(d107);

			Disciplina d108 = new Disciplina("Organizzazione dei servizi sanitari di	base", "108");
			disciplinaService.save(d108);

			Disciplina d111 = new Disciplina("Audiologia e foniatria", "111");
			disciplinaService.save(d111);

			Disciplina d112 = new Disciplina("Psicoterapia", "112");
			disciplinaService.save(d112);

			Disciplina d113 = new Disciplina("Privo di specializzazione", "113");
			disciplinaService.save(d113);

			Disciplina d114 = new Disciplina("Cure palliative", "114");
			disciplinaService.save(d114);

			Disciplina d115 = new Disciplina("Epidemiologia", "115");
			disciplinaService.save(d115);

			Disciplina d116 = new Disciplina("Medicina di comunità", "116");
			disciplinaService.save(d116);

			Disciplina d64 = new Disciplina("Odontoiatria", "64");
			disciplinaService.save(d64);

			Disciplina d66 = new Disciplina("Farmacia ospedaliera", "66");
			disciplinaService.save(d66);

			Disciplina d67 = new Disciplina("Farmacia territoriale", "67");
			disciplinaService.save(d67);

			Disciplina d61 = new Disciplina("Igiene degli allevamenti e delle produzioni zootecniche", "61");
			disciplinaService.save(d61);

			Disciplina d62 = new Disciplina("Igiene prod., trasf., commercial., conserv. E tras. Alimenti di origine animale e derivati", "62");
			disciplinaService.save(d62);

			Disciplina d63 = new Disciplina("Sanità animale", "63");
			disciplinaService.save(d63);

			Disciplina d77 = new Disciplina("Psicoterapia", "77");
			disciplinaService.save(d77);

			Disciplina d78 = new Disciplina("Psicologia", "78");
			disciplinaService.save(d78);

			Disciplina d68 = new Disciplina("Biologo", "68");
			disciplinaService.save(d68);

			Disciplina d76 = new Disciplina("Chimica analitica", "76");
			disciplinaService.save(d76);

			Disciplina d79 = new Disciplina("Fisica sanitaria", "79");
			disciplinaService.save(d79);

			Disciplina d80 = new Disciplina("Assistente sanitario", "80");
			disciplinaService.save(d80);

			Disciplina d81 = new Disciplina("Dietista", "81");
			disciplinaService.save(d81);

			Disciplina d83 = new Disciplina("Educatore professionale", "83");
			disciplinaService.save(d83);

			Disciplina d82 = new Disciplina("Fisioterapista", "82");
			disciplinaService.save(d82);

			Disciplina d84 = new Disciplina("Igienista dentale", "84");
			disciplinaService.save(d84);

			Disciplina d85 = new Disciplina("Infermiere", "85");
			disciplinaService.save(d85);

			Disciplina d86 = new Disciplina("Infermiere pediatrico", "86");
			disciplinaService.save(d86);

			Disciplina d87 = new Disciplina("Logopedista", "87");
			disciplinaService.save(d87);

			Disciplina d88 = new Disciplina("Ortottista/Assistente di oftalmologia", "88");
			disciplinaService.save(d88);

			Disciplina d89 = new Disciplina("Ostetrica/o", "89");
			disciplinaService.save(d89);

			Disciplina d90 = new Disciplina("Podologo", "90");
			disciplinaService.save(d90);

			Disciplina d95 = new Disciplina("Tecnico audiometrista", "95");
			disciplinaService.save(d95);

			Disciplina d96 = new Disciplina("Tecnico audioprotesista", "96");
			disciplinaService.save(d96);

			Disciplina d92 = new Disciplina("Tecnico della fisiopatologia cardiocircolatoria e perfusione cardiovascolare", "92");
			disciplinaService.save(d92);

			Disciplina d105 = new Disciplina("Tecnico della prevenzione nell'ambiente e nei luoghi di lavoro", "105");
			disciplinaService.save(d105);

			Disciplina d91 = new Disciplina("Tecnico della riabilitazione psichiatrica", "91");
			disciplinaService.save(d91);

			Disciplina d98 = new Disciplina("Tecnico di neurofisiopatologia", "98");
			disciplinaService.save(d98);

			Disciplina d99 = new Disciplina("Tecnico ortopedico", "99");
			disciplinaService.save(d99);

			Disciplina d94 = new Disciplina("Tecnico sanitario di radiologia medica", "94");
			disciplinaService.save(d94);

			Disciplina d93 = new Disciplina("Tecnico sanitario laboratorio biomedico", "93");
			disciplinaService.save(d93);

			Disciplina d100 = new Disciplina("Terapista della neuro e psicomotricità dell'età evolutiva", "100");
			disciplinaService.save(d100);

			Disciplina d101 = new Disciplina("Terapista occupazionale", "101");
			disciplinaService.save(d101);

			Professione p1 = new Professione("Medico chirurgo", "1");
			p1.setSanitaria(true);
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
			Professione p2 = new Professione("Odontoiatra", "2");
			p2.setSanitaria(true);
			p2.addDisciplina(d64);
			professioneService.save(p2);
			Professione p3 = new Professione("Farmacista", "3");
			p3.setSanitaria(true);
			p3.addDisciplina(d66);
			p3.addDisciplina(d67);
			professioneService.save(p3);
			Professione p4 = new Professione("Veterinario", "4");
			p4.setSanitaria(true);
			p4.addDisciplina(d61);
			p4.addDisciplina(d62);
			p4.addDisciplina(d63);
			professioneService.save(p4);
			Professione p5 = new Professione("Psicologo", "5");
			p5.setSanitaria(true);
			p5.addDisciplina(d77);
			p5.addDisciplina(d78);
			professioneService.save(p5);
			Professione p6 = new Professione("Biologo", "6");
			p6.addDisciplina(d68);
			professioneService.save(p6);
			Professione p7 = new Professione("Chimico", "7");
			p7.addDisciplina(d76);
			professioneService.save(p7);
			Professione p8 = new Professione("Fisico", "8");
			p8.addDisciplina(d79);
			professioneService.save(p8);
			Professione p9 = new Professione("Assistente sanitario", "9");
			p9.setSanitaria(true);
			p9.addDisciplina(d80);
			professioneService.save(p9);
			Professione p10 = new Professione("Dietista", "10");
			p10.setSanitaria(true);
			p10.addDisciplina(d81);
			professioneService.save(p10);
			Professione p11 = new Professione("Educatore professionale", "11");
			p11.setSanitaria(true);
			p11.addDisciplina(d83);
			professioneService.save(p11);
			Professione p12 = new Professione("Fisioterapista", "12");
			p12.setSanitaria(true);
			p12.addDisciplina(d82);
			professioneService.save(p12);
			Professione p13 = new Professione("Igienista dentale", "13");
			p13.setSanitaria(true);
			p13.addDisciplina(d84);
			professioneService.save(p13);
			Professione p14 = new Professione("Infermiere", "14");
			p14.setSanitaria(true);
			p14.addDisciplina(d85);
			professioneService.save(p14);
			Professione p15 = new Professione("Infermiere pediatrico", "15");
			p15.setSanitaria(true);
			p15.addDisciplina(d86);
			professioneService.save(p15);
			Professione p16 = new Professione("Logopedista", "16");
			p16.setSanitaria(true);
			p16.addDisciplina(d87);
			professioneService.save(p16);
			Professione p17 = new Professione("Ortottista/Assistente di oftalmologia", "17");
			p17.setSanitaria(true);
			p17.addDisciplina(d88);
			professioneService.save(p17);
			Professione p18 = new Professione("Ostetrica/o", "18");
			p18.setSanitaria(true);
			p18.addDisciplina(d89);
			professioneService.save(p18);
			Professione p19 = new Professione("Podologo", "19");
			p19.setSanitaria(true);
			p19.addDisciplina(d90);
			professioneService.save(p19);
			Professione p20 = new Professione("Tecnico audiometrista", "20");
			p20.setSanitaria(true);
			p20.addDisciplina(d95);
			professioneService.save(p20);
			Professione p21 = new Professione("Tecnico audioprotesista", "21");
			p21.setSanitaria(true);
			p21.addDisciplina(d96);
			professioneService.save(p21);
			Professione p22 = new Professione("Tecnico della fisiopatologia cardiocircolatoria e perfusione cardiovascolare", "22");
			p22.setSanitaria(true);
			p22.addDisciplina(d92);
			professioneService.save(p22);
			Professione p23 = new Professione("Tecnico della prevenzione nell'ambiente e nei luoghi di lavoro", "23");
			p23.setSanitaria(true);
			p23.addDisciplina(d105);
			professioneService.save(p23);
			Professione p24 = new Professione("Tecnico della riabilitazione psichiatrica", "24");
			p24.setSanitaria(true);
			p24.addDisciplina(d91);
			professioneService.save(p24);
			Professione p25 = new Professione("Tecnico di neurofisiopatologia", "25");
			p25.setSanitaria(true);
			p25.addDisciplina(d98);
			professioneService.save(p25);
			Professione p26 = new Professione("Tecnico ortopedico", "26");
			p26.setSanitaria(true);
			p26.addDisciplina(d99);
			professioneService.save(p26);
			Professione p27 = new Professione("Tecnico sanitario di radiologia medica", "27");
			p27.setSanitaria(true);
			p27.addDisciplina(d94);
			professioneService.save(p27);
			Professione p28 = new Professione("Tecnico sanitario laboratorio biomedico", "28");
			p28.setSanitaria(true);
			p28.addDisciplina(d93);
			professioneService.save(p28);
			Professione p29 = new Professione("Terapista della neuro e psicomotricità dell'età evolutiva", "29");
			p29.setSanitaria(true);
			p29.addDisciplina(d100);
			professioneService.save(p29);
			Professione p30 = new Professione("Terapista occupazionale", "30");
			p30.setSanitaria(true);
			p30.addDisciplina(d101);
			professioneService.save(p30);
			Professione p31 = new Professione("Nessuna professione sanitaria", "0");
			p31.setSanitaria(false);
			professioneService.save(p31);
			LOGGER.info("BOOTSTRAP ECM - PROFESSIONI/DISCIPLINE create");
		}else{
			LOGGER.info("BOOTSTRAP ECM - PROFESSIONI/DISCIPLINE trovate(" + disciplinea.size() +")");
		}
	}
}
