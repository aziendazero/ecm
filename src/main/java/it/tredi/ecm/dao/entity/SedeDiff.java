package it.tredi.ecm.dao.entity;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SedeDiff extends BaseEntity {

	private Long sedeId;

	private String provincia;

	private String comune;

	private String indirizzo;

	private String cap;

	private String telefono;

	private String altroTelefono;

	private String fax;

	private String email;

	private boolean sedeLegale;

	private boolean sedeOperativa;

}
