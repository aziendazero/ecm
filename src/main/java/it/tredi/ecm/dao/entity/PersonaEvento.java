package it.tredi.ecm.dao.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import it.tredi.ecm.dao.enumlist.RuoloPersonaEventoEnum;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.EventoController;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PersonaEvento extends BaseEntity implements Serializable{
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

	public PersonaEvento(){}

	public PersonaEvento(AnagraficaEvento anagrafica){
		try{
			//this.anagrafica = (AnagraficaEventoBase) anagrafica.getAnagrafica().clone();
			//this.anagrafica = (AnagraficaEventoBase) Utils.copy(anagrafica.getAnagrafica());
			anagrafica.getAnagrafica().getCv().getData();
			this.anagrafica = (AnagraficaEventoBase) SerializationUtils.clone(anagrafica.getAnagrafica());
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("Errore cast AnagraficaEventoBase"), ex);
		}
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
