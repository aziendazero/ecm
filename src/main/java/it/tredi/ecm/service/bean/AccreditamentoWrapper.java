package it.tredi.ecm.service.bean;


import java.util.ArrayList;
import java.util.List;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccreditamentoWrapper {
	//dati provider
	private Provider provider;
	private boolean providerStato; 
	
	private Sede sedeLegale;
	private Sede sedeOperativa;
	
	private Persona legaleRappresentante;
	private Persona delegatoLegaleRappresentante;
	
	//dati domanda di accreditamento
	private Accreditamento accreditamento;
	
	//dati anagrafiche interne provider
	private Persona responsabileSegreteria;
	private Persona responsabileAmministrativo;
	private Persona coordinatoreComitatoScientifico;
	private List<Persona> componentiComitatoScientifico = new ArrayList<Persona>();
	private Persona responsabileSistemaInformatico;
	private Persona responsabileQualita;
	
}
