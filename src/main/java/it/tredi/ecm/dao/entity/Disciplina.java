package it.tredi.ecm.dao.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Disciplina extends BaseEntityDefaultId{
	private String nome;

	@Column(name ="codice_cogeaps")
	private String codiceCogeaps;

	@OneToOne
	private Professione professione;

	public Disciplina(){}
	public Disciplina(String nome, String codiceCogeaps){
		this.nome = nome;
		this.codiceCogeaps = codiceCogeaps;
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
