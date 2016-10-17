package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum RendicontazioneInviataStatoEnum {
	PENDING ("Elaborazione in corso"),
	COMPLETED ("Completato");
	
	private String nome;

	private RendicontazioneInviataStatoEnum(String nome){
		this.nome = nome;
	}
}
