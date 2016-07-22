package it.tredi.ecm.dao.entity;

import java.util.Objects;

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
	
	public Disciplina(){}
	public Disciplina(String nome){
		this.nome = nome;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Disciplina entitapiatta = (Disciplina) o;
        return Objects.equals(id, entitapiatta.id);
    }
}
