package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum AccreditamentoTipoEnum {
	PROVVISORIO (1, "Provvisorio", WorkflowTipoEnum.ACCREDITAMENTOPROVVISORIO),
	STANDARD (2, "Standard", WorkflowTipoEnum.ACCREDITAMENTOSTANDARD);
	
	private int id;
	private String nome;
	private WorkflowTipoEnum workflowTipoEnum; 
	
	private AccreditamentoTipoEnum(int id, String nome, WorkflowTipoEnum workflowTipoEnum){
		this.id = id;
		this.nome = nome;
		this.workflowTipoEnum = workflowTipoEnum;
	}
}