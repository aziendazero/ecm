package it.tredi.ecm.dao.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Pagamento extends BaseEntity{
	@Column(name = "data_pagamento")
	private LocalDate dataPagamento;
	@Column(name = "importo", precision=10, scale=2)
	private BigDecimal importo;
	@Column(name = "anno_pagamento")
	private Integer annoPagamento;
	
	@ManyToOne
	private Provider provider;
}
