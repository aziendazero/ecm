package it.tredi.ecm.dao.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import it.tredi.ecm.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class RuoloOreFSC implements Serializable{

	private static final long serialVersionUID = -6166294681029592942L;
	
	@Enumerated(EnumType.STRING)
	private RuoloFSCEnum ruolo;
	private Float tempoDedicato;
	
	public RuoloOreFSC(){}
	public RuoloOreFSC(RuoloFSCEnum ruolo, Float tempoDedicato) {
		this.ruolo = ruolo;
		this.tempoDedicato = tempoDedicato;
	}
	
	public void setTempoDedicato(float t){
		this.tempoDedicato = Utils.getRoundedFloatValue(t, 2);
	}
}
