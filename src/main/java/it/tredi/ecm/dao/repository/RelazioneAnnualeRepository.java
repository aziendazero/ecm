package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.RelazioneAnnuale;

public interface RelazioneAnnualeRepository extends CrudRepository<RelazioneAnnuale, Long> {

	public Set<RelazioneAnnuale> findAll();
	public Set<RelazioneAnnuale> findAllByProviderId(Long providerId);
	public RelazioneAnnuale findOneByProviderIdAndAnnoRiferimento(Long providerId, Integer annoRiferimento);

	@Query("SELECT p FROM Provider p WHERE p.status IN ('ACCREDITATO_PROVVISORIAMENTE','ACCREDITATO_STANDARD') AND p.id NOT IN (SELECT distinct r.provider.id FROM RelazioneAnnuale r WHERE r.annoRiferimento = :annoRiferimento AND r.bozza = FALSE)")
	public Set<Provider> findAllProviderNotRelazioneAnnualeRegistrata(@Param("annoRiferimento")Integer annoRiferimento);
	public int countAllByProviderIdAndAnnoRiferimento(Long providerId, int annoCorrente);
}

// and p.codiceEsito not in ('PAA_ESEGUITO', 'PAA_PAGAMENTO_ANNULLATO', 'PAA_PAGAMENTO_SCADUTO', 'PAA_ENTE_NON_VALIDO', 'PAA_ID_SESSION_NON_VALIDO')")

