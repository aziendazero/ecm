package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum CategoriaObiettivoNazionale {
	TECNICO_PROFESSIONALI (1,"Obiettivi tecnico-professionali"),
	DI_PROCESSO (2,"Obiettivi di processo"),
	DI_SISTEMA (3,"Obiettivi di sistema");
	
	private int id;
	private String nome;

	private CategoriaObiettivoNazionale(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
