package it.tredi.ecm.dao.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Pagamento extends BaseEntity{
	@Column(name = "data_pagamento")
	private LocalDate dataPagamento;
	@Column(name = "costo", precision=10, scale=2)
	private BigDecimal costo;
	@Column(name = "anno_pagamento")
	private Integer annoPagamento;
	
	@ManyToOne
	private Provider provider;
}
