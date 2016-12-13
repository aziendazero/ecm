package it.tredi.ecm.service;

import java.time.LocalDateTime;
import java.util.Set;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.AlertEmail;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.QuotaAnnuale;

public interface AlertEmailService {
	public Set<AlertEmail> getAll();
	public Set<AlertEmail> getAllNotInviati();
	public void save(AlertEmail alertMail);

	public void creaAlertForProvider(Accreditamento accreditamento);
	public void creaAlertInvioDomandaStandardForProvider(Provider provider);
	public void creaAlertContributoAnnuoForProvider(QuotaAnnuale quota);
	public void creaAlertForReferee(Set<Account> refereeGroup, Provider provider, LocalDateTime dataScadenza);

	public void creaAlertForEvento(Evento evento);

	public void creaAlertRipetibiliAnnuali();
	public void inviaAlertsEmail() throws Exception;

}
