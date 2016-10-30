package it.tredi.ecm.web.bean;

import it.tredi.ecm.dao.entity.ProgrammaGiornalieroRES;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventoRESProgrammaGiornalieroWrapper {
	private EventoRESTipoDataProgrammaGiornalieroEnum tipoData; 
	private ProgrammaGiornalieroRES programma = new ProgrammaGiornalieroRES();
	
	EventoRESProgrammaGiornalieroWrapper(EventoRESTipoDataProgrammaGiornalieroEnum tipoData, ProgrammaGiornalieroRES programma) {
		this.tipoData = tipoData;
		this.programma = programma;
	}
}
