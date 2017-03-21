package it.tredi.ecm.audit.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.javers.core.metamodel.annotation.TypeName;
import org.javers.core.metamodel.annotation.ValueObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.EventoPianoFormativo;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@TypeName("EventoPianoFormativoAudit")
@Getter
@Setter
public class EventoPianoFormativoAudit {

//	EventoPianoFormativoAudit.id=label.id
//			EventoPianoFormativoAudit.codiceIdentificativo=label.codice_identificativo
//			EventoPianoFormativoAudit.proceduraFormativa=label.procedure_formative_tipologia
//			EventoPianoFormativoAudit.titolo=label.titolo
//			EventoPianoFormativoAudit.obiettivoNazionale=label.obiettivo_formativo_nazionale
//			EventoPianoFormativoAudit.obiettivoRegionale=label.obiettivo_formativo_regionale
//			EventoPianoFormativoAudit.professioniEvento=label.professioni_cui_evento_si_riferisce

	private Long id;
	private String codiceIdentificativo;
	//Enum
	private ProceduraFormativa proceduraFormativa;
	private String titolo;
	private String obiettivoNazionale;
	private String obiettivoRegionale;
	//Generale, Settoriale
	private String professioniEvento;
	//flag per capire se è attuato o meno
	private boolean attuato = false;

	private Set<String> discipline = new HashSet<String>();

	private Set<String> professioniSelezionate = new HashSet<String>();
//	public Set<Professione> getProfessioniSelezionate(){
//		Set<Professione> professioniSelezionate = new HashSet<Professione>();
//		if(discipline != null){
//			for(Disciplina d : discipline)
//				professioniSelezionate.add(d.getProfessione());
//		}
//		return professioniSelezionate;
//	}

	//Scartati perche' si usa codiceIdentificativo al posto di prefix + edizione, e pianoFormativo e l'anno del piano formativo che contiene l'evento
//	private String prefix;
//	private int edizione = 1;
//	private Integer pianoFormativo;

	public EventoPianoFormativoAudit(EventoPianoFormativo eventoPianoFormativo) {
		this.id = eventoPianoFormativo.getId();
		this.attuato = eventoPianoFormativo.isAttuato();
		this.codiceIdentificativo = eventoPianoFormativo.getCodiceIdentificativo();
		for(Disciplina disc : eventoPianoFormativo.getDiscipline())
			this.discipline.add(disc.getNome());
		if(eventoPianoFormativo.getObiettivoNazionale() == null)
			this.obiettivoNazionale = "";
		else
			this.obiettivoNazionale = eventoPianoFormativo.getObiettivoNazionale().getNome();
		if(eventoPianoFormativo.getObiettivoRegionale() == null)
			this.obiettivoRegionale = "";
		else
			this.obiettivoRegionale = eventoPianoFormativo.getObiettivoRegionale().getNome();
//		this.edizione = eventoPianoFormativo.getEdizione();
//		this.pianoFormativo = eventoPianoFormativo.getPianoFormativo();
//		this.prefix = eventoPianoFormativo.getPrefix();
		this.proceduraFormativa = eventoPianoFormativo.getProceduraFormativa();
		this.professioniEvento = eventoPianoFormativo.getProfessioniEvento();
		for(Professione prof : eventoPianoFormativo.getProfessioniSelezionate())
			this.professioniSelezionate.add(prof.getNome());
		this.titolo = eventoPianoFormativo.getTitolo();

	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EventoPianoFormativoAudit entitapiatta = (EventoPianoFormativoAudit) o;
        return Objects.equals(id, entitapiatta.id);
    }

	@Override
	public int hashCode() {
		return id.intValue();
	}
}
