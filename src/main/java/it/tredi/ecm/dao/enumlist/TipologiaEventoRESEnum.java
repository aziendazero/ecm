package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum TipologiaEventoRESEnum {
	CONVEGNO_CONGRESSO(3,"",200,-1),
	WORKSHOP_SEMINARIO(4,"",1,100),
	CORSO_AGGIORNAMENTO(1,"",1,200);
	
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
