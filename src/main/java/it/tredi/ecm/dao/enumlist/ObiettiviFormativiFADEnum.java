package it.tredi.ecm.dao.enumlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public enum ObiettiviFormativiFADEnum implements INomeEnum {
	OBV1_1(1,"Acquisire conoscenze teoriche e/o pratiche",Arrays.asList(MetodologiaDidatticaFADEnum._1,MetodologiaDidatticaFADEnum._2,MetodologiaDidatticaFADEnum._3,MetodologiaDidatticaFADEnum._4,MetodologiaDidatticaFADEnum._5,MetodologiaDidatticaFADEnum._6,MetodologiaDidatticaFADEnum._7,MetodologiaDidatticaFADEnum._8,MetodologiaDidatticaFADEnum._9,MetodologiaDidatticaFADEnum._10)),
	OBV2_1(2,"Acquisire abilità nell'uso di strumenti, di tecniche e di metodologie",Arrays.asList(MetodologiaDidatticaFADEnum._11,MetodologiaDidatticaFADEnum._12)),
	OBV3_1(3,"Acquisire abilità comunicative e relazionali",Arrays.asList(MetodologiaDidatticaFADEnum._13,MetodologiaDidatticaFADEnum._14)),
	OBV4_1(4,"Acquisire competenze per l'analaisi e la risoluzione di problemi",Arrays.asList(MetodologiaDidatticaFADEnum._15,MetodologiaDidatticaFADEnum._16)),

	OBV1_2(1,"Acquisire conoscenze teoriche e/o pratiche",Arrays.asList(MetodologiaDidatticaFADEnum._17,MetodologiaDidatticaFADEnum._18,MetodologiaDidatticaFADEnum._19,MetodologiaDidatticaFADEnum._20,MetodologiaDidatticaFADEnum._21,MetodologiaDidatticaFADEnum._22,MetodologiaDidatticaFADEnum._23,MetodologiaDidatticaFADEnum._24,MetodologiaDidatticaFADEnum._25,MetodologiaDidatticaFADEnum._26,MetodologiaDidatticaFADEnum._27,MetodologiaDidatticaFADEnum._28,MetodologiaDidatticaFADEnum._29,MetodologiaDidatticaFADEnum._30,MetodologiaDidatticaFADEnum._31)),
	OBV2_2(2,"Acquisire abilità nell'uso di strumenti, di tecniche e di metodologie",Arrays.asList(MetodologiaDidatticaFADEnum._32,MetodologiaDidatticaFADEnum._33)),
	OBV3_2(3,"Acquisire abilità comunicative e relazionali",Arrays.asList(MetodologiaDidatticaFADEnum._34,MetodologiaDidatticaFADEnum._35,MetodologiaDidatticaFADEnum._36,MetodologiaDidatticaFADEnum._37,MetodologiaDidatticaFADEnum._38,MetodologiaDidatticaFADEnum._39,MetodologiaDidatticaFADEnum._40,MetodologiaDidatticaFADEnum._41)),
	OBV4_2(4,"Acquisire competenze per l'analaisi e la risoluzione di problemi",Arrays.asList(MetodologiaDidatticaFADEnum._42,MetodologiaDidatticaFADEnum._43,MetodologiaDidatticaFADEnum._44,MetodologiaDidatticaFADEnum._45,MetodologiaDidatticaFADEnum._46,MetodologiaDidatticaFADEnum._47,MetodologiaDidatticaFADEnum._48));

	private int id;
	private String nome;
	private List<MetodologiaDidatticaFADEnum> metodologieDidattiche = new ArrayList<MetodologiaDidatticaFADEnum>();

	private ObiettiviFormativiFADEnum(int id, String nome, List<MetodologiaDidatticaFADEnum> metodologiaDidattiche){
		this.id = id;
		this.nome = nome;
		this.metodologieDidattiche = metodologiaDidattiche;
	}
}
