package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum DestinatariEventoEnum {
	PERSONALE_DIPENDENTE(1,"Personale dipendente"),
	PERSONALE_CONVENZIONATO(2,"Personale convenzionato"),
	ALTRO_PERSONALE(3,"Altro personale");
	
	private int id;
	private String nome;

	private DestinatariEventoEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
