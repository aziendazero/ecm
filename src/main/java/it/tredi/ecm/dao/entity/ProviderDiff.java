package it.tredi.ecm.dao.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import it.tredi.ecm.dao.enumlist.RagioneSocialeEnum;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ProviderDiff extends BaseEntityDefaultId {

	@Enumerated(EnumType.STRING)
	private TipoOrganizzatore tipoOrganizzatore;

	private String denominazioneLegale;

	private boolean hasPartitaIVA;

	private String partitaIva;

	private String codiceFiscale;

	@Enumerated(EnumType.STRING)
	private RagioneSocialeEnum ragioneSociale;

	private String emailStruttura;

	private String naturaOrganizzazione;

	private boolean noProfit;
}
