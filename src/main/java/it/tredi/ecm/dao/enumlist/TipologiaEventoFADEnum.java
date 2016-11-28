package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum TipologiaEventoFADEnum implements INomeEnum {
	APPRENDIMENTO_INDIVIDUALE_NO_ONLINE(1,"Percorso formativo per l'apprendimento individuale senza attività on-line"),
	APPRENDIMENTO_INDIVIDUALE_SI_ONLINE(2,"Percorso formativo per l'apprendimento individuale con attività on-line"),
	EVENTI_SEMINARIALI_IN_RETE(3,"Eventi seminariali in rete (videoconferenze in modalità sincrona)"),
	APPRENDIMENTO_CONTESTO_SOCIALE(4,"Percorsi formativi per l'apprendimento in contesto sociale (con attività di apprendimento collaborativo)");

	private int id;
	private String nome;

	private TipologiaEventoFADEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
