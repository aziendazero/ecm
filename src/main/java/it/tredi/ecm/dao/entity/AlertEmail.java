package it.tredi.ecm.dao.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;

import it.tredi.ecm.dao.enumlist.AlertTipoEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AlertEmail extends BaseEntity{

	private AlertTipoEnum tipo;
	private LocalDateTime dataScadenza;
	private Provider provider;
	private Evento evento;

	private Set<String> destinatari = new HashSet<String>();
	private LocalDateTime dataInvio;
	private Boolean inviato;
}
