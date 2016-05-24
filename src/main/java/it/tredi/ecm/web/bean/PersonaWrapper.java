package it.tredi.ecm.web.bean;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonaWrapper extends Wrapper {
	private Persona persona;
	private Long accreditamentoId;
	private Long providerId;
	
	public void setOffsetAndIds(){
		if(persona.isLegaleRappresentante())
			setIdOffset(22);
		else if(persona.isLegaleRappresentante())
			setIdOffset(31);
	}
	
	private File nomina;
	private File curriculumVitae;
	private File delega;
}
