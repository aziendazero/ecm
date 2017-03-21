package it.tredi.ecm.audit.entity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.javers.core.metamodel.annotation.TypeName;
import org.javers.core.metamodel.annotation.ValueObject;

import it.tredi.ecm.dao.entity.EventoPianoFormativo;
import it.tredi.ecm.dao.entity.PianoFormativo;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@TypeName("PianoFormativoAudit")
@Getter
@Setter
public class PianoFormativoAudit {
	private Long id;
	private Integer annoPianoFormativo;

	//data per gestire le modifiche dei piani formativi annuali
	private LocalDate dataFineModifca;

	private Map<Long, EventoPianoFormativoAudit> eventiPianoFormativo = new HashMap<Long, EventoPianoFormativoAudit>();

	public PianoFormativoAudit(PianoFormativo pianoFormativo) {
		this.id = pianoFormativo.getId();
		this.annoPianoFormativo = pianoFormativo.getAnnoPianoFormativo();
		this.dataFineModifca = pianoFormativo.getDataFineModifca();
		EventoPianoFormativoAudit evento;
		for(EventoPianoFormativo ev : pianoFormativo.getEventiPianoFormativo()) {
			evento = new EventoPianoFormativoAudit(ev);
			this.eventiPianoFormativo.put(evento.getId(), evento);
		}
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PianoFormativoAudit entitapiatta = (PianoFormativoAudit) o;
        return Objects.equals(id, entitapiatta.id);
    }

	@Override
	public int hashCode() {
		return id.intValue();
	}
}
