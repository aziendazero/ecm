package it.tredi.ecm.dao.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.tredi.ecm.cogeaps.CogeapsCaricaResponse;
import it.tredi.ecm.cogeaps.CogeapsStatoElaborazioneResponse;
import it.tredi.ecm.dao.enumlist.RendicontazioneInviataResultEnum;
import it.tredi.ecm.dao.enumlist.RendicontazioneInviataStatoEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RendicontazioneInviata extends BaseEntityDefaultId{
	private static final long serialVersionUID = 5942497014918512296L;

	@Column(name="data_invio")
	private LocalDateTime dataInvio;

	@OneToOne
	private Account accountInvio;

	@OneToOne
	private File fileRendicontazione;

	@Column(columnDefinition="text")
	private String response;//json risposta COGEAPS

	private String fileName;//cogeapsID

	@Enumerated(EnumType.STRING)
	private RendicontazioneInviataStatoEnum stato;//ENUM

	@Enumerated(EnumType.STRING)
	private RendicontazioneInviataResultEnum result;//ENUM

	@ManyToOne
	@JoinColumn(name = "evento_id")
	private Evento evento;

}
