package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ProgrammaGiornalieroRES extends BaseEntity {
	//@ManyToOne
	//private EventoRES eventoRES;

	@DateTimeFormat (pattern = "dd/MM/yyyy")
	private LocalDate giorno;
	private SedeEvento sede;
	@ElementCollection
	@OrderBy("orario_inizio ASC")
	private List<DettaglioAttivitaRES> programma = new ArrayList<DettaglioAttivitaRES>();

}
