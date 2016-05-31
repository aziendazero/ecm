package it.tredi.ecm.dao.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Professione extends BaseEntity{
	private String nome;
	
	@OneToMany
	private List<Disciplina> discipline;
}
