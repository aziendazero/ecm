package it.tredi.ecm.service.bean;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Provider;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderRegistrationWrapper {
	private Provider provider;
	private Persona richiedente;
	private boolean delegato = false;
	private File delega;
	private Persona legale;
}
