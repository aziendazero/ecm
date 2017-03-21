package it.tredi.ecm.audit.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Embeddable;

import org.javers.core.metamodel.annotation.TypeName;
import org.javers.core.metamodel.annotation.ValueObject;

import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.DatiEconomici;
import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import lombok.Getter;
import lombok.Setter;

@ValueObject
@TypeName("DatiAccreditamentoAudit")
@Getter
@Setter
public class DatiAccreditamentoAudit {
	private Long id;
	/***  TIPOLOGIA FORMATIVA INIZIO ***/
	//Tipologia Formativa - Accreditamento per tipologia
	//Generale, Settoriale
	private String tipologiaAccreditamento;

	//Tipologia Formativa - Tipologia dell'offerta formativa
	//Enum
	private Set<ProceduraFormativa> procedureFormative = new HashSet<ProceduraFormativa>();

	//Tipologia Formativa - Accreditamento per professioni
	//Generale, Settoriale
	private String professioniAccreditamento;

	//Tipologia Formativa - Professioni / Discipline
	//private Set<Disciplina> discipline = new HashSet<Disciplina>();
	private Set<String> discipline = new HashSet<String>();
	private Set<String> professioniSelezionate = new HashSet<String>();
//	public Set<Professione> getProfessioniSelezionate(){
//		Set<Professione> professioniSelezionate = new HashSet<Professione>();
//		if(discipline != null){
//			for(Disciplina d : discipline)
//				professioniSelezionate.add(d.getProfessione());
//		}
//		return professioniSelezionate;
//	}
	/***  TIPOLOGIA FORMATIVA FINE ***/

	/*** DATI ECONOMICI INIZIO ***/
	//Embeddable
	private DatiEconomiciAudit datiEconomici;
	//Dati economici - Estratto del bilancio complessivo degli ultimi 3 anni
	private File estrattoBilancioComplessivo;
	//Dati economici - Estratto del bilancio relativo alla formazione in ambito sanitario degli ultimi tre anni e budget previsionale
	private File estrattoBilancioFormazione;
	/*** DATI ECONOMICI FINE ***/

	/*** DATI DELLA STRUTTURA INIZIO ***/
	//Dati della Struttura - Numero dipendenti dedicati alla formazione Tempo indeterminato
	private Integer numeroDipendentiFormazioneTempoIndeterminato;
	//Dati della Struttura - Numero dipendenti dedicati alla formazione Altro personale
	private Integer numeroDipendentiFormazioneAltro;

	//Dati della Struttura - Organigramma
	private File organigramma;
	//Dati della Struttura - finzionigramma
	private File funzionigramma;
	/*** DATI DELLA STRUTTURA FINE ***/

	/*** ALLEGATI INIZIO ***/
	//Atto Costitutivo e statuto
	private File attoCostitutivo;
	//Dichiarazione esclusione conflitto di interessi per l’oggetto sociale
	private File assenzaConflittiInteresse;
	//Esperienza formazione in ambito sanitario
	private File esperienzaFormazione;
	//Utilizzo di sedi, strutture ed attrezzature di altro soggetto
	private File utilizzo;
	//Sistema informatico dedicato alla formazione in ambito sanitario
	private File sistemaInformatico;
	//Piano di Qualità
	private File pianoQualita;
	//Dichiarazione del Legale Rappresentante
	private File dichiarazioneLegale;

	//Dichiarazione del Legale Rappresentante
	private File richiestaAccreditamentoStandard;
	//Dichiarazione del Legale Rappresentante
	private File relazioneAttivitaFormativa;

	/*** ALLEGATI FINE ***/

	public DatiAccreditamentoAudit(DatiAccreditamento datiAccreditamento) {
		this.id = datiAccreditamento.getId();
		this.datiEconomici = new DatiEconomiciAudit(datiAccreditamento.getDatiEconomici());

		for(Disciplina disc : datiAccreditamento.getDiscipline())
			this.discipline.add(disc.getNome());
		this.numeroDipendentiFormazioneAltro = datiAccreditamento.getNumeroDipendentiFormazioneAltro();
		this.numeroDipendentiFormazioneTempoIndeterminato = datiAccreditamento.getNumeroDipendentiFormazioneTempoIndeterminato();
		this.procedureFormative = datiAccreditamento.getProcedureFormative();
		this.professioniAccreditamento = datiAccreditamento.getProfessioniAccreditamento();
		for(Professione prof : datiAccreditamento.getProfessioniSelezionate())
			this.professioniSelezionate.add(prof.getNome());
		this.tipologiaAccreditamento = datiAccreditamento.getTipologiaAccreditamento();

		for(File file : datiAccreditamento.getFiles()) {
			if (file.getTipo() == FileEnum.FILE_ESTRATTO_BILANCIO_COMPLESSIVO) {
				this.estrattoBilancioComplessivo = file;
			} else if (file.getTipo() == FileEnum.FILE_ESTRATTO_BILANCIO_FORMAZIONE) {
				this.estrattoBilancioFormazione = file;
			} else if (file.getTipo() == FileEnum.FILE_ORGANIGRAMMA) {
				this.organigramma = file;
			} else if (file.getTipo() == FileEnum.FILE_FUNZIONIGRAMMA) {
				this.funzionigramma = file;
			} else if (file.getTipo() == FileEnum.FILE_ATTO_COSTITUTIVO) {
				this.attoCostitutivo = file;
			} else if (file.getTipo() == FileEnum.FILE_DICHIARAZIONE_ESCLUSIONE) {
				this.assenzaConflittiInteresse = file;
			} else if (file.getTipo() == FileEnum.FILE_ESPERIENZA_FORMAZIONE) {
				this.esperienzaFormazione = file;
			} else if (file.getTipo() == FileEnum.FILE_UTILIZZO) {
				this.utilizzo = file;
			} else if (file.getTipo() == FileEnum.FILE_SISTEMA_INFORMATICO) {
				this.sistemaInformatico = file;
			} else if (file.getTipo() == FileEnum.FILE_PIANO_QUALITA) {
				this.pianoQualita = file;
			} else if (file.getTipo() == FileEnum.FILE_DICHIARAZIONE_LEGALE) {
				this.dichiarazioneLegale = file;
			} else if(file.getTipo() == FileEnum.FILE_RICHIESTA_ACCREDITAMENTO_STANDARD) {
				this.richiestaAccreditamentoStandard = file;
			} else if (file.getTipo() == FileEnum.FILE_RELAZIONE_ATTIVITA_FORMATIVA) {
				this.relazioneAttivitaFormativa = file;
			}

		}
	}


	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DatiAccreditamentoAudit entitapiatta = (DatiAccreditamentoAudit) o;
        return Objects.equals(id, entitapiatta.id);
    }

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return id.intValue();
	}
}
