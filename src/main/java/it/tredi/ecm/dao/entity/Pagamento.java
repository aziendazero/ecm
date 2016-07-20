package it.tredi.ecm.dao.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;

public class Pagamento {
	@Column(name = "data_pagamento")
	private LocalDate dataPagamento;
	@Column(name = "costo", precision=10, scale=2)
	private BigDecimal costo;
	@Column(name = "anno_pagamento")
	private Integer annoPagamento;
}
