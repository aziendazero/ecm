package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum TipologiaEventoFSCEnum {
	TRAINING_INDIVIDUALIZZATO(1,""),
	GRUPPI_DI_MIGLIORAMENTO(2,""),//QUESTO ATTIVA LA SOTTOLISTA
	PROGETTI_DI_MIGLIORAMENTO(3,""),
	ATTIVITA_DI_RICERCA(4,""),
	AUDIT_CLINICO_ASSISTENZIALE(5,"");
	
	private int id;
	private String nome;

	private TipologiaEventoFSCEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
	
}
