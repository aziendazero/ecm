package it.tredi.ecm.dao.enumlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public enum RuoloFSCEnum {
	//anche in TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO
	PARTECIPANTE(1,"Partecipante",Arrays.asList(TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO,TipologiaEventoFSCEnum.GRUPPI_DI_MIGLIORAMENTO,TipologiaEventoFSCEnum.ATTIVITA_DI_RICERCA,TipologiaEventoFSCEnum.AUDIT_CLINICO_ASSISTENZIALE)),
	TUTOR(2,"Tutor",Arrays.asList(TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO)),
	//anche in TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO
	ESPERTO(2,"Esperto",Arrays.asList(TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO)),
	COORDINATORE(3,"Coordinatore del programma di training di cui i primi tre replicabili",Arrays.asList(TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO)),

	//anche in TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO
	COORDINATORE_GRUPPI(3,"Coordinatore di gruppi di lavoro, di studio o miglioramento in qualità",Arrays.asList(TipologiaEventoFSCEnum.GRUPPI_DI_MIGLIORAMENTO)),

	//anche in TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO
	//RESPONSABILE_PROGETTO_MIGLIORAMENTO(5,"Responsabile del progetto di miglioramento",Arrays.asList(TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO)),

	COORDINATORE_ATTIVITA_RICERCA(3,"Coordinatore attività di ricerca",Arrays.asList(TipologiaEventoFSCEnum.ATTIVITA_DI_RICERCA)),

	COORDINATORE_ATTIVITA_AUDIT(3,"Coordinatore attività di audit",Arrays.asList(TipologiaEventoFSCEnum.AUDIT_CLINICO_ASSISTENZIALE));
	
	private int id;
	private String nome;
	private List<TipologiaEventoFSCEnum> tipologieEventoFSC = new ArrayList<TipologiaEventoFSCEnum>();

	private RuoloFSCEnum(int id, String nome, List<TipologiaEventoFSCEnum> tipologieEventoFSC){
		this.id = id;
		this.nome = nome;
		this.tipologieEventoFSC = tipologieEventoFSC;
	}
	
	public List<MetodologiaDidatticaRESEnum> getMetodologie(ObiettiviFormativiRESEnum obiettivo){
		return obiettivo.getMetodologieDidattiche();
	}
}
