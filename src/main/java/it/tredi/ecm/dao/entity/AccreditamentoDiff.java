package it.tredi.ecm.dao.entity;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AccreditamentoDiff extends BaseEntityDefaultId {

	private LocalDateTime dataCreazione;

	private Long accreditamentoIdRiferimento;

	private Long providerIdRiferimento;

	@OneToOne
	private ProviderDiff provider;

	@OneToOne
	private PersonaDiff legaleRappresentante;

	@OneToOne
	private PersonaDiff delegatoLegaleRappresentante;

	@OneToMany
	@JoinTable(name = "accreditamento_diff_sedi",
		joinColumns = @JoinColumn(name = "dati_accreditamento_diff_id"),
		inverseJoinColumns = @JoinColumn(name = "sede_diff_id")
	)
	private Set<SedeDiff> sedi;

	@OneToOne
	private DatiAccreditamentoDiff datiAccreditamento;

	@OneToOne
	private PersonaDiff responsabileSegreteria;

	@OneToOne
	private PersonaDiff responsabileAmministrativo;

	@OneToOne
	private PersonaDiff responsabileSistemaInformatico;

	@OneToOne
	private PersonaDiff responsabileQualita;

	@OneToMany
	@JoinTable(name = "accreditamento_diff_comitato",
		joinColumns = @JoinColumn(name = "dati_accreditamento_diff_id"),
		inverseJoinColumns = @JoinColumn(name = "componente_diff_id")
	)
	private Set<PersonaDiff> componentiComitatoScientifico;

	private Long fileAttoCostitutivo;

	private Long fileDichiarazioneEsclusione;

	private Long fileEsperienzaFormazione;

	private Long fileUtilizzoSedi;

	private Long fileSistemaInformatico;

	private Long filePianoQualita;

	private Long fileDichiarazioneLegaleRappresentante;

	private Long fileRichiestaAccreditamentoStandard;

	private Long fileRelazioneAttivitaFormativa;

}
