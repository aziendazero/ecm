package it.tredi.ecm.dao.entity;

import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RuoloOreFSC{
	
	private RuoloFSCEnum ruolo;
	private float tempoDedicato;
}
