package it.tredi.ecm.service;

import java.util.Map;
import java.util.Set;

import it.tredi.ecm.dao.entity.Professione;

public interface ProfessioneService {
	public Set<Professione> getAllProfessioni();
	public void save(Professione professione);
	public void saveAll(Set<Professione> professioneList);

	public Map<String,String> getProfessioniMap();
}
