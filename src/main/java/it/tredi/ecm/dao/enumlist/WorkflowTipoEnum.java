package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum WorkflowTipoEnum {
	ACCREDITAMENTOPROVVISORIO (1, "Accreditamento Provvisorio"),
	ACCREDITAMENTOSTANDARD (2, "Accreditamento Standard"),
	ACCREDITAMENTOVARIAZIONEDATI (3, "Accreditamento Variazione Dati"),
	ACCREDITAMENTOCONCLUSIONEPROCEDIMENTO (3, "Accreditamento Conclusione Procedimento");

	private int id;
	private String nome;

	private WorkflowTipoEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}