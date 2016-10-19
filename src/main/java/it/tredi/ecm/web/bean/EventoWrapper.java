package it.tredi.ecm.web.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.tredi.ecm.dao.entity.DettaglioAttivitaRES;
import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Obiettivo;
import it.tredi.ecm.dao.entity.Partner;
import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.entity.PersonaFullEvento;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.entity.ProgrammaGiornalieroRES;
import it.tredi.ecm.dao.entity.Sponsor;
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

	//parte ripetibili
	private List<String> dateIntermedieTemp = new ArrayList<String>();
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

	private String gotoLink;
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


}
