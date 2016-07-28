package it.tredi.ecm.dao.enumlist;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

@Getter
public enum IdFieldEnum {
	PROVIDER__TIPO_ORGANIZZATORE ("provider.tipoOrganizzatore", 1, null, SubSetFieldEnum.PROVIDER),
	PROVIDER__DENOMINAZIONE_LEGALE ("provider.denominazioneLegale", 2, null, SubSetFieldEnum.PROVIDER),
	PROVIDER__PARTITA_IVA ("provider.partitaIva", 3, null, SubSetFieldEnum.PROVIDER),
	PROVIDER__CODICE_FISCALE ("provider.codiceFiscale", 4, null, SubSetFieldEnum.PROVIDER),
	PROVIDER__RAGIONE_SOCIALE ("provider.ragioneSociale", 5, null, SubSetFieldEnum.PROVIDER),
	PROVIDER__NATURA_ORGANIZZAZIONE ("provider.naturaOrganizzazione", 6, null, SubSetFieldEnum.PROVIDER),
	PROVIDER__NO_PROFIT ("provider.noProfit", 7, null, SubSetFieldEnum.PROVIDER),

	SEDE_LEGALE__FULL ("SedeLegale", -1, null, SubSetFieldEnum.SEDE_LEGALE),
	SEDE_LEGALE__PROVINCIA ("SedeLegale.provincia", 8, null, SubSetFieldEnum.SEDE_LEGALE),
	SEDE_LEGALE__COMUNE ("SedeLegale.comune", 9, null, SubSetFieldEnum.SEDE_LEGALE),
	SEDE_LEGALE__INDIRIZZO ("SedeLegale.indirizzo", 10, null, SubSetFieldEnum.SEDE_LEGALE),
	SEDE_LEGALE__CAP ("SedeLegale.cap", 11, null, SubSetFieldEnum.SEDE_LEGALE),
	SEDE_LEGALE__TELEFONO ("SedeLegale.telefono", 12, null, SubSetFieldEnum.SEDE_LEGALE),
	SEDE_LEGALE__FAX ("SedeLegale.fax", 13, null, SubSetFieldEnum.SEDE_LEGALE),
	SEDE_LEGALE__EMAIL ("SedeLegale.email", 14, null, SubSetFieldEnum.SEDE_LEGALE),

	SEDE_OPERATIVA__FULL ("SedeOperativa", -1, null, SubSetFieldEnum.SEDE_OPERATIVA),
	SEDE_OPERATIVA__PROVINCIA ("SedeOperativa.provincia", 15, null, SubSetFieldEnum.SEDE_OPERATIVA),
	SEDE_OPERATIVA__COMUNE ("SedeOperativa.comune", 16, null, SubSetFieldEnum.SEDE_OPERATIVA),
	SEDE_OPERATIVA__INDIRIZZO ("SedeOperativa.indirizzo", 17, null, SubSetFieldEnum.SEDE_OPERATIVA),
	SEDE_OPERATIVA__CAP ("SedeOperativa.cap", 18, null, SubSetFieldEnum.SEDE_OPERATIVA),
	SEDE_OPERATIVA__TELEFONO ("SedeOperativa.telefono", 19, null, SubSetFieldEnum.SEDE_OPERATIVA),
	SEDE_OPERATIVA__FAX ("SedeOperativa.fax", 20, null, SubSetFieldEnum.SEDE_OPERATIVA),
	SEDE_OPERATIVA__EMAIL ("SedeOperativa.email", 21, null, SubSetFieldEnum.SEDE_OPERATIVA),

	LEGALE_RAPPRESENTANTE__FULL ("persona.legaleRappresentante",-1,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE),
	LEGALE_RAPPRESENTANTE__COGNOME ("persona.anagrafica.cognome",22,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE),
	LEGALE_RAPPRESENTANTE__NOME ("persona.anagrafica.nome",23,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE),
	LEGALE_RAPPRESENTANTE__CODICEFISCALE ("persona.anagrafica.codiceFiscale",24,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE),
	LEGALE_RAPPRESENTANTE__TELEFONO ("persona.anagrafica.telefono",25,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE),
	LEGALE_RAPPRESENTANTE__CELLULARE ("persona.anagrafica.cellulare",26,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE),
	LEGALE_RAPPRESENTANTE__EMAIL ("persona.anagrafica.email",27,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE),
	LEGALE_RAPPRESENTANTE__PEC ("persona.anagrafica.pec",28,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE),
	LEGALE_RAPPRESENTANTE__ATTO_NOMINA ("persona.file.attoNomina",29,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE),
	LEGALE_RAPPRESENTANTE__CV ("persona.file.cv",30,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE),

	DELEGATO_LEGALE_RAPPRESENTANTE__FULL ("persona.delegatoLegaleRappresentante",-1,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE),
	DELEGATO_LEGALE_RAPPRESENTANTE__COGNOME ("persona.anagrafica.cognome",31,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE),
	DELEGATO_LEGALE_RAPPRESENTANTE__NOME ("persona.anagrafica.nome",32,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE),
	DELEGATO_LEGALE_RAPPRESENTANTE__CODICEFISCALE ("persona.anagrafica.codiceFiscale",33,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE),
	DELEGATO_LEGALE_RAPPRESENTANTE__TELEFONO ("persona.anagrafica.telefono",34,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE),
	DELEGATO_LEGALE_RAPPRESENTANTE__CELLULARE ("persona.anagrafica.cellulare",35,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE),
	DELEGATO_LEGALE_RAPPRESENTANTE__EMAIL ("persona.anagrafica.email",36,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE),
	DELEGATO_LEGALE_RAPPRESENTANTE__CV ("persona.file.cv",37,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE),
	DELEGATO_LEGALE_RAPPRESENTANTE__DELEGA ("persona.file.delega",38,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE),

	DATI_ACCREDITAMENTO__TIPOLOGIA_ACCREDITAMENTO ("datiAccreditamento.tipologiaAccreditamento", 39, null, SubSetFieldEnum.DATI_ACCREDITAMENTO),
	DATI_ACCREDITAMENTO__PROCEDURE_FORMATIVE ("datiAccreditamento.procedureFormative", 40, null, SubSetFieldEnum.DATI_ACCREDITAMENTO),
	DATI_ACCREDITAMENTO__PROFESSIONI_ACCREDITAMENTO ("datiAccreditamento.professioniAccreditamento", 41, null, SubSetFieldEnum.DATI_ACCREDITAMENTO),
	DATI_ACCREDITAMENTO__DISCIPLINE ("datiAccreditamento.discipline", 42, null, SubSetFieldEnum.DATI_ACCREDITAMENTO),
	DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO ("datiAccreditamento.datiEconomici.fatturatoComplessivo", 43, null, SubSetFieldEnum.DATI_ACCREDITAMENTO),
	DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_COMPLESSIVO ("datiAccreditamento.file.estrattoBilancioComplessivo", 44, null, SubSetFieldEnum.DATI_ACCREDITAMENTO),
	DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE ("datiAccreditamento.datiEconomici.fatturatoFormazione", 45, null, SubSetFieldEnum.DATI_ACCREDITAMENTO),
	DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_FORMAZIONE ("datiAccreditamento.file.estrattoBilancioFormazione", 46, null, SubSetFieldEnum.DATI_ACCREDITAMENTO),	
	DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI ("datiAccreditamento.numeroDipendenti", 47, null, SubSetFieldEnum.DATI_ACCREDITAMENTO),
	DATI_ACCREDITAMENTO__ORGANIGRAMMA ("datiAccreditamento.file.organigramma", 48, null, SubSetFieldEnum.DATI_ACCREDITAMENTO),
	DATI_ACCREDITAMENTO__FUNZIONIGRAMMA ("datiAccreditamento.file.funzionigramma", 49, null, SubSetFieldEnum.DATI_ACCREDITAMENTO),

	RESPONSABILE_SEGRETERIA__FULL ("persona.anagrafica", -1, Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA),
	RESPONSABILE_SEGRETERIA__COGNOME ("persona.anagrafica.cognome",50,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA),
	RESPONSABILE_SEGRETERIA__NOME ("persona.anagrafica.nome",51,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA),
	RESPONSABILE_SEGRETERIA__CODICEFISCALE ("persona.anagrafica.codiceFiscale",52,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA),
	RESPONSABILE_SEGRETERIA__TELEFONO ("persona.anagrafica.telefono",53,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA),
	RESPONSABILE_SEGRETERIA__EMAIL ("persona.anagrafica.email",54,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA),
	RESPONSABILE_SEGRETERIA__ATTO_NOMINA ("persona.file.attoNomina",55,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA),
	RESPONSABILE_SEGRETERIA__CV ("persona.file.cv",56,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA),

	RESPONSABILE_AMMINISTRATIVO__FULL ("persona.anagrafica", -1, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO),
	RESPONSABILE_AMMINISTRATIVO__COGNOME ("persona.anagrafica.cognome", 57, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO),
	RESPONSABILE_AMMINISTRATIVO__NOME ("persona.anagrafica.nome", 58, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO),
	RESPONSABILE_AMMINISTRATIVO__CODICEFISCALE ("persona.anagrafica.codiceFiscale", 59, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO),
	RESPONSABILE_AMMINISTRATIVO__TELEFONO ("persona.anagrafica.telefono", 60, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO),
	RESPONSABILE_AMMINISTRATIVO__CELLULARE ("persona.anagrafica.cellulare", 61, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO),
	RESPONSABILE_AMMINISTRATIVO__EMAIL ("persona.anagrafica.email", 62, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO),
	RESPONSABILE_AMMINISTRATIVO__ATTO_NOMINA ("persona.file.attoNomina", 63, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO),
	RESPONSABILE_AMMINISTRATIVO__CV ("persona.file.cv", 64, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO),

	RESPONSABILE_SISTEMA_INFORMATICO__FULL ("persona.anagrafica", -1, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO),
	RESPONSABILE_SISTEMA_INFORMATICO__COGNOME ("persona.anagrafica.cognome", 74, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO),
	RESPONSABILE_SISTEMA_INFORMATICO__NOME ("persona.anagrafica.nome", 75, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO),
	RESPONSABILE_SISTEMA_INFORMATICO__CODICEFISCALE ("persona.anagrafica.codiceFiscale", 76, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO),
	RESPONSABILE_SISTEMA_INFORMATICO__TELEFONO ("persona.anagrafica.telefono", 77, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO),
	RESPONSABILE_SISTEMA_INFORMATICO__EMAIL ("persona.anagrafica.email", 78, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO),
	RESPONSABILE_SISTEMA_INFORMATICO__PROFESSIONE ("persona.professione", 79, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO),
	RESPONSABILE_SISTEMA_INFORMATICO__ATTO_NOMINA ("persona.file.attoNomina", 80, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO),
	RESPONSABILE_SISTEMA_INFORMATICO__CV ("persona.file.cv", 81, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO),

	RESPONSABILE_QUALITA__FULL ("persona.anagrafica", -1, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA),
	RESPONSABILE_QUALITA__COGNOME ("persona.anagrafica.cognome", 82, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA),
	RESPONSABILE_QUALITA__NOME ("persona.anagrafica.nome", 83, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA),
	RESPONSABILE_QUALITA__CODICEFISCALE ("persona.anagrafica.codiceFiscale", 84, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA),
	RESPONSABILE_QUALITA__TELEFONO ("persona.anagrafica.telefono", 85, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA),
	RESPONSABILE_QUALITA__EMAIL ("persona.anagrafica.email", 86, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA),
	RESPONSABILE_QUALITA__PROFESSIONE ("persona.professione", 87, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA),
	RESPONSABILE_QUALITA__ATTO_NOMINA ("persona.file.attoNomina", 88, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA),
	RESPONSABILE_QUALITA__CV ("persona.file.cv", 89, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA),

	COMITATO_SCIENTIFICO__FULL ("comitatoScientifico", -1, null, SubSetFieldEnum.COMITATO_SCIENTIFICO),
	COMPONENTE_COMITATO_SCIENTIFICO__FULL ("persona.anagrafica", -1,Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO),
	COMPONENTE_COMITATO_SCIENTIFICO__COGNOME ("persona.anagrafica.cognome", 65,Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO),
	COMPONENTE_COMITATO_SCIENTIFICO__NOME ("persona.anagrafica.nome", 66,Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO),
	COMPONENTE_COMITATO_SCIENTIFICO__CODICEFISCALE ("persona.anagrafica.codiceFiscale", 67, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO),
	COMPONENTE_COMITATO_SCIENTIFICO__TELEFONO ("persona.anagrafica.telefono", 68, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO),
	COMPONENTE_COMITATO_SCIENTIFICO__CELLULARE ("persona.anagrafica.cellulare", 69, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO),
	COMPONENTE_COMITATO_SCIENTIFICO__EMAIL ("persona.anagrafica.email", 70, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO),
	COMPONENTE_COMITATO_SCIENTIFICO__PROFESSIONE ("persona.professione", 71, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO),
	COMPONENTE_COMITATO_SCIENTIFICO__ATTO_NOMINA ("persona.file.attoNomina", 72, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO),
	COMPONENTE_COMITATO_SCIENTIFICO__CV ("persona.file.cv", 73, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO),

	ACCREDITAMENTO_ALLEGATI__ATTO_COSTITUIVO ("accreditamentoAllegati.file.attoCostitutivo", 90, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO),
	ACCREDITAMENTO_ALLEGATI__ESPERIENZA_FORMAZIONE ("accreditamentoAllegati.file.esperienzaFormazione", 91, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO),
	ACCREDITAMENTO_ALLEGATI__UTILIZZO ("accreditamentoAllegati.file.utilizzo", 92, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO),
	ACCREDITAMENTO_ALLEGATI__SISTEMA_INFORMATICO ("accreditamentoAllegati.file.sistemaInformatico", 93, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO),
	ACCREDITAMENTO_ALLEGATI__PIANO_QUALITA ("accreditamentoAllegati.file.pianoQualita", 94, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO),
	ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_LEGALE ("accreditamentoAllegati.file.dichiarazioneLegale", 95, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO),
	ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_ESCLUSIONE ("accreditamentoAllegati.file.dichiarazioneEsclusione", 96, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO),

	EVENTO_PIANO_FORMATIVO__FULL ("pianoFormativo", -1, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO),//gestiamo la possibilità di modificare o meno il piano formativo dentro l'accreditamento
	EVENTO_PIANO_FORMATIVO__PROCEDURA_FORMATIVA ("evento.proceduraFormativa", 1, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO),
	EVENTO_PIANO_FORMATIVO__TITOLO ("evento.titolo", 2, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO),
	EVENTO_PIANO_FORMATIVO__OBIETTIVO_NAZIONALE ("evento.obiettivoNazionale", 3, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO),
	EVENTO_PIANO_FORMATIVO__OBIETTIVO_REGIONALE ("evento.obiettivoRegionale", 4, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO),
	EVENTO_PIANO_FORMATIVO__PROFESSIONI_EVENTO ("evento.professioniEvento", 5, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO),
	EVENTO_PIANO_FORMATIVO__DISCIPLINE ("evento.discipline", 6, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO);

	private int idEcm;
	private String key;
	private Ruolo ruolo;
	private SubSetFieldEnum subSetField;

	private IdFieldEnum(String key, int idEcm, Ruolo ruolo, SubSetFieldEnum subSetField){
		this.key = key;
		this.idEcm = idEcm;
		this.ruolo = ruolo;
		this.subSetField = subSetField;
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

	public static Set<IdFieldEnum> getAllForSubset(SubSetFieldEnum subset){
		Set<IdFieldEnum> ids = new HashSet<IdFieldEnum>();
		for(IdFieldEnum e : IdFieldEnum.values()){
			if(e.getSubSetField() == subset)
				ids.add(e);
		}
		return ids;
	}
}
