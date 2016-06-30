package it.tredi.ecm.dao.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import it.tredi.ecm.dao.enumlist.CategoriaObiettivoNazionale;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Obiettivo extends BaseEntity{
	private String nome;
	private boolean nazionale;
	@Enumerated(EnumType.STRING)
	private CategoriaObiettivoNazionale categoria; 
	
	public Obiettivo(){}
	public Obiettivo(String nome, boolean nazionale, CategoriaObiettivoNazionale categoria){
		this.nome = nome;
		this.nazionale = nazionale;
		this.categoria = categoria;
	}
}
