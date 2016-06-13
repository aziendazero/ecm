package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum TipoOrganizzatore {
	UNIVERSITA (1, "Università, facoltà e dipartimenti universitari", "A"),
	ISTITUTI_RICOVERO (14, "Istituti di ricovero e cure a a carattere sceintifico (IRCCS)", "A"),
	ISTITUTI_SCIENTIFICI (3, "Istituti scientifici del servizio sanitario nazionale", "A"),
	ISTITUTI_CONSIGLIO (4, "Istituti del Consiglio Nazionale delle Ricerche", "A"),
	SOCIETA_SCIENTIFICHE (6, "Società scientifiche e associazioni professionali in campo sanitario", "A"),
	COLLEGI (7, "Ordini e Collegi delle Professioni Sanitarie", "A"),
	FONDAZIONI (8, "Fondazioni a carattere scientifico", "A"),
	CASE_EDITRICI (9, "Case editrici scientifiche", "A"),
	PUBBLICI (10, "Società, Agenzie ed Enti Pubblici", "A"),
	PRIVATI (11, "Società, Agenzie ed Enti Privati", "A"),
	AZIENDE_SANITARIE (2, "Aziende Sanitarie (Aziende Usl, Aziende Ospedaliere, Policlinici)", "A"),
	RICOVERO_PUBBLICHE	(12, "Strutture di ricovero pubbliche", "A"),
	RICOVERO_PRIVATE (13, "Strutture di ricovero private", "A"),
	ZOOPROFILATTICO (5, "Istituto zooprofilattico", "A"),
	ENTE_FORMAZIONE (15, "Ente di formazione a partecipazione prevalentemente pubblica regionale o provinciale", "A");

	private int id;
	private String nome;
	private String gruppo;
	
	private TipoOrganizzatore(int id, String nome, String gruppo){
		this.id = id;
		this.nome = nome;
		this.gruppo = gruppo;
	}
}
