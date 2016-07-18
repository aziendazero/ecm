package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PianoFormativo extends BaseEntity{
	@Column(name="anno_piano_formativo")
	private Integer annoPianoFormativo;
	
	//data per gestire le modifiche dei piani formativi annuali
	@Column(name="data_fine_modifica")
	private LocalDate dataFineModifca;
	
	//flag di modifica per gestire le modifiche del piano formativo inserito nella domanda di accreditamento
	private boolean editabile = false;
	
	@OneToOne(fetch = FetchType.LAZY)
	private Provider provider;

	@ManyToMany
	private Set<Evento> eventi = new HashSet<Evento>();

	public void addEvento(Evento evento){
		this.getEventi().add(evento);
		if(evento.getPianoFormativo() != null){
			if(evento.getPianoFormativo().intValue() < this.annoPianoFormativo.intValue())
				evento.setPianoFormativo(this.annoPianoFormativo);
		}else
			evento.setPianoFormativo(this.annoPianoFormativo);
	}
	
	public boolean isPianoModificabile(){
		if(isEditabile())
			return true;
		
		if(dataFineModifca == null)
			return true;
		if(dataFineModifca.isBefore(LocalDate.now()) || dataFineModifca.isEqual(LocalDate.now()))
			return true;
		return false;
	}
}
