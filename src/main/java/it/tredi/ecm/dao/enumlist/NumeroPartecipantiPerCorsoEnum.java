package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum NumeroPartecipantiPerCorsoEnum implements INomeEnum {
	//Corso teorico e/o pratico (fino a 100 partecipanti previsti)
	//Corso teorico e/o pratico (da 101 a 200 partecipanti previsti)
	CORSO_AGGIORNAMENTO_FINO_100_PARTECIPANTI(1,"Corso teorico e/o pratico (fino a 100 partecipanti previsti)"),
	CORSO_AGGIORNAMENTO_DA_101_A_200_PARTECIPANTI(1,"Corso teorico e/o pratico (da 101 a 200 partecipanti previsti)");

	private int id;
	private String nome;

	private NumeroPartecipantiPerCorsoEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
