package it.tredi.ecm.dao.entity;

import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="file")
@Getter
@Setter
public class File extends BaseEntity {
	private String nomeFile;
	private byte[] data;
	
	@Column(name = "creato")
	private LocalTime dataCreazione;
	
	private String tipo;
	@ManyToOne
	private Persona persona;
	@ManyToOne
	private Provider provider;
	
}
