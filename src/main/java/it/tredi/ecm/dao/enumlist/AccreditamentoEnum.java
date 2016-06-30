package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum AccreditamentoEnum {
	ACCREDITAMENTO_TIPO_PROVVISORIO (1, "Provvisorio"),
	ACCREDITAMENTO_TIPO_STANDARD (2, "Standard"),
	ACCREDITAMENTO_STATO_BOZZA (3, "Bozza"),
	ACCREDITAMENTO_STATO_INVIATO (4, "Inviata alla segreteria"),
	ACCREDITAMENTO_STATO_INTEGRAZIONE (5, "Richiesta integrazione"),
	ACCREDITAMENTO_STATO_VALUTAZIONE (6, "Richiesta integrazione"),
	ACCREDITAMENTO_STATO_APPROVATO (7, "Richiesta integrazione"),
	ACCREDITAMENTO_STATO_RESPINTO (8, "Richiesta integrazione");
	
	private int id;
	private String nome;
	
	private AccreditamentoEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}