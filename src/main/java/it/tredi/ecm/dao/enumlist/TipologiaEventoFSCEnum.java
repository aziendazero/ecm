package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum TipologiaEventoFSCEnum {
	TRAINING_INDIVIDUALIZZATO(6,""),
	GRUPPI_DI_MIGLIORAMENTO(7,""),//QUESTO ATTIVA LA SOTTOLISTA
	//PROGETTI_DI_MIGLIORAMENTO(3,""), //sstagni - tolto in quando non gestito dal cogeaps
	ATTIVITA_DI_RICERCA(8,""),
	AUDIT_CLINICO_ASSISTENZIALE(9,"");
	
	private int id;
	private String nome;

	private TipologiaEventoFSCEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
	
}
