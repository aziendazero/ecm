package it.tredi.ecm.dao.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import org.springframework.format.annotation.NumberFormat;

import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class DatiAccreditamentoDiff extends BaseEntity {

	//generale/settoriale tipologia procedure formative
	private String tipologiaAccreditamento;

	@ElementCollection
	@Enumerated(EnumType.STRING)
	private Set<ProceduraFormativa> procedureFormative = new HashSet<ProceduraFormativa>();

	//generale/settoriale professioni/discipline
	private String accreditamentoPerProfessioni;

	@ManyToMany
	@JoinTable(name = "dati_accreditamento_diff_discipline",
				joinColumns = @JoinColumn(name = "dati_accreditamento_diff_id"),
				inverseJoinColumns = @JoinColumn(name = "disciplina_id")
	)
	private Set<Disciplina> discipline = new HashSet<Disciplina>();

	@NumberFormat(pattern = "0.00")
	private BigDecimal fatturatoComplessivoValoreUno;

	@NumberFormat(pattern = "0.00")
	private BigDecimal fatturatoComplessivoValoreDue;

	@NumberFormat(pattern = "0.00")
	private BigDecimal fatturatoComplessivoValoreTre;

	@NumberFormat(pattern = "0.00")
	private BigDecimal fatturatoFormazioneValoreUno;

	@NumberFormat(pattern = "0.00")
	private BigDecimal fatturatoFormazioneValoreDue;

	@NumberFormat(pattern = "0.00")
	private BigDecimal fatturatoFormazioneValoreTre;

	private Integer numeroDipendentiFormazioneTempoIndeterminato;

	private Integer numeroDipendentiFormazioneAltro;

	private Long fileEstrattoBilancioComplessivo;

	private Long fileEstrattoBilancioFormazione;

	private Long fileOrganigramma;

	private Long fileFunzionigramma;
}
