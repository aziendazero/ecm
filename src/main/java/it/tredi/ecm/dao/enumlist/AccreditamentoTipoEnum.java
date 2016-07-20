package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum AccreditamentoTipoEnum {
	PROVVISORIO (1, "Provvisorio"),
	STANDARD (2, "Standard");
	
	private int id;
	private String nome;
	
	private AccreditamentoTipoEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}