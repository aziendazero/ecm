package it.tredi.ecm.dao.enumlist;

import java.util.List;

import lombok.Getter;

@Getter
public enum MetodoDiLavoroEnum {
	LAVORO_AUTONOMO(1,"Lavoro autonomo"),
	AFFIANCAMANTO_INDIVIDUALE(2,"Affiancamento individuale nel progetto di training"),
	AFFIANCAMANTO_IN_PICCOLO_GRUPPO(3,"Affiancamento in piccolo gruppo del progetto di training"),
	LAVORO_IN_PICCOLO_GRUPPO(4,"Lavoro in piccolo gruppo"),
	LAVORO_IN_GRANDE_GRUPPO(5,"Lavoro in grande gruppo");
	
	private int id;
	private String nome;

	private MetodoDiLavoroEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}

}
