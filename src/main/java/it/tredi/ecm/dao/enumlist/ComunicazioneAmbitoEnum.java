package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum ComunicazioneAmbitoEnum {
	PROVIDER(1, "Provider"),
	EVENTI(2, "Eventi");

	private int id;
	private String nome;

	private ComunicazioneAmbitoEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
