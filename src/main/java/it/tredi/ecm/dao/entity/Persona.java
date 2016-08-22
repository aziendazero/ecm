package it.tredi.ecm.dao.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToOne;
import javax.swing.text.View;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import it.tredi.ecm.dao.enumlist.Ruolo;
import lombok.Getter;
import lombok.Setter;

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
public class Persona extends BaseEntity{
	@JsonView(JsonViewModel.Integrazione.class)
	@JoinColumn(name = "anagrafica_id")
	@ManyToOne(cascade = {CascadeType.PERSIST , CascadeType.MERGE})
	private Anagrafica anagrafica = new Anagrafica();
	
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
	@ManyToMany(cascade= CascadeType.REMOVE)
	@JoinTable(name="persona_files",
				joinColumns={@JoinColumn(name="persona_id")},
				inverseJoinColumns={@JoinColumn(name="files_id")}
	)
	Set<File> files = new HashSet<File>();

	public Persona(){}
	public Persona(Ruolo ruolo){this.ruolo = ruolo;}

	public void setProvider(Provider provider){
		this.provider = provider;
		this.getAnagrafica().setProvider(provider);
	}

	public void addFile(File file){
		this.getFiles().add(file);
	}
	
	/***	CHECK RUOLO DELLA PERSONA	***/
	public boolean isResponsabileSegreteria(){
		return ruolo.equals(Ruolo.RESPONSABILE_SEGRETERIA);
	}
	public boolean isResponsabileFormazione(){
		return ruolo.equals(Ruolo.RESPONSABILE_FORMAZIONE);
	}
	public boolean isResponsabileSistemaInformatico(){
		return ruolo.equals(Ruolo.RESPONSABILE_SISTEMA_INFORMATICO);
	}
	public boolean isResponsabileAmministrativo(){
		return ruolo.equals(Ruolo.RESPONSABILE_AMMINISTRATIVO);
	}
	public boolean isResponsabileQualita(){
		return ruolo.equals(Ruolo.RESPONSABILE_QUALITA);
	}
	public boolean isLegaleRappresentante(){
		return ruolo.equals(Ruolo.LEGALE_RAPPRESENTANTE);
	}
	public boolean isDelegatoLegaleRappresentante(){
		return ruolo.equals(Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE);
	}
	public boolean isCoordinatoreComitatoScientifico(){
		return isComponenteComitatoScientifico() && (this.coordinatoreComitatoScientifico != null) && this.coordinatoreComitatoScientifico.booleanValue();
	}
	public boolean isComponenteComitatoScientifico(){
		return ruolo.equals(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO);
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
