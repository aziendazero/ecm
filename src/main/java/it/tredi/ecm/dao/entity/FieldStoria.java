package it.tredi.ecm.dao.entity;

import java.time.LocalDate;

import javax.persistence.Column;

public class FieldStoria extends Field{
	@Column(name="data_applicazione_modifica")
	private LocalDate dataApplicazioneModifica;
	@Column(name="data_modifica")
	private LocalDate dataModifica;
	private Object value;
}
