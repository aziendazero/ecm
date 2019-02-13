package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PianoFormativo extends BaseEntityDefaultId{
	@Column(name="anno_piano_formativo")
	private Integer annoPianoFormativo;

	//data per gestire le modifiche dei piani formativi annuali
	@Column(name="data_fine_modifica")
	private LocalDate dataFineModifca;

	@OneToOne(fetch = FetchType.LAZY)
	private Provider provider;

	@ManyToMany
	@OrderBy("id ASC")
	private Set<EventoPianoFormativo> eventiPianoFormativo = new HashSet<EventoPianoFormativo>();

	public void addEvento(EventoPianoFormativo evento, boolean nativo){
		this.getEventiPianoFormativo().add(evento);
		evento.setPianoFormativo(this.annoPianoFormativo);
		if(nativo)
			evento.setPianoFormativoNativo(this.annoPianoFormativo);

//		if(evento.getPianoFormativo() != null){
//			if(evento.getPianoFormativo().intValue() < this.annoPianoFormativo.intValue())
//				evento.setPianoFormativo(this.annoPianoFormativo);
//		}else
//			evento.setPianoFormativo(this.annoPianoFormativo);
	}

	public void removeEvento(long eventoId){
		this.getEventiPianoFormativo().removeIf(e -> e.getId() == eventoId);
	}

	public boolean isPianoModificabile(){
		if(dataFineModifca == null)
			return true;
		if( dataFineModifca.isAfter(LocalDate.now()) || dataFineModifca.isEqual(LocalDate.now()) )
			return true;
		return false;
	}
}
