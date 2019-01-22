package it.tredi.ecm.dao.enumlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public enum TematicheInteresseEnum implements INomeEnum {
	NON_RIGUARDA_UNA_TEMATICA_SPECIALE(0, "L'evento non riguarda una tematica speciale", TipologiaTematicheInteresseEnum.ALTRO, EventoVersioneEnum.TRE_DAL_2019, Arrays.asList()),

	FERTILITA(1,"Fertilità", TipologiaTematicheInteresseEnum.NAZIONALE, EventoVersioneEnum.TRE_DAL_2019  , Arrays.asList("20")),
	VACCINI_E_STRATEGIE(2,"Vaccini e strategie vaccinali", TipologiaTematicheInteresseEnum.NAZIONALE, EventoVersioneEnum.TRE_DAL_2019, Arrays.asList("20")),
	RESPONSABILITA_PROFESSIONALE(3,"Responsabilità professionale", TipologiaTematicheInteresseEnum.NAZIONALE, EventoVersioneEnum.TRE_DAL_2019, Arrays.asList("6")),
	VIOLENZA(4,"Gestione delle situazioni che generano violenza nei confronti dell'operatore sanitario", TipologiaTematicheInteresseEnum.NAZIONALE, EventoVersioneEnum.TRE_DAL_2019, Arrays.asList("20","32","33")),
	ANTIMICROBICO(4,"Antimicrobico-resistenza", TipologiaTematicheInteresseEnum.NAZIONALE, EventoVersioneEnum.TRE_DAL_2019, Arrays.asList("20","32","33")),

	MODELLI_INNOVATIVI(5,"Modelli innovativi di presa in carico e governo dell'assistenza", TipologiaTematicheInteresseEnum.REGIONALE, EventoVersioneEnum.TRE_DAL_2019, Arrays.asList("20")),
	OUTCOME_CLINICO(6,"Outcome clinico assistenziali e/o organizzativi", TipologiaTematicheInteresseEnum.REGIONALE, EventoVersioneEnum.TRE_DAL_2019, Arrays.asList("20")),
	ELEMENTI_INNOVAZIONE(7,"Elementi di innovazione nel governo e nelle politiche del personale (con particolare attenzione all'age diversity management)", TipologiaTematicheInteresseEnum.REGIONALE, EventoVersioneEnum.TRE_DAL_2019, Arrays.asList("20"));

	private int id;
	private String nome;
	private TipologiaTematicheInteresseEnum tipo;
	private List<String> obiettiviNazionali = new ArrayList<String>();
	private EventoVersioneEnum versioneEvento;


	private TematicheInteresseEnum(int id, String nome, TipologiaTematicheInteresseEnum tipo, EventoVersioneEnum versioneEvento, List<String> obiettiviNazionali){
		this.id = id;
		this.nome = nome;
		this.tipo = tipo;
		this.versioneEvento = versioneEvento;
		this.obiettiviNazionali = obiettiviNazionali;
	}

}
