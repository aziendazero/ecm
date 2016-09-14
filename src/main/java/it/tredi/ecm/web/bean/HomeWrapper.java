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
	private Integer badReferee;
	private Integer domandeStandardNotTaken;
	private Integer domandeProvvisorieNotTaken;
	private Integer domandeAssegnamento;
	private Integer domandeProvvisorieRichiestaIntegrazione;
	private Integer domandeProvvisorieValutazioneIntegrazione;
	private Integer domandeProvvisoriePreavvisoRigetto;
	private Integer domandeInScadenza;

	//Referee
	private Boolean isReferee;
	private Integer domandeInCarica;
	private Integer domandeNonValutateConsecutivamente;


	//User

}
