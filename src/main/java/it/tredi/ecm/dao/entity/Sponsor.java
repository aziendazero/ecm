package it.tredi.ecm.dao.entity;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Sponsor extends BaseEntityDefaultId{
	private String name;
	@OneToOne
	private File sponsorFile;

	//@ManyToOne
	//private Evento evento;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Sponsor entitapiatta = (Sponsor) o;
		return Objects.equals(id, entitapiatta.id);
	}
}
