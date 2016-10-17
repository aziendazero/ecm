package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum TipologiaGruppoFSCEnum {
	GRUPPI_DI_LAVORO(1,"Gruppi di lavoro"),
	GRUPPI_DI_STUDIO(2,"Gruppi di studio"),
	GRUPPI_DI_MIGLIORAMENTO(3,"Gruppi di miglioramento"),
	COMITATI_AZIENDALI_PERMANENTI(4,"Comitati aziendali permanenti"),
	COMMISSIONI_DI_STUDIO(5,"Commissioni di studio"),
	COMUNITA_DI_APPRENDIMENTO_O_PRATICA(6,"Comunità di apprendimento o di pratica");
	
	private int id;
	private String nome;

	private TipologiaGruppoFSCEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
	
}
