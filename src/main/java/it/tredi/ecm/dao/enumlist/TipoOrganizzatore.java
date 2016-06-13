package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum TipoOrganizzatore {
	UNIVERSITA (1, "Università, facoltà e dipartimenti universitari", "A", true),
	ISTITUTI_RICOVERO (14, "Istituti di ricovero e cure a a carattere sceintifico (IRCCS)", "A", true),
	ISTITUTI_SCIENTIFICI (3, "Istituti scientifici del servizio sanitario nazionale", "A", true),
	ISTITUTI_CONSIGLIO (4, "Istituti del Consiglio Nazionale delle Ricerche", "A", true),
	SOCIETA_SCIENTIFICHE (6, "Società scientifiche e associazioni professionali in campo sanitario", "A", false),
	COLLEGI (7, "Ordini e Collegi delle Professioni Sanitarie", "A", true),
	FONDAZIONI (8, "Fondazioni a carattere scientifico", "A", false),
	CASE_EDITRICI (9, "Case editrici scientifiche", "A", false),
	PUBBLICI (10, "Società, Agenzie ed Enti Pubblici", "A", true),
	PRIVATI (11, "Società, Agenzie ed Enti Privati", "A", false),
	AZIENDE_SANITARIE (2, "Aziende Sanitarie (Aziende Usl, Aziende Ospedaliere, Policlinici)", "A", true),
	RICOVERO_PUBBLICHE	(12, "Strutture di ricovero pubbliche", "A", true),
	RICOVERO_PRIVATE (13, "Strutture di ricovero private", "A", false),
	ZOOPROFILATTICO (5, "Istituto zooprofilattico", "A", true),
	ENTE_FORMAZIONE (15, "Ente di formazione a partecipazione prevalentemente pubblica regionale o provinciale", "A", true);

	private int id;
	private String nome;
	private String gruppo;
	private boolean tipoP;
	
	private TipoOrganizzatore(int id, String nome, String gruppo, boolean tipoP){
		this.id = id;
		this.nome = nome;
		this.gruppo = gruppo;
		this.tipoP = tipoP;
	}
}
