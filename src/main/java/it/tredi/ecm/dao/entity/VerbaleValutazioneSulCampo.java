package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

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
	private Accreditamento accreditamento;
//	@OneToOne
//	private Valutazione valutazione;
//	@OneToOne
//	private File verbaleFirmato;

	@ManyToOne
	private Account valutatore;

	@ManyToOne
	private Account teamLeader;

	@ManyToOne
	private Account osservatoreRegionale;

	@ManyToMany
	private Set<Account> componentiSegreteria;

	@ManyToOne
	private Account referenteInformatico;

	@OneToOne
	private Sede sede;

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
