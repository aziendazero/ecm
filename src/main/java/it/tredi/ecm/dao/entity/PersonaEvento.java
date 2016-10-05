package it.tredi.ecm.dao.entity;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.tredi.ecm.dao.enumlist.RuoloPersonaEventoEnum;
import it.tredi.ecm.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PersonaEvento extends BaseEntity{
	private static Logger LOGGER = LoggerFactory.getLogger(PersonaEvento.class);
	
	@Embedded
	private AnagraficaEventoBase anagrafica;
	private String qualifica;//TODO chiedere possibili valori
	
	@Enumerated(EnumType.STRING)
	private RuoloPersonaEventoEnum ruolo;
	
	@ManyToOne
	private Evento eventoResponsabile;
	@ManyToOne
	private Evento eventoDocente;
	
	public PersonaEvento(AnagraficaEvento anagrafica){
		try{
			this.anagrafica = (AnagraficaEventoBase) anagrafica.getAnagrafica().clone();
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("Errore cast AnagraficaEventoBase"), ex);
		}
	}
}
