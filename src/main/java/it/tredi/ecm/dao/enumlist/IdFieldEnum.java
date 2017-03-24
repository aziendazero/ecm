package it.tredi.ecm.dao.enumlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;

@Getter
public enum IdFieldEnum {
	PROVIDER__TIPO_ORGANIZZATORE ("provider.tipoOrganizzatore", 1, null, SubSetFieldEnum.PROVIDER, "tipoOrganizzatore", true),
	PROVIDER__DENOMINAZIONE_LEGALE ("provider.denominazioneLegale", 2, null, SubSetFieldEnum.PROVIDER, "denominazioneLegale", true),
	PROVIDER__PARTITA_IVA ("provider.partitaIva", 3, null, SubSetFieldEnum.PROVIDER, "partitaIva", true),
	PROVIDER__CODICE_FISCALE ("provider.codiceFiscale", 4, null, SubSetFieldEnum.PROVIDER, "codiceFiscale", true),
	PROVIDER__RAGIONE_SOCIALE ("provider.ragioneSociale", 5, null, SubSetFieldEnum.PROVIDER, "ragioneSociale", true),
	PROVIDER__EMAIL_STRUTTURA ("provider.emailStruttura", 6, null, SubSetFieldEnum.PROVIDER, "emailStruttura", true),
	PROVIDER__NATURA_ORGANIZZAZIONE ("provider.naturaOrganizzazione", 7, null, SubSetFieldEnum.PROVIDER, "naturaOrganizzazione", true),
	PROVIDER__NO_PROFIT ("provider.noProfit", 8, null, SubSetFieldEnum.PROVIDER, "noProfit", true),

	LEGALE_RAPPRESENTANTE__FULL ("persona.anagrafica",-1,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "full", false),
	LEGALE_RAPPRESENTANTE__COGNOME ("persona.anagrafica.cognome",9,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.cognome", true),
	LEGALE_RAPPRESENTANTE__NOME ("persona.anagrafica.nome",10,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.nome", true),
	LEGALE_RAPPRESENTANTE__CODICEFISCALE ("persona.anagrafica.codiceFiscale",11,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.codiceFiscale", true),
	LEGALE_RAPPRESENTANTE__TELEFONO ("persona.anagrafica.telefono",12,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.telefono", true),
	LEGALE_RAPPRESENTANTE__CELLULARE ("persona.anagrafica.cellulare",13,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.cellulare", true),
	LEGALE_RAPPRESENTANTE__EMAIL ("persona.anagrafica.email",14,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.email", true),
	LEGALE_RAPPRESENTANTE__PEC ("persona.anagrafica.pec",15,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "anagrafica.pec", true),
	LEGALE_RAPPRESENTANTE__ATTO_NOMINA ("attoNomina",16,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "files.FILE_ATTO_NOMINA", false),
	LEGALE_RAPPRESENTANTE__CV ("cv",17,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE, "files.FILE_CV", false),

	DELEGATO_LEGALE_RAPPRESENTANTE__FULL ("persona.anagrafica",-1,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "full", false),
	DELEGATO_LEGALE_RAPPRESENTANTE__COGNOME ("persona.anagrafica.cognome",18,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.cognome", true),
	DELEGATO_LEGALE_RAPPRESENTANTE__NOME ("persona.anagrafica.nome",19,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.nome", true),
	DELEGATO_LEGALE_RAPPRESENTANTE__CODICEFISCALE ("persona.anagrafica.codiceFiscale",20,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.codiceFiscale", true),
	DELEGATO_LEGALE_RAPPRESENTANTE__TELEFONO ("persona.anagrafica.telefono",21,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.telefono", true),
	DELEGATO_LEGALE_RAPPRESENTANTE__CELLULARE ("persona.anagrafica.cellulare",22,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.cellulare", true),
	DELEGATO_LEGALE_RAPPRESENTANTE__EMAIL ("persona.anagrafica.email",23,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "anagrafica.email", true),
	DELEGATO_LEGALE_RAPPRESENTANTE__DELEGA ("delega",24,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "files.FILE_DELEGA", false),
	DELEGATO_LEGALE_RAPPRESENTANTE__CV ("cv",25,Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE, "files.FILE_CV", false),

	SEDE__IS_LEGALE ("sede.sedeLegale", -1, null, SubSetFieldEnum.SEDE, "sedeLegale", true),
	SEDE__IS_OPERATIVA ("sede.sedeOperativa", -1, null, SubSetFieldEnum.SEDE, "sedeOperativa", true),
	SEDE__PROVINCIA ("sede.provincia", 26, null, SubSetFieldEnum.SEDE, "provincia", true),
	SEDE__COMUNE ("sede.comune", 27, null, SubSetFieldEnum.SEDE, "comune", true),
	SEDE__INDIRIZZO ("sede.indirizzo", 28, null, SubSetFieldEnum.SEDE, "indirizzo", true),
	SEDE__CAP ("sede.cap", 29, null, SubSetFieldEnum.SEDE, "cap", true),
	SEDE__TELEFONO ("sede.telefono", 30, null, SubSetFieldEnum.SEDE, "telefono", true),
	SEDE__FAX ("sede.fax", 31, null, SubSetFieldEnum.SEDE, "fax", true),
	SEDE__EMAIL ("sede.email", 32, null, SubSetFieldEnum.SEDE, "email", true),

	DATI_ACCREDITAMENTO__TIPOLOGIA_ACCREDITAMENTO ("datiAccreditamento.tipologiaAccreditamento", 33, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "tipologiaAccreditamento", true),
	DATI_ACCREDITAMENTO__PROCEDURE_FORMATIVE ("datiAccreditamento.procedureFormative", 34, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "procedureFormative", true),
	DATI_ACCREDITAMENTO__PROFESSIONI_ACCREDITAMENTO ("datiAccreditamento.professioniAccreditamento", 35, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "professioniAccreditamento", true),
	DATI_ACCREDITAMENTO__DISCIPLINE ("datiAccreditamento.discipline", 36, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "discipline", true),

	DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_UNO ("datiAccreditamento.datiEconomici.fatturatoComplessivoValoreUno", -1, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "datiEconomici.fatturatoComplessivoValoreUno", false),
	DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_DUE ("datiAccreditamento.datiEconomici.fatturatoComplessivoValoreDue", -1, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "datiEconomici.fatturatoComplessivoValoreDue", false),
	DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_TRE ("datiAccreditamento.datiEconomici.fatturatoComplessivoValoreTre", -1, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "datiEconomici.fatturatoComplessivoValoreTre", false),
	DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO ("datiAccreditamento.datiEconomici.fatturatoComplessivo", 37, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "fatturatoComplessivo",Arrays.asList(DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_UNO,DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_DUE,DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_TRE), false),

	DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_COMPLESSIVO ("estrattoBilancioComplessivo", 38, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "files.FILE_ESTRATTO_BILANCIO_COMPLESSIVO", false),

	DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_UNO ("datiAccreditamento.datiEconomici.fatturatoFormazioneValoreUno", -1, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "datiEconomici.fatturatoFormazioneValoreUno", false),
	DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_DUE ("datiAccreditamento.datiEconomici.fatturatoFormazioneValoreDue", -1, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "datiEconomici.fatturatoFormazioneValoreDue", false),
	DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_TRE ("datiAccreditamento.datiEconomici.fatturatoFormazioneValoreTre", -1, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "datiEconomici.fatturatoFormazioneValoreTre", false),
	DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE ("datiAccreditamento.datiEconomici.fatturatoFormazione", 39, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "fatturatoFormazione",Arrays.asList(DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_UNO,DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_DUE,DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_TRE), false),

	DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_FORMAZIONE ("estrattoBilancioFormazione", 40, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "files.FILE_ESTRATTO_BILANCIO_FORMAZIONE", false),

	DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI_FORMAZIONE_TEMPO_INDETERMINATO ("datiAccreditamento.numeroDipendentiFormazioneTempoIndeterminato", -1, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "numeroDipendentiFormazioneTempoIndeterminato", false),
	DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI_FORMAZIONE_ALTRO ("datiAccreditamento.numeroDipendentiFormazioneAltro", -1, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "numeroDipendentiFormazioneAltro", false),
	DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI ("datiAccreditamento.numeroDipendenti", 41, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "numeroDipendentiFormazioneTempoIndeterminato", Arrays.asList(DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI_FORMAZIONE_TEMPO_INDETERMINATO, DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI_FORMAZIONE_ALTRO), false),

	DATI_ACCREDITAMENTO__ORGANIGRAMMA ("organigramma", 42, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "files.FILE_ORGANIGRAMMA", false),
	DATI_ACCREDITAMENTO__FUNZIONIGRAMMA ("funzionigramma", 43, null, SubSetFieldEnum.DATI_ACCREDITAMENTO, "files.FILE_FUNZIONIGRAMMA", false),

	RESPONSABILE_SEGRETERIA__FULL ("persona.anagrafica", -1, Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "full", false),
	RESPONSABILE_SEGRETERIA__COGNOME ("persona.anagrafica.cognome",44,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "anagrafica.cognome", true),
	RESPONSABILE_SEGRETERIA__NOME ("persona.anagrafica.nome",45,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "anagrafica.nome", true),
	RESPONSABILE_SEGRETERIA__CODICEFISCALE ("persona.anagrafica.codiceFiscale",46,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "anagrafica.codiceFiscale", true),
	RESPONSABILE_SEGRETERIA__TELEFONO ("persona.anagrafica.telefono",47,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "anagrafica.telefono", true),
	RESPONSABILE_SEGRETERIA__EMAIL ("persona.anagrafica.email",48,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "anagrafica.email", true),
	RESPONSABILE_SEGRETERIA__ATTO_NOMINA ("attoNomina",49,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "files.FILE_ATTO_NOMINA", false),
	RESPONSABILE_SEGRETERIA__CV ("cv",50,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA, "files.FILE_CV", false),

	RESPONSABILE_AMMINISTRATIVO__FULL ("persona.anagrafica", -1, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "full", false),
	RESPONSABILE_AMMINISTRATIVO__COGNOME ("persona.anagrafica.cognome", 51, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "anagrafica.cognome", true),
	RESPONSABILE_AMMINISTRATIVO__NOME ("persona.anagrafica.nome", 52, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "anagrafica.nome", true),
	RESPONSABILE_AMMINISTRATIVO__CODICEFISCALE ("persona.anagrafica.codiceFiscale", 53, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "anagrafica.codiceFiscale", true),
	RESPONSABILE_AMMINISTRATIVO__TELEFONO ("persona.anagrafica.telefono", 54, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "anagrafica.telefono", true),
	RESPONSABILE_AMMINISTRATIVO__EMAIL ("persona.anagrafica.email", 55, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "anagrafica.email", true),
	RESPONSABILE_AMMINISTRATIVO__ATTO_NOMINA ("attoNomina", 56, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "files.FILE_ATTO_NOMINA", false),
	RESPONSABILE_AMMINISTRATIVO__CV ("cv", 57, Ruolo.RESPONSABILE_AMMINISTRATIVO, SubSetFieldEnum.RESPONSABILE_AMMINISTRATIVO, "files.FILE_CV", false),

	RESPONSABILE_SISTEMA_INFORMATICO__FULL ("persona.anagrafica", -1, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "full", false),
	RESPONSABILE_SISTEMA_INFORMATICO__COGNOME ("persona.anagrafica.cognome", 58, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "anagrafica.cognome", true),
	RESPONSABILE_SISTEMA_INFORMATICO__NOME ("persona.anagrafica.nome", 59, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "anagrafica.nome", true),
	RESPONSABILE_SISTEMA_INFORMATICO__CODICEFISCALE ("persona.anagrafica.codiceFiscale", 60, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "anagrafica.codiceFiscale", true),
	RESPONSABILE_SISTEMA_INFORMATICO__TELEFONO ("persona.anagrafica.telefono", 61, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "anagrafica.telefono", true),
	RESPONSABILE_SISTEMA_INFORMATICO__EMAIL ("persona.anagrafica.email", 62, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "anagrafica.email", true),
	RESPONSABILE_SISTEMA_INFORMATICO__ATTO_NOMINA ("attoNomina", 63, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "files.FILE_ATTO_NOMINA", false),
	RESPONSABILE_SISTEMA_INFORMATICO__CV ("cv", 64, Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, SubSetFieldEnum.RESPONSABILE_SISTEMA_INFORMATICO, "files.FILE_CV", false),

	RESPONSABILE_QUALITA__FULL ("persona.anagrafica", -1, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "full", false),
	RESPONSABILE_QUALITA__COGNOME ("persona.anagrafica.cognome", 65, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "anagrafica.cognome", true),
	RESPONSABILE_QUALITA__NOME ("persona.anagrafica.nome", 66, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "anagrafica.nome", true),
	RESPONSABILE_QUALITA__CODICEFISCALE ("persona.anagrafica.codiceFiscale", 67, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "anagrafica.codiceFiscale", true),
	RESPONSABILE_QUALITA__TELEFONO ("persona.anagrafica.telefono", 68, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "anagrafica.telefono", true),
	RESPONSABILE_QUALITA__EMAIL ("persona.anagrafica.email", 69, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "anagrafica.email", true),
	RESPONSABILE_QUALITA__ATTO_NOMINA ("attoNomina", 70, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "files.FILE_ATTO_NOMINA", false),
	RESPONSABILE_QUALITA__CV ("cv", 71, Ruolo.RESPONSABILE_QUALITA, SubSetFieldEnum.RESPONSABILE_QUALITA, "files.FILE_CV", false),

	COMPONENTE_COMITATO_SCIENTIFICO__IS_COORDINATORE("persona.coordinatore", -1, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "coordinatoreComitatoScientifico", true),
	COMPONENTE_COMITATO_SCIENTIFICO__COGNOME ("persona.anagrafica.cognome", 72, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "anagrafica.cognome", true),
	COMPONENTE_COMITATO_SCIENTIFICO__NOME ("persona.anagrafica.nome", 73,Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "anagrafica.nome", true),
	COMPONENTE_COMITATO_SCIENTIFICO__CODICEFISCALE ("persona.anagrafica.codiceFiscale", 74, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "anagrafica.codiceFiscale", true),
	COMPONENTE_COMITATO_SCIENTIFICO__TELEFONO ("persona.anagrafica.telefono", 75, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "anagrafica.telefono", true),
	COMPONENTE_COMITATO_SCIENTIFICO__EMAIL ("persona.anagrafica.email", 76, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "anagrafica.email", true),
	COMPONENTE_COMITATO_SCIENTIFICO__PROFESSIONE ("persona.professione", 77, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "professione", true),
	COMPONENTE_COMITATO_SCIENTIFICO__ATTO_NOMINA ("attoNomina", 78, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "files.FILE_ATTO_NOMINA", false),
	COMPONENTE_COMITATO_SCIENTIFICO__CV ("cv", 79, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO, "files.FILE_CV", false),

	ACCREDITAMENTO_ALLEGATI__ATTO_COSTITUIVO ("attoCostitutivo", 80, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.FILE_ATTO_COSTITUTIVO", false),
	ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_ESCLUSIONE ("dichiarazioneEsclusione", 81, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.FILE_DICHIARAZIONE_ESCLUSIONE", false),
	ACCREDITAMENTO_ALLEGATI__ESPERIENZA_FORMAZIONE ("esperienzaFormazione", 82, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.FILE_ESPERIENZA_FORMAZIONE", false),
	ACCREDITAMENTO_ALLEGATI__UTILIZZO ("utilizzo", 83, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.FILE_UTILIZZO", false),
	ACCREDITAMENTO_ALLEGATI__SISTEMA_INFORMATICO ("sistemaInformatico", 84, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.FILE_SISTEMA_INFORMATICO", false),
	ACCREDITAMENTO_ALLEGATI__PIANO_QUALITA ("pianoQualita", 85, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.FILE_PIANO_QUALITA", false),
	ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_LEGALE ("dichiarazioneLegale", 86, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.FILE_DICHIARAZIONE_LEGALE", false),
	ACCREDITAMENTO_ALLEGATI__RICHIESTA_ACCREDITAMENTO_STANDARD ("richiestaAccreditamentoStandard", 87, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.FILE_RICHIESTA_ACCREDITAMENTO_STANDARD", false),
	ACCREDITAMENTO_ALLEGATI__RELAZIONE_ATTIVITA_FORMATIVA ("relazioneAttivitaFormativa", 88, null, SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, "files.FILE_RELAZIONE_ATTIVITA_FORMATIVA", false),

	VALUTAZIONE_SUL_CAMPO__PIANO_FORMATIVO("pianoFormativo", 89, null, SubSetFieldEnum.VALUTAZIONE_SUL_CAMPO, "pianoFormativo", false),
	VALUTAZIONE_SUL_CAMPO__IDONEITA_SEDE("idoneitaSede", 90, null, SubSetFieldEnum.VALUTAZIONE_SUL_CAMPO, "idoneitaSede", false),
	VALUTAZIONE_SUL_CAMPO__RELAZIONE_ANNUALE("relazioneAnnuale", 91, null, SubSetFieldEnum.VALUTAZIONE_SUL_CAMPO, "relazioneAnnuale", false),
	VALUTAZIONE_SUL_CAMPO__PERCEZIONE_INTERESSE_COMMERICALE_SANITA("percezioneInteresseCommercialeQualita", 92, null, SubSetFieldEnum.VALUTAZIONE_SUL_CAMPO, "percezioneInteresseCommercialeQualita", false),
	VALUTAZIONE_SUL_CAMPO__SCHEDA_QUALITA_PERCEPITA("schedaQualitaPercepita", 93, null, SubSetFieldEnum.VALUTAZIONE_SUL_CAMPO, "schedaQualitaPercepita", false),
	VALUTAZIONE_SUL_CAMPO__PRESENZA_PARTECIPANTI("presenzaPartecipanti", 94, null, SubSetFieldEnum.VALUTAZIONE_SUL_CAMPO, "presenzaPartecipanti", false),
	VALUTAZIONE_SUL_CAMPO__RECLUTAMENTO_DIRETTO("reclutamentoDiretto", 95, null, SubSetFieldEnum.VALUTAZIONE_SUL_CAMPO, "reclutamentoDiretto", false),
	VALUTAZIONE_SUL_CAMPO__VERIFICA_APPRENDIMENTO("verificaApprendimento", 96, null, SubSetFieldEnum.VALUTAZIONE_SUL_CAMPO, "verificaApprendimento", false),

	EVENTO_PIANO_FORMATIVO__PROCEDURA_FORMATIVA ("evento.proceduraFormativa", 1, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO, false),
	EVENTO_PIANO_FORMATIVO__TITOLO ("evento.titolo", 2, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO, false),
	EVENTO_PIANO_FORMATIVO__OBIETTIVO_NAZIONALE ("evento.obiettivoNazionale", 3, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO, false),
	EVENTO_PIANO_FORMATIVO__OBIETTIVO_REGIONALE ("evento.obiettivoRegionale", 4, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO, false),
	EVENTO_PIANO_FORMATIVO__PROFESSIONI_EVENTO ("evento.professioniEvento", 5, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO, false),
	EVENTO_PIANO_FORMATIVO__DISCIPLINE ("evento.discipline", 6, null, SubSetFieldEnum.EVENTO_PIANO_FORMATIVO, false),

	SEDE__FULL("sedi", -1, null, SubSetFieldEnum.FULL, "full", false),
	COMPONENTE_COMITATO_SCIENTIFICO__FULL ("comitatoScientifico", -1, null, SubSetFieldEnum.FULL, "full", false),
	EVENTO_PIANO_FORMATIVO__FULL ("pianoFormativo", -1, null, SubSetFieldEnum.FULL, "full", false);//gestiamo la possibilità di modificare o meno il piano formativo dentro l'accreditamento

	private int idEcm;
	private String key;
	private Ruolo ruolo;
	private SubSetFieldEnum subSetField;
	private Class<?> parentClassRef;//it.tredi.ecm.dao.entity.Persona
	private String nameRef;//nome
	private String typeRef;//java.lang.String
	private List<IdFieldEnum> gruppo = new ArrayList<IdFieldEnum>();
	private boolean defaultVal;

	private IdFieldEnum(String key, int idEcm, Ruolo ruolo, SubSetFieldEnum subSetField, boolean defaultVal){
		this.key = key;
		this.idEcm = idEcm;
		this.ruolo = ruolo;
		this.subSetField = subSetField;
		this.defaultVal = defaultVal;
	}

	private IdFieldEnum(String key, int idEcm, Ruolo ruolo, SubSetFieldEnum subSetField, String nameRef, boolean defaultVal){
		this.key = key;
		this.idEcm = idEcm;
		this.ruolo = ruolo;
		this.subSetField = subSetField;
		this.nameRef = nameRef;
		this.defaultVal = defaultVal;
	}

	private IdFieldEnum(String key, int idEcm, Ruolo ruolo, SubSetFieldEnum subSetField, String nameRef, List<IdFieldEnum> gruppo, boolean defaultVal){
		this.key = key;
		this.idEcm = idEcm;
		this.ruolo = ruolo;
		this.subSetField = subSetField;
		this.nameRef = nameRef;
		this.gruppo = gruppo;
		this.defaultVal = defaultVal;
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

	public static boolean isFull(IdFieldEnum idField){
		if(idField == null)
			return false;
		if(idField.getNameRef() == null)
			return false;
		else return idField.getNameRef().equals("full");
	}

	public static Set<IdFieldEnum> getAllIdField() {
		Set<IdFieldEnum> ids = new HashSet<IdFieldEnum>();
		for(IdFieldEnum e : IdFieldEnum.values()){
			ids.add(e);
		}
		return ids;
	}

	public static Set<IdFieldEnum> getAllFromIdToId(int i, int j) {
		Set<IdFieldEnum> ids = new HashSet<IdFieldEnum>();
		for(int x = i; x <= j; x++){
			IdFieldEnum id = getIdFieldFromIdEcm(x);
			ids.add(id);
		}
		return ids;
	}

	public static IdFieldEnum getIdFieldFromIdEcm(int i) {
		for(IdFieldEnum e : IdFieldEnum.values()){
			if(e.getIdEcm() == i)
				return e;
		}
		return null;
	}

	public static Set<IdFieldEnum> getDatiAccreditamentoSplitBySezione(int sezione) {
		Set<IdFieldEnum> ids = new HashSet<IdFieldEnum>();
		if(sezione == 1){
			ids.add(DATI_ACCREDITAMENTO__TIPOLOGIA_ACCREDITAMENTO);
			ids.add(DATI_ACCREDITAMENTO__PROCEDURE_FORMATIVE);
			ids.add(DATI_ACCREDITAMENTO__PROFESSIONI_ACCREDITAMENTO);
			ids.add(DATI_ACCREDITAMENTO__DISCIPLINE);
		}else if(sezione == 2){
			ids.add(DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_UNO);
			ids.add(DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_DUE);
			ids.add(DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_TRE);
			ids.add(DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO);

			ids.add(DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_COMPLESSIVO);

			ids.add(DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_UNO);
			ids.add(DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_DUE);
			ids.add(DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_TRE);
			ids.add(DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE);

			ids.add(DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_FORMAZIONE);
		}else if(sezione == 3){
			ids.add(DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI_FORMAZIONE_TEMPO_INDETERMINATO);
			ids.add(DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI_FORMAZIONE_ALTRO);
			ids.add(DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI);
			ids.add(DATI_ACCREDITAMENTO__ORGANIGRAMMA);
			ids.add(DATI_ACCREDITAMENTO__FUNZIONIGRAMMA);
		}

		return ids;
	}

	public boolean isFileFromSet(){
		return nameRef != null && nameRef.startsWith("files.");
	}

	public boolean hasGruppo() {
		switch(this) {
		case DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_UNO:
		case DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_DUE:
		case DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_TRE:
		case DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_UNO:
		case DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_DUE:
		case DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_TRE:
		case DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI_FORMAZIONE_TEMPO_INDETERMINATO:
		case DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI_FORMAZIONE_ALTRO:
			return true;
		default:
			return false;
		}
	}
}
