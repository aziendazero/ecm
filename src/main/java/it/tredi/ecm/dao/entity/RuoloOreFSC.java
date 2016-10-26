package it.tredi.ecm.dao.entity;

import javax.persistence.Embeddable;

import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class RuoloOreFSC{

	public RuoloOreFSC(){}
	public RuoloOreFSC(RuoloFSCEnum ruolo, Float tempoDedicato) {
		this.ruolo = ruolo;
		this.tempoDedicato = tempoDedicato;
	}
	private RuoloFSCEnum ruolo;
	private Float tempoDedicato;

}
