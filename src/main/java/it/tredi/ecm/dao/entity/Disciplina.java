package it.tredi.ecm.dao.entity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Disciplina extends BaseEntity{
	private String nome;
	
	@OneToOne
	private Professione professione;
}
