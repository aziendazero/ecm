package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum EventoSearchEnum {
	SCADENZA_PAGAMENTO(1, "Eventi in scadenza per Pagamento"),
	NON_RAPPORTATI(2, "Eventi scaduti e non rapportati"),
	BOZZA(3, "Eventi in stato di Bozza"),
	CREDITI_NON_CONFERMATI(4, "Eventi con crediti non confermati"),
	ALIMENTAZIONE_PRIMA_INFANZIA(5, "Eventi con contenuti di alimentazione di prima infanzia"),
	MEDICINE_NON_CONVENZIONALI(6, "Eventi con contenuti di medicine non convenzionali"),
	SCADENZA_RENDICONTAZIONE(7, "Eventi in scadenza per Rendicontazione"),
	NON_PAGATI(8, "Eventi scaduti e non pagati"),
	NON_RENDICONTATI(9, "Eventi scaduti e non rendicontati");

	private int id;
	private String nome;

	private EventoSearchEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
