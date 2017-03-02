package it.tredi.ecm.dao.entity;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = {"codiceFiscale", "provider_id"},
												name="uk_anagraficafullevento_codicefiscale_providerid")})
public class AnagraficaFullEvento extends BaseEntity{
	@Embedded
	private AnagraficaFullEventoBase anagrafica;

	@JsonIgnore
	@ManyToOne
	private Provider provider;

	//TODO procedura che al salvataggio dell'anagrafica va ad aggiornare i campi delle PersonaFullEvento che sono negli eventi ancora modificabili
}
