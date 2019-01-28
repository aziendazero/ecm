package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum TipologiaTematicheInteresseEnum implements INomeEnum {
	ALTRO(0,""),
	NAZIONALE(1, "Tematiche Speciali di interesse Nazionale"),
	REGIONALE(2,"Tematiche Speciali di interesse Regionale");

	private int id;
	private String nome;

	private TipologiaTematicheInteresseEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}

}
