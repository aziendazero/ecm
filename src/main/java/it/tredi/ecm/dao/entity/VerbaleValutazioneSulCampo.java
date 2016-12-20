package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class VerbaleValutazioneSulCampo extends BaseEntity {
	@DateTimeFormat (pattern = "dd/MM/yyyy")
	private LocalDate giorno;
	@OneToOne
	@JoinColumn(name = "accreditamento_id")
	private Accreditamento accreditamento;
//	@OneToOne
//	private Valutazione valutazione;
//	@OneToOne
//	private File verbaleFirmato;

	@OneToOne
	private Account valutatore;

	@OneToOne
	private Account teamLeader;

	@OneToOne
	private Account osservatoreRegionale;

	@ManyToMany
	private Set<Account> componentiSegreteria;

	@OneToOne
	private Account referenteInformatico;

	@OneToOne
	private Provider provider;

	@OneToOne
	private File cartaIdentita;

	private Boolean isPresenteLegaleRappresentante;

	@Embedded
	private DelegatoValutazioneSulCampo delegato;

	@Embedded
	private DatiValutazioneSulCampo datiValutazioneSulCampo;
}
