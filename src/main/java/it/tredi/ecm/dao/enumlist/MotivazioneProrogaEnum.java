package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum MotivazioneProrogaEnum {
	INSERIMENTO_PIANO_FORMATIVO (1, "Inserimento del Piano Formativo"),
	INSERIMENTO_DOMANDA_STANDARD (2, "Inserimento della Domanda Standard"),
	INSERIMENTO_RELAZIONE_ANNUALE (3, "Inserimento della Relazione Annuale"),
	PAGAMENTO_EVENTO (4, "Pagamento dell'evento"),
	RENDICONTAZIONE_EVENTO (5, "Rendicontazione dell'evento");

	private int id;
	private String nome;

	private MotivazioneProrogaEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
