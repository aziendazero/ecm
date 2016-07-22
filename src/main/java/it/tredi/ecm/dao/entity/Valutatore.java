package it.tredi.ecm.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Valutatore extends BaseEntity{
	@OneToOne
	private Account account;
	@OneToOne
	private GruppoCrecm gruppoCrecm;
	private String note;
	@Column(name="valutazioni_non_date")
	private int valutazioniNonDate = 0;
}
