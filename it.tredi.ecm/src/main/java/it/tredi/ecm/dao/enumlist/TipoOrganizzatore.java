package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum TipoOrganizzatore {
	AZIENDE_SANITARIE (1, "Aziende Sanitarie (Aziende USL, Aziende Sanitareie, Policlinici)"),
	CASE_EDITRICI_SCIENTFICHE (2, "Case editirici scientifiche"),
	PRIVATI (3,"Società, aziende ed enti privati"),
	PUBBLICI (4,"Società, aziende ed enti pubblici");

	private int id;
	private String nome;
	
	private TipoOrganizzatore(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
