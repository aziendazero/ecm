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
@org.springframework.context.annotation.Profile({"dev","demo","prod","simone","abarducci"})
public class ObiettiviLoader implements ApplicationListener<ContextRefreshedEvent> {
	private final static Logger LOGGER = LoggerFactory.getLogger(ObiettiviLoader.class);

	@Autowired
	private ObiettivoService obiettivoService;

	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent arg0) {
		LOGGER.info("BOOTSTRAP ECM - Inizializzazione OBIETTIVI/EVENTI...");

		Set<Obiettivo> obiettivi = obiettivoService.getAllObiettivi();

		if(obiettivi.isEmpty()){
			boolean nazionale = true;
			boolean regionale = false;

			//Obiettivi Regionali
			obiettivi.add(new Obiettivo("Non rientra in uno degli obiettivi regionali",regionale,null));
			obiettivi.add(new Obiettivo("Appropriatezza delle prestazioni sanitarie in conformità ai LEA",regionale,null));
			obiettivi.add(new Obiettivo("Governo clinico, qualità e sicurezza del paziente",regionale,null));
			obiettivi.add(new Obiettivo("Adozione di linee guida basate sull’evidenza, qualità dei sistemi e dei processi clinico assistenziali",regionale,null));
			obiettivi.add(new Obiettivo("Umanizzazione delle cure/relazione/comunicazione",regionale,null));
			obiettivi.add(new Obiettivo("Cure palliative e terapia del dolore",regionale,null));
			obiettivi.add(new Obiettivo("Prevenzione e presa in carico della persona affetta da cronicità",regionale,null));
			obiettivi.add(new Obiettivo("Integrazione professionale tra ospedale e territorio, con sviluppo e implementazione dei PDTA (prioritariamente su BPCO, scompenso cardiaco, fibrillazione atriale, diabete, sclerosi multipla)",regionale,null));
			obiettivi.add(new Obiettivo("Malattia di Alzheimer e altri tipi di declino cognitivo e demenze",regionale,null));
			obiettivi.add(new Obiettivo("Promozione corretti stili di vita",regionale,null));
			obiettivi.add(new Obiettivo("Promozione dell’appropriatezza prescrittiva dei farmaci",regionale,null));
			obiettivi.add(new Obiettivo("Interventi socio-sanitari (famiglia, infanzia, adolescenza, giovani, anziani, disabilità, dipendenze, salute mentale)",regionale,null));
			obiettivi.add(new Obiettivo("Cultura del lavoro in team multiprofessionale e adozioni di modelli di lavoro in rete",regionale,null));
			obiettivi.add(new Obiettivo("Sicurezza degli operatori nell'ambiente di lavoro (T.U. 81/2008)",regionale,null));
			obiettivi.add(new Obiettivo("Valorizzazione delle risorse umane",regionale,null));

			//Obettivi Nazionali
			//TECNICO-PROFESSIONALI
			obiettivi.add(new Obiettivo("Epidemiologia- prevenzione e primozione della salute. (ob.10)", nazionale, CategoriaObiettivoNazionale.TECNICO_PROFESSIONALI, "10"));
			obiettivi.add(new Obiettivo("Argomenti di carattere generale: informatica e lingua inglese scientifica di livello avanzato. (17)", nazionale, CategoriaObiettivoNazionale.TECNICO_PROFESSIONALI, "17"));
			obiettivi.add(new Obiettivo("Contenuti tecnico-professionali (conoschenze e competenze) specifici di ciascuna professione, di ciascuna specializzazione e di ciascuna attivita ultraspecialistica. malattie rare. (18)", nazionale, CategoriaObiettivoNazionale.TECNICO_PROFESSIONALI, "18"));
			obiettivi.add(new Obiettivo("Medicine non convenzionali: valutazione dell’efficacia in ragione degli esiti e degli ambniti di complementarieta’. (19)", nazionale, CategoriaObiettivoNazionale.TECNICO_PROFESSIONALI, "19"));
			obiettivi.add(new Obiettivo("Trattamento del dolore acuto e cronico. palliazione. (21)", nazionale, CategoriaObiettivoNazionale.TECNICO_PROFESSIONALI, "21"));
			obiettivi.add(new Obiettivo("Fragilita’ (minori, anziani, tossico-dipendenti. salute mentale): tutela degli aspetti assistenziali e socio-assistenziali. (22)", nazionale, CategoriaObiettivoNazionale.TECNICO_PROFESSIONALI, "22"));
			obiettivi.add(new Obiettivo("Sicurezza alimentare. (23)", nazionale, CategoriaObiettivoNazionale.TECNICO_PROFESSIONALI, "23"));
			obiettivi.add(new Obiettivo("Sicurezza ambientale. (26)", nazionale, CategoriaObiettivoNazionale.TECNICO_PROFESSIONALI, "26"));
			obiettivi.add(new Obiettivo("Sicurezza negli ambienti e nei luoghi di lavoro e patologie correlate.(27)", nazionale, CategoriaObiettivoNazionale.TECNICO_PROFESSIONALI, "27"));
			obiettivi.add(new Obiettivo("Sanita’ veterinaria. (24)", nazionale, CategoriaObiettivoNazionale.TECNICO_PROFESSIONALI, "24"));
			obiettivi.add(new Obiettivo("Farmacoepidemiologia, farmacoeconomia, farmacovigilanza.(25)", nazionale, CategoriaObiettivoNazionale.TECNICO_PROFESSIONALI, "25"));
			obiettivi.add(new Obiettivo("Implementazione della cultura e della sicurezza in ,ateire di donazione trapianto.(28)", nazionale, CategoriaObiettivoNazionale.TECNICO_PROFESSIONALI, "28"));
			obiettivi.add(new Obiettivo("Innovazione tecnologicca: valutazione. miglioramento dei processi di gestione delle tecnologie biomediche e dei dispositivi medici. Health technology assessment.(29)", nazionale, CategoriaObiettivoNazionale.TECNICO_PROFESSIONALI, "29"));
			obiettivi.add(new Obiettivo("Tematiche speciali del SSN e SSR ed a carattere urgente e/o straordinario individuate dalla commissione nazionale ECM e dalle regioni/province autonome per far fronte a specifiche emergenze sanitarie.(20)", nazionale, CategoriaObiettivoNazionale.TECNICO_PROFESSIONALI, "20"));

			//DI PROCESSO
			obiettivi.add(new Obiettivo("Documentazione clinica, percorsi clinico-assistenziali diagnostici e riabilitativi, profili di assistenza-profili di cura. (ob.3)", nazionale, CategoriaObiettivoNazionale.DI_PROCESSO, "3"));
			obiettivi.add(new Obiettivo("Appropriatezza prestazioni sanitarie nei LEA. Sistemi di valutazione, verifica e miglioramento dell’efficienza e dell’efficacia. (4)", nazionale, CategoriaObiettivoNazionale.DI_PROCESSO, "4"));
			obiettivi.add(new Obiettivo("Integrazione interprofessionale e multiprofessionale, interistituzionale. (8)", nazionale, CategoriaObiettivoNazionale.DI_PROCESSO, "8"));
			obiettivi.add(new Obiettivo("Integrazione fra assistenza territoriale ed ospedaliera. (9)", nazionale, CategoriaObiettivoNazionale.DI_PROCESSO, "9"));
			obiettivi.add(new Obiettivo("Management sanitario. innovazione gestionale e sperimentazione di modelli organizzativi e gestionali. (11)", nazionale, CategoriaObiettivoNazionale.DI_PROCESSO, "11"));
			obiettivi.add(new Obiettivo("Aspetti relazionali comunicazione interna, esterna, con paziente e umanizzazione delle cure. (12)", nazionale, CategoriaObiettivoNazionale.DI_PROCESSO, "12"));
			obiettivi.add(new Obiettivo("La comunicazione efficace la privacy ed il consenso informato. (7)", nazionale, CategoriaObiettivoNazionale.DI_PROCESSO, "7"));
			obiettivi.add(new Obiettivo("Metoddologia e tecniche di comunicazione sociale per lo sviluppo dei programmi nazionali e regionali di prevenzione primaria. (13)", nazionale, CategoriaObiettivoNazionale.DI_PROCESSO, "13"));
			obiettivi.add(new Obiettivo("Multiculturalita’ e cultura dell’accoglienza nell’attivita’ sanitaria. (15)", nazionale, CategoriaObiettivoNazionale.DI_PROCESSO, "15"));
			obiettivi.add(new Obiettivo("Tematiche speciali del SSN e SSR ed a carattere urgente e/o straordinario individuate dalla commissione nazionale ECM e dalle regioni/province autonome per far fronte a specficfiche emergenze sanitarie. (20)", nazionale, CategoriaObiettivoNazionale.DI_PROCESSO, "20"));

			//DI SISTEMA
			obiettivi.add(new Obiettivo("Applicazione nella pratica quotidiana dei principi e delle prodcedure dell’evidenze based practice (EBM. EBN. EBP). (ob.1)", nazionale, CategoriaObiettivoNazionale.DI_SISTEMA, "1"));
			obiettivi.add(new Obiettivo("Linee guida protocolli-procedure. (ob.2)", nazionale, CategoriaObiettivoNazionale.DI_SISTEMA, "2"));
			obiettivi.add(new Obiettivo("Principi, procedure e strumenti per il governo clinico delle attività sanitarie. (5)", nazionale, CategoriaObiettivoNazionale.DI_SISTEMA, "5"));
			obiettivi.add(new Obiettivo("La sicurezza del paziente. risk management. (6)", nazionale, CategoriaObiettivoNazionale.DI_SISTEMA, "6"));
			obiettivi.add(new Obiettivo("Epidemiologia – prevenziaone e promozione della salute. (10)", nazionale, CategoriaObiettivoNazionale.DI_SISTEMA, "10"));
			obiettivi.add(new Obiettivo("Etica, bioetica e deontologia. (16)", nazionale, CategoriaObiettivoNazionale.DI_SISTEMA, "16"));
			obiettivi.add(new Obiettivo("Argomenti di carattere generale: informatica ed ingleser scientifico livello avanzato; normativa in materia sanitaria: i principi etici e civili del ssn. (17)", nazionale, CategoriaObiettivoNazionale.DI_SISTEMA, "17"));
			obiettivi.add(new Obiettivo("Tematiche speciali del SSN e SSR ed a carattere urgente e/o straordinario individuate dalla commissione nazionale ECM e dalle regioni/province autonome per far fronte a specifiche emergenze sanitarie. (20)", nazionale, CategoriaObiettivoNazionale.DI_SISTEMA, "20"));

			obiettivoService.save(obiettivi);

			LOGGER.info("BOOTSTRAP ECM - OBIETTIVI/EVENTI creati");
		}else{
			LOGGER.info("BOOTSTRAP ECM - OBIETTIVI/EVENTI trovati (" + obiettivi.size() +")");
		}
	}
}
