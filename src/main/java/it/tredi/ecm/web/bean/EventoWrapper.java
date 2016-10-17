package it.tredi.ecm.web.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.tredi.ecm.dao.entity.DettaglioAttivitaRES;
import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Obiettivo;
import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.entity.PersonaFullEvento;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.entity.ProgrammaGiornalieroRES;
import it.tredi.ecm.dao.entity.RendicontazioneInviata;
import it.tredi.ecm.dao.enumlist.EventoWrapperModeEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoWrapper {
	private Evento evento;
	private ProceduraFormativa proceduraFormativa;
	private Long providerId;
	private EventoWrapperModeEnum wrapperMode;

	//parte rendicontazione
	private File reportPartecipanti;
	private RendicontazioneInviata ultimoReportInviato;

	//parte ripetibili
	private List<String> dateIntermedieTemp = new ArrayList<String>();
	private List<String> risultatiAttesiTemp = new ArrayList<String>();
	
	private List<PersonaEvento> responsabiliScientifici = new ArrayList<PersonaEvento>();

	//liste edit
	private Set<Obiettivo> obiettiviNazionali;
	private Set<Obiettivo> obiettiviRegionali;
	private Set<Disciplina> disciplinaList;
	private Set<Professione> professioneList;
	
	//allegati
	private File brochure;
	private File documentoVerificaRicaduteFormative;
	private File autocertificazioneAssenzaFinanziamenti;
	private File contrattiAccordiConvenzioni;
	private File dichiarazioneAssenzaConflittoInteresse;

	private String gotoLink;
	//gestire l'aggiunta di una PersonaEvento
	private PersonaEvento tempPersonaEvento = new PersonaEvento();
	
	/* RES */
	private List<PersonaEvento> docenti = new ArrayList<PersonaEvento>();
	private List<ProgrammaGiornalieroRES> programmaEventoRES = new ArrayList<ProgrammaGiornalieroRES>();
	
	private PersonaFullEvento tempPersonaFullEvento = new PersonaFullEvento();
	private DettaglioAttivitaRES tempAttivitaRES = new DettaglioAttivitaRES();
	private File cv;

}
