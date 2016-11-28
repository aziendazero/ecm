package it.tredi.ecm.dao.enumlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public enum TipologiaEventoFSCEnum implements INomeEnum {
	TRAINING_INDIVIDUALIZZATO(1,"Training individualizzato",Arrays.asList(
			RuoloFSCEnum.PARTECIPANTE_A,
			RuoloFSCEnum.PARTECIPANTE_B,
			RuoloFSCEnum.PARTECIPANTE_C,
			RuoloFSCEnum.TUTOR_A,
			RuoloFSCEnum.TUTOR_B,
			RuoloFSCEnum.TUTOR_C,
			RuoloFSCEnum.ESPERTO_A,
			RuoloFSCEnum.ESPERTO_B,
			RuoloFSCEnum.ESPERTO_C,
			RuoloFSCEnum.COORDINATORE)),

	GRUPPI_DI_MIGLIORAMENTO(2,"Gruppi di miglioramento",Arrays.asList(
			RuoloFSCEnum.PARTECIPANTE,
			RuoloFSCEnum.COORDINATORE_GRUPPI)),//QUESTO ATTIVA LA SOTTOLISTA

	PROGETTI_DI_MIGLIORAMENTO(3,"Progetti di miglioramento",Arrays.asList(
			RuoloFSCEnum.PARTECIPANTE_A,
			RuoloFSCEnum.PARTECIPANTE_B,
			RuoloFSCEnum.PARTECIPANTE_C,
			RuoloFSCEnum.ESPERTO_A,
			RuoloFSCEnum.ESPERTO_B,
			RuoloFSCEnum.ESPERTO_C,
			RuoloFSCEnum.COORDINATORE_GRUPPI_A,
			RuoloFSCEnum.COORDINATORE_GRUPPI_B,
			RuoloFSCEnum.COORDINATORE_GRUPPI_C,
			RuoloFSCEnum.RESPONSABILE_PROGETTO)), //sstagni - tolto in quando non gestito dal cogeaps

	ATTIVITA_DI_RICERCA(4,"Attivita di ricerca",Arrays.asList(
			RuoloFSCEnum.PARTECIPANTE_A,
			RuoloFSCEnum.PARTECIPANTE_B,
			RuoloFSCEnum.PARTECIPANTE_C,
			RuoloFSCEnum.COORDINATORE_ATTIVITA_RICERCA)),

	AUDIT_CLINICO_ASSISTENZIALE(5,"Audit clinico e/o assistenziale",Arrays.asList(
			RuoloFSCEnum.PARTECIPANTE_A,
			RuoloFSCEnum.PARTECIPANTE_B,
			RuoloFSCEnum.PARTECIPANTE_C,
			RuoloFSCEnum.COORDINATORE_ATTIVITA_AUDIT));

	private int id;
	private String nome;
	private List<RuoloFSCEnum> ruoliCoinvolti = new ArrayList<RuoloFSCEnum>();

	private TipologiaEventoFSCEnum(int id, String nome,List<RuoloFSCEnum>ruoliCoinvolti){
		this.id = id;
		this.nome = nome;
		this.ruoliCoinvolti = ruoliCoinvolti;
	}

}
