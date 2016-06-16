package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum AccreditamentoEnum {
	ACCREDITAMENTO_TIPO_PROVVISORIO (1, "Provvisorio"),
	ACCREDITAMENTO_TIPO_STANDARD (2, "Standard"),
	ACCREDITAMENTO_STATO_BOZZA (3, "Bozza"),
	ACCREDITAMENTO_STATO_INVIATO (4, "Inviato");
	
	private int id;
	private String nome;
	
	private AccreditamentoEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}