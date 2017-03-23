package it.tredi.ecm.audit.entity;

import java.util.Objects;

import org.javers.core.metamodel.annotation.Entity;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@TypeName("AccreditamentoAudit")
@Getter
@Setter
public class AccreditamentoAudit {
	@Id
	private Long id;

	private AccreditamentoTipoEnum tipoDomanda;

	private DatiAccreditamentoAudit datiAccreditamento;

	private PianoFormativoAudit pianoFormativo;

	//Chiedere se sono dati che cambiano per i quali occorre l'audit
//	private File NoteOsservazioniIntegrazione;
//	private File NoteOsservazioniPreavvisoRigetto;
//	private File decretoAccreditamento;
//	private File decretoDiniego;
//	private File richiestaIntegrazione;
//	private File richiestaPreavvisoRigetto;
//	private File verbaleValutazioneSulCampoPdf;
//	private File fileDecadenza;

	//TODO chiedere se serve
	//private VerbaleValutazioneSulCampo verbaleValutazioneSulCampo;


	public AccreditamentoAudit(Accreditamento accreditamento){
		this.id = accreditamento.getId();
		this.tipoDomanda = accreditamento.getTipoDomanda();
		if(accreditamento.getDatiAccreditamento() != null)
			this.datiAccreditamento = new DatiAccreditamentoAudit(accreditamento.getDatiAccreditamento());
		if(accreditamento.getPianoFormativo() != null)
			this.pianoFormativo = new PianoFormativoAudit(accreditamento.getPianoFormativo());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		AccreditamentoAudit entitapiatta = (AccreditamentoAudit) o;
		return Objects.equals(id, entitapiatta.id);
	}

	@Override
	public int hashCode() {
		return id.intValue();
	}
}
