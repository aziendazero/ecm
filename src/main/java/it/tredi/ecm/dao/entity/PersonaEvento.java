package it.tredi.ecm.dao.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import org.apache.commons.lang3.SerializationUtils;
import org.javers.core.metamodel.annotation.TypeName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.tredi.ecm.dao.enumlist.IdentificativoPersonaRuoloEvento;
import it.tredi.ecm.dao.enumlist.RuoloPersonaEventoEnum;
import it.tredi.ecm.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@TypeName("PersonaEvento")
public class PersonaEvento extends BaseEntityDefaultId implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = -811983653516900898L;

	private static Logger LOGGER = LoggerFactory.getLogger(PersonaEvento.class);

	@Embedded
	private AnagraficaEventoBase anagrafica;
	private String qualifica;//TODO chiedere possibili valori

	@Enumerated(EnumType.STRING)
	private RuoloPersonaEventoEnum ruolo;

	private String titolare;
	
	private boolean svolgeAttivitaDiDocenza = false;
	@Enumerated(EnumType.STRING)
	private IdentificativoPersonaRuoloEvento identificativoPersonaRuoloEvento;

	public PersonaEvento(){}

	public PersonaEvento(AnagraficaEvento anagrafica){
		try{
			//this.anagrafica = (AnagraficaEventoBase) anagrafica.getAnagrafica().clone();
			//this.anagrafica = (AnagraficaEventoBase) Utils.copy(anagrafica.getAnagrafica());
			if(anagrafica.getAnagrafica().getCv() != null)
				anagrafica.getAnagrafica().getCv().getData();
			this.anagrafica = (AnagraficaEventoBase) SerializationUtils.clone(anagrafica.getAnagrafica());
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("Errore cast AnagraficaEventoBase"), ex);
		}
	}
	
	@Transient
	public String getDescrizionePerAttivitaRES() {
		String toRet = "";
		if(this.anagrafica != null && this.anagrafica.getCognome() != null && !this.anagrafica.getCognome().isEmpty()) {
			toRet = this.anagrafica.getCognome();
		}
		if(this.anagrafica != null && this.anagrafica.getCodiceFiscale() != null && !this.anagrafica.getCodiceFiscale().isEmpty()) {
			if(toRet.isEmpty())
				toRet = this.anagrafica.getCodiceFiscale();
			else
				toRet += " " + this.anagrafica.getCodiceFiscale();
		}
		if(this.ruolo != null) {
			if(toRet.isEmpty())
				toRet = this.getRuolo().getNomeCorto();
			else
				toRet += " " + this.getRuolo().getNomeCorto();
		}
		if(this.titolare != null) {
			if(toRet.isEmpty())
				toRet = getLetteraFromTitolare();
			else
				toRet += " " + getLetteraFromTitolare();
		}
		
		return toRet;
	}
	
	@Transient
	private String getLetteraFromTitolare() {
		if(titolare != null) {
			if("titolare".equals(titolare)) {
				return "T";
			} else {
				return "S";
			}
		}
		return "";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		PersonaEvento p = (PersonaEvento) super.clone();
		if(p.getAnagrafica() != null)
			p.setAnagrafica((AnagraficaEventoBase) p.getAnagrafica().clone());

		p.setRuolo(RuoloPersonaEventoEnum.valueOf(p.getRuolo().name()));

		return p;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PersonaEvento entitapiatta = (PersonaEvento) o;
		return Objects.equals(id, entitapiatta.id);
	}


}
