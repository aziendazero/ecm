package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum TipoMetodologiaEnum {
	FRONTALE(1,"Metodologia frontale"),
	INTERATTIVA(2,"Metodologia interattiva");
	
	private int id;
	private String nome;

	private TipoMetodologiaEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
