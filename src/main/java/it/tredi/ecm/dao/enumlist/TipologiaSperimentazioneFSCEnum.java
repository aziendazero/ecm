package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum TipologiaSperimentazioneFSCEnum {
	STUDI_OSSERVAZIONALI(1,"Studi osservazionali"),
	STUDI_EPIDEMIOLOGICI(2,"Studi epidemiologici"),
	RICERCA_CLINICA(3,"Ricerca clinica"),
	SPERIMENTAZIONE_FARMACO_DIPSOSITIVO_MEDICO(4,"Sperimentazione di farmaco o dispositivo medico (secondo la normativa vigente)");
	
	private int id;
	private String nome;

	private TipologiaSperimentazioneFSCEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
	
}
