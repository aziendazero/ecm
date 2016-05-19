package it.tredi.ecm.service;

import it.tredi.ecm.dao.entity.Sede;

public interface SedeService {
	public Sede getSede(Long id);
	public void save(Sede sede);
}
