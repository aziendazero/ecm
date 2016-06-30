package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum CategoriaObiettivoNazionale {
	TECNICO_PROFESSIONALI (1,"tecnico-proessionali"),
	DI_PROCESSO (2,"tecnico-proessionali"),
	DI_SISTEMA (3,"tecnico-proessionali");
	
	private int id;
	private String nome;

	private CategoriaObiettivoNazionale(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
