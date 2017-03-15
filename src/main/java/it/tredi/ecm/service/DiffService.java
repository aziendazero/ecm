package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.AccreditamentoDiff;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamentoDiff;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.PersonaDiff;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.ProviderDiff;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.entity.SedeDiff;

public interface DiffService {
	public AccreditamentoDiff creaAllDiffAccreditamento(Accreditamento accreditamento);
	public ProviderDiff creaDiffProvider(Provider provider);
	public SedeDiff creaDiffSede(Sede sede);
	public PersonaDiff creaDiffPersona(Persona persona);
	public DatiAccreditamentoDiff creaDiffDatiAccreditamento(DatiAccreditamento datiAccreditamento);
	public Set<FieldValutazioneAccreditamento> confrontaDiffAccreditamento(AccreditamentoDiff diffOld, AccreditamentoDiff diffNew);
	public AccreditamentoDiff findLastDiffByProviderId(Long providerId);
}
