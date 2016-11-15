package it.tredi.ecm.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="comuni")
@Getter
@Setter
public class Comune {
	@Id
	@Column(name = "codice_comune")
	private String codiceComune;
	@JsonView(JsonViewModel.Provincia.class)
	private String nome;
	
	@ManyToOne
	@JoinColumn(name = "codice_provincia")
	private Provincia provincia;
}
