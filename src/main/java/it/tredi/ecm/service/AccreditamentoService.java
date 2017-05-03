package it.tredi.ecm.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Set;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.AccreditamentoDiff;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamentoDiff;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.PersonaDiff;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.ProviderDiff;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.entity.SedeDiff;
import it.tredi.ecm.dao.entity.Seduta;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.entity.VerbaleValutazioneSulCampo;
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
	public void saveAndAudit(Accreditamento accreditamento);
	public void audit(Accreditamento accreditamento);
	public void inserisciPianoFormativo(Long accreditamentoId);

	public void inviaDomandaAccreditamento(Long accreditamentoId) throws Exception;
	public void prendiInCarico(Long accreditamentoId, CurrentUser currentUser) throws Exception;
	public void inviaValutazioneDomanda(Long accreditamentoId, String valutazioneComplessiva, Set<Account> refereeGroup, VerbaleValutazioneSulCampo verbale) throws Exception;
	public void riassegnaGruppoCrecm(Long accreditamentoId, Set<Account> refereeGroup) throws Exception;
	public void inserisciInValutazioneCommissioneForSystemUser(Long accreditamentoId) throws Exception;
	public void inviaValutazioneCommissione(Seduta seduta, Long accreditamentoId, AccreditamentoStatoEnum stato) throws Exception;

	public void inviaRichiestaIntegrazione(Long accreditamentoId, Long giorniTimer) throws Exception;
	public void inviaRichiestaIntegrazioneInAttesaDiFirma(Long accreditamentoId, File fileFirmato) throws Exception;

	public void inviaAccreditamentoInAttesaDiFirma(Long accreditamentoId, File fileFirmato) throws Exception;
	public void inviaDiniegoInAttesaDiFirma(Long accreditamentoId, File fileFirmato) throws Exception;

	public void inviaRichiestaPreavvisoRigetto(Long accreditamentoId, Long giorniTimer) throws Exception;
	public void inviaRichiestaPreavvisoRigettoInAttesaDiFirma(Long accreditamentoId, File fileFirmato) throws Exception;

	public void inviaIntegrazione(Long accreditamentoId) throws Exception;
	public void eseguiTaskInviaIntegrazione(Long accreditamentoId) throws Exception;
	public void presaVisione(Long accreditamentoId) throws Exception;
	public void rivaluta(Long accreditamentoId);
	public void assegnaStessoGruppoCrecm(Long accreditamentoId, String valutazioneComplessiva) throws Exception;

	public void assegnaTeamLeader(Long accreditamentoId, String valutazioneComplessiva) throws Exception;
	//modifica??
	//inserisciPianoFormativo
	//invia domanda
	public boolean canUserPrendiInCarica(Long accreditamentoId, CurrentUser currentUser) throws Exception;
	public boolean canUserValutaDomanda(Long accreditamentoId, CurrentUser currentUser) throws Exception;
	public boolean canUserValutaDomandaShow(Long accreditamentoId, CurrentUser currentUser);
	public boolean canUserValutaDomandaShowRiepilogo(Long accreditamentoId, CurrentUser currentUser);
	public boolean canUserInviaAValutazioneCommissione(Long accreditamentoId, CurrentUser currentUser) throws Exception;

	public boolean canUserInserisciValutazioneCommissione(Long accreditamentoId, CurrentUser currentUser) throws Exception;

	public boolean canRiassegnaGruppo(Long accreditamentoId, CurrentUser currentUser);
	public boolean canUserInviaRichiestaIntegrazione(Long accreditamentoId, CurrentUser currentUser) throws Exception;
	public boolean canUserinviaRichiestaIntegrazioneInAttesaDiFirma(Long accreditamentoId, CurrentUser currentUser) throws Exception;
	public boolean canUserinviaRichiestaPreavvisoRigettoInAttesaDiFirma(Long accreditamentoId, CurrentUser currentUser) throws Exception;
	public boolean canUserInviaIntegrazione(Long accreditamentoId, CurrentUser currentUser) throws Exception;
	public boolean canUserPresaVisione(Long accreditamentoId, CurrentUser currentUser) throws Exception;
	public boolean canUserAbilitaVariazioneDati(Long accreditamentoId, CurrentUser currentUser);
	public boolean canUserEnableField(Long accreditamentoId, CurrentUser currentUser) throws Exception;
	public boolean canUserInviaCampiVariazioneDati(Long accreditamentoId, CurrentUser currentUser);
	public boolean canUserInviaVariazioneDati(Long accreditamentoId, CurrentUser currentUser);
	public boolean canUserValutaDomandaShowStorico(Long accreditamentoId, CurrentUser currentUser);

	public boolean canUserAccreditatoInAttesaDiFirma(Long accreditamentoId, CurrentUser currentUser) throws Exception;
	public boolean canUserDiniegoInAttesaDiFirma(Long accreditamentoId, CurrentUser currentUser) throws Exception;

	public void changeState(Long accreditamentoId, AccreditamentoStatoEnum stato) throws Exception;
	public void changeState(Long accreditamentoId, AccreditamentoStatoEnum stato, Boolean eseguitoDaUtente) throws Exception;
	public void approvaIntegrazione(Long accreditamentoId) throws Exception;

	public DatiAccreditamento getDatiAccreditamentoForAccreditamentoId(Long accreditamentoId) throws Exception;
	public Long getProviderIdForAccreditamento(Long accreditamentoId);

	//Vaschetta segreteria
	public Set<Accreditamento> getAllAccreditamentiByStatoAndTipoDomanda(AccreditamentoStatoEnum stato,	AccreditamentoTipoEnum tipo, Boolean filterTaken);
	public int countAllAccreditamentiByStatoAndTipoDomanda(AccreditamentoStatoEnum stato, AccreditamentoTipoEnum tipo, Boolean filterTaken);

	public Set<Accreditamento> getAllAccreditamentiByGruppoAndTipoDomanda(String gruppo, AccreditamentoTipoEnum tipo, Boolean filterTaken);
	public int countAllAccreditamentiByGruppoAndTipoDomanda(String gruppo, AccreditamentoTipoEnum tipo, Boolean filterTaken);

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

	public void saveFileNoteOsservazioni(Long fileId, Long accreditamentoId);
	public Set<Accreditamento> getAllDomandeNonValutateByRefereeId(Long refereeId);
	public void inviaValutazioneSulCampoStandard(Long accreditamentoId, String valutazioneComplessiva, File verbaleFirmatoPdf, AccreditamentoStatoEnum destinazioneStatoDomandaStandard, File allegato1, File allegato2, File allegato3) throws Exception;
	public void saveSottoscriventeVerbaleValutazioneSulCampo(Accreditamento accreditamento, VerbaleValutazioneSulCampo verbaleNew);
	public void editScheduleVerbaleValutazioneSulCampo(Accreditamento accreditamento, VerbaleValutazioneSulCampo verbaleNew);

	public void settaStatusProviderAndDateAccreditamentoAndQuotaAnnuale(LocalDate dataSeduta, Long accreditamentoId, AccreditamentoStatoEnum stato) throws Exception;

	public void inviaEmailConvocazioneValutazioneSulCampo(Long accreditamentoId) throws Exception;
	public void inviaValutazioneTeamLeaderStandard(Long accreditamentoId, String valutazioneComplessiva) throws Exception;
	public void avviaFlussoVariazioneDati(Accreditamento accreditamento) throws Exception;
	public void inviaCampiSbloccatiVariazioneDati(Long accreditamentoId) throws Exception;
	public void inviaValutazioneVariazioneDati(Long accreditamentoId, String valutazioneComplessiva, AccreditamentoStatoEnum destinazioneVariazioneDati, Account refereeVariazioneDati) throws Exception;
	public void conclusioneProcedimento(Accreditamento accreditamento, CurrentUser currentUser) throws Exception;
	public Accreditamento getLastAccreditamentoForProviderId(Long providerId);

	public String[] controllaValidazioneIntegrazione(Long accreditamentoId) throws Exception;

	//post refactoring
	public void inviaValutazioneSegreteriaAssegnamentoProvvisorio(Long accreditamentoId, String valutazioneComplessiva, Set<Account> referee) throws Exception;
	public void inviaValutazioneCrecmProvvisorio(Long accreditamentoId, String valutazioneComplessiva) throws Exception;
	public void inviaValutazioneSegreteriaProvvisorio(Long accreditamentoId, String valutazioneComplessiva) throws Exception;
	public void inviaValutazioneSegreteriaAssegnamentoStandard(Long accreditamentoId, String valutazioneComplessiva, VerbaleValutazioneSulCampo verbale) throws Exception;
	public void inviaValutazioneSegreteriaStandard(Long accreditamentoId, String valutazioneComplessiva) throws Exception;
	public void inviaValutazioneSegreteriaVariazioneDati(Long accreditamentoId, String valutazioneComplessiva, AccreditamentoStatoEnum destinazioneVariazioneDati, Account refereeVariazioneDati) throws Exception;
	public void inviaValutazioneCrecmVariazioneDati(Long accreditamentoId, String valutazioneComplessiva) throws Exception;

	public void generaDecretoDecadenza(ByteArrayOutputStream byteArrayOutputStreamAccreditata, Long providerId) throws Exception;

	public boolean canRiassegnaRefereeVariazioneDati(Long accreditamentoId, CurrentUser currentUser);
}
