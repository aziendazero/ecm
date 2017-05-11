package it.tredi.ecm.dao.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.tredi.ecm.dao.entity.BonitaSemaphore;

public interface SemaphoreRepository extends JpaRepository<BonitaSemaphore, Long>{
	Optional<BonitaSemaphore> findOneByAccreditamentoId(Long accreditamentoId);
}
