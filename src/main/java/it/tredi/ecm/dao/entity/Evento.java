package it.tredi.ecm.dao.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Evento extends BaseEntity{
	
	@Enumerated(EnumType.STRING)
	private ProceduraFormativa tipologia;
	private String titolo;
	
	@OneToOne
	private Obiettivo obiettivoNazionale;
	@OneToOne
	private Obiettivo obiettivoRegionale; 
	
	@Column(name="anno_piano_formativo")
	private Integer pianoFormativo;

	@ManyToOne @JoinColumn(name = "provider_id")
	private Provider provider;
	
	@OneToMany 
	@JoinTable(name = "evento_discipline",
				joinColumns = @JoinColumn(name = "evento_id"),
				inverseJoinColumns = @JoinColumn(name = "disciplina_id")
	)
	private Set<Disciplina> discipline = new HashSet<Disciplina>();
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Evento entitapiatta = (Evento) o;
        return Objects.equals(id, entitapiatta.id);
    }
}
