package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum FileEnum {
	FILE_DELEGA (1, "Delega"),
	FILE_CV (2, "CV"),
	FILE_ATTO_NOMINA (3, "Atto di nomina"),
	FILE_ESTRATTO_BILANCIO_FORMAZIONE (4, "Estratto del bilancio relativo alla formazione"),
	FILE_BUDGET_PREVISIONALE (5, "Budget previsionale"),
	FILE_FUNZIONIGRAMMA (6, "Funzionigramma"),
	FILE_ORGANIGRAMMA (7, "Organigramma"),
	FILE_ATTO_COSTITUTIVO (8, "Atto Costitutivo e statuto"),
	FILE_ESPERIENZA_FORMAZIONE (9, "Esperienza formazione in ambito sanitario"),
	FILE_UTILIZZO (10, "Utilizzo di sedi, strutture ed attrezzature di altro soggetto"),
	FILE_SISTEMA_INFORMATICO (11, "Sistema informatico dedicato alla formazione"),
	FILE_PIANO_QUALITA (12, "Piano di Qualità"),
	FILE_DICHIARAZIONE_LEGALE (13, "Dichiarazione del Legale Rappresentante attestante la veridicità della documentazione"),
	FILE_MODELLO_ATTO_COSTITUTIVO (14, ""),
	FILE_MODELLO_ESPERIENZA_FORMAZIONE (15, ""),
	FILE_MODELLO_UTILIZZO (16, ""),
	FILE_MODELLO_SISTEMA_INFORMATICO (17, ""),
	FILE_MODELLO_PIANO_QUALITA (18, ""),
	FILE_MODELLO_DICHIARAZIONE_LEGALE (19, "");
	
	private int id;
	private String nome;
	
	private FileEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
