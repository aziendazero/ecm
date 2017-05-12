package it.tredi.ecm.dao.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
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

	//@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE } )
	@JoinTable(name = "anagrafe_regionale_crediti_discipline",
		joinColumns = @JoinColumn(name = "anagrafe_regionale_crediti_id"),
		inverseJoinColumns = @JoinColumn(name = "discipline_id")
	)
	private Set<Disciplina> discipline = new HashSet<Disciplina>();

/*
@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE } )
@JoinTable(name = "post_tag",
joinColumns = @JoinColumn(name = "post_id"),
inverseJoinColumns = @JoinColumn(name = "tag_id")
)
private List<Tag> tags = new ArrayList<>();
*/

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
