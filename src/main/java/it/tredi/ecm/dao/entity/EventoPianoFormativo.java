package it.tredi.ecm.dao.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class EventoPianoFormativo extends BaseEntity{
	private static Logger LOGGER = LoggerFactory.getLogger(EventoPianoFormativo.class);

	/*
	 * PREFIX[-edizione]
	 * 	PREFIX=
	 * 		*EVENTO inserito in PIANO FORMATIVO -> COD_PROVIDER-ID_EVENTO (quando lo creo nel piano formativo)
	 *
	 * 		*EVENTO inserito da nuovo in EVENTI -> COD_PROVIDER-ID_EVENTO
	 *
	 * 		*EVENTO inserito in EVENTI a partire da evento in piano formativo -> PREFIX dell'evento di partenza
	 *
	 * 		*EVENTO inserito come RIEDIZIONE -> PREFIX dell'evento padre
	 * */
	private String prefix;
	private int edizione = 1;
	public String getCodiceIdentificativo(){
		if(edizione > 1)
			return prefix + "-" + edizione;
		else return prefix;
	}

	public void buildPrefix() throws Exception{
		if(provider == null || accreditamento == null)
			throw new Exception("Evento non agganciato a Provider o Accreditamento");

		//TODO sostituire con getCodiceIdentificativoUnivoco di DB
		this.setPrefix(provider.getCodiceIdentificativoUnivoco() + "-" + this.id);
	}

	@Enumerated(EnumType.STRING)
	private ProceduraFormativa proceduraFormativa;
	private String titolo;

	@OneToOne
	private Obiettivo obiettivoNazionale;
	@OneToOne
	private Obiettivo obiettivoRegionale;

	@Column(name="anno_piano_formativo")
	private Integer pianoFormativo;

	@ManyToOne @JoinColumn(name = "provider_id")
	private Provider provider;
	@ManyToOne @JoinColumn(name = "accreditamento_id")
	private Accreditamento accreditamento;

	private String professioniEvento;
	@ManyToMany
	@JoinTable(name = "evento_piano_formativo_discipline",
				joinColumns = @JoinColumn(name = "evento_id"),
				inverseJoinColumns = @JoinColumn(name = "disciplina_id")
	)
	private Set<Disciplina> discipline = new HashSet<Disciplina>();

	//flag per capire se è attuato o meno
	private boolean attuato = false;

	public Set<Professione> getProfessioniSelezionate(){
		Set<Professione> professioniSelezionate = new HashSet<Professione>();
		if(discipline != null){
			for(Disciplina d : discipline)
				professioniSelezionate.add(d.getProfessione());
		}
		return professioniSelezionate;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EventoPianoFormativo entitapiatta = (EventoPianoFormativo) o;
        return Objects.equals(id, entitapiatta.id);
    }
}
