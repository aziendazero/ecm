package it.tredi.ecm.service;

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
	public boolean canProviderCreateAccreditamento(Long providerId,AccreditamentoTipoEnum tipoTomanda);
	public Accreditamento getNewAccreditamentoForCurrentProvider(AccreditamentoTipoEnum tipoDomanda) throws Exception;
	public Accreditamento getNewAccreditamentoForProvider(Long providerId, AccreditamentoTipoEnum tipoDomanda) throws Exception;

	public Accreditamento getAccreditamento(Long id);
	public Set<Accreditamento> getAllAccreditamentiForProvider(Long providerId);
	public Set<Accreditamento> getAllAccreditamentiForProvider(Long providerId,AccreditamentoTipoEnum tipoDomanda);
	public Set<Accreditamento> getAccreditamentiAvviatiForProvider(Long providerId, AccreditamentoTipoEnum tipoTomanda);
	public Accreditamento getAccreditamentoAttivoForProvider(Long providerId) throws AccreditamentoNotFoundException;
	public AccreditamentoStatoEnum getStatoAccreditamento(Long accreditamentoId);

	//azioni sulla domanda che corrsipondono ad avanzamenti del flusso
	public void save(Accreditamento accreditamento);
	public void inserisciPianoFormativo(Long accreditamentoId);

	public void inviaDomandaAccreditamento(Long accreditamentoId) throws Exception;
	public void prendiInCarica(Long accreditamentoId, CurrentUser currentUser) throws Exception;
	public void inviaValutazioneDomanda(Long accreditamentoId, String valutazioneComplessiva, Set<Account> refereeGroup) throws Exception;
	public void riassegnaGruppoCrecm(Long accreditamentoId, Set<Account> refereeGroup) throws Exception;
	public void inserisciInValutazioneCommissione(Long accreditamentoId, CurrentUser curentUser) throws Exception;
	public void inviaValutazioneCommissione(Long accreditamentoId, CurrentUser curentUser, AccreditamentoStatoEnum stato) throws Exception;
	public void inviaRichiestaIntegrazione(Long accreditamentoId, Long giorniTimer) throws Exception;
	public void inviaRichiestaPreavvisoRigetto(Long accreditamentoId, Long giorniTimer) throws Exception;
	public void inviaIntegrazione(Long accreditamentoId) throws Exception;
	public void presaVisione(Long accreditamentoId) throws Exception;
	public void rivaluta(Long accreditamentoId);
	public void assegnaStessoGruppoCrecm(Long accreditamentoId, String valutazioneComplessiva) throws Exception;

	//modifica??
	//inserisciPianoFormativo
	//invia domanda
	public boolean canUserPrendiInCarica(Long accreditamentoId, CurrentUser currentUser) throws Exception;
	public boolean canUserValutaDomanda(Long accreditamentoId, CurrentUser currentUser) throws Exception;
	public boolean canUserValutaDomandaShow(Long id, CurrentUser authenticatedUser);
	public boolean canUserValutaDomandaShowRiepilogo(Long accreditamentoId, CurrentUser currentUser);
	public boolean canUserInviaAValutazioneCommissione(Long accreditamentoId, CurrentUser currentUser) throws Exception;

	public boolean canUserInserisciValutazioneCommissione(Long accreditamentoId, CurrentUser currentUser) throws Exception;

	public boolean canRiassegnaGruppo(Long accreditamentoId, CurrentUser currentUser);
	public boolean canUserInviaRichiestaIntegrazione(Long accreditamentoId, CurrentUser currentUser) throws Exception;
	public boolean canUserInviaIntegrazione(Long accreditamentoId, CurrentUser currentUser) throws Exception;
	public boolean canUserPresaVisione(Long accreditamentoId, CurrentUser currentUser) throws Exception;

	public void changeState(Long accreditamentoId, AccreditamentoStatoEnum stato) throws Exception;
	public void changeState(Long accreditamentoId, AccreditamentoStatoEnum stato, Boolean eseguitoDaUtente) throws Exception;
	public void approvaIntegrazione(Long accreditamentoId) throws Exception;

	public DatiAccreditamento getDatiAccreditamentoForAccreditamento(Long accreditamentoId) throws Exception;
	public Long getProviderIdForAccreditamento(Long accreditamentoId);

	//Vaschetta segreteria
	public Set<Accreditamento> getAllAccreditamentiByStatoAndTipoDomanda(AccreditamentoStatoEnum stato,	AccreditamentoTipoEnum tipo, Boolean filterTaken);
	public int countAllAccreditamentiByStatoAndTipoDomanda(AccreditamentoStatoEnum stato, AccreditamentoTipoEnum tipo, Boolean filterTaken);

	public Set<Accreditamento> getAllAccreditamentiInseribiliInODG();
	public int countAllAccreditamentiInseribiliInODG();

	public Set<Accreditamento> getAllAccreditamentiInScadenza();
	public int countAllAccreditamentiInScadenza();

	//Vaschetta Referee
	public Set<Accreditamento> getAllAccreditamentiByStatoAndTipoDomandaForValutatoreId(AccreditamentoStatoEnum stato,	AccreditamentoTipoEnum tipo, Long refereeId, Boolean filterDone);
	public int countAllAccreditamentiByStatoAndTipoDomandaForValutatoreId(AccreditamentoStatoEnum stato, AccreditamentoTipoEnum tipo, Long refereeId, Boolean filterDone);

	//Vaschetta Provider
	public Set<Accreditamento> getAllAccreditamentiByStatoAndTipoDomandaForProviderId(AccreditamentoStatoEnum stato, AccreditamentoTipoEnum tipoByNome, Long providerId);
	public int countAllAccreditamentiByStatoAndTipoDomandaForProviderId(AccreditamentoStatoEnum stato, AccreditamentoTipoEnum tipoByNome, Long providerId);

	public Set<Accreditamento> getAllAccreditamentiInviati();
	public int countAllAccreditamentiByStatoAndProviderId(AccreditamentoStatoEnum stato, Long providerId);
	public Set<Accreditamento> getAllAccreditamentiByStatoAndProviderId(AccreditamentoStatoEnum stato, Long providerId);
	boolean canUserEnableField(CurrentUser currentUser, Long accreditamentoId) throws Exception;

	public void saveFileNoteOsservazioni(Long fileId, Long accreditamentoId);
	public Set<Accreditamento> getAllDomandeNonValutateByRefereeId(Long refereeId);
}
