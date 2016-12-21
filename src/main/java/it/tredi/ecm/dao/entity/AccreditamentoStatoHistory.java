package it.tredi.ecm.dao.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AccreditamentoStatoHistory extends BaseEntity{
	private static final long serialVersionUID = -7320175410382130407L;

	@ManyToOne
	private Accreditamento accreditamento;
	private Long processInstanceId;
	@Enumerated(EnumType.STRING)
	private AccreditamentoStatoEnum stato;
	@Column(name = "data_fine")
	private LocalDateTime dataFine;

	@Column(name = "data_inizio")
	private LocalDateTime dataInizio;
}
