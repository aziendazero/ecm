package it.tredi.ecm.dao.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.TypeName;

import com.fasterxml.jackson.annotation.JsonView;

import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import lombok.Getter;
import lombok.Setter;

@TypeName("Persona")
@Entity
@NamedEntityGraph(name="graph.persona.files",
					attributeNodes = @NamedAttributeNode(value="files", subgraph="minimalFileInfo"),
					subgraphs = @NamedSubgraph(name="minimalFileInfo", attributeNodes={
							@NamedAttributeNode("id"),
							@NamedAttributeNode("nomeFile"),
							@NamedAttributeNode("tipo")
					}))
@Getter
@Setter
public class Persona extends BaseEntityDefaultId{
	@JsonView(JsonViewModel.Integrazione.class)
	@JoinColumn(name = "anagrafica_id")
	@ManyToOne(cascade = {CascadeType.PERSIST , CascadeType.MERGE})
	private Anagrafica anagrafica = new Anagrafica();

	@DiffIgnore
	@JsonView(JsonViewModel.Integrazione.class)
	@ManyToOne @JoinColumn(name = "provider_id")
	private Provider provider;

	@JsonView(JsonViewModel.Integrazione.class)
	@Enumerated(EnumType.STRING)
	private Ruolo ruolo;

	@JsonView(JsonViewModel.Integrazione.class)
	private String incarico = "";

	@JsonView(JsonViewModel.Integrazione.class)
	@OneToOne
	private Professione professione;

	@JsonView(JsonViewModel.Integrazione.class)
	private Boolean coordinatoreComitatoScientifico;

	@JsonView(JsonViewModel.Integrazione.class)
	@ManyToMany(cascade= CascadeType.REMOVE,fetch=FetchType.EAGER)
	@JoinTable(name="persona_files",
				joinColumns={@JoinColumn(name="persona_id")},
				inverseJoinColumns={@JoinColumn(name="files_id")}
	)
	Set<File> files = new HashSet<File>();

	//se true significa che non è stato ancora validato dalla segreteria
	@JsonView(JsonViewModel.Integrazione.class)
	private boolean dirty = false;

	public Persona(){}
	public Persona(Ruolo ruolo){this.ruolo = ruolo;}

	public void setProvider(Provider provider){
		this.provider = provider;
		this.getAnagrafica().setProvider(provider);
	}

	//nel caso esista già un file di quel tipo lo sostituisco
	public void addFile(File file){
		Iterator<File> it = this.getFiles().iterator();
		while(it.hasNext()){
			if(it.next().getTipo() == file.getTipo())
				it.remove();
		}
		this.getFiles().add(file);
	}

	/***	CHECK RUOLO DELLA PERSONA	***/
	public boolean isResponsabileSegreteria(){
		return ruolo == Ruolo.RESPONSABILE_SEGRETERIA;
	}
	public boolean isResponsabileFormazione(){
		return ruolo == Ruolo.RESPONSABILE_FORMAZIONE;
	}
	public boolean isResponsabileSistemaInformatico(){
		return ruolo == Ruolo.RESPONSABILE_SISTEMA_INFORMATICO;
	}
	public boolean isResponsabileAmministrativo(){
		return ruolo == Ruolo.RESPONSABILE_AMMINISTRATIVO;
	}
	public boolean isResponsabileQualita(){
		return ruolo == Ruolo.RESPONSABILE_QUALITA;
	}
	public boolean isLegaleRappresentante(){
		return ruolo == Ruolo.LEGALE_RAPPRESENTANTE;
	}
	public boolean isDelegatoLegaleRappresentante(){
		return ruolo == Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE;
	}
	public boolean isCoordinatoreComitatoScientifico(){
		return isComponenteComitatoScientifico() && (this.coordinatoreComitatoScientifico != null) && this.coordinatoreComitatoScientifico.booleanValue();
	}
	public boolean isComponenteComitatoScientifico(){
		return ruolo == Ruolo.COMPONENTE_COMITATO_SCIENTIFICO;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Persona entitapiatta = (Persona) o;
		return Objects.equals(id, entitapiatta.id);
	}

	@Override
	public Persona clone() throws CloneNotSupportedException {
		Persona cloned = (Persona) super.clone();
		cloned.setAnagrafica((Anagrafica)cloned.getAnagrafica().clone());
		return cloned;
	}
}
