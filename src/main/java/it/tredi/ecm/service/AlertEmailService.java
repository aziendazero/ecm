package it.tredi.ecm.service;

import java.time.LocalDateTime;
import java.util.Set;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.AlertEmail;
import it.tredi.ecm.dao.entity.QuotaAnnuale;
import it.tredi.ecm.dao.enumlist.AlertTipoEnum;

public interface AlertEmailService {
	public Set<AlertEmail> getAll();
	public Set<AlertEmail> getAllNotInviati();
	public void save(AlertEmail alertMail);

	public void creaAlertForProvider(Accreditamento accreditamento);
	public void creaAlertContributoAnnuoForProvider(QuotaAnnuale quota);
	public void inviaAlertsEmail() throws Exception;

	public void creaAlertRipetibiliAnnuali();

	public boolean checkIfExistForEvento(AlertTipoEnum tipo, Long eventoId, LocalDateTime dataScadenza);

}
