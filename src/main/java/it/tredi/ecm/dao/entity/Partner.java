package it.tredi.ecm.dao.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Partner extends BaseEntity{
	private String name;
	@OneToOne
	private File partnerFile;
	
	@ManyToOne
	private Evento evento;
}
