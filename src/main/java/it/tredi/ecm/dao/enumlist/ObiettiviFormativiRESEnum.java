package it.tredi.ecm.dao.enumlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public enum ObiettiviFormativiRESEnum {
	OBV1(1,"Acquisire conoscenze teoriche e/o pratiche",Arrays.asList(MetodologiaDidatticaRESEnum._1,MetodologiaDidatticaRESEnum._2,MetodologiaDidatticaRESEnum._3,MetodologiaDidatticaRESEnum._4,MetodologiaDidatticaRESEnum._5)),
	OBV2(2,"Acquisire abilità nell'uso di strumenti, di tecniche e di metodologie",Arrays.asList(MetodologiaDidatticaRESEnum._6)),
	OBV3(3,"Acquisire abilità comunicative e relazionali",Arrays.asList(MetodologiaDidatticaRESEnum._7)),
	OBV4(4,"Acquisire competenze per l'analaisi e la risoluzione di problemi",Arrays.asList(MetodologiaDidatticaRESEnum._8,MetodologiaDidatticaRESEnum._9,MetodologiaDidatticaRESEnum._10,MetodologiaDidatticaRESEnum._11));
	
	private int id;
	private String nome;
	private List<MetodologiaDidatticaRESEnum> metodologieDidattiche = new ArrayList<MetodologiaDidatticaRESEnum>();

	private ObiettiviFormativiRESEnum(int id, String nome, List<MetodologiaDidatticaRESEnum> metodologiaDidattiche){
		this.id = id;
		this.nome = nome;
		this.metodologieDidattiche = metodologiaDidattiche;
	}
	
	public List<MetodologiaDidatticaRESEnum> getMetodologie(ObiettiviFormativiRESEnum obiettivo){
		return obiettivo.getMetodologieDidattiche();
	}
}
