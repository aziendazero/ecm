package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum ProceduraFormativa {
	FAD (1, "Formazione a distanza"),
	FSC (2, "Formazione sul campo"),
	RES (3, "Formazione residenziale");
	
	private int id;
	private String nome;
	
	private ProceduraFormativa(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
