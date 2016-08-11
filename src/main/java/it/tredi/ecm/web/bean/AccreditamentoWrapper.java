package it.tredi.ecm.web.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.FieldEditabileAccreditamento;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.PianoFormativo;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.ProfileEnum;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.dao.enumlist.ValutazioneTipoEnum;
import it.tredi.ecm.service.bean.CurrentUser;
import it.tredi.ecm.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccreditamentoWrapper {

	//dati domanda di accreditamento
	private Accreditamento accreditamento;

	//dati provider
	private Provider provider;

	private Sede sedeLegale;
	private Sede sedeOperativa;

	private Persona legaleRappresentante;
	private Persona delegatoLegaleRappresentante;

	//dati domanda accreditamento
	private DatiAccreditamento datiAccreditamento;

	//dati anagrafiche interne provider
	private Persona responsabileSegreteria;
	private Persona responsabileAmministrativo;
	private Persona coordinatoreComitatoScientifico;
	private List<Persona> componentiComitatoScientifico = new ArrayList<Persona>();
	private Persona responsabileSistemaInformatico;
	private Persona responsabileQualita;

	/*	flag per stato avanzamento domanda	*/
	private boolean providerStato;
	private boolean sedeLegaleStato;
	private boolean sedeOperativaStato;
	private boolean legaleRappresentanteStato;
	private boolean delegatoLegaleRappresentanteStato;

	private boolean datiAccreditamentoStato;

	private boolean responsabileSegreteriaStato;
	private boolean responsabileAmministrativoStato;
	private boolean responsabileSistemaInformaticoStato;
	private boolean responsabileQualitaStato;

	private boolean comitatoScientificoStato;
	private boolean tuttiComponentiValutati = true;
	private String comitatoScientificoErrorMessage;

	private boolean attoCostitutivoStato;
	private boolean esperienzaFormazioneStato;
	private boolean utilizzoStato;
	private boolean sistemaInformaticoStato;
	private boolean pianoQualitaStato;
	private boolean dichiarazioneLegaleStato;
	private boolean dichiarazioneEsclusioneStato;

	private boolean tuttiEventiValutati = true;

	private boolean sezione1Stato;
	private boolean sezione2Stato;
	private boolean sezione3Stato;
	private boolean sezione4Stato;

	private boolean canSendValutazione;

	//stati per i pulsanti segreteria
	private boolean canPrendiInCarica;
	private boolean canValutaDomanda;
	private boolean canAssegnaNuovoGruppo;
	private boolean canConfermaValutazione;

	//boolean Stati dei multistanza
	private Map<Long, Boolean> componentiComitatoScientificoStati = new HashMap<Long, Boolean>();
	private boolean coordinatoreComitatoScientificoStato;
	private Map<Long, Boolean> eventiStati = new HashMap<Long, Boolean>();

	//Mappe con i FieldValutazione
	private Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa;
	private Map<Long, Map<IdFieldEnum, FieldValutazioneAccreditamento>> mappaComponenti;
	private Map<IdFieldEnum, FieldValutazioneAccreditamento> mappaCoordinatore;
	private Map<Long, Map<IdFieldEnum, FieldValutazioneAccreditamento>> mappaEventi;


	public AccreditamentoWrapper(){};
	public AccreditamentoWrapper(Accreditamento accreditamento){
		this.setAccreditamento(accreditamento);

		// PROVIDER
		this.setProvider(accreditamento.getProvider());

		//SEDE LEGALE
		Sede sede = accreditamento.getProvider().getSedeLegale();
		this.setSedeLegale( sede != null ? sede : new Sede());

		//SEDE OPERATIVA
		sede = accreditamento.getProvider().getSedeOperativa();
		this.setSedeOperativa( sede != null ? sede : new Sede());

		//DATI ACCREDITAMENTO
		DatiAccreditamento datiAccreditamento = accreditamento.getDatiAccreditamento();
		this.setDatiAccreditamento(datiAccreditamento != null ? datiAccreditamento : new DatiAccreditamento());

		// LEGALE RAPPRESENTANTE E RESPONSABILI
		for(Persona p : accreditamento.getProvider().getPersone()){
			if(p.isLegaleRappresentante())
				this.setLegaleRappresentante(p);
			else if(p.isDelegatoLegaleRappresentante())
				this.setDelegatoLegaleRappresentante(p);
			else if(p.isResponsabileSegreteria())
				this.setResponsabileSegreteria(p);
			else if(p.isResponsabileAmministrativo())
				this.setResponsabileAmministrativo(p);
			else if(p.isResponsabileSistemaInformatico())
				this.setResponsabileSistemaInformatico(p);
			else if(p.isResponsabileQualita())
				this.setResponsabileQualita(p);
			else if(p.isCoordinatoreComitatoScientifico())
				this.setCoordinatoreComitatoScientifico(p);
			else if(p.isComponenteComitatoScientifico())
				this.getComponentiComitatoScientifico().add(p);
		}
	}




	public void checkStati(int numeroComponentiComitatoScientifico, int numeroProfessionistiSanitarie, Set<Professione> elencoProfessioniDeiComponenti, int professioniDeiComponentiAnaloghe,Set<String> filesDelProvider,String mode){
		//TODO migliorare la logica per evitare di fare troppi if
		// ad esempio inizializzare gli stati a true e poi ad ogni controllo se fallisce si mette il false sia allo stato che al valid
		// cosi facendo valid è settato in automatico senza rifare tutti i controlli
		//
		//NON lo faccio adesso perchè voglio capire in fase di validazione della domanda come gestiremo i vari stati

		//check sulla modalità di visualizzazione
		if(mode.equals("show") || mode.equals("edit")) {
			providerStato = (provider.getRagioneSociale()!= null) ? true : false;

			sedeLegaleStato = (sedeLegale != null && !sedeLegale.isNew()) ? true : false;
			sedeOperativaStato = (sedeOperativa != null && !sedeOperativa.isNew()) ? true : false;

			//check sul cv unico campo non settabile in registrazione o in modifica delle anagrafiche, ma solo durante la domanda di accreditamento
			legaleRappresentanteStato = (legaleRappresentante != null && !legaleRappresentante.isNew() && legaleRappresentante.getAnagrafica().getCellulare() != null && !legaleRappresentante.getAnagrafica().getCellulare().isEmpty()) ? true : false;
			delegatoLegaleRappresentanteStato = (delegatoLegaleRappresentante != null && !delegatoLegaleRappresentante.isNew() && delegatoLegaleRappresentante.getAnagrafica().getCellulare() != null && !delegatoLegaleRappresentante.getAnagrafica().getCellulare().isEmpty()) ? true : false;

			datiAccreditamentoStato = (datiAccreditamento != null && !datiAccreditamento.isNew()) ? true : false;

			responsabileSegreteriaStato = (responsabileSegreteria != null && !responsabileSegreteria.isNew()) ? true : false;
			responsabileAmministrativoStato = (responsabileAmministrativo != null && !responsabileAmministrativo.isNew()) ? true : false;
			responsabileSistemaInformaticoStato = (responsabileSistemaInformatico != null && !responsabileSistemaInformatico.isNew()) ? true : false;
			responsabileQualitaStato = (responsabileQualita != null && !responsabileQualita.isNew()) ? true : false;

			checkComitatoScientifico_fromDB(numeroComponentiComitatoScientifico, numeroProfessionistiSanitarie, elencoProfessioniDeiComponenti, professioniDeiComponentiAnaloghe);
			setFilesStato(filesDelProvider);

			sezione1Stato = (providerStato && sedeLegaleStato && sedeOperativaStato && legaleRappresentanteStato && datiAccreditamentoStato) ? true : false;
			sezione2Stato = (responsabileSegreteriaStato && responsabileAmministrativoStato && responsabileSistemaInformaticoStato && responsabileQualitaStato && comitatoScientificoStato) ? true : false;
			sezione3Stato = (attoCostitutivoStato && esperienzaFormazioneStato && utilizzoStato && sistemaInformaticoStato && pianoQualitaStato && dichiarazioneLegaleStato) ? true : false;
		}
		if(mode.equals("validate")) {

			providerStato = (mappa.containsKey(IdFieldEnum.PROVIDER__TIPO_ORGANIZZATORE) &&
				mappa.containsKey(IdFieldEnum.PROVIDER__DENOMINAZIONE_LEGALE) &&
				mappa.containsKey(IdFieldEnum.PROVIDER__PARTITA_IVA) &&
				mappa.containsKey(IdFieldEnum.PROVIDER__CODICE_FISCALE) &&
				mappa.containsKey(IdFieldEnum.PROVIDER__RAGIONE_SOCIALE) &&
				mappa.containsKey(IdFieldEnum.PROVIDER__NATURA_ORGANIZZAZIONE) &&
				mappa.containsKey(IdFieldEnum.PROVIDER__NO_PROFIT));

			sedeLegaleStato = (mappa.containsKey(IdFieldEnum.SEDE_LEGALE__PROVINCIA) &&
				mappa.containsKey(IdFieldEnum.SEDE_LEGALE__COMUNE) &&
				mappa.containsKey(IdFieldEnum.SEDE_LEGALE__INDIRIZZO) &&
				mappa.containsKey(IdFieldEnum.SEDE_LEGALE__CAP) &&
				mappa.containsKey(IdFieldEnum.SEDE_LEGALE__TELEFONO) &&
				mappa.containsKey(IdFieldEnum.SEDE_LEGALE__FAX) &&
				mappa.containsKey(IdFieldEnum.SEDE_LEGALE__EMAIL));

			sedeOperativaStato = (mappa.containsKey(IdFieldEnum.SEDE_OPERATIVA__PROVINCIA) &&
				mappa.containsKey(IdFieldEnum.SEDE_OPERATIVA__COMUNE) &&
				mappa.containsKey(IdFieldEnum.SEDE_OPERATIVA__INDIRIZZO) &&
				mappa.containsKey(IdFieldEnum.SEDE_OPERATIVA__CAP) &&
				mappa.containsKey(IdFieldEnum.SEDE_OPERATIVA__TELEFONO) &&
				mappa.containsKey(IdFieldEnum.SEDE_OPERATIVA__FAX) &&
				mappa.containsKey(IdFieldEnum.SEDE_OPERATIVA__EMAIL));

			legaleRappresentanteStato =	(mappa.containsKey(IdFieldEnum.LEGALE_RAPPRESENTANTE__COGNOME) &&
				mappa.containsKey(IdFieldEnum.LEGALE_RAPPRESENTANTE__NOME) &&
				mappa.containsKey(IdFieldEnum.LEGALE_RAPPRESENTANTE__CODICEFISCALE) &&
				mappa.containsKey(IdFieldEnum.LEGALE_RAPPRESENTANTE__CELLULARE) &&
				mappa.containsKey(IdFieldEnum.LEGALE_RAPPRESENTANTE__EMAIL) &&
				mappa.containsKey(IdFieldEnum.LEGALE_RAPPRESENTANTE__PEC) &&
				mappa.containsKey(IdFieldEnum.LEGALE_RAPPRESENTANTE__ATTO_NOMINA) &&
				mappa.containsKey(IdFieldEnum.LEGALE_RAPPRESENTANTE__CV));

			delegatoLegaleRappresentanteStato = (mappa.containsKey(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__COGNOME) &&
				mappa.containsKey(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__NOME) &&
				mappa.containsKey(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__CODICEFISCALE) &&
				mappa.containsKey(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__TELEFONO) &&
				mappa.containsKey(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__CELLULARE) &&
				mappa.containsKey(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__EMAIL) &&
				mappa.containsKey(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__CV) &&
				mappa.containsKey(IdFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE__DELEGA));

			datiAccreditamentoStato = (mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__TIPOLOGIA_ACCREDITAMENTO) &&
				mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__PROCEDURE_FORMATIVE) &&
				mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__PROFESSIONI_ACCREDITAMENTO) &&
				mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__DISCIPLINE) &&
				mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO) &&
				mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_COMPLESSIVO) &&
				mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE) &&
				mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_FORMAZIONE) &&
				mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI) &&
				mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__ORGANIGRAMMA) &&
				mappa.containsKey(IdFieldEnum.DATI_ACCREDITAMENTO__FUNZIONIGRAMMA));

			responsabileSegreteriaStato = (mappa.containsKey(IdFieldEnum.RESPONSABILE_SEGRETERIA__COGNOME) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_SEGRETERIA__NOME) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_SEGRETERIA__CODICEFISCALE) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_SEGRETERIA__TELEFONO) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_SEGRETERIA__EMAIL) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_SEGRETERIA__ATTO_NOMINA) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_SEGRETERIA__CV));

			responsabileAmministrativoStato = (mappa.containsKey(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__COGNOME) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__NOME) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__CODICEFISCALE) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__TELEFONO) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__CELLULARE) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__EMAIL) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__ATTO_NOMINA) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_AMMINISTRATIVO__CV));

			responsabileSistemaInformaticoStato = (mappa.containsKey(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__COGNOME) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__NOME) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__CODICEFISCALE) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__TELEFONO) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__EMAIL) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__ATTO_NOMINA) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO__CV));

			responsabileQualitaStato = (mappa.containsKey(IdFieldEnum.RESPONSABILE_QUALITA__COGNOME) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_QUALITA__NOME) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_QUALITA__CODICEFISCALE) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_QUALITA__TELEFONO) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_QUALITA__EMAIL) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_QUALITA__ATTO_NOMINA) &&
				mappa.containsKey(IdFieldEnum.RESPONSABILE_QUALITA__CV));

			attoCostitutivoStato = mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__ATTO_COSTITUIVO);
			esperienzaFormazioneStato = mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__ESPERIENZA_FORMAZIONE);
			utilizzoStato = mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__UTILIZZO);
			sistemaInformaticoStato = mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__SISTEMA_INFORMATICO);
			pianoQualitaStato = mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__PIANO_QUALITA);
			dichiarazioneLegaleStato = mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_LEGALE);
			dichiarazioneEsclusioneStato = mappa.containsKey(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_ESCLUSIONE);

			//check valutazione dei multistanza

			//componenti comitato scientifico N.B. NON controlla bene tutti i FieldValutazione come gli altri per semplicità TODO decidere se implementare o se semplificare anche le altre
			for (Persona p : componentiComitatoScientifico) {
				if(mappaComponenti.get(p.getId()) != null && !mappaComponenti.get(p.getId()).isEmpty())
					componentiComitatoScientificoStati.replace(p.getId(), true);
			};

			//controllo anche il coordinatore N.B. stesso discorso dei componenti
			coordinatoreComitatoScientificoStato = (mappaCoordinatore != null && !mappaCoordinatore.isEmpty()) ? true : false;

//			coordinatoreComitatoScientificoStato = (mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__COGNOME) &&
//				mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__NOME) &&
//				mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__CODICEFISCALE) &&
//				mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__TELEFONO) &&
//				mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__CELLULARE) &&
//				mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__EMAIL) &&
//				mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__PROFESSIONE) &&
//				mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__ATTO_NOMINA) &&
//				mappaCoordinatore.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__CV));

			//eventi pianoFormativo N.B. stesso discorso dei componenti
			for (Evento e : accreditamento.getPianoFormativo().getEventi()) {
				if(mappaEventi.get(e.getId()) != null && !mappaEventi.get(e.getId()).isEmpty())
					eventiStati.replace(e.getId(), true);
			};

			//ciclo la mappa degli stati dei componenti (tuttiComponentiValutati default == true)
			componentiComitatoScientificoStati.forEach((k,v) -> {
				if(v == false)
					tuttiComponentiValutati = false;
			});

			comitatoScientificoStato = (coordinatoreComitatoScientificoStato && tuttiComponentiValutati) ? true : false;

			//ciclo la mappa degli stati degli eventi (tuttiEventiValutati default == true)
			eventiStati.forEach((k,v) -> {
				if(v == false)
					tuttiEventiValutati = false;
			});

			//sezioni validate o meno
			sezione1Stato = (providerStato && sedeLegaleStato && sedeOperativaStato && legaleRappresentanteStato && datiAccreditamentoStato) ? true : false;
			sezione2Stato = (responsabileSegreteriaStato && responsabileAmministrativoStato && responsabileSistemaInformaticoStato && responsabileQualitaStato && comitatoScientificoStato) ? true : false;
			sezione3Stato = (attoCostitutivoStato && esperienzaFormazioneStato && utilizzoStato && sistemaInformaticoStato && pianoQualitaStato && dichiarazioneLegaleStato) ? true : false;
			sezione4Stato = tuttiEventiValutati ? true : false;

			//show del pulsante per inviare la valutazione
			canSendValutazione = (sezione1Stato && sezione2Stato && sezione3Stato && sezione4Stato);
		}
	}

	/*
	 * [A] Almeno 5 componenti (coordinatore incluso)
	 * [B] Almeno 5 professionisti Sanitari
	 * [C] Se settato "Generale" -> Almeno 2 professioni diverse tra i componenti
	 * [D] Se settato "Settoriale" -> Almeno 2 professioni analoghe a quelle selezionate (almeno che non sia stata selezionate solo 1 professione)
	 * [D-bis] Se Selezionata solo 1 professione -> deve conicidere con quella e UNICA dei componenti del comitato scientifico
	 * */
	public void checkComitatoScientifico_fromDB(int numeroComponentiComitatoScientifico, int numeroProfessionistiSanitari, Set<Professione> elencoProfessioniDeiComponenti, int professioniDeiComponentiAnaloghe){
		comitatoScientificoStato = true;

		int professioniDeiComponenti = elencoProfessioniDeiComponenti.size();

		//[A]
		if(numeroComponentiComitatoScientifico < 4 || (coordinatoreComitatoScientifico == null || coordinatoreComitatoScientifico.isNew())){
			comitatoScientificoStato = false;
			comitatoScientificoErrorMessage = "error.numero_minimo_comitato";
		}//[B]
		else if(numeroProfessionistiSanitari < 5){
			comitatoScientificoStato = false;
			comitatoScientificoErrorMessage = "error.numero_minimo_professionisti_sanitari";
		}else{
			// mi assicuro che sono stati gia' inseriti i dati relativi all'accreditamento
			if(datiAccreditamentoStato){
				//[C]
				if(datiAccreditamento.getProfessioniAccreditamento().equalsIgnoreCase("generale")){
					if(professioniDeiComponenti < 2){
						comitatoScientificoStato = false;
						comitatoScientificoErrorMessage = "error.numero_minimo_professioni";
					}
				}//[D]
				else{
					if(datiAccreditamento.getProfessioniSelezionate().size() > 1){
						if(professioniDeiComponentiAnaloghe < 2){
							comitatoScientificoStato = false;
							comitatoScientificoErrorMessage = "error.numero_minimo_professioni_settoriale";
						}
					}//[D - bis]
					else{
						if(elencoProfessioniDeiComponenti.size() > 1 || elencoProfessioniDeiComponenti.size() == 0){
							comitatoScientificoStato = false;
							comitatoScientificoErrorMessage = "error.professioni_non_conformi_con_accreditamento";
						}else{
							if(elencoProfessioniDeiComponenti.iterator().next() != datiAccreditamento.getProfessioniSelezionate().iterator().next()){
								comitatoScientificoStato = false;
								comitatoScientificoErrorMessage = "error.professioni_non_conformi_con_accreditamento";
							}
						}
					}
				}
			}else{
				comitatoScientificoStato = false;
				comitatoScientificoErrorMessage = "error.datiaccreditamento_mancanti";
			}
		}
	}

	private void setFilesStato(Set<String> filesDelProvider){
		if(filesDelProvider.contains(FileEnum.FILE_ATTO_COSTITUTIVO))
			attoCostitutivoStato = true;
		if(filesDelProvider.contains(FileEnum.FILE_ESPERIENZA_FORMAZIONE))
			esperienzaFormazioneStato = true;
		if(filesDelProvider.contains(FileEnum.FILE_UTILIZZO))
			utilizzoStato = true;
		if(filesDelProvider.contains(FileEnum.FILE_SISTEMA_INFORMATICO))
			sistemaInformaticoStato = true;
		if(filesDelProvider.contains(FileEnum.FILE_PIANO_QUALITA))
			pianoQualitaStato = true;
		if(filesDelProvider.contains(FileEnum.FILE_DICHIARAZIONE_LEGALE))
			dichiarazioneLegaleStato = true;
		if(filesDelProvider.contains(FileEnum.FILE_DICHIARAZIONE_ESCLUSIONE))
			dichiarazioneEsclusioneStato = true;
	}

	public boolean isComitatoScientificoEditabile(){
		Set<IdFieldEnum> ids = Utils.getSubsetOfIdFieldEnum(new HashSet<FieldEditabileAccreditamento>(getAccreditamento().getIdEditabili()), SubSetFieldEnum.COMITATO_SCIENTIFICO);
		if(ids.isEmpty())
			return false;
		else
			return true;
	}

	//la domanda è stata compilata in tutte le sue parti (tutti i flag sono TRUE)
	public boolean isCompleta(){
		if(sezione1Stato && sezione2Stato && sezione3Stato)
			return true;
		else
			return false;
	}

	//Inserisci piano formativo e blocca idEditabili
	public boolean isCanInsertPianoFormativo(){
		if(accreditamento.isBozza() && isCompleta() && !accreditamento.hasPianoFormativo())
			return true;
		else
			return false;
	}

	//Invia domanda alla segreteria cambiando stato all'accreditamento e rendendo la domanda non più modificabile
	public boolean isCanSend(){
		if(accreditamento.isBozza() && isCompleta() && isPianoFormativoCompleto())
			return true;
		else
			return false;
	}

	//ci sono eventi inseriti nel piano formativo
	private boolean isPianoFormativoCompleto(){
		return ((accreditamento.getPianoFormativo() != null) && !accreditamento.getPianoFormativo().getEventi().isEmpty());
	}

	public boolean isCanInsertEventoInPianoFormativo(){
		return (accreditamento.hasPianoFormativo());
		//return (accreditamento.hasPianoFormativo() && accreditamento.isEditabile());
		//TODO controllo FieldEditabileAccreditamento
	}
}
