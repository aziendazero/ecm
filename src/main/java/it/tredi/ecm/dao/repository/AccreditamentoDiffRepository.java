package it.tredi.ecm.dao.repository;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.AccreditamentoDiff;

public interface AccreditamentoDiffRepository extends CrudRepository<AccreditamentoDiff, Long> {

	AccreditamentoDiff findFirstByProviderIdRiferimentoOrderByDataCreazioneDesc(Long providerId);

}
