package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum FileEnum {
	FILE_DELEGA (1, "Delega"),
	FILE_CV (2, "CV"),
	FILE_ATTO_NOMINA (3, "Atto di nomina"),
	FILE_ESTRATTO_BILANCIO_FORMAZIONE (4, "Estratto del bilancio relativo alla formazione"),
	FILE_ESTRATTO_BILANCIO_COMPLESSIVO (5, "Estratto del bilancio complessivo degli ultimi 3 anni"),
	FILE_FUNZIONIGRAMMA (7, "Funzionigramma"),
	FILE_ORGANIGRAMMA (8, "Organigramma"),
	FILE_ATTO_COSTITUTIVO (9, "Atto Costitutivo e statuto"),
	FILE_ESPERIENZA_FORMAZIONE (10, "Esperienza formazione in ambito sanitario"),
	FILE_UTILIZZO (11, "Utilizzo di sedi, strutture ed attrezzature di altro soggetto"),
	FILE_SISTEMA_INFORMATICO (12, "Sistema informatico dedicato alla formazione"),
	FILE_PIANO_QUALITA (13, "Piano di Qualità"),
	FILE_DICHIARAZIONE_LEGALE (14, "Dichiarazione del Legale Rappresentante attestante la veridicità della documentazione"),
	FILE_MODELLO_ATTO_COSTITUTIVO (15, ""),
	FILE_MODELLO_ESPERIENZA_FORMAZIONE (16, ""),
	FILE_MODELLO_UTILIZZO (17, ""),
	FILE_MODELLO_SISTEMA_INFORMATICO (18, ""),
	FILE_MODELLO_PIANO_QUALITA (19, ""),
	FILE_MODELLO_DICHIARAZIONE_LEGALE (20, ""),
	FILE_DICHIARAZIONE_ESCLUSIONE (21, ""),
	FILE_VERBALE_VALUTAZIONE_SUL_CAMPO (22, "Verbale della valutazione sul campo"),

	FILE_NOTE_OSSERVAZIONI_INTEGRAZIONE (23, "Note e osservazioni integrazione"),
	FILE_NOTE_OSSERVAZIONI_PREAVVISO_RIGETTO (24, "Note e osservazioni preavviso rigetto"),

	FILE_ALLEGATO_COMUNICAZIONE (25, "Allegato Comunicazione"),
	FILE_ALLEGATO_RISPOSTA (26, "Allegato Risposta"),

	FILE_ACCREDITAMENTO_PROVVISORIO_DECRETO_ACCREDITAMENTO (27, "Decreto accreditamento provvisorio"),
	FILE_ACCREDITAMENTO_PROVVISORIO_DECRETO_DINIEGO (28, "Decreto diniego accreditamento provvisorio"),
	FILE_ACCREDITAMENTO_PROVVISORIO_INTEGRAZIONE (29, "Integrazione accreditamento provvisorio"),
	FILE_ACCREDITAMENTO_PROVVISORIO_PREAVVISO_RIGETTO (30, "Preavviso rigetto accreditamento provvisorio"),

	FILE_REPORT_PARTECIPANTI(31, "Report Partecipanti"),
	FILE_REPORT_PARTECIPANTI_XML(32, "File XML generato"),
	FILE_REPORT_PARTECIPANTI_CSV(33, "File CSV caricato"),

	FILE_BROCHURE_EVENTO(34, "Brochure dell'Evento"),
	FILE_VERIFICA_RICADUTE_FORMATIVE(35, "Verifica a distanza delle ricadute formative"),
	FILE_CONTRATTI_ACCORDI_CONVENZIONI(36, "Allegato contratti/accordi/convenzioni"),
	FILE_AUTOCERTIFICAZIONE_ASSENZA_FINANZIAMENTI(37, "Autocertificazione assenza finanziamenti"),
	FILE_DICHIARAZIONE_ASSENZA_CONFLITTO_INTERESSE(38, "Dichiarazione di assenza del conflitto di interesse"),
	FILE_CONTRATTO_SPONSOR(39, "Contratto dello sponsor"),
	FILE_CONTRATTO_PARTNER(40, "Contratto del partner"),
	FILE_AUTOCERTIFICAZIONE_AUTORIZZAZIONE_MINISTERO_SALUTE(41, "Autocertificazione di autorizzazione del Ministero della Salute"),
	FILE_AUTOCERTIFICAZIONE_ASSENZA_PARTECIPAZIONE_SPONSOR_INFANZIA(42, "Autocertificazione relativa all'assenza di partecipazione finanziaria di imprese interessate agli alimenti per la prima infanzia"),
	FILE_EVENTI_PIANO_FORMATIVO(43, "File CSV per import eventi piano formativo"),
	FILE_REQUISITI_HARDWARE_SOFTWARE(43, "Dotazione hardware e software necessaria all'utente per svolgere l'evento"),

	FILE_RELAZIONE_FINALE(44, "Relazione Finale"),
	
	//ENGINEERING TEST FILE
	FILE_DA_FIRMARE(99, "");

	private int id;
	private String nome;

	private FileEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
