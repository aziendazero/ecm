package it.tredi.ecm.dao.enumlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;

@Getter
public enum IdFieldEnum {
	PROVIDER__TIPO_ORGANIZZATORE ("provider.tipoOrganizzatore", 1, null, SubSetFieldEnum.PROVIDER, "tipoOrganizzatore"),
	PROVIDER__DENOMINAZIONE_LEGALE ("provider.denominazioneLegale", 2, null, SubSetFieldEnum.PROVIDER, "denominazioneLegale"),
	PROVIDER__PARTITA_IVA ("provider.partitaIva", 3, null, SubSetFieldEnum.PROVIDER, "partitaIva"),
	PROVIDER__CODICE_FISCALE ("provider.codiceFiscale", 4, null, SubSetFieldEnum.PROVIDER, "codiceFiscale"),
	PROVIDER__RAGIONE_SOCIALE ("provider.ragioneSociale", 5, null, SubSetFieldEnum.PROVIDER, "ragioneSociale"),
	PROVIDER__EMAIL_STRUTTURA ("provider.emailStruttura", 6, null, SubSetFieldEnum.PROVIDER, "emailStruttura"),
	PROVIDER__NATURA_ORGANIZZAZIONE ("provider.naturaOrganizzazione", 7, null, SubSetFieldEnum.PROVIDER, "naturaOrganizzazione"),
	PROVIDER__NO_PROFIT ("provider.noProfit", 8, null, SubSetFieldEnum.PROVIDER, "noProfit"),

	LEGALE_RAPPRESENTANTE__FULL ("persona.anagrafica",-1,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "full"),
	LEGALE_RAPPRESENTANTE__COGNOME ("persona.anagrafica.cognome",9,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.cognome"),
	LEGALE_RAPPRESENTANTE__NOME ("persona.anagrafica.nome",10,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.nome"),
	LEGALE_RAPPRESENTANTE__CODICEFISCALE ("persona.anagrafica.codiceFiscale",11,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.codiceFiscale"),
	LEGALE_RAPPRESENTANTE__TELEFONO ("persona.anagrafica.telefono",12,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.telefono"),
	LEGALE_RAPPRESENTANTE__CELLULARE ("persona.anagrafica.cellulare",13,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.cellulare"),
	LEGALE_RAPPRESENTANTE__EMAIL ("persona.anagrafica.email",14,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.email"),
	LEGALE_RAPPRESENTANTE__PEC ("persona.anagrafica.pec",15,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.pec"),
	LEGALE_RAPPRESENTANTE__ATTO_NOMINA ("attoNomina",16,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "files.FILE_ATTO_NOMINA"),
	LEGALE_RAPPRESENTANTE__CV ("cv",17,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "files.FILE_CV"),

	DELEGATO_LEGALE_RAPPRESENTANTE__FULL ("persona.anagrafica",-1,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "full"),
	DELEGATO_LEGALE_RAPPRESENTANTE__COGNOME ("persona.anagrafica.cognome",18,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.cognome"),
	DELEGATO_LEGALE_RAPPRESENTANTE__NOME ("persona.anagrafica.nome",19,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.nome"),
	DELEGATO_LEGALE_RAPPRESENTANTE__CODICEFISCALE ("persona.anagrafica.codiceFiscale",20,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.codiceFiscale"),
	DELEGATO_LEGALE_RAPPRESENTANTE__TELEFONO ("persona.anagrafica.telefono",21,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.telefono"),
	DELEGATO_LEGALE_RAPPRESENTANTE__CELLULARE ("persona.anagrafica.cellulare",22,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.cellulare"),
	DELEGATO_LEGALE_RAPPRESENTANTE__EMAIL ("persona.anagrafica.email",23,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.email"),
	DELEGATO_LEGALE_RAPPRESENTANTE__DELEGA ("delega",24,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "files.FILE_DELEGA"),
	DELEGATO_LEGALE_RAPPRESENTANTE__CV ("cv",25,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "files.FILE_CV"),

	SEDE__IS_LEGALE ("sede.sedeLegale", -1, null, SubSetFieldEnum.SEDE, "sedeLegale"),
	SEDE__IS_OPERATIVA ("sede.sedeOperativa", -1, null, SubSetFieldEnum.SEDE, "sedeOperativa"),
	SEDE__PROVINCIA ("sede.provincia", 26, null, SubSetFieldEnum.SEDE, "provincia"),
	SEDE__COMUNE ("sede.comune", 27, null, SubSetFieldEnum.SEDE, "comune"),
	SEDE__INDIRIZZO ("sede.indirizzo", 28, null, SubSetFieldEnum.SEDE, "indirizzo"),
	SEDE__CAP ("sede.cap", 29, null, SubSetFieldEnum.SEDE, "cap"),
	SEDE__TELEFONO ("sede.telefono", 30, null, SubSetFieldEnum.SEDE, "telefono"),
	SEDE__FAX ("sede.fax", 31, null, SubSetFieldEnum.SEDE, "fax"),
	SEDE__EMAIL ("sede.email", 32, null, SubSetFieldEnum.SEDE, "email"),

	DATI_ACCREDITAMENTO__TIPOLOGIA_ACCREDITAMENTO ("datiAccreditamento.tipologiaAccreditamento", 33, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "tipologiaAccreditamento"),
	DATI_ACCREDITAMENTO__PROCEDURE_FORMATIVE ("datiAccreditamento.procedureFormative", 34, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "procedureFormative"),
	DATI_ACCREDITAMENTO__PROFESSIONI_ACCREDITAMENTO ("datiAccreditamento.professioniAccreditamento", 35, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "professioniAccreditamento"),
	DATI_ACCREDITAMENTO__DISCIPLINE ("datiAccreditamento.discipline", 36, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "discipline"),

	DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_UNO ("datiAccreditamento.datiEconomici.fatturatoComplessivoValoreUno", -1, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "datiEconomici.fatturatoComplessivoValoreUno"),
	DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_DUE ("datiAccreditamento.datiEconomici.fatturatoComplessivoValoreDue", -1, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "datiEconomici.fatturatoComplessivoValoreDue"),
	DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_TRE ("datiAccreditamento.datiEconomici.fatturatoComplessivoValoreTre", -1, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "datiEconomici.fatturatoComplessivoValoreTre"),
	DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO ("datiAccreditamento.datiEconomici.fatturatoComplessivo", 37, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "fatturatoComplessivo",Arrays.asList(DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_UNO,DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_DUE,DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_TRE)),

	DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_COMPLESSIVO ("estrattoBilancioComplessivo", 38, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "files.FILE_ESTRATTO_BILANCIO_COMPLESSIVO"),

	DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_UNO ("datiAccreditamento.datiEconomici.fatturatoFormazioneValoreUno", -1, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "datiEconomici.fatturatoFormazioneValoreUno"),
	DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_DUE ("datiAccreditamento.datiEconomici.fatturatoFormazioneValoreDue", -1, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "datiEconomici.fatturatoFormazioneValoreDue"),
	DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_TRE ("datiAccreditamento.datiEconomici.fatturatoFormazioneValoreTre", -1, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "datiEconomici.fatturatoFormazioneValoreTre"),
	DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE ("datiAccreditamento.datiEconomici.fatturatoFormazione", 39, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "fatturatoFormazione",Arrays.asList(DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_UNO,DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_DUE,DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_TRE)),

	DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_FORMAZIONE ("estrattoBilancioFormazione", 40, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "files.FILE_ESTRATTO_BILANCIO_FORMAZIONE"),

	DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI_FORMAZIONE_TEMPO_INDETERMINATO ("datiAccreditamento.numeroDipendentiFormazioneTempoIndeterminato", -1, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "numeroDipendentiFormazioneTempoIndeterminato"),
	DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI_FORMAZIONE_ALTRO ("datiAccreditamento.numeroDipendentiFormazioneAltro", -1, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "numeroDipendentiFormazioneAltro"),
	DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI ("datiAccreditamento.numeroDipendenti", 41, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "numeroDipendentiFormazioneTempoIndeterminato", Arrays.asList(DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI_FORMAZIONE_TEMPO_INDETERMINATO, DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI_FORMAZIONE_ALTRO)),

	DATI_ACCREDITAMENTO__ORGANIGRAMMA ("organigramma", 42, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "files.FILE_ORGANIGRAMMA"),
	DATI_ACCREDITAMENTO__FUNZIONIGRAMMA ("funzionigramma", 43, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "files.FILE_FUNZIONIGRAMMA"),

	RESPONSABILE_SEGRETERIA__FULL ("persona.anagrafica", -1, Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "full"),
	RESPONSABILE_SEGRETERIA__COGNOME ("persona.anagrafica.cognome",44,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "anagrafica.cognome"),
	RESPONSABILE_SEGRETERIA__NOME ("persona.anagrafica.nome",45,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "anagrafica.nome"),
	RESPONSABILE_SEGRETERIA__CODICEFISCALE ("persona.anagrafica.codiceFiscale",46,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "anagrafica.codiceFiscale"),
	RESPONSABILE_SEGRETERIA__TELEFONO ("persona.anagrafica.telefono",47,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "anagrafica.telefono"),
	RESPONSABILE_SEGRETERIA__EMAIL ("persona.anagrafica.email",48,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "anagrafica.email"),
	RESPONSABILE_SEGRETERIA__ATTO_NOMINA ("attoNomina",49,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "files.FILE_ATTO_NOMINA"),
	RESPONSABILE_SEGRETERIA__CV ("cv",50,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "files.FILE_CV"),

	RESPONSABILE_AMMINISTRATIVO__FULL ("persona.anagrafica", -1, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "full"),
	RESPONSABILE_AMMINISTRATIVO__COGNOME ("persona.anagrafica.cognome", 51, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "anagrafica.cognome"),
	RESPONSABILE_AMMINISTRATIVO__NOME ("persona.anagrafica.nome", 52, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "anagrafica.nome"),
	RESPONSABILE_AMMINISTRATIVO__CODICEFISCALE ("persona.anagrafica.codiceFiscale", 53, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "anagrafica.codiceFiscale"),
	RESPONSABILE_AMMINISTRATIVO__TELEFONO ("persona.anagrafica.telefono", 54, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "anagrafica.telefono"),
	RESPONSABILE_AMMINISTRATIVO__EMAIL ("persona.anagrafica.email", 55, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "anagrafica.email"),
	RESPONSABILE_AMMINISTRATIVO__ATTO_NOMINA ("attoNomina", 56, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "files.FILE_ATTO_NOMINA"),
	RESPONSABILE_AMMINISTRATIVO__CV ("cv", 57, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "files.FILE_CV"),

	RESPONSABILE_SISTEMA_INFORMATICO__FULL ("persona.anagrafica", -1, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "full"),
	RESPONSABILE_SISTEMA_INFORMATICO__COGNOME ("persona.anagrafica.cognome", 58, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "anagrafica.cognome"),
	RESPONSABILE_SISTEMA_INFORMATICO__NOME ("persona.anagrafica.nome", 59, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "anagrafica.nome"),
	RESPONSABILE_SISTEMA_INFORMATICO__CODICEFISCALE ("persona.anagrafica.codiceFiscale", 60, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "anagrafica.codiceFiscale"),
	RESPONSABILE_SISTEMA_INFORMATICO__TELEFONO ("persona.anagrafica.telefono", 61, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "anagrafica.telefono"),
	RESPONSABILE_SISTEMA_INFORMATICO__EMAIL ("persona.anagrafica.email", 62, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "anagrafica.email"),
	RESPONSABILE_SISTEMA_INFORMATICO__ATTO_NOMINA ("attoNomina", 63, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "files.FILE_ATTO_NOMINA"),
	RESPONSABILE_SISTEMA_INFORMATICO__CV ("cv", 64, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "files.FILE_CV"),

	RESPONSABILE_QUALITA__FULL ("persona.anagrafica", -1, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "full"),
	RESPONSABILE_QUALITA__COGNOME ("persona.anagrafica.cognome", 65, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "anagrafica.cognome"),
	RESPONSABILE_QUALITA__NOME ("persona.anagrafica.nome", 66, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "anagrafica.nome"),
	RESPONSABILE_QUALITA__CODICEFISCALE ("persona.anagrafica.codiceFiscale", 67, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "anagrafica.codiceFiscale"),
	RESPONSABILE_QUALITA__TELEFONO ("persona.anagrafica.telefono", 68, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "anagrafica.telefono"),
	RESPONSABILE_QUALITA__EMAIL ("persona.anagrafica.email", 69, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "anagrafica.email"),
	RESPONSABILE_QUALITA__ATTO_NOMINA ("attoNomina", 70, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "files.FILE_ATTO_NOMINA"),
	RESPONSABILE_QUALITA__CV ("cv", 71, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "files.FILE_CV"),

	COMPONENTE_COMITATO_SCIENTIFICO__COGNOME ("persona.anagrafica.cognome", 72,Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "anagrafica.cognome"),
	COMPONENTE_COMITATO_SCIENTIFICO__NOME ("persona.anagrafica.nome", 73,Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "anagrafica.nome"),
	COMPONENTE_COMITATO_SCIENTIFICO__CODICEFISCALE ("persona.anagrafica.codiceFiscale", 74, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "anagrafica.codiceFiscale"),
	COMPONENTE_COMITATO_SCIENTIFICO__TELEFONO ("persona.anagrafica.telefono", 75, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "anagrafica.telefono"),
	COMPONENTE_COMITATO_SCIENTIFICO__EMAIL ("persona.anagrafica.email", 76, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "anagrafica.email"),
	COMPONENTE_COMITATO_SCIENTIFICO__PROFESSIONE ("persona.professione", 77, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "professione"),
	COMPONENTE_COMITATO_SCIENTIFICO__ATTO_NOMINA ("attoNomina", 78, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "files.FILE_ATTO_NOMINA"),
	COMPONENTE_COMITATO_SCIENTIFICO__CV ("cv", 79, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "files.FILE_CV"),

	ACCREDITAMENTO_ALLEGATI__ATTO_COSTITUIVO ("attoCostitutivo", 80, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.FILE_ATTO_COSTITUTIVO"),
	ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_ESCLUSIONE ("dichiarazioneEsclusione", 81, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.FILE_DICHIARAZIONE_ESCLUSIONE"),
	ACCREDITAMENTO_ALLEGATI__ESPERIENZA_FORMAZIONE ("esperienzaFormazione", 82, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.FILE_ESPERIENZA_FORMAZIONE"),
	ACCREDITAMENTO_ALLEGATI__UTILIZZO ("utilizzo", 83, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.FILE_UTILIZZO"),
	ACCREDITAMENTO_ALLEGATI__SISTEMA_INFORMATICO ("sistemaInformatico", 84, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.FILE_SISTEMA_INFORMATICO"),
	ACCREDITAMENTO_ALLEGATI__PIANO_QUALITA ("pianoQualita", 85, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.FILE_PIANO_QUALITA"),
	ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_LEGALE ("dichiarazioneLegale", 86, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.FILE_DICHIARAZIONE_LEGALE"),


	EVENTO_PIANO_FORMATIVO__PROCEDURA_FORMATIVA ("evento.proceduraFormativa", 1, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO),
	EVENTO_PIANO_FORMATIVO__TITOLO ("evento.titolo", 2, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO),
	EVENTO_PIANO_FORMATIVO__OBIETTIVO_NAZIONALE ("evento.obiettivoNazionale", 3, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO),
	EVENTO_PIANO_FORMATIVO__OBIETTIVO_REGIONALE ("evento.obiettivoRegionale", 4, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO),
	EVENTO_PIANO_FORMATIVO__PROFESSIONI_EVENTO ("evento.professioniEvento", 5, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO),
	EVENTO_PIANO_FORMATIVO__DISCIPLINE ("evento.discipline", 6, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO),

	SEDE__FULL("sedi", -1, null, SubSetFieldEnum.FULL, "full"),
	COMPONENTE_COMITATO_SCIENTIFICO__FULL ("comitatoScientifico", -1, null, SubSetFieldEnum.FULL, "full"),
	EVENTO_PIANO_FORMATIVO__FULL ("pianoFormativo", -1, null, SubSetFieldEnum.FULL, "full");//gestiamo la possibilità di modificare o meno il piano formativo dentro l'accreditamento

	private int idEcm;
	private String key;
	private Ruolo ruolo;
	private SubSetFieldEnum subSetField;
	private Class<?> parentClassRef;//it.tredi.ecm.dao.entity.Persona
	private String nameRef;//nome
	private String typeRef;//java.lang.String
	private List<IdFieldEnum> gruppo = new ArrayList<IdFieldEnum>();

	private IdFieldEnum(String key, int idEcm, Ruolo ruolo, SubSetFieldEnum subSetField){
		this.key = key;
		this.idEcm = idEcm;
		this.ruolo = ruolo;
		this.subSetField = subSetField;
	}

	private IdFieldEnum(String key, int idEcm, Ruolo ruolo, SubSetFieldEnum subSetField, String nameRef){
		this.key = key;
		this.idEcm = idEcm;
		this.ruolo = ruolo;
		this.subSetField = subSetField;
		this.nameRef = nameRef;
	}

	private IdFieldEnum(String key, int idEcm, Ruolo ruolo, SubSetFieldEnum subSetField, String nameRef, List<IdFieldEnum> gruppo){
		this.key = key;
		this.idEcm = idEcm;
		this.ruolo = ruolo;
		this.subSetField = subSetField;
		this.nameRef = nameRef;
		this.gruppo = gruppo;
	}

	public static int getIdEcm(String key){
		for(IdFieldEnum e : IdFieldEnum.values()){
			if(e.getKey().equals(key))
				return e.getIdEcm();
		}
		return 0;
	}

	public static int getIdEcm(String key, Ruolo ruolo){
		for(IdFieldEnum e : IdFieldEnum.values()){
			if(e.getRuolo() == ruolo && e.getKey().equals(key))
				return e.getIdEcm();
		}
		return 0;
	}

	public static int getIdEcm(String key, SubSetFieldEnum subSet){
		for(IdFieldEnum e : IdFieldEnum.values()){
			if(e.getSubSetField() == subSet && e.getKey().equals(key))
				return e.getIdEcm();
		}
		return 0;
	}

	public static IdFieldEnum getIdField(String key){
		for(IdFieldEnum e : IdFieldEnum.values()){
			if(e.getKey().equals(key))
				return e;
		}
		return null;
	}

	public static IdFieldEnum getIdField(String key, Ruolo ruolo){
		for(IdFieldEnum e : IdFieldEnum.values()){
			if(e.getRuolo() == ruolo && e.getKey().equals(key))
				return e;
		}
		return null;
	}

	public static IdFieldEnum getIdField(String key, SubSetFieldEnum subSet){
		for(IdFieldEnum e : IdFieldEnum.values()){
			if(e.getSubSetField() == subSet && e.getKey().equals(key))
				return e;
		}
		return null;
	}


	public static Set<IdFieldEnum> getAllForSubset(SubSetFieldEnum subset){
		Set<IdFieldEnum> ids = new HashSet<IdFieldEnum>();
		for(IdFieldEnum e : IdFieldEnum.values()){
			if(subset == null || e.getSubSetField() == subset)
				ids.add(e);
		}
		return ids;
	}

	public static Set<IdFieldEnum> getAllForSubsetWithNameRefPrefix(SubSetFieldEnum subset, String nameRefPrefix){
		Set<IdFieldEnum> ids = new HashSet<IdFieldEnum>();
		for(IdFieldEnum e : IdFieldEnum.values()){
			if(e.getSubSetField() == subset && e.getNameRef().startsWith(nameRefPrefix))
				ids.add(e);
		}
		return ids;
	}

	public static boolean isFull(String nameRef){
		return nameRef.equals("full");
	}
}
