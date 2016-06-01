package it.tredi.ecm.dao.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Professione extends BaseEntity{
	private String nome;
	
	@OneToMany(mappedBy="professione")
	private Set<Disciplina> discipline = new HashSet<Disciplina>();
	
	public Professione(){}
	public Professione(String nome){
		this.nome = nome;
	}
	
	/* UTILS */
	public void addDisciplina(Disciplina d){
		this.discipline.add(d);
		d.setProfessione(this);
	}
}
