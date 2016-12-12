package it.tredi.ecm.dao.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import org.jvnet.hk2.config.Element;

import it.tredi.ecm.dao.enumlist.AlertTipoEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AlertEmail extends BaseEntity{

	@Enumerated(EnumType.STRING)
	private AlertTipoEnum tipo;
	@Column(name="data_scadenza")
	private LocalDateTime dataScadenza;
	@ManyToOne
	private Provider provider;
	@ManyToOne
	private Evento evento;

	@ElementCollection
	private Set<String> destinatari = new HashSet<String>();
	@Column(name="data_invio")
	private LocalDateTime dataInvio;
	private Boolean inviato;
}
