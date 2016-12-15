package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum EventoSearchEnum {
	SCADENZA_PAGAMENTO(1, "Eventi in scadenza per Pagamento/Rendicontazione"),
	NON_RAPPORTATI(2, "Eventi scaduti e non rapportati"),
	BOZZA(3, "Eventi in stato di Bozza");

	private int id;
	private String nome;

	private EventoSearchEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
