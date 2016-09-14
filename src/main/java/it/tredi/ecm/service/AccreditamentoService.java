package it.tredi.ecm.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import it.tredi.ecm.exception.AccreditamentoNotFoundException;
import it.tredi.ecm.service.bean.CurrentUser;

public interface AccreditamentoService{
	public Accreditamento getNewAccreditamentoForCurrentProvider(AccreditamentoTipoEnum tipoDomanda) throws Exception;
	public Accreditamento getNewAccreditamentoForProvider(Long providerId, AccreditamentoTipoEnum tipoDomanda) throws Exception;

	public Accreditamento getAccreditamento(Long id);
	public Set<Accreditamento> getAllAccreditamentiForProvider(Long providerId);
	public Set<Accreditamento> getAllAccreditamentiForProvider(Long providerId,AccreditamentoTipoEnum tipoDomanda);
	public Set<Accreditamento> getAccreditamentiAvviatiForProvider(Long providerId, AccreditamentoTipoEnum tipoTomanda);
	public Accreditamento getAccreditamentoAttivoForProvider(Long providerId) throws AccreditamentoNotFoundException;
	public AccreditamentoStatoEnum getStatoAccreditamento(Long accreditamentoId);

	public void save(Accreditamento accreditamento);

	public boolean canProviderCreateAccreditamento(Long providerId,AccreditamentoTipoEnum tipoTomanda);

	public void inviaDomandaAccreditamento(Long accreditamentoId);
	public void inserisciPianoFormativo(Long accreditamentoId);
	public void inviaValutazioneDomanda(Long accreditamentoId, String valutazioneComplessiva, Set<Account> refereeGroup) throws Exception;
	public void riassegnaGruppoCrecm(Long accreditamentoId, Set<Account> refereeGroup);
	public void assegnaStessoGruppoCrecm(Long accreditamentoId, String valutazioneComplessiva);
	public void presaVisione(Long accreditamentoId);
	public void inviaRichiestaIntegrazione(Long accreditamentoId);
	public void inviaIntegrazione(Long accreditamentoId);

	public DatiAccreditamento getDatiAccreditamentoForAccreditamento(Long accreditamentoId) throws Exception;
	public Long getProviderIdForAccreditamento(Long accreditamentoId);

	//Vaschetta segreteria
	public Set<Accreditamento> getAllAccreditamentiByStatoAndTipoDomanda(AccreditamentoStatoEnum stato,	AccreditamentoTipoEnum tipo, Boolean filterTaken);
	public int countAllAccreditamentiByStatoAndTipoDomanda(AccreditamentoStatoEnum stato, AccreditamentoTipoEnum tipo, Boolean filterTaken);

	public Set<Accreditamento> getAllAccreditamentiInScadenza();
	public int countAllAccreditamentiInScadenza();

	//Vaschetta generica
	public Set<Accreditamento> getAllAccreditamentiByStatoAndTipoDomandaForAccountId(AccreditamentoStatoEnum stato,	AccreditamentoTipoEnum tipo, Long id);
	public int countAllAccreditamentiByStatoAndTipoDomandaForAccountId(AccreditamentoStatoEnum stato, AccreditamentoTipoEnum tipo, Long id);

	public Set<Accreditamento> getAllAccreditamentiInviati();
	public int countAllAccreditamentiByStatoAndProviderId(AccreditamentoStatoEnum stato, Long providerId);
	public Set<Accreditamento> getAllAccreditamentiByStatoAndProviderId(AccreditamentoStatoEnum stato, Long providerId);

	//Controlli valutazione
	public boolean canUserPrendiInCarica(Long accreditamentoId, CurrentUser currentUser);
	public boolean canUserValutaDomanda(Long accreditamentoId, CurrentUser currentUser);
	public boolean canUserValutaDomandaShow(Long id, CurrentUser authenticatedUser);
	public boolean canUserValutaDomandaShowRiepilogo(Long accreditamentoId, CurrentUser currentUser);
	public boolean canRiassegnaGruppo(Long accreditamentoId, CurrentUser currentUser);
	public boolean canPresaVisione(Long accreditamentoId, CurrentUser currentUser);
	
	//RichiestaIntegrazione
	public boolean canUserInviaRichiestaIntegrazione(Long accreditamentoId, CurrentUser currentUser);
	public boolean canUserEnableField(CurrentUser currentUser);
}
