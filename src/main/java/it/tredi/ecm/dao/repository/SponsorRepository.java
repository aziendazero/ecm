package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Sponsor;

public interface SponsorRepository extends CrudRepository<Sponsor, Long>{
	Set<Sponsor> findAll();

}
