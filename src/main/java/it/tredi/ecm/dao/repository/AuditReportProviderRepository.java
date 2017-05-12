package it.tredi.ecm.dao.repository;

import org.joda.time.LocalDateTime;
import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.AuditReportProvider;
import it.tredi.ecm.dao.entity.Provider;

public interface AuditReportProviderRepository extends CrudRepository<AuditReportProvider, Long> {
	AuditReportProvider findOneByProviderAndDataFine(Provider provider, LocalDateTime dataFine);
}
