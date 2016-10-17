package it.tredi.ecm.dao.enumlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public enum ObiettiviFormativiFSCEnum {
	//per tipologia: Training individualizzato
	//per tipologia: Gruppi di migioramento
	//per tipologia: Progetti di miglioramento
	//per tipologia: Attivita' di ricerca
	//per tipologia: Audit clinico e/o assistenziale
	OBV1(1,"Acquisire conoscenze teoriche e/o pratiche"),
	OBV2(2,"Acquisire abilità nell'uso di strumenti, di tecniche e di metodologie"),
	OBV3(3,"Acquisire abilità comunicative e relazionali"),
	OBV4(4,"Acquisire competenze per l'analisi e la risoluzione di problemi"),
	OBV5(5,"Acquisire competenze metacognitive");
	
	private int id;
	private String nome;

	private ObiettiviFormativiFSCEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
