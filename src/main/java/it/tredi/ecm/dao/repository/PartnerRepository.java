package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Partner;

public interface PartnerRepository extends CrudRepository<Partner, Long>{
	Set<Partner> findAll();

}
