package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum TipoOrganizzatore {
	ISTITUTI_RICOVERO (14, "Istituti di ricovero e cura a carattere scientifico (IRCCS)", "A", true),
	AZIENDE_SANITARIE (2, "Aziende Sanitarie (Aziende Usl, Aziende Ospedaliere, Policlinici)", "A", true),
	UNIVERSITA (1, "Università, facoltà e dipartimenti universitari", "B", true),
	ISTITUTI_SCIENTIFICI (3, "Istituti scientifici del servizio sanitario nazionale", "B", true),
	ISTITUTI_CONSIGLIO (4, "Istituti del Consiglio Nazionale delle Ricerche", "B", true),
	SOCIETA_SCIENTIFICHE (6, "Società scientifiche e associazioni professionali in campo sanitario", "B", false),
	COLLEGI (7, "Ordini e Collegi delle Professioni Sanitarie", "B", true),
	FONDAZIONI (8, "Fondazioni a carattere scientifico", "B", false),
	CASE_EDITRICI (9, "Case editrici scientifiche", "B", false),
	PUBBLICI (10, "Società, Agenzie ed Enti Pubblici", "B", true),
	PRIVATI (11, "Società, Agenzie ed Enti Privati", "B", false),
	RICOVERO_PUBBLICHE	(12, "Strutture di ricovero pubbliche", "B", true),
	RICOVERO_PRIVATE (13, "Strutture di ricovero private", "B", false),
	ZOOPROFILATTICO (5, "Istituto zooprofilattico", "B", true),
	ENTE_FORMAZIONE (15, "Ente di formazione a partecipazione prevalentemente pubblica regionale o provinciale", "B", true),
	OSPEDALI_CLASSIFICATI (16, "Ospedali classificati ex. Art. 1 legge 132 1968", "C", true);

	private int id;
	private String nome;
	private String gruppo;
	private boolean isTipoP;

	private TipoOrganizzatore(int id, String nome, String gruppo, boolean isTipoP){
		this.id = id;
		this.nome = nome;
		this.gruppo = gruppo;
		this.isTipoP = isTipoP;
	}
}
