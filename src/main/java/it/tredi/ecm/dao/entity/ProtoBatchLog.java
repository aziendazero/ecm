package it.tredi.ecm.dao.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ProtoBatchLog extends BaseEntityDefaultId {

	private Date dtIns;
	private Date dtUpd;

	private String stato;
	private String codStato;

	private String log;

	private String nSpedizione;
	private Date dtSpedizione;
	
	private boolean pecInviata;

	@ManyToOne @JoinColumn(name = "protocollo_id")
	private Protocollo protocollo;

}
