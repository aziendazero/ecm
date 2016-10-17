package it.tredi.ecm.dao.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class SedeEvento implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1418164019707534287L;
	private String provincia; //TODO da lista
	private String comune; //TODO da lista
	private String indirizzo;//campo libero
	private String luogo;//campo libero
}
