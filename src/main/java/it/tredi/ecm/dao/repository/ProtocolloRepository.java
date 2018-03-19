package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.Protocollo;

public interface ProtocolloRepository extends CrudRepository<Protocollo, Long> {
	public Set<Protocollo> findAll();

	@Query("SELECT p From Protocollo p WHERE (p.idProtoBatch is not null AND p.numero is null) OR pecInviata = false")
//	@Query("SELECT p From Protocollo p WHERE p.idProtoBatch is not null AND p.numero is null")
	public Set<Protocollo> getProtocolliInUscita();

	@Query("SELECT p From Protocollo p WHERE p.idProtoBatch is not null AND p.numero is not null AND p.statoSpedizione<>'avvenuta-consegna'")
	public Set<Protocollo> getStatoSpedizioneNonConsegnate();

	@Query("SELECT p From Protocollo p WHERE (p.idProtoBatch is not null AND p.numero is not null AND (p.statoSpedizione IS NULL OR p.statoSpedizione NOT IN ('avvenuta-consegna', 'errore'))) OR (pecInviata = false AND (p.statoSpedizione IS NULL OR p.statoSpedizione NOT IN ('avvenuta-consegna', 'errore')))")
//	@Query("SELECT p From Protocollo p WHERE p.idProtoBatch is not null AND p.numero is not null AND (p.statoSpedizione IS NULL OR p.statoSpedizione NOT IN ('avvenuta-consegna', 'errore'))")
	public Set<Protocollo> getStatoSpedizioneNonConsegnateENonInErrore();

	@Query("SELECT p From Protocollo p WHERE p.idProtoBatch is not null AND (p.statoSpedizione is null OR p.statoSpedizione <> :statoSpedizione)")
	public Set<Protocollo> findAllWithErrors(@Param("statoSpedizione") String statoSpedizione);

}
