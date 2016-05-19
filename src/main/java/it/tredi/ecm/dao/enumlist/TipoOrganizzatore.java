package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum TipoOrganizzatore {
	AZIENDE_SANITARIE (1, "Aziende Sanitarie (Aziende USL, Aziende Sanitareie, Policlinici)","A"),
	CASE_EDITRICI_SCIENTFICHE (2, "Case editirici scientifiche","A"),
	PRIVATI (3,"Società, aziende ed enti privati","B"),
	PUBBLICI (4,"Società, aziende ed enti pubblici","A");

	private int id;
	private String nome;
	private String gruppo;
	
	private TipoOrganizzatore(int id, String nome, String gruppo){
		this.id = id;
		this.nome = nome;
		this.gruppo = gruppo;
	}
}
