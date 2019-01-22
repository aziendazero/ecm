package it.tredi.ecm.dao.enumlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public enum TematicheInteresseEnum implements INomeEnum {
	FERTILITA(1,"Fertilità", true, EventoVersioneEnum.TRE_DAL_2019  , Arrays.asList("20")),
	VACCINI_E_STRATEGIE(2,"Vaccini e strategie vaccinali", true, EventoVersioneEnum.TRE_DAL_2019, Arrays.asList("20")),
	RESPONSABILITA_PROFESSIONALE(3,"Responsabilità professionale", true, EventoVersioneEnum.TRE_DAL_2019, Arrays.asList("6")),
	VIOLENZA(4,"Gestione delle situazioni che generano violenza nei confronti dell'operatore sanitario", true, EventoVersioneEnum.TRE_DAL_2019, Arrays.asList("20","32","33")),
	ANTIMICROBICO(4,"Antimicrobico-resistenza", true, EventoVersioneEnum.TRE_DAL_2019, Arrays.asList("20","32","33")),

	MODELLI_INNOVATIVI(5,"Modelli innovativi di presa in carico e governo dell'assistenza", false, EventoVersioneEnum.TRE_DAL_2019, Arrays.asList("20")),
	OUTCOME_CLINICO(6,"Outcome clinico assistenziali e/o organizzativi", false, EventoVersioneEnum.TRE_DAL_2019, Arrays.asList("20")),
	ELEMENTI_INNOVAZIONE(7,"Elementi di innovazione nel governo e nelle politiche del personale (con particolare attenzione all'age diversity management)", false, EventoVersioneEnum.TRE_DAL_2019, Arrays.asList("20"));

	private int id;
	private String nome;
	private boolean nazionale;
	private List<String> obiettiviNazionali = new ArrayList<String>();
	private EventoVersioneEnum versioneEvento;


	private TematicheInteresseEnum(int id, String nome, boolean nazionale, EventoVersioneEnum versioneEvento, List<String> obiettiviNazionali){
		this.id = id;
		this.nome = nome;
		this.nazionale = nazionale;
		this.versioneEvento = versioneEvento;
		this.obiettiviNazionali = obiettiviNazionali;
	}

}
