package it.tredi.ecm.dao.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class AuditReportProviderBase extends BaseEntityDefaultId {

	@ManyToOne
	private Provider provider;
	@Column(name="data_inizio")
	private LocalDateTime dataInizio;
	@Column(name="data_fine")
	private LocalDateTime dataFine;


}
