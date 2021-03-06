package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class VerbaleValutazioneSulCampo extends BaseEntityDefaultId {
	@DateTimeFormat (pattern = "dd/MM/yyyy")
	private LocalDate giorno;

	@DateTimeFormat (pattern = "HH:mm")
	private LocalTime ora;
	@Column(name = "dataora_visita")
	private LocalDateTime dataoraVisita;

	public void setGiorno(LocalDate data) {
		this.giorno = data;
		setDataOraByDataAndOra();
	}

	public void setOra(LocalTime ora) {
		this.ora = ora;
		setDataOraByDataAndOra();
	}

	private void setDataOraByDataAndOra() {
		if(this.giorno == null)
			this.dataoraVisita = null;
		else {
			if(this.ora == null) {
				this.dataoraVisita = LocalDateTime.of(this.giorno, LocalTime.MIN);
			} else {
				this.dataoraVisita = LocalDateTime.of(this.giorno, this.ora);
			}
		}
	}

	@OneToOne
	@JoinColumn(name = "accreditamento_id")
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
