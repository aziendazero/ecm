package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum EsecutoreStatoEnum {
	SEGRETERIA (1, "Segreteria"),
	PROVIDER (2, "Provider");

	private int id;
	private String nome;

	private EsecutoreStatoEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}