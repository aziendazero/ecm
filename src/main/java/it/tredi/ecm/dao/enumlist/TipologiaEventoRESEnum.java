package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum TipologiaEventoRESEnum {
	CONVEGNO_CONGRESSO(3,"Convegno/Congresso/Simposio/Conferenza (con un numero di partecipanti oltre 200)",200,-1),
	WORKSHOP_SEMINARIO(4,"Workshop/Seminario, che si svolge all’interno di Convegno/Congresso/Simposio/Conferenza (con meno di 100 partecipanti)",1,100),
	CORSO_AGGIORNAMENTO(1,"Corso di aggiornamento teorico e/o pratico (massimo 200 partecipanti)",1,200);

	private int id;
	private String nome;
	private int minNumeroPartecipanti;
	private int maxNumeroPartecipanti;

	private TipologiaEventoRESEnum(int id, String nome, int minNumeroPartecipanti, int maxNumeroPartecipanti){
		this.id = id;
		this.nome = nome;
		this.minNumeroPartecipanti = minNumeroPartecipanti;
		this.maxNumeroPartecipanti = maxNumeroPartecipanti;
	}

	public boolean checkValidate(int numeroPartecipanti){
		if(maxNumeroPartecipanti == -1)
			return numeroPartecipanti >= minNumeroPartecipanti;
		else
			return (numeroPartecipanti >= minNumeroPartecipanti) && (numeroPartecipanti <= maxNumeroPartecipanti);
	}
}
