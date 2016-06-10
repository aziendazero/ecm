package it.tredi.ecm.dao.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import it.tredi.ecm.dao.enumlist.Ruolo;
import lombok.Getter;
import lombok.Setter;

@Entity
//@Table(name="persona",
//		uniqueConstraints = {
//								@UniqueConstraint(columnNames={"provider_id", "ruolo"})
//								}
//)
@Getter
@Setter
public class Persona extends BaseEntity{
	@JoinColumn(name = "anagrafica_id")
	@ManyToOne(cascade = {CascadeType.PERSIST , CascadeType.MERGE})
	private Anagrafica anagrafica = new Anagrafica();
	@ManyToOne @JoinColumn(name = "provider_id")
	private Provider provider;
	@Enumerated(EnumType.STRING)
	private Ruolo ruolo;
	private String incarico = "";
	@OneToOne
	private Professione professione;
	private Boolean coordinatoreComitatoScientifico; 
	
	public Persona(){}
	public Persona(Ruolo ruolo){this.ruolo = ruolo;}
	
	public void setProvider(Provider provider){
		this.provider = provider;
		this.getAnagrafica().setProvider(provider);
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
}
