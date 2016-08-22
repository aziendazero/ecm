package it.tredi.ecm.dao.enumlist;

import java.util.HashSet;
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

	SEDE_LEGALE__FULL ("sede", -1, null, SubSetFieldEnum.SEDE_LEGALE ),
	SEDE_LEGALE__PROVINCIA ("sede.provincia", 9, null, SubSetFieldEnum.SEDE_LEGALE, "provincia"),
	SEDE_LEGALE__COMUNE ("sede.comune", 10, null, SubSetFieldEnum.SEDE_LEGALE, "comune"),
	SEDE_LEGALE__INDIRIZZO ("sede.indirizzo", 11, null, SubSetFieldEnum.SEDE_LEGALE, "indirizzo"),
	SEDE_LEGALE__CAP ("sede.cap", 12, null, SubSetFieldEnum.SEDE_LEGALE, "cap"),
	SEDE_LEGALE__TELEFONO ("sede.telefono", 13, null, SubSetFieldEnum.SEDE_LEGALE, "telefono"),
	SEDE_LEGALE__FAX ("sede.fax", 14, null, SubSetFieldEnum.SEDE_LEGALE, "fax"),
	SEDE_LEGALE__EMAIL ("sede.email", 15, null, SubSetFieldEnum.SEDE_LEGALE, "email"),

	SEDE_OPERATIVA__FULL ("sede", -1, null, SubSetFieldEnum.SEDE_OPERATIVA),
	SEDE_OPERATIVA__PROVINCIA ("sede.provincia", 16, null, SubSetFieldEnum.SEDE_OPERATIVA, "provincia"),
	SEDE_OPERATIVA__COMUNE ("sede.comune", 17, null, SubSetFieldEnum.SEDE_OPERATIVA, "comune"),
	SEDE_OPERATIVA__INDIRIZZO ("sede.indirizzo", 18, null, SubSetFieldEnum.SEDE_OPERATIVA, "indirizzo"),
	SEDE_OPERATIVA__CAP ("sede.cap", 19, null, SubSetFieldEnum.SEDE_OPERATIVA, "cap"),
	SEDE_OPERATIVA__TELEFONO ("sede.telefono", 20, null, SubSetFieldEnum.SEDE_OPERATIVA, "telefono"),
	SEDE_OPERATIVA__FAX ("sede.fax", 21, null, SubSetFieldEnum.SEDE_OPERATIVA, "fax"),
	SEDE_OPERATIVA__EMAIL ("sede.email", 22, null, SubSetFieldEnum.SEDE_OPERATIVA, "email"),

	LEGALE_RAPPRESENTANTE__FULL ("persona.legaleRappresentante",-1,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE),
	LEGALE_RAPPRESENTANTE__COGNOME ("persona.anagrafica.cognome",23,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.cognome"),
	LEGALE_RAPPRESENTANTE__NOME ("persona.anagrafica.nome",24,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.nome"),
	LEGALE_RAPPRESENTANTE__CODICEFISCALE ("persona.anagrafica.codiceFiscale",25,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.codiceFiscale"),
	LEGALE_RAPPRESENTANTE__TELEFONO ("persona.anagrafica.telefono",26,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.telefono"),
	LEGALE_RAPPRESENTANTE__CELLULARE ("persona.anagrafica.cellulare",27,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.cellulare"),
	LEGALE_RAPPRESENTANTE__EMAIL ("persona.anagrafica.email",28,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.email"),
	LEGALE_RAPPRESENTANTE__PEC ("persona.anagrafica.pec",29,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.pec"),
	LEGALE_RAPPRESENTANTE__ATTO_NOMINA ("attoNomina",30,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "files.FILE_ATTO_NOMINA"),
	LEGALE_RAPPRESENTANTE__CV ("cv",31,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "files.FILE_CV"),

	DELEGATO_LEGALE_RAPPRESENTANTE__FULL ("persona.delegatoLegaleRappresentante",-1,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE),
	DELEGATO_LEGALE_RAPPRESENTANTE__COGNOME ("persona.anagrafica.cognome",32,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.cognome"),
	DELEGATO_LEGALE_RAPPRESENTANTE__NOME ("persona.anagrafica.nome",33,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.nome"),
	DELEGATO_LEGALE_RAPPRESENTANTE__CODICEFISCALE ("persona.anagrafica.codiceFiscale",34,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.codiceFiscale"),
	DELEGATO_LEGALE_RAPPRESENTANTE__TELEFONO ("persona.anagrafica.telefono",35,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.telefono"),
	DELEGATO_LEGALE_RAPPRESENTANTE__CELLULARE ("persona.anagrafica.cellulare",36,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.cellulare"),
	DELEGATO_LEGALE_RAPPRESENTANTE__EMAIL ("persona.anagrafica.email",37,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.email"),
	DELEGATO_LEGALE_RAPPRESENTANTE__DELEGA ("delega",38,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "files.FILE_DELEGA"),
	DELEGATO_LEGALE_RAPPRESENTANTE__CV ("cv",39,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "files.FILE_CV"),

	DATI_ACCREDITAMENTO__TIPOLOGIA_ACCREDITAMENTO ("datiAccreditamento.tipologiaAccreditamento", 40, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "tipologiaAccreditamento"),
	DATI_ACCREDITAMENTO__PROCEDURE_FORMATIVE ("datiAccreditamento.procedureFormative", 41, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "procedureFormative"),
	DATI_ACCREDITAMENTO__PROFESSIONI_ACCREDITAMENTO ("datiAccreditamento.professioniAccreditamento", 42, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "professioniAccreditamento"),
	DATI_ACCREDITAMENTO__DISCIPLINE ("datiAccreditamento.discipline", 43, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "discipline"),
	DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO ("datiAccreditamento.datiEconomici.fatturatoComplessivo", 44, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "fatturatoComplessivo"),
	DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_COMPLESSIVO ("estrattoBilancioComplessivo", 45, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "files.FILE_ESTRATTO_BILANCIO_COMPLESSIVO"),
	DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE ("datiAccreditamento.datiEconomici.fatturatoFormazione", 46, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "fatturatoFormazione"),
	DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_FORMAZIONE ("estrattoBilancioFormazione", 47, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "files.FILE_ESTRATTO_BILANCIO_FORMAZIONE"),
	DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI ("datiAccreditamento.numeroDipendenti", 48, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "numeroDipendentiFormazioneTempoIndeterminato"),
	DATI_ACCREDITAMENTO__ORGANIGRAMMA ("organigramma", 49, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "files.FILE_ORGANIGRAMMA"),
	DATI_ACCREDITAMENTO__FUNZIONIGRAMMA ("funzionigramma", 50, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "files.FILE_FUNZIONIGRAMMA"),

	RESPONSABILE_SEGRETERIA__FULL ("persona.anagrafica", -1, Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "full"),
	RESPONSABILE_SEGRETERIA__COGNOME ("persona.anagrafica.cognome",51,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "anagrafica.cognome"),
	RESPONSABILE_SEGRETERIA__NOME ("persona.anagrafica.nome",52,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "anagrafica.nome"),
	RESPONSABILE_SEGRETERIA__CODICEFISCALE ("persona.anagrafica.codiceFiscale",53,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "anagrafica.codiceFiscale"),
	RESPONSABILE_SEGRETERIA__TELEFONO ("persona.anagrafica.telefono",54,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "anagrafica.telefono"),
	RESPONSABILE_SEGRETERIA__EMAIL ("persona.anagrafica.email",55,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "anagrafica.email"),
	RESPONSABILE_SEGRETERIA__ATTO_NOMINA ("attoNomina",56,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "files.FILE_ATTO_NOMINA"),
	RESPONSABILE_SEGRETERIA__CV ("cv",57,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "files.FILE_CV"),

	RESPONSABILE_AMMINISTRATIVO__FULL ("persona.anagrafica", -1, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO),
	RESPONSABILE_AMMINISTRATIVO__COGNOME ("persona.anagrafica.cognome", 58, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "anagrafica.cognome"),
	RESPONSABILE_AMMINISTRATIVO__NOME ("persona.anagrafica.nome", 59, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "anagrafica.nome"),
	RESPONSABILE_AMMINISTRATIVO__CODICEFISCALE ("persona.anagrafica.codiceFiscale", 60, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "anagrafica.codiceFiscale"),
	RESPONSABILE_AMMINISTRATIVO__TELEFONO ("persona.anagrafica.telefono", 61, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "anagrafica.telefono"),
	RESPONSABILE_AMMINISTRATIVO__EMAIL ("persona.anagrafica.email", 62, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "anagrafica.email"),
	RESPONSABILE_AMMINISTRATIVO__ATTO_NOMINA ("attoNomina", 63, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "files.FILE_ATTO_NOMINA"),
	RESPONSABILE_AMMINISTRATIVO__CV ("cv", 64, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "files.FILE_CV"),

	RESPONSABILE_SISTEMA_INFORMATICO__FULL ("persona.anagrafica", -1, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO),
	RESPONSABILE_SISTEMA_INFORMATICO__COGNOME ("persona.anagrafica.cognome", 74, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "anagrafica.cognome"),
	RESPONSABILE_SISTEMA_INFORMATICO__NOME ("persona.anagrafica.nome", 75, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "anagrafica.nome"),
	RESPONSABILE_SISTEMA_INFORMATICO__CODICEFISCALE ("persona.anagrafica.codiceFiscale", 76, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "anagrafica.codiceFiscale"),
	RESPONSABILE_SISTEMA_INFORMATICO__TELEFONO ("persona.anagrafica.telefono", 77, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "anagrafica.telefono"),
	RESPONSABILE_SISTEMA_INFORMATICO__EMAIL ("persona.anagrafica.email", 78, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "anagrafica.email"),
	RESPONSABILE_SISTEMA_INFORMATICO__ATTO_NOMINA ("attoNomina", 79, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "files.FILE_ATTO_NOMINA"),
	RESPONSABILE_SISTEMA_INFORMATICO__CV ("cv", 80, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "files.FILE_CV"),

	RESPONSABILE_QUALITA__FULL ("persona.anagrafica", -1, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA),
	RESPONSABILE_QUALITA__COGNOME ("persona.anagrafica.cognome", 81, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "anagrafica.cognome"),
	RESPONSABILE_QUALITA__NOME ("persona.anagrafica.nome", 82, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "anagrafica.nome"),
	RESPONSABILE_QUALITA__CODICEFISCALE ("persona.anagrafica.codiceFiscale", 83, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "anagrafica.codiceFiscale"),
	RESPONSABILE_QUALITA__TELEFONO ("persona.anagrafica.telefono", 84, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "anagrafica.telefono"),
	RESPONSABILE_QUALITA__EMAIL ("persona.anagrafica.email", 85, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "anagrafica.email"),
	RESPONSABILE_QUALITA__ATTO_NOMINA ("attoNomina", 86, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "files.FILE_ATTO_NOMINA"),
	RESPONSABILE_QUALITA__CV ("cv", 87, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "files.FILE_CV"),

	COMPONENTE_COMITATO_SCIENTIFICO__COGNOME ("persona.anagrafica.cognome", 65,Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "anagrafica.cognome"),
	COMPONENTE_COMITATO_SCIENTIFICO__NOME ("persona.anagrafica.nome", 66,Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "anagrafica.nome"),
	COMPONENTE_COMITATO_SCIENTIFICO__CODICEFISCALE ("persona.anagrafica.codiceFiscale", 67, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "anagrafica.codiceFiscale"),
	COMPONENTE_COMITATO_SCIENTIFICO__TELEFONO ("persona.anagrafica.telefono", 68, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "anagrafica.telefono"),
	COMPONENTE_COMITATO_SCIENTIFICO__EMAIL ("persona.anagrafica.email", 70, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "anagrafica.email"),
	COMPONENTE_COMITATO_SCIENTIFICO__PROFESSIONE ("persona.professione", 71, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "professione"),
	COMPONENTE_COMITATO_SCIENTIFICO__ATTO_NOMINA ("attoNomina", 72, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "files.FILE_ATTO_NOMINA"),
	COMPONENTE_COMITATO_SCIENTIFICO__CV ("cv", 73, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "files.FILE_CV"),

	ACCREDITAMENTO_ALLEGATI__ATTO_COSTITUIVO ("attoCostitutivo", 88, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.ATTO_COSTITUTIVO"),
	ACCREDITAMENTO_ALLEGATI__ESPERIENZA_FORMAZIONE ("esperienzaFormazione", 89, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.ESPERIENZA_FORMAZIONE"),
	ACCREDITAMENTO_ALLEGATI__UTILIZZO ("utilizzo", 90, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.UTILIZZO"),
	ACCREDITAMENTO_ALLEGATI__SISTEMA_INFORMATICO ("sistemaInformatico", 91, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.SISTEMA_INFORMATICO"),
	ACCREDITAMENTO_ALLEGATI__PIANO_QUALITA ("pianoQualita", 92, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.PIANO_QUALITA"),
	ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_LEGALE ("dichiarazioneLegale", 93, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.DICHIARAZIONE_LEGALE"),
	ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_ESCLUSIONE ("dichiarazioneEsclusione", 94, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.DICHIARAZIONE_ESCLUSIONE"),

	EVENTO_PIANO_FORMATIVO__PROCEDURA_FORMATIVA ("evento.proceduraFormativa", 1, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO),
	EVENTO_PIANO_FORMATIVO__TITOLO ("evento.titolo", 2, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO),
	EVENTO_PIANO_FORMATIVO__OBIETTIVO_NAZIONALE ("evento.obiettivoNazionale", 3, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO),
	EVENTO_PIANO_FORMATIVO__OBIETTIVO_REGIONALE ("evento.obiettivoRegionale", 4, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO),
	EVENTO_PIANO_FORMATIVO__PROFESSIONI_EVENTO ("evento.professioniEvento", 5, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO),
	EVENTO_PIANO_FORMATIVO__DISCIPLINE ("evento.discipline", 6, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO),

	COMPONENTE_COMITATO_SCIENTIFICO__FULL ("comitatoScientifico", -1, null, SubSetFieldEnum.FULL),
	EVENTO_PIANO_FORMATIVO__FULL ("pianoFormativo", -1, null, SubSetFieldEnum.FULL);//gestiamo la possibilità di modificare o meno il piano formativo dentro l'accreditamento

	private int idEcm;
	private String key;
	private Ruolo ruolo;
	private SubSetFieldEnum subSetField;
	private Class parentClassRef;//it.tredi.ecm.dao.entity.Persona
	private String nameRef;//nome
	private String typeRef;//java.lang.String

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
			if(e.getSubSetField() == subset)
				ids.add(e);
		}
		return ids;
	}
	
	public static boolean isFull(String nameRef){
		return nameRef.equals("full");
	}
}
