package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum ProviderStatoEnum {
	INSERITO (1, "Inserito - Domanda in stato di bozza"),
	VALIDATO (2, "Domanda inviata"),
	ACCREDITATO_PROVVISORIAMENTE (3, "Accreditamento provvisorio accettato"),
	DINIEGO (4, "Accreditamento rifiutato"),
	ACCREDITATO_STANDARD (5, "Accreditamento standard accettato"),
	SOSPESO (6, "Accreditamento temporaneamente sospeso"),
	CANCELLATO (7, "Accreditamento cancellato");

	private int id;
	private String nome;

	private ProviderStatoEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}