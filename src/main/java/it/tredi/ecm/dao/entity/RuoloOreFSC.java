package it.tredi.ecm.dao.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class RuoloOreFSC implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6166294681029592942L;
	public RuoloOreFSC(){}
	public RuoloOreFSC(RuoloFSCEnum ruolo, Float tempoDedicato) {
		this.ruolo = ruolo;
		this.tempoDedicato = tempoDedicato;
	}
	private RuoloFSCEnum ruolo;
	private Float tempoDedicato;

}
