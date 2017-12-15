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
	private Integer eventiInScadenzaPagamento;
	private Integer eventiInScadenzaRendicontazione;
	private Integer eventiPagamentoScaduto;
	private Integer eventiRendicontazioneScaduto;
	private Integer messaggi;
	private Integer accreditamentiDaIntegrare;
	private Integer accreditamentiInPreavvisoRigetto;
	private String nomeProvider;
	private Integer eventiBozza;
	private Long nuoviMessaggi;

	//Segreteria
	private Boolean isSegreteria;
	private Boolean isResponsabileSegreteriaEcm;
	private Integer badReferee;
	private Integer domandeNotTaken;
	private Integer domandeAssegnamento;
	private Integer domandeValutazioneIntegrazione;
	private Integer domandeInScadenza;
	private Integer domandeInFirma;
	private Integer domandeDaValutareAll;
	private Integer domandeInInsODG;
	private Integer providerPagamentoNonEffettuatoAllaScadenza;
	private Integer providerPianoFormativoNonInserito;
	private Integer eventiCreditiNonConfermati;
	private Integer domandeSbloccoCampiIntegrazione;
	private Integer providerInadempienti;
	private Integer eventiAlimentazionePrimaInfanzia;
	private Integer eventiMedicineNonConvenzionali;
	private Integer providerNotRelazioneAnnualeRegistrata;
	private Integer domandeTipoStandart;


	//Referee
	private Boolean isReferee;
	private Integer domandeDaValutareNotDone;
	private Integer domandeNonValutateConsecutivamente;

	//Commissione
	private Boolean isCommissione;
	private Seduta prossimaSeduta;
}
