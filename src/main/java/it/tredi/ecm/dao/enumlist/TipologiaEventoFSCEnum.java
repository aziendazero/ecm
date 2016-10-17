package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum TipologiaEventoFSCEnum {
	TRAINING_INDIVIDUALIZZATO(6,"Training individualizzato"),
	GRUPPI_DI_MIGLIORAMENTO(7,"Gruppi di miglioramento"),//QUESTO ATTIVA LA SOTTOLISTA
	//PROGETTI_DI_MIGLIORAMENTO(3,"Progetti di miglioramento"), //sstagni - tolto in quando non gestito dal cogeaps
	ATTIVITA_DI_RICERCA(8,"Attivita di ricerca"),
	AUDIT_CLINICO_ASSISTENZIALE(9,"Audit clinico e/o assistenziale");
	
	private int id;
	private String nome;

	private TipologiaEventoFSCEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}

}
