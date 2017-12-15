package it.tredi.ecm.web.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.tredi.ecm.dao.entity.AzioneRuoliEventoFSC;
import it.tredi.ecm.dao.entity.DettaglioAttivitaFAD;
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
import it.tredi.ecm.dao.entity.RiepilogoRuoliFSC;
import it.tredi.ecm.dao.entity.RuoloOreFSC;
import it.tredi.ecm.dao.entity.Sponsor;
import it.tredi.ecm.dao.entity.VerificaApprendimentoFAD;
import it.tredi.ecm.dao.enumlist.EventoWrapperModeEnum;
import it.tredi.ecm.dao.enumlist.FaseDiLavoroFSCEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import it.tredi.ecm.dao.enumlist.VerificaApprendimentoFADEnum;
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

	private float creditiOld;

	//parte rendicontazione
	private File reportPartecipanti;

	//parte ripetibili
	//private List<String> dateIntermedieTemp = new ArrayList<String>();
	//private List<String> risultatiAttesiTemp = new ArrayList<String>();

	private Map<Long, String> risultatiAttesiMapTemp = new LinkedHashMap<Long, String>();

	private List<PersonaEvento> responsabiliScientifici = new ArrayList<PersonaEvento>();
	private List<PersonaEvento> docenti = new ArrayList<PersonaEvento>();

	/* FSC */
	private List<PersonaEvento> esperti = new ArrayList<PersonaEvento>();
	private List<PersonaEvento> coordinatori = new ArrayList<PersonaEvento>();
	private List<PersonaEvento> investigatori = new ArrayList<PersonaEvento>();

	//liste edit
	private Set<Obiettivo> obiettiviNazionali;
	private Set<Obiettivo> obiettiviRegionali;
	private List<Disciplina> disciplinaList;
	private List<Professione> professioneList;
	private File cv;
	private File sponsorFile;
	private File partnerFile;

	//allegati
	private File brochure;
	private File documentoVerificaRicaduteFormative;
	private File autocertificazioneAssenzaFinanziamenti;
	private File contrattiAccordiConvenzioni;
	private File dichiarazioneAssenzaConflittoInteresse;
	private File autocertificazioneAssenzaAziendeAlimentiPrimaInfanzia;
	private File autocertificazioneAutorizzazioneMinisteroSalute;
	private File requisitiHardwareSoftware;

	//mappa FAD verificaApprendimento
	private Map<VerificaApprendimentoFADEnum, VerificaApprendimentoFAD> mappaVerificaApprendimento = new HashMap<VerificaApprendimentoFADEnum, VerificaApprendimentoFAD>();
	//mappa FSC ruoloOre
	private Map<RuoloFSCEnum, RuoloOreFSC> mappaRuoloOre = new HashMap<RuoloFSCEnum, RuoloOreFSC>();

	//gestire l'aggiunta di una PersonaEvento
	private PersonaEvento tempPersonaEvento = new PersonaEvento();
	private List<PersonaEvento> personeEventoModificate = new ArrayList<PersonaEvento>();


	private Sponsor tempSponsorEvento = new Sponsor();
	private Partner tempPartnerEvento = new Partner();

	private List<Sponsor> sponsors = new ArrayList<Sponsor>();
	private List<Partner> partners = new ArrayList<Partner>();

	private PersonaFullEvento tempPersonaFullEvento = new PersonaFullEvento();

	/* RES */
	private DettaglioAttivitaRES tempAttivitaRES = new DettaglioAttivitaRES();
	private EventoRESDateProgrammiGiornalieriWrapper eventoRESDateProgrammiGiornalieriWrapper = null;


	/* FSC */
	private AzioneRuoliEventoFSC tempAttivitaFSC = new AzioneRuoliEventoFSC();
	private RuoloOreFSC tempRuoloOreFSC = new RuoloOreFSC();
	private Map<TipologiaEventoFSCEnum,List<FaseAzioniRuoliEventoFSCTypeA>> possibiliProgrammiFSC = new HashMap<TipologiaEventoFSCEnum, List<FaseAzioniRuoliEventoFSCTypeA>>();
	private Map<TipologiaEventoFSCEnum,Map<RuoloFSCEnum,RiepilogoRuoliFSC>> possibiliRiepilogoRuoliFSC = new HashMap<TipologiaEventoFSCEnum, Map<RuoloFSCEnum,RiepilogoRuoliFSC>>();

	/* FAD */
	private DettaglioAttivitaFAD tempAttivitaFAD = new DettaglioAttivitaFAD();
	private List<DettaglioAttivitaFAD> programmaEventoFAD = new ArrayList<DettaglioAttivitaFAD>();

//	private List<FaseAzioniRuoliEventoFSCTypeA> programmaEventoFSC_TI = new ArrayList<FaseAzioniRuoliEventoFSCTypeA>();
//	private List<FaseAzioniRuoliEventoFSCTypeA> programmaEventoFSC_GM = new ArrayList<FaseAzioniRuoliEventoFSCTypeA>();
//	private List<FaseAzioniRuoliEventoFSCTypeA> programmaEventoFSC_AR = new ArrayList<FaseAzioniRuoliEventoFSCTypeA>();
//	private List<FaseAzioniRuoliEventoFSCTypeA> programmaEventoFSC_ACA = new ArrayList<FaseAzioniRuoliEventoFSCTypeA>();

	/* GESTIONE ERRORI VALIDAZIONE */
	private Map<String, String> mappaErroriValidazione = new HashMap<String, String>();

	//gestione editabilita dell'Evento
	private boolean editSemiBloccato = false;
	private boolean eventoIniziato = false;
	private boolean hasDataInizioRestrictions = false;
	private boolean hasRiedizioni = false;

	public List<FaseAzioniRuoliEventoFSCTypeA> getProgrammaEventoFSC(){
		if(evento != null && evento instanceof EventoFSC){
			if(((EventoFSC)evento).getTipologiaEventoFSC() != null){
				return possibiliProgrammiFSC.get(((EventoFSC)evento).getTipologiaEventoFSC());
			}
		}
		return null;
	}

	public List<DettaglioAttivitaFAD> getProgrammaEventoFAD(){
		if(evento != null && (evento instanceof EventoFAD) && ((EventoFAD)evento).getTipologiaEventoFAD() != null){
			return programmaEventoFAD;
		}
		return null;
	}

	public void setProgrammaEventoFSC(List<FaseAzioniRuoliEventoFSCTypeA> programmaFSC){
		if(evento != null && evento instanceof EventoFSC){
			if(((EventoFSC)evento).getTipologiaEventoFSC() != null){
				possibiliProgrammiFSC.put(((EventoFSC)evento).getTipologiaEventoFSC(), programmaFSC);
			}
		}
	}

	public Map<RuoloFSCEnum,RiepilogoRuoliFSC> getRiepilogoRuoliFSC(){
		if(evento != null && evento instanceof EventoFSC){
			if(((EventoFSC)evento).getTipologiaEventoFSC() != null){
				return possibiliRiepilogoRuoliFSC.get(((EventoFSC)evento).getTipologiaEventoFSC());
			}
		}
		return null;
	}

	public void setRiepilogoRuoliFSC(Map<RuoloFSCEnum,RiepilogoRuoliFSC> riepilogoRuoliFSC){
		if(evento != null && evento instanceof EventoFSC){
			if(((EventoFSC)evento).getTipologiaEventoFSC() != null){
				possibiliRiepilogoRuoliFSC.put(((EventoFSC)evento).getTipologiaEventoFSC(), riepilogoRuoliFSC);
			}
		}
	}

	public void initProgrammi(){
		if(evento instanceof EventoRES){
			initProgrammiRES();
		}else if(evento instanceof EventoFSC){
			initProgrammiFSC();
			initRiepilogoRuoliFSC();
		}else if(evento instanceof EventoFAD){
			initProgrammiFAD();
		}
	}

	public void initProgrammiRES(){
		//TODO BARDUCCI a seconda di come decidi la creazione a partire dalla data decidi se è necessario o meno un programma vuoto
		this.getRisultatiAttesiMapTemp().put(1L, null);
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

		//this.setProgrammaEventoFSC_TI(programmaEvento);
		this.possibiliProgrammiFSC.put(TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO, programmaEvento);

		//TIPOLOGIA GRUPPI DI MIGLIORAMENTO
		programmaEvento = new ArrayList<FaseAzioniRuoliEventoFSCTypeA>();
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.CAMPO_LIBERO));

		//this.setProgrammaEventoFSC_GM(programmaEvento);
		this.possibiliProgrammiFSC.put(TipologiaEventoFSCEnum.GRUPPI_DI_MIGLIORAMENTO, programmaEvento);

		//TIPOLOGIA ATTIVITA DI RICERCA
		programmaEvento = new ArrayList<FaseAzioniRuoliEventoFSCTypeA>();
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.ESPLICITAZIONE_IPOTESI_LAVORO));
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.FASE_RACCOLTA_DATI));
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.ANALISI_DATI));
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.PRESENTAZIONE_RISULTATI));

		//this.setProgrammaEventoFSC_AR(programmaEvento);
		this.possibiliProgrammiFSC.put(TipologiaEventoFSCEnum.ATTIVITA_DI_RICERCA, programmaEvento);

		//TIPOLOGIA AUDIT CLINICO E/O ASSISTENZIALE
		programmaEvento = new ArrayList<FaseAzioniRuoliEventoFSCTypeA>();
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.DEFINIZIONE_CRITERI_VALUTAZIONI_PRATICITA_CLINICA));
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.ELABORAZIONE_PROPOSTE));
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.APPLICAZIONI_GESTIONALI_ORGANIZZATIVE));
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.VERIFICA_PRATICA_CORRENTE));
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.VALUTAZIONE_IMPATTO));

		//this.setProgrammaEventoFSC_ACA(programmaEvento);
		this.possibiliProgrammiFSC.put(TipologiaEventoFSCEnum.AUDIT_CLINICO_ASSISTENZIALE, programmaEvento);

		//TIPOLOGIA PROGETTI DI MIGLIORAMENTO
		programmaEvento = new ArrayList<FaseAzioniRuoliEventoFSCTypeA>();
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.ANALISI_DEL_PROBLEMA));
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.INDIVIDUAZIONE_DELLE_SOLUZIONI));
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.CONFRONTO_E_CONDIVISIONE));
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.IMPLEMENTAZIONE_CAMBIAMENTO_E_MONITORAGGIO));
		programmaEvento.add(new FaseAzioniRuoliEventoFSCTypeA(FaseDiLavoroFSCEnum.VALUTAZIONE_IMPATTO_CAMBIAMENTO));

		//this.setProgrammaEventoFSC_ACA(programmaEvento);
		this.possibiliProgrammiFSC.put(TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO, programmaEvento);
	}

	public void initRiepilogoRuoliFSC(){
		//TIPOLOGIA TRAINING INDIVIDUALIZZATO
		Map<RuoloFSCEnum,RiepilogoRuoliFSC> riepilogoRuoli = new HashMap<RuoloFSCEnum, RiepilogoRuoliFSC>();
//		riepilogoRuoli.put(RuoloFSCEnum.PARTECIPANTE, new RiepilogoRuoliFSC(RuoloFSCEnum.PARTECIPANTE));
//		riepilogoRuoli.put(RuoloFSCEnum.TUTOR, new RiepilogoRuoliFSC(RuoloFSCEnum.TUTOR));
//		riepilogoRuoli.put(RuoloFSCEnum.ESPERTO, new RiepilogoRuoliFSC(RuoloFSCEnum.ESPERTO));
//		riepilogoRuoli.put(RuoloFSCEnum.COORDINATORE, new RiepilogoRuoliFSC(RuoloFSCEnum.COORDINATORE));

		possibiliRiepilogoRuoliFSC.put(TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO, riepilogoRuoli);

		//TIPOLOGIA GRUPPI DI MIGLIORAMENTO
		riepilogoRuoli = new HashMap<RuoloFSCEnum, RiepilogoRuoliFSC>();
//		riepilogoRuoli.put(RuoloFSCEnum.PARTECIPANTE, new RiepilogoRuoliFSC(RuoloFSCEnum.PARTECIPANTE));
//		riepilogoRuoli.put(RuoloFSCEnum.COORDINATORE_GRUPPI, new RiepilogoRuoliFSC(RuoloFSCEnum.COORDINATORE_GRUPPI));

		possibiliRiepilogoRuoliFSC.put(TipologiaEventoFSCEnum.GRUPPI_DI_MIGLIORAMENTO, riepilogoRuoli);

		//TIPOLOGIA ATTIVITA DI RICERCA
		riepilogoRuoli = new HashMap<RuoloFSCEnum, RiepilogoRuoliFSC>();
//		riepilogoRuoli.put(RuoloFSCEnum.PARTECIPANTE, new RiepilogoRuoliFSC(RuoloFSCEnum.PARTECIPANTE));
//		riepilogoRuoli.put(RuoloFSCEnum.COORDINATORE_ATTIVITA_RICERCA, new RiepilogoRuoliFSC(RuoloFSCEnum.COORDINATORE_ATTIVITA_RICERCA));

		possibiliRiepilogoRuoliFSC.put(TipologiaEventoFSCEnum.ATTIVITA_DI_RICERCA, riepilogoRuoli);

		//TIPOLOGIA AUDIT CLINICO E/O ASSISTENZIALE
		riepilogoRuoli = new HashMap<RuoloFSCEnum, RiepilogoRuoliFSC>();
//		riepilogoRuoli.put(RuoloFSCEnum.PARTECIPANTE, new RiepilogoRuoliFSC(RuoloFSCEnum.PARTECIPANTE));
//		riepilogoRuoli.put(RuoloFSCEnum.COORDINATORE_ATTIVITA_AUDIT, new RiepilogoRuoliFSC(RuoloFSCEnum.COORDINATORE_ATTIVITA_AUDIT));

		possibiliRiepilogoRuoliFSC.put(TipologiaEventoFSCEnum.AUDIT_CLINICO_ASSISTENZIALE, riepilogoRuoli);

		//TIPOLOGIA PROGRAMMA DI MIGLIORAMENTO
		riepilogoRuoli = new HashMap<RuoloFSCEnum, RiepilogoRuoliFSC>();
//		riepilogoRuoli.put(RuoloFSCEnum.PARTECIPANTE, new RiepilogoRuoliFSC(RuoloFSCEnum.PARTECIPANTE));
//		riepilogoRuoli.put(RuoloFSCEnum.ESPERTO, new RiepilogoRuoliFSC(RuoloFSCEnum.ESPERTO));
//		riepilogoRuoli.put(RuoloFSCEnum.COORDINATORE_GRUPPI, new RiepilogoRuoliFSC(RuoloFSCEnum.COORDINATORE_GRUPPI));
//		riepilogoRuoli.put(RuoloFSCEnum.RESPONSABILE_PROGETTO, new RiepilogoRuoliFSC(RuoloFSCEnum.RESPONSABILE_PROGETTO));

		possibiliRiepilogoRuoliFSC.put(TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO, riepilogoRuoli);
	}

	public void initProgrammiFAD(){
		this.getRisultatiAttesiMapTemp().put(1L, null);
//		this.programmaEventoFAD.add(new DettaglioAttivitaFAD());
	}

	public void initMappaVerificaApprendimentoFAD() {
		//caso new
		if(((EventoFAD) evento).getVerificaApprendimento() == null || ((EventoFAD) evento).getVerificaApprendimento().isEmpty()) {
			for (VerificaApprendimentoFADEnum vafe : VerificaApprendimentoFADEnum.values()) {
				VerificaApprendimentoFAD temp = new VerificaApprendimentoFAD();
				mappaVerificaApprendimento.put(vafe, temp);
			}
		}
		//caso edit
		else {
			EventoFAD eventoFAD =  (EventoFAD) this.getEvento();
			for (VerificaApprendimentoFAD vaf : eventoFAD.getVerificaApprendimento()) {
				mappaVerificaApprendimento.put(vaf.getVerificaApprendimento(), vaf);
			}
		}
	}

	public void initMappaRuoloOreFSC() {
		//caso new
		if(this.tempAttivitaFSC == null) {
			for (RuoloFSCEnum rfe : RuoloFSCEnum.values()) {
				RuoloOreFSC temp = new RuoloOreFSC();
				mappaRuoloOre.put(rfe, temp);
			}
		}
		//caso edit
		else {
			AzioneRuoliEventoFSC azioneRuoliFSC = this.getTempAttivitaFSC();
			for (RuoloOreFSC rof : azioneRuoliFSC.getRuoli()) {
				mappaRuoloOre.put(rof.getRuolo(), rof);
			}
		}
	}
	public void setEvento(Evento evento) {
		if(evento.getProceduraFormativa() == ProceduraFormativa.RES)
			this.eventoRESDateProgrammiGiornalieriWrapper = new EventoRESDateProgrammiGiornalieriWrapper((EventoRES)evento);
		this.evento = evento;
	}
	
	public List<RuoloFSCEnum> getListRuoloFSCEnumPerResponsabiliScientifici() {
		List<RuoloFSCEnum> toRet = new ArrayList<RuoloFSCEnum>();
		if(this.responsabiliScientifici != null) {
			for(PersonaEvento pEv : this.responsabiliScientifici) {
				if(pEv.isSvolgeAttivitaDiDocenza() && pEv.getIdentificativoPersonaRuoloEventoTemp() != null)
					toRet.add(pEv.getIdentificativoPersonaRuoloEventoTemp().getRuoloFSCResponsabileSCientifico());
			}
		}
		return toRet;
	}

	public List<RuoloFSCEnum> getListRuoloFSCEnumPerEsperti() {
		List<RuoloFSCEnum> toRet = new ArrayList<RuoloFSCEnum>();
		if(this.esperti != null) {
			for(PersonaEvento pEv : this.esperti) {
				if(pEv.isSvolgeAttivitaDiDocenza() && pEv.getIdentificativoPersonaRuoloEventoTemp() != null)
					toRet.add(pEv.getIdentificativoPersonaRuoloEventoTemp().getRuoloFSCEsperto());
			}
		}
		return toRet;
	}

	public List<RuoloFSCEnum> getListRuoloFSCEnumPerCoordinatori() {
		List<RuoloFSCEnum> toRet = new ArrayList<RuoloFSCEnum>();
		if(this.coordinatori != null) {
			for(PersonaEvento pEv : this.coordinatori) {
				if(pEv.isSvolgeAttivitaDiDocenza() && pEv.getIdentificativoPersonaRuoloEventoTemp() != null)
					toRet.add(pEv.getIdentificativoPersonaRuoloEventoTemp().getRuoloFSCCoordinatore());
			}
		}
		return toRet;
	}
}
