package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum RuoloFSCEnum {
//	PARTECIPANTE(1,"Partecipante", "P", Arrays.asList(TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO,TipologiaEventoFSCEnum.GRUPPI_DI_MIGLIORAMENTO,TipologiaEventoFSCEnum.ATTIVITA_DI_RICERCA,TipologiaEventoFSCEnum.AUDIT_CLINICO_ASSISTENZIALE,TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO)),
//	TUTOR(2,"Tutor", "T", Arrays.asList(TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO)),
//	ESPERTO(3,"Esperto","D", Arrays.asList(TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO,TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO)),
//	
//	COORDINATORE(4,"Coordinatore del programma di training di cui i primi tre replicabili","D",Arrays.asList(TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO)),
//
//	//anche in TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO
//	COORDINATORE_GRUPPI(5,"Coordinatore di gruppi di lavoro, di studio o miglioramento in qualità","D",Arrays.asList(TipologiaEventoFSCEnum.GRUPPI_DI_MIGLIORAMENTO,TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO)),
//
//	COORDINATORE_ATTIVITA_RICERCA(6,"Coordinatore attività di ricerca","D",Arrays.asList(TipologiaEventoFSCEnum.ATTIVITA_DI_RICERCA)),
//
//	COORDINATORE_ATTIVITA_AUDIT(7,"Coordinatore attività di audit","D",Arrays.asList(TipologiaEventoFSCEnum.AUDIT_CLINICO_ASSISTENZIALE)),
//	
//	RESPONSABILE_PROGETTO(8,"Responsabile del Progetto di miglioramento","D",Arrays.asList(TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO));
//	
//	
//	PARTECIPANTE_A(9,"Partecipante A", "P", Arrays.asList(TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO,TipologiaEventoFSCEnum.GRUPPI_DI_MIGLIORAMENTO,TipologiaEventoFSCEnum.ATTIVITA_DI_RICERCA,TipologiaEventoFSCEnum.AUDIT_CLINICO_ASSISTENZIALE,TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO)),
//	PARTECIPANTE_B(10,"Partecipante B", "P", Arrays.asList(TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO,TipologiaEventoFSCEnum.GRUPPI_DI_MIGLIORAMENTO,TipologiaEventoFSCEnum.ATTIVITA_DI_RICERCA,TipologiaEventoFSCEnum.AUDIT_CLINICO_ASSISTENZIALE,TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO)),
//	PARTECIPANTE_C(11,"Partecipante C", "P", Arrays.asList(TipologiaEventoFSCEnum.TRAINING_INDIVIDUALIZZATO,TipologiaEventoFSCEnum.GRUPPI_DI_MIGLIORAMENTO,TipologiaEventoFSCEnum.ATTIVITA_DI_RICERCA,TipologiaEventoFSCEnum.AUDIT_CLINICO_ASSISTENZIALE,TipologiaEventoFSCEnum.PROGETTI_DI_MIGLIORAMENTO)),
	
	PARTECIPANTE(1,"Partecipante", RuoloFSCBaseEnum.PARTECIPANTE),
	TUTOR(2,"Tutor", RuoloFSCBaseEnum.TUTOR),
	ESPERTO(3,"Esperto", RuoloFSCBaseEnum.ESPERTO),
	
	COORDINATORE(4,"Coordinatore del programma di training", RuoloFSCBaseEnum.COORDINATORE),
	COORDINATORE_GRUPPI(5,"Coordinatore di gruppi di lavoro, di studio o miglioramento in qualità", RuoloFSCBaseEnum.COORDINATORE),
	COORDINATORE_ATTIVITA_RICERCA(6,"Coordinatore attività di ricerca", RuoloFSCBaseEnum.COORDINATORE),
	COORDINATORE_ATTIVITA_AUDIT(7,"Coordinatore attività di audit", RuoloFSCBaseEnum.COORDINATORE),
	RESPONSABILE_PROGETTO(8,"Responsabile del Progetto di miglioramento", RuoloFSCBaseEnum.RESPONSABILE),
	
	PARTECIPANTE_A(9,"Partecipante A", RuoloFSCBaseEnum.PARTECIPANTE),
	PARTECIPANTE_B(10,"Partecipante B", RuoloFSCBaseEnum.PARTECIPANTE),
	PARTECIPANTE_C(11,"Partecipante C", RuoloFSCBaseEnum.PARTECIPANTE),
	
	TUTOR_A(12,"Tutor A", RuoloFSCBaseEnum.TUTOR),
	TUTOR_B(13,"Tutor B", RuoloFSCBaseEnum.TUTOR),
	TUTOR_C(14,"Tutor C", RuoloFSCBaseEnum.TUTOR),
	
	ESPERTO_A(3,"Esperto A", RuoloFSCBaseEnum.ESPERTO),
	ESPERTO_B(3,"Esperto B", RuoloFSCBaseEnum.ESPERTO),
	ESPERTO_C(3,"Esperto C", RuoloFSCBaseEnum.ESPERTO),
	
	COORDINATORE_GRUPPI_A(5,"Coordinatore di gruppi di lavoro, di studio o miglioramento in qualità A", RuoloFSCBaseEnum.COORDINATORE),
	COORDINATORE_GRUPPI_B(5,"Coordinatore di gruppi di lavoro, di studio o miglioramento in qualità B", RuoloFSCBaseEnum.COORDINATORE),
	COORDINATORE_GRUPPI_C(5,"Coordinatore di gruppi di lavoro, di studio o miglioramento in qualità C", RuoloFSCBaseEnum.COORDINATORE),
	
	RESPONSABILE_SCIENTIFICO_A(15,"Responsabile scientifico A", RuoloFSCBaseEnum.RESPONSABILE_SCIENTIFICO),
	RESPONSABILE_SCIENTIFICO_B(16,"Responsabile scientifico B", RuoloFSCBaseEnum.RESPONSABILE_SCIENTIFICO),
	RESPONSABILE_SCIENTIFICO_C(17,"Responsabile scientifico C", RuoloFSCBaseEnum.RESPONSABILE_SCIENTIFICO),
	
	COORDINATORE_A(18,"Coordinatore A", RuoloFSCBaseEnum.COORDINATORE_X),
	COORDINATORE_B(19,"Coordinatore B", RuoloFSCBaseEnum.COORDINATORE_X),
	COORDINATORE_C(20,"Coordinatore C", RuoloFSCBaseEnum.COORDINATORE_X);
	
	private int id;
	private String nome;
	private RuoloFSCBaseEnum ruoloBase;

	private RuoloFSCEnum(int id, String nome, RuoloFSCBaseEnum ruoloBase){
		this.id = id;
		this.nome = nome;
		this.ruoloBase = ruoloBase;
	}
}
