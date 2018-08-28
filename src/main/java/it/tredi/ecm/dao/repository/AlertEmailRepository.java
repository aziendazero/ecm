package it.tredi.ecm.dao.repository;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.AlertEmail;
import it.tredi.ecm.dao.enumlist.AlertTipoEnum;

public interface AlertEmailRepository extends CrudRepository<AlertEmail, Long> {

	public Set<AlertEmail> findAll();
	public Set<AlertEmail> findAllByInviatoFalse();
	public AlertEmail findByTipoAndProviderIdAndDataScadenza(AlertTipoEnum tipo, Long providerId, LocalDateTime dataScadenza);
	public AlertEmail findByTipoAndEventoIdAndDataScadenza(AlertTipoEnum tipo, Long eventoId, LocalDateTime dataScadenza);
	public Set<AlertEmail> findAllByDataInvioIsNullAndDataScadenzaBefore(LocalDateTime localDateTime);
	public AlertEmail findByTipoAndProviderIdAndDataInvioIsNull(AlertTipoEnum tipo, Long providerId);
	public AlertEmail findByTipoAndEventoIdAndDataInvioIsNull(AlertTipoEnum tipo, Long eventoId);
}
