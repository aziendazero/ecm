package it.tredi.ecm.dao.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.TypeName;

import lombok.Getter;
import lombok.Setter;

@TypeName("Professione")
@Entity
@Getter
@Setter
public class Professione extends BaseEntityDefaultId{
	private String nome;
	private boolean sanitaria = false;

	@Column(name ="codice_cogeaps")
	private String codiceCogeaps;

	@DiffIgnore
	@OneToMany(mappedBy="professione")
	private Set<Disciplina> discipline = new HashSet<Disciplina>();

	public Professione(){}
	public Professione(String nome, String codiceCogeaps){
		this.nome = nome;
		this.codiceCogeaps = codiceCogeaps;
	}

	/* UTILS */
	public void addDisciplina(Disciplina d){
		this.discipline.add(d);
		d.setProfessione(this);
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Professione entitapiatta = (Professione) o;
        return Objects.equals(id, entitapiatta.id);
    }
}
