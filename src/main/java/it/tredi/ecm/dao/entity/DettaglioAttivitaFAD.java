package it.tredi.ecm.dao.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import it.tredi.ecm.dao.enumlist.MetodologiaDidatticaFADEnum;
import it.tredi.ecm.dao.enumlist.ObiettiviFormativiFADEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class DettaglioAttivitaFAD extends BaseEntity implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 8581821767653797912L;

	@Column(columnDefinition = "text")
	private String argomento;
	@ManyToMany
	@JoinTable(name = "dettaglio_attivitafad_docente",
		joinColumns = { @JoinColumn(name = "dettaglio_id") },
		inverseJoinColumns = { @JoinColumn(name = "docente_id") })
	private Set<PersonaEvento> docenti = new HashSet<PersonaEvento>();
	@Column(columnDefinition = "text")
	private String risultatoAtteso;
	@Enumerated(EnumType.STRING)
	private ObiettiviFormativiFADEnum obiettivoFormativo;
	@Enumerated(EnumType.STRING)
	private MetodologiaDidatticaFADEnum metodologiaDidattica;
	private float oreAttivita;
}

