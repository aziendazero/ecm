package it.tredi.ecm.dao.entity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="sede")
@Getter
@Setter
public class Sede extends BaseEntity{
	@NotEmpty
	private String provincia;
	@NotEmpty
	private String comune;
	@NotEmpty
	private String indirizzo;
	@NotNull
	private int cap;
	@NotNull
	private int telefono;
	@NotNull
	private int altroTelefono;
	@NotNull
	private int fax;
	@NotEmpty
	@Email
	private String email;
}
