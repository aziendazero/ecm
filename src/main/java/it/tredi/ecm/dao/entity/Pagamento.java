package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Pagamento extends BaseEntityDefaultId{
	/*
	 * La logica per il pagamento delle quote annuali e' gestita attraverso la entity QuotaAnnuale
	 *
	 * */
	@Column(name = "data_pagamento")
	private LocalDate dataPagamento;
	@Column(name = "data_scadenza_pagamento")
	private LocalDate dataScadenzaPagamento;
	private Double importo;

	@JoinColumn(name = "fk_quota_annuale")
	@OneToOne(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	private QuotaAnnuale quotaAnnuale;

	@JoinColumn(name = "fk_evento")
	@OneToOne(fetch = FetchType.LAZY)
	private Evento evento;
	private String anagrafica;
	private String indirizzo;
	private String civico;
	private String localita;
	private String provincia;
	private String nazione;
	private String cap;
	private String codiceFiscale;
	private String partitaIva;
	private String email;
	private String tipoVersamento;
	private String causale;
	private String datiSpecificiRiscossione;
	private String identificativoUnivocoDovuto;
	private Double commissioneCaricoPa;
	private String idSession;
	private Date dataInvio;
	private String codiceEsito;
	private Date dataEsito;
	private Double importoTotalePagato;
	private String esitoSingoloPagamento;
	private Date dataEsitoSingoloPagamento;
	private String identificativoUnivocoRiscosse;


}
