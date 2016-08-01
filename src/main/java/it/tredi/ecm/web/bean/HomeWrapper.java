package it.tredi.ecm.web.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HomeWrapper {

	//TODO inserire tutte le info necessarie nella home per i vari profili utente

	//Admin
	private Boolean isAdmin;
	private Integer utentiInAttesaDiAttivazione;

	//Provider
	private Boolean isProvider;
	private Long providerId;
	private Integer eventiDaPagare;
	private Integer messaggi;
	private Integer accreditamentiDaIntegrare;

	//Segreteria
	private Boolean isSegreteria;
	private Integer richiesteInviateDaiProvider;
	private Integer providerQuotaAnnuale;
	private Integer providerQuotaEventi;

	//User

}
