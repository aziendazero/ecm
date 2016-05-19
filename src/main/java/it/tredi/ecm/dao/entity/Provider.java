package it.tredi.ecm.dao.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Provider extends BaseEntity{
	/*	ACCOUNT LEGATO AL PROFILO PROVIDER	*/
	@OneToOne(cascade = CascadeType.ALL)
	private Account account;
	
	/*	INFO PROVIDER FORNITE IN FASE DI REGISTRAZIONE	*/
	private String denominazioneLegale;
	@Enumerated(EnumType.STRING)
	private TipoOrganizzatore tipoOrganizzatore;//TODO enum?
	private String gruppo;
	private String partitaIva;
	private String codiceFiscale;
	
	/*	PERSONE REGISTRATE DAL PROVIDER	
	 * 	alcune in fase di registrazione, altre in fase di accreditamento */
	@OneToMany(mappedBy="provider")
	private Set<Persona> persone = new HashSet<Persona>();
	
	/*	SEDI DEL PROVIDER FORNITE IN FASE DI ACCREDITAMENTO	*/
	@OneToOne
	private Sede sedeLegale;
	@OneToOne
	private Sede sedeOperativa;
	
	/*	INFO PROVIDER FORNITE IN FASE DI ACCREDITAMENTO	*/
	private String ragioneSociale;
	private String naturaOrganizzazione;
	@Column(name ="no_profit")
	private boolean noProfit = false;
	
	/*	IL GRUPPO VIENE DESIGNATO IN FUNZIONE DEL TIPO DI ORGANIZZATORE	*/
	public void setTipoOrganizzatore(TipoOrganizzatore tipoOrganizzatore){
		this.tipoOrganizzatore = tipoOrganizzatore;
		this.gruppo = tipoOrganizzatore.getGruppo();
	}
	
	/** UTILS **/
	public void addPersona(Persona persona){
		this.persone.add(persona);
		persona.setProvider(this);
	}
}
