package it.tredi.ecm.dao.repository;

import org.joda.time.LocalDateTime;
import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.AuditReportProviderStatus;
import it.tredi.ecm.dao.entity.Provider;

public interface AuditReportProviderStatusRepository extends CrudRepository<AuditReportProviderStatus, Long> {
	AuditReportProviderStatus findOneByProviderAndDataFine(Provider provider, LocalDateTime dataFine);
}
