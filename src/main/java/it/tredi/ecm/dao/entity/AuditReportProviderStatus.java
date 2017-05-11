package it.tredi.ecm.dao.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AuditReportProviderStatus extends AuditReportProviderBase {

	@Enumerated(EnumType.STRING)
	private ProviderStatoEnum status;

}
