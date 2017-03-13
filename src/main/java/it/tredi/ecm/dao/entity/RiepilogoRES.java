package it.tredi.ecm.dao.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;

import org.javers.core.metamodel.annotation.TypeName;

import it.tredi.ecm.dao.enumlist.MetodologiaDidatticaRESEnum;
import it.tredi.ecm.dao.enumlist.ObiettiviFormativiRESEnum;
import it.tredi.ecm.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@TypeName("RiepilogoRES")
@Getter
@Setter
@Embeddable
public class RiepilogoRES {

	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<ObiettiviFormativiRESEnum> obiettivi = new HashSet<ObiettiviFormativiRESEnum>();

	@ElementCollection
	@MapKeyColumn(name="key_metodologia")
	@MapKeyEnumerated(EnumType.STRING)
    @Column(name="value")
	@CollectionTable(name="riepilogo_RES", joinColumns=@JoinColumn(name="riepilogo_res_id"))
	private Map<MetodologiaDidatticaRESEnum, Float> metodologie = new HashMap<MetodologiaDidatticaRESEnum, Float>();

	private Float totaleOreFrontali = 0.0f;
	private Float totaleOreInterattive = 0.0f;

	public void clear(){
		this.obiettivi.clear();
		this.metodologie.clear();
		this.totaleOreFrontali = 0.0f;
		this.totaleOreInterattive = 0.0f;
	}

	public void setTotaleOreFrontali(Float t){
		this.totaleOreFrontali = Utils.getRoundedFloatValue(t, 2);
	}

	public void setTotaleOreInterattive(Float t){
		this.totaleOreInterattive = Utils.getRoundedFloatValue(t, 2);
	}
}
