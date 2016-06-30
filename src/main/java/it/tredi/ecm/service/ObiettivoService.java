package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.Obiettivo;

public interface ObiettivoService {
	public Set<Obiettivo> getAllObiettivi();
	public Set<Obiettivo> getObiettiviNazionali();
	public Set<Obiettivo> getObiettiviRegionali();
	public void save(Obiettivo obiettivo);
	public void save(Set<Obiettivo> obiettivi);
}
