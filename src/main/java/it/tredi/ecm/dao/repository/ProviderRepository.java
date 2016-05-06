package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Provider;

public interface ProviderRepository extends CrudRepository<Provider, Long> {
	public Provider findOneByAccountId(Long id);
	public Provider findOneByCfPiva(String cf_piva);
	public Set<Provider> findAll();
}
