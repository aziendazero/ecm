package it.tredi.ecm.web.bean;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Seduta;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HomeWrapper {

	//User
	private Account user;

	//Admin
	private Boolean isAdmin;
	private Integer utentiInAttesaDiAttivazione;

	//Provider
	private Boolean isProvider;
	private Long providerId;
	private Integer eventiDaPagare;
	private Integer messaggi;
	private Integer accreditamentiDaIntegrare;
	private String nomeProvider;

	//Segreteria
	private Boolean isSegreteria;
	private Integer badReferee;
	private Integer domandeNotTaken;
	private Integer domandeAssegnamento;
	private Integer domandeValutazioneIntegrazione;
	private Integer domandeInScadenza;
	private Integer domandeDaValutareAll;
	private Integer domandeInInsODG;

	//Referee
	private Boolean isReferee;
	private Integer domandeInCarica;
	private Integer domandeNonValutateConsecutivamente;

	//Commissione
	private Boolean isCommissione;
	private Seduta prossimaSeduta;
}
