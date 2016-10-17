package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum RendicontazioneInviataResultEnum {
	SUCCESS ("Positivo"),
	ERROR ("Negativo");
	
	private String nome;

	private RendicontazioneInviataResultEnum(String nome){
		this.nome = nome;
	}
}
