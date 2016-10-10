package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum TipologiaGruppoFSCEnum {
	GRUPPI_DI_LAVORO(1,""),
	GRUPPI_DI_STUDIO(2,""),
	GRUPPI_DI_MIGLIORAMENTO(3,""),
	COMITATI_AZIENDALI_PERMANENTI(4,""),
	COMMISSIONI_DI_STUDIO(5,""),
	COMUNITA_DI_APPRENDIMENTO_O_PRATICA(6,"");
	
	private int id;
	private String nome;

	private TipologiaGruppoFSCEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
	
}
