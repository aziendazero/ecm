package it.tredi.ecm.dao.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PersonaDiff extends BaseEntityDefaultId{

	private Long personaId;

	private Long anagraficaId;

	private String cognome;

	private String nome;

	private String codiceFiscale;

	private boolean straniero;

	private String telefono;

	private String cellulare;

	private String email;

	private String pec;

	@ManyToOne
	private Professione professione;

	private boolean coordinatoreComitatoScientifico;

	private Long fileAttoDiNomina;

	private Long fileCurriculumVitae;

	private Long fileDelega;

}
