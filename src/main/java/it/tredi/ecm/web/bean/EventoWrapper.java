package it.tredi.ecm.web.bean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import it.tredi.ecm.dao.entity.AzioneRuoliEventoFSC;
import it.tredi.ecm.dao.entity.DettaglioAttivitaRES;
import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoFAD;
import it.tredi.ecm.dao.entity.EventoFSC;
import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.dao.entity.FaseAzioniRuoliEventoFSCTypeA;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Obiettivo;
import it.tredi.ecm.dao.entity.Partner;
import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.entity.PersonaFullEvento;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.entity.ProgrammaGiornalieroRES;
import it.tredi.ecm.dao.entity.Sponsor;
import it.tredi.ecm.dao.enumlist.EventoWrapperModeEnum;
import it.tredi.ecm.dao.enumlist.FaseDiLavoroFSCEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoWrapper {
	private Evento evento;
	private ProceduraFormativa proceduraFormativa;
	private Long providerId;
	private EventoWrapperModeEnum wrapperMode;
	private float creditiProposti;

	//parte rendicontazione
	private File reportPartecipanti;

	//parte ripetibili modificata ab
	private Map<Long, String> dateIntermedieMapTemp = new LinkedHashMap<Long, String>();

	//parte ripetibili
	//private List<String> dateIntermedieTemp = new ArrayList<String>();
	private List<String> risultatiAttesiTemp = new ArrayList<String>();

	private List<PersonaEvento> responsabiliScientifici = new ArrayList<PersonaEvento>();

	//liste edit
	private Set<Obiettivo> obiettiviNazionali;
	private Set<Obiettivo> obiettiviRegionali;
	private Set<Disciplina> disciplinaList;
	private Set<Professione> professioneList;
	private File cv;
	private File sponsorFile;
	private File partnerFile;
	private File autocertificazioneAssenzaAziendeAlimentiPrimaInfanzia;
	private File autocertificazioneAutorizzazioneMinisteroSalute;

	//allegati
	private File brochure;
	private File documentoVerificaRicaduteFormative;
	private File autocertificazioneAssenzaFinanziamenti;
	private File contrattiAccordiConvenzioni;
	private File dichiarazioneAssenzaConflittoInteresse;

	//gestire l'aggiunta di una PersonaEvento
	private PersonaEvento tempPersonaEvento = new PersonaEvento();

	private Sponsor tempSponsorEvento = new Sponsor();
	private Partner tempPartnerEvento = new Partner();

	/* RES */
	private List<PersonaEvento> docenti = new ArrayList<PersonaEvento>();
	private List<ProgrammaGiornalieroRES> programmaEventoRES = new ArrayList<ProgrammaGiornalieroRES>();
	private List<Sponsor> sponsors = new ArrayList<Sponsor>();
	private List<Partner> partners = new ArrayList<Partner>();

	private PersonaFullEvento tempPersonaFullEvento = new PersonaFullEvento();
	private DettaglioAttivitaRES tempAttivitaRES = new DettaglioAttivitaRES();


	/* FSC */
	private AzioneRuoliEventoFSC tempAttivitaFSC = new AzioneRuoliEventoFSC();

	private List<FaseAzioniRuoliEventoFSCTypeA> programmaEventoFSC_TI = new ArrayList<FaseAzioniRuoliEventoFSCTypeA>();
	private List<FaseAzioniRuoliEventoFSCTypeA> programmaEventoFSC_GM = new ArrayList<FaseAzioniRuoliEventoFSCTypeA>();
	private List<FaseAzioniRuoliEventoFSCTypeA> programmaEventoFSC_AR = new ArrayList<FaseAzioniRuoliEventoFSCTypeA>();
	private List<FaseAzioniRuoliEventoFSCTypeA> programmaEventoFSC_ACA = new ArrayList<FaseAzioniRuoliEventoFSCTypeA>();
	
	private Map<RuoloFSCEnum,Float> mappaRuoloOre = new HashMap<RuoloFSCEnum, Float>();
	
	public List<ProgrammaGiornalieroRES> getProgrammaEventoRES(){
		if(evento != null && (evento instanceof EventoRES) && ((EventoRES)evento).getTipologiaEvento() != null){
			return programmaEventoRES;
		}
		return null;
	}
	
	public List<FaseAzioniRuoliEventoFSCTypeA> getProgrammaEventoFSC(){
		if(evento != null && evento instanceof EventoFSC){
			if(((EventoFSC)evento).getTipologiaEvento() != null){
				switch (((EventoFSC)evento).getTipologiaEvento()){
				case TRAINING_INDIVIDUALIZZATO:		return getProgrammaEventoFSC_TI(); 

				case GRUPPI_DI_MIGLIORAMENTO: 		return getProgrammaEventoFSC_GM();

				case ATTIVITA_DI_RICERCA: 			return getProgrammaEventoFSC_AR();

				case AUDIT_CLINICO_ASSISTENZIALE: 	return getProgrammaEventoFSC_ACA();
									default: 		
													return null;
				}
			}
		}
		return null;
	}
	
	public void setProgrammaEventoFSC(List<FaseAzioniRuoliEventoFSCTypeA> programmaFSC){
		if(evento != null && evento instanceof EventoFSC){
			if(((EventoFSC)evento).getTipologiaEvento() != null){
				switch (((EventoFSC)evento).getTipologiaEvento()){
				case TRAINING_INDIVIDUALIZZATO:		setProgrammaEventoFSC_TI(programmaFSC); 
													break;

				case GRUPPI_DI_MIGLIORAMENTO: 		setProgrammaEventoFSC_GM(programmaFSC);
													break;

				case ATTIVITA_DI_RICERCA: 			setProgrammaEventoFSC_AR(programmaFSC);
													break;

				case AUDIT_CLINICO_ASSISTENZIALE: 	setProgrammaEventoFSC_ACA(programmaFSC);
													break;
									default: 		
													break;
				}
			}
		}
	}
	
	public void initProgrammi(){
		if(evento instanceof EventoRES){
			initProgrammiRES();
		}else if(evento instanceof EventoFSC){
			initProgrammiFSC();
		}else if(evento instanceof EventoFAD){
			initProgrammiFAD();
		}
		
		for(RuoloFSCEnum r : RuoloFSCEnum.values()){
			mappaRuoloOre.put(r, new Float(0.0));
		}
	}
	
	public void initProgrammiRES(){
		//TODO BARDUCCI a seconda di come decidi la creazione a partire dalla data decidi se è necessario o meno un programma vuoto

		//Lista programmi giornalieri dell'evento
		List<ProgrammaGiornalieroRES> programmaEvento = new ArrayList<ProgrammaGiornalieroRES>();
		ProgrammaGiornalieroRES prog = new ProgrammaGiornalieroRES();
		prog.setGiorno(LocalDate.now());
		programmaEvento.add(prog);
		this.setProgrammaEventoRES(programmaEvento);
		this.getDateIntermedieMapTemp().put(1L, null);
	}

	/*
	 *	creo tutte le possibili tipologie di programmi vuoti
	 * */
	public void initProgrammiFSC(){
		//TIPOLOGIA TRAINING INDIVIDUALIZZATO
		List<FaseAzioniRuoliEventoFSCTypeA> programmaEvento = new ArrayList<FaseAzioniRuoliEventoFSCTypeA>();
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.AMBIENTAMENTO));
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.LAVORO_AFFIANCATO_TUTOR));
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.LAVORO_AUTONOMO_COLLABORAZIONE_TUTOR));
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.VALUTAZIONE_FINALE));

		this.setProgrammaEventoFSC_TI(programmaEvento);
		
		//TIPOLOGIA GRUPPI DI MIGLIORAMENTO
		programmaEvento = new ArrayList<FaseAzioniRuoliEventoFSCTypeA>();
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.CAMPO_LIBERO));

		this.setProgrammaEventoFSC_GM(programmaEvento);

		//TIPOLOGIA ATTIVITA DI RICERCA
		programmaEvento = new ArrayList<FaseAzioniRuoliEventoFSCTypeA>();
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.ESPLICITAZIONE_IPOTESI_LAVORO));
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.FASE_RACCOLTA_DATI));
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.ANALISI_DATI));
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.PRESENTAZIONE_RISULTATI));

		this.setProgrammaEventoFSC_AR(programmaEvento);

		//TIPOLOGIA AUDIT CLINICO E/O ASSISTENZIALE
		programmaEvento = new ArrayList<FaseAzioniRuoliEventoFSCTypeA>();
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.DEFINIZIONE_CRITERI_VALUTAZIONI_PRATICITA_CLINICA));
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.ELABORAZIONE_PROPOSTE));
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.APPLICAZIONI_GESTIONALI_ORGANIZZATIVE));
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.VERIFICA_PRATICA_CORRENTE));
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.VALUTAZIONE_IMPATTO_CAMBIAMENTO));

		this.setProgrammaEventoFSC_ACA(programmaEvento);
	}

	public void initProgrammiFAD(){
		//TODO FAD
	}
}
