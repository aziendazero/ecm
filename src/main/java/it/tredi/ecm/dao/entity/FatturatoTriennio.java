package it.tredi.ecm.dao.entity;

import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class FatturatoTriennio {
	private int fatturato1Anno;
	private Double fatturato1Valore;
	private int fatturato2Anno;
	private Double fatturato2Valore;
	private int fatturato3Anno;
	private Double fatturato3Valore;
}
