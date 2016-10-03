package it.tredi.ecm.dao.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RendicontazioneInviata extends BaseEntity{
	@Column(name="data_invio")	
	private LocalDateTime dataInvio;
	@OneToOne
	private Account accountInvio;
	@OneToOne
	private File fileRendicontazione;
	private String response;//json risposta COGEAPS
	private String fileName;//cogeapsID
	private String stato;//ENUM pending/finished
	private String result;//ENUM ok/ko
	
	@ManyToOne 
	@JoinColumn(name = "evento_id")
	private Evento evento;
}
