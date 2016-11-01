package it.tredi.ecm.service;

import java.util.Optional;
import java.util.Set;

import it.tredi.ecm.dao.entity.Obiettivo;
import it.tredi.ecm.dao.entity.PianoFormativo;
import it.tredi.ecm.utils.Utils;

public interface ObiettivoService {
	public Set<Obiettivo> getAllObiettivi();
	public Set<Obiettivo> getObiettiviNazionali();
	public Set<Obiettivo> getObiettiviRegionali();
	public void save(Obiettivo obiettivo);
	public void save(Set<Obiettivo> obiettivi);
	public Obiettivo getObiettivo(Long obiettivoId);
	public Obiettivo findOneByCodiceCogeaps(String codiceCogeaps, boolean nazionale);
}
