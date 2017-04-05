package it.tredi.ecm.audit.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.Entity;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;
import org.javers.core.metamodel.annotation.ValueObject;

import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.enumlist.RagioneSocialeEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@TypeName("ProviderAttivitaAudit")
@Getter
@Setter
public class ProviderAttivitaAudit {
	@DiffIgnore
	private Long id;

	/*	INFO PROVIDER FORNITE IN FASE DI REGISTRAZIONE	*/
	private ProviderStatoEnum status;

	private boolean canInsertAccreditamentoStandard;
	private LocalDate dataScadenzaInsertAccreditamentoStandard;

	private boolean canInsertAccreditamentoProvvisorio;
	private LocalDate dataRinnovoInsertAccreditamentoProvvisorio;

	private boolean canInsertPianoFormativo;
	private LocalDate dataScadenzaInsertPianoFormativo;

	private boolean canInsertEvento;

	private Boolean myPay;

	private boolean canInsertRelazioneAnnuale = true;
	private LocalDate dataScadenzaInsertRelazioneAnnuale;


	public ProviderAttivitaAudit(Provider provider) {
		this.id = provider.getId();
		this.status = provider.getStatus();

		this.canInsertAccreditamentoStandard = provider.isCanInsertAccreditamentoStandard();
		this.dataScadenzaInsertAccreditamentoStandard = provider.getDataScadenzaInsertAccreditamentoStandard();

		this.canInsertAccreditamentoProvvisorio = provider.isCanInsertAccreditamentoProvvisorio();
		this.dataRinnovoInsertAccreditamentoProvvisorio = provider.getDataRinnovoInsertAccreditamentoProvvisorio();

		this.canInsertPianoFormativo = provider.isCanInsertPianoFormativo();
		this.dataScadenzaInsertPianoFormativo = provider.getDataScadenzaInsertPianoFormativo();

		this.canInsertEvento = provider.isCanInsertEvento();

		this.myPay = provider.getMyPay();

		this.canInsertRelazioneAnnuale = provider.isCanInsertRelazioneAnnuale();
		this.dataScadenzaInsertRelazioneAnnuale = provider.getDataScadenzaInsertRelazioneAnnuale();

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ProviderAttivitaAudit entitapiatta = (ProviderAttivitaAudit) o;
		return Objects.equals(id, entitapiatta.id);
	}

	@Override
	public int hashCode() {
		return id.intValue();
	}
}
