package it.tredi.ecm.service.bean;


import java.util.ArrayList;
import java.util.List;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.DatiEconomici;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccreditamentoWrapper {
	//dati domanda di accreditamento
	private Accreditamento accreditamento;
	
	//dati provider
	private Provider provider;
	private boolean providerStato; 
	
	private Sede sedeLegale;
	private Sede sedeOperativa;
	
	private Persona legaleRappresentante;
	private Persona delegatoLegaleRappresentante;
	
	//dati domanda accreditamento
	private DatiAccreditamento datiAccreditamento;
	
	//dati anagrafiche interne provider
	private Persona responsabileSegreteria;
	private Persona responsabileAmministrativo;
	private Persona coordinatoreComitatoScientifico;
	private List<Persona> componentiComitatoScientifico = new ArrayList<Persona>();
	private Persona responsabileSistemaInformatico;
	private Persona responsabileQualita;
	
	//allegati
	private boolean attoCostitutivo;
	private boolean esperienzaFormazione;
	private boolean utilizzo;
	private boolean sistemaInformatico;
	private boolean pianoQualita;
	private boolean dichiarazioneLegale;
	
}
