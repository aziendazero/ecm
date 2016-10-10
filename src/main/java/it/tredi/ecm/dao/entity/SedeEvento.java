package it.tredi.ecm.dao.entity;

import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class SedeEvento {
	private String provincia; //TODO da lista
	private String comune; //TODO da lista
	private String indirizzo;//campo libero
	private String luogo;//campo libero
}
