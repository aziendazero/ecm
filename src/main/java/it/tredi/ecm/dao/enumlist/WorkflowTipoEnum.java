package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum WorkflowTipoEnum {
	ACCREDITAMENTOPROVVISORIO (1, "Accreditamento Provvisorio"),
	ACCREDITAMENTOSTANDARD (2, "Accreditamento Standard");
	
	private int id;
	private String nome;
	
	private WorkflowTipoEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}