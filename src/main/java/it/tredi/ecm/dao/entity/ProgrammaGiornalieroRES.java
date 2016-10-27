package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ProgrammaGiornalieroRES extends BaseEntity {
	@ManyToOne
	private EventoRES eventoRES;

	private LocalDate giorno;
	private SedeEvento sede;
	@ElementCollection
	@OrderBy("orario ASC")
	private List<DettaglioAttivitaRES> programma = new ArrayList<DettaglioAttivitaRES>();

}
