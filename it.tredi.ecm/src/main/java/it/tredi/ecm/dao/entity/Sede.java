package it.tredi.ecm.dao.entity;

import lombok.Getter;
import lombok.Setter;

//@Entity
//@Table(name="sede")
@Getter
@Setter
public class Sede {
	private String provincia;
	private String comune;
	private String indirizzo;
	private int cap;
	private int telefono;
	private int altroTelefono;
	private int fax;
	private String email;
}
