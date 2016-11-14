package it.tredi.ecm.dao.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="province")
@Getter
@Setter
public class Provincia {
	@Id
	@Column(name = "codice_provincia")
	private String codiceProvincia;
	@JsonView(JsonViewModel.Provincia.class)
	private String nome;
	@Column(name = "codice_regione")
	private String codiceRegione ;
	private String sigla;
	
	//@OneToMany
	//@JoinColumn(name = "codice_provincia")
	@JsonView(JsonViewModel.Provincia.class)
	@OneToMany(mappedBy = "provincia")
	@OrderBy("nome")
	List<Comune> comuni;
}
