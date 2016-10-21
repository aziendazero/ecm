package it.tredi.ecm.dao.entity;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.tredi.ecm.dao.enumlist.RuoloPersonaEventoEnum;
import it.tredi.ecm.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PersonaFullEvento extends BaseEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3024376063379310342L;

	private static Logger LOGGER = LoggerFactory.getLogger(PersonaFullEvento.class);
	
	@Embedded
	private AnagraficaFullEventoBase anagrafica;
	
	public PersonaFullEvento() {
		
	}
	
	public PersonaFullEvento(AnagraficaFullEvento anagrafica){
		try{
			//this.anagrafica = (AnagraficaFullEventoBase) anagrafica.getAnagrafica().clone();
			//this.anagrafica = (AnagraficaFullEventoBase) Utils.copy(anagrafica.getAnagrafica());
			this.anagrafica = (AnagraficaFullEventoBase) SerializationUtils.clone(anagrafica.getAnagrafica());
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("Errore cast AnagraficaFullEventoBase"), ex);
		}
	}
}
