package it.tredi.ecm.dao.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AnagrafeRegionaleCrediti extends BaseEntityDefaultId{
	private static final long serialVersionUID = 3131358508500753058L;

	private String codiceFiscale;
	private String nome;
	private String cognome;
	private String ruolo;
	private BigDecimal crediti;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Disciplina> discipline = new HashSet<Disciplina>();

	@ManyToOne
	private Evento evento;

	@DateTimeFormat (pattern = "dd/MM/yyyy")
	@Column(name="data")
	private LocalDate data;

	public AnagrafeRegionaleCrediti() {
	}

	public AnagrafeRegionaleCrediti(String codiceFiscale, String cognome, String nome, String ruolo, BigDecimal crediti, LocalDate data, Set<Disciplina> discipline){
		this.codiceFiscale = codiceFiscale;
		this.cognome = cognome;
		this.nome = nome;
		this.ruolo = ruolo;
		this.crediti = crediti;
		this.data = data;
		this.discipline = discipline;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, codiceFiscale, nome, cognome, ruolo, crediti, data);
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AnagrafeRegionaleCrediti entitapiatta = (AnagrafeRegionaleCrediti) o;
        return Objects.equals(id, entitapiatta.id);
    }
}
