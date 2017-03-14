package it.tredi.ecm.dao.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="pag_pagati_log")
@Getter
@Setter
public class PagPagatiLog extends BaseEntityDefaultId {

	@ManyToOne @JoinColumn(name = "pagamento_id")
	private Pagamento pagamento;
	private String idSession;
	private Date dataRichiesta;
	private String codiceEsito;
	private Date dataEsito;
	private Double importoTotalePagato;
	private String esitoSingoloPagamento;
	private Date dataEsitoSingoloPagamento;
	private String identificativoUnivocoRiscosse;
	private String faultCode;
	private String faultString;
	@Lob
	@Type(type = "org.hibernate.type.TextType")
	private String faultDescription;



}
