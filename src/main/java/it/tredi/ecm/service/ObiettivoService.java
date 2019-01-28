package it.tredi.ecm.service;

import java.util.List;
import java.util.Set;

import it.tredi.ecm.dao.entity.Obiettivo;
import it.tredi.ecm.dao.enumlist.EventoVersioneEnum;

public interface ObiettivoService {
	public Set<Obiettivo> getAllObiettivi();
	public Set<Obiettivo> getObiettiviNazionali();
	public Set<Obiettivo> getObiettiviNazionali(EventoVersioneEnum versione);
	public Set<Obiettivo> getObiettiviRegionali();
	public Set<Obiettivo> getObiettiviRegionali(EventoVersioneEnum versione);
	public void save(Obiettivo obiettivo);
	public void save(Set<Obiettivo> obiettivi);
	public Obiettivo getObiettivo(Long obiettivoId);
	public Obiettivo findOneByCodiceCogeaps(String codiceCogeaps, boolean nazionale);

	public Set<Obiettivo> getObiettiviByCodiceCogeapsAndVersioneEventi(boolean nazionale, List<String> codiceCogeaps, EventoVersioneEnum versione);
}
