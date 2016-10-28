package it.tredi.ecm.dao.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;

import it.tredi.ecm.dao.enumlist.MetodologiaDidatticaFADEnum;
import it.tredi.ecm.dao.enumlist.ObiettiviFormativiFADEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class RiepilogoFAD {

	@ElementCollection
	private Set<ObiettiviFormativiFADEnum> obiettivi = new HashSet<ObiettiviFormativiFADEnum>();
	
	@ElementCollection
	@MapKeyColumn(name="key_metodologia")
    @Column(name="value")
	@CollectionTable(name="riepilogo_FAD", joinColumns=@JoinColumn(name="riepilogo_fad_id"))
	private Map<MetodologiaDidatticaFADEnum, Float> metodologie = new HashMap<MetodologiaDidatticaFADEnum, Float>();
	
	public void clear(){
		this.obiettivi.clear();
		this.metodologie.clear();
	}
	
}
