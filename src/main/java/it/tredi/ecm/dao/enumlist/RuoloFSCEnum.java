package it.tredi.ecm.dao.enumlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public enum RuoloFSCEnum {
	PARTECIPANTE(1,"Partecipante", "P", Arrays.asList(TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO,TipologiaEventoFSCEnum.GRUPPI_DI_MIGLIORAMENTO,TipologiaEventoFSCEnum.ATTIVITA_DI_RICERCA,TipologiaEventoFSCEnum.AUDIT_CLINICO_ASSISTENZIALE,TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO)),
	TUTOR(2,"Tutor", "T", Arrays.asList(TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO)),
	ESPERTO(3,"Esperto","D", Arrays.asList(TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO,TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO)),
	
	
	COORDINATORE(4,"Coordinatore del programma di training di cui i primi tre replicabili","D",Arrays.asList(TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO)),

	//anche in TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO
	COORDINATORE_GRUPPI(5,"Coordinatore di gruppi di lavoro, di studio o miglioramento in qualità","D",Arrays.asList(TipologiaEventoFSCEnum.GRUPPI_DI_MIGLIORAMENTO,TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO)),

	COORDINATORE_ATTIVITA_RICERCA(6,"Coordinatore attività di ricerca","D",Arrays.asList(TipologiaEventoFSCEnum.ATTIVITA_DI_RICERCA)),

	COORDINATORE_ATTIVITA_AUDIT(7,"Coordinatore attività di audit","D",Arrays.asList(TipologiaEventoFSCEnum.AUDIT_CLINICO_ASSISTENZIALE)),
	
	RESPONSABILE_PROGETTO(8,"Responsabile del Progetto di miglioramento","D",Arrays.asList(TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO));
	
	private int id;
	private String nome;
	private String codifica; 
	private List<TipologiaEventoFSCEnum> tipologieEventoFSC = new ArrayList<TipologiaEventoFSCEnum>();

	private RuoloFSCEnum(int id, String nome, String codifica, List<TipologiaEventoFSCEnum> tipologieEventoFSC){
		this.id = id;
		this.nome = nome;
		this.codifica = codifica;
		this.tipologieEventoFSC = tipologieEventoFSC;
	}
	
	public List<MetodologiaDidatticaRESEnum> getMetodologie(ObiettiviFormativiRESEnum obiettivo){
		return obiettivo.getMetodologieDidattiche();
	}
}
