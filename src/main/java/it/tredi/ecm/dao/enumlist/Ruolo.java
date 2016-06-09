package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum Ruolo {
	RICHIEDENTE (1, "Richiedente"),
	LEGALE_RAPPRESENTANTE (2, "Legale Rappresentante"),
	DELEGATO_LEGALE_RAPPRESENTANTE (3, "Delegato Legale Rappresentante"),
	RESPONSABILE_SEGRETERIA (4, "Responsabile Segreteria"),
	RESPONSABILE_FORMAZIONE (5, "Responsabile Formazione"),
	RESPONSABILE_AMMINISTRATIVO (6, "Responsabile Amministrativo"),
	RESPONSABILE_SISTEMA_INFORMATICO (7,"Responsabile Sistema Informatico"),
	RESPONSABILE_QUALITA (8, "Responsabile Qualità"),
	COMPONENTE_COMITATO_SCIENTIFICO (9, "Componente Comitato Scientifico"),
	COORDINATORE_COMITATO_SCIENTIFICO (10, "Coordinatore Comitato Scientifico");
	
	private int id;
	private String nome;
	
	private Ruolo(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
