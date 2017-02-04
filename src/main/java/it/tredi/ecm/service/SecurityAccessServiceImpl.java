package it.tredi.ecm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.PianoFormativo;
import it.tredi.bonita.api.model.TaskInstanceDataModel;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.enumlist.RoleEnum;
import it.tredi.ecm.service.bean.CurrentUser;

@Service
public class SecurityAccessServiceImpl implements SecurityAccessService {

	@Autowired private ProviderService providerService;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private PianoFormativoService pianoFormativoService;
	@Autowired private WorkflowService workflowService;
	@Autowired private AccountService accountService;
	@Autowired private EventoService eventoService;

	/**		PROVIDER	**/
	@Override
	public boolean canShowAllProvider(CurrentUser currentUser) {
		if(currentUser == null)
			return false;

		if(currentUser.hasRole(RoleEnum.PROVIDER_SHOW_ALL))
			return true;

		return false;
	}

	@Override
	public boolean canEditAllProvider(CurrentUser currentUser) {
		if(currentUser == null)
			return false;

		if(currentUser.hasRole(RoleEnum.PROVIDER_EDIT_ALL))
			return true;

		return false;
	}

	@Override
	public boolean canShowProvider(CurrentUser currentUser, Long providerId) {
		if(canShowAllProvider(currentUser))
			return true;

		if(currentUser.hasRole(RoleEnum.PROVIDER_SHOW)){
			return isProviderUser(currentUser.getAccount().getId(), providerId);
		}

		return false;
	}

	@Override
	public boolean canEditProvider(CurrentUser currentUser, Long providerId) {
		if(canEditAllProvider(currentUser))
			return true;

		if(currentUser.hasRole(RoleEnum.PROVIDER_EDIT)){
			return isProviderUser(currentUser.getAccount().getId(), providerId);
		}

		return false;
	}

	private boolean isProviderUser(Long currentUserAccountId, Long providerId){
		Long accountProviderId = accountService.getProviderIdById(currentUserAccountId);
		return providerId.equals(accountProviderId);
	}

	/**		ACCREDITAMENTO	**/
	@Override
	public boolean canShowAllAccreditamento(CurrentUser currentUser) {
		if(currentUser == null)
			return false;

		if(currentUser.hasRole(RoleEnum.ACCREDITAMENTO_SHOW_ALL))
			return true;

		return false;
	}

	@Override
	public boolean canShowAccreditamento(CurrentUser currentUser, Long accreditamentoId) {
		if(canShowAllAccreditamento(currentUser))
			return true;

		if(currentUser.hasRole(RoleEnum.ACCREDITAMENTO_SHOW)){
			return isAccreditamentoOwner(currentUser.getAccount().getId(), accreditamentoId);
		}

		return false;
	}

	@Override
	public boolean canEditAccreditamento(CurrentUser currentUser, Long accreditamentoId) {
		if(currentUser == null)
			return false;

		if(currentUser.hasRole(RoleEnum.ACCREDITAMENTO_EDIT_ALL))
			return true;

		if(currentUser.hasRole(RoleEnum.ACCREDITAMENTO_EDIT)){
			return isAccreditamentoOwner(currentUser.getAccount().getId(), accreditamentoId);
		}

		return false;
	}

	private boolean isAccreditamentoOwner(Long currentUserAccountId, Long accreditamentoId){
		Long providerId = accreditamentoService.getProviderIdForAccreditamento(accreditamentoId);
		return isProviderUser(currentUserAccountId, providerId);
	}

	/**		USER	**/
	@Override
	public boolean canShowAllUser(CurrentUser currentUser) {
		if(currentUser == null)
			return false;

		if(currentUser.hasRole(RoleEnum.USER_SHOW_ALL))
			return true;

		return false;
	}

	//Controlla se l'utente corrente può modificare l'utente di un dato provider
	@Override
	public boolean canProviderEditUser(CurrentUser currentUser, Long providerId, Long userId) {
		if(currentUser == null)
			return false;

		if(currentUser.hasRole(RoleEnum.PROVIDER_USER_EDIT)) {
			//Controllo se il providerId su cui si sta operando corrisponde al provider dell'utente corrente
			if(currentUser.getAccount().getProvider() != null && currentUser.getAccount().getProvider().getId().equals(providerId)) {
				//Controllo se l'utente in modifica userId fda parte del provider
				return isProviderUser(userId, providerId);
			}
		}

		return false;
	}

	//Controlla se l'utente corrente può visualizzare la lista degli utenti di un dato provider
	@Override
	public boolean canShowAllProviderUser(CurrentUser currentUser, Long providerId) {
		if(currentUser == null)
			return false;

		if(currentUser.hasRole(RoleEnum.PROVIDER_USER_SHOW)) {
			if(currentUser.getAccount().getProvider() != null && currentUser.getAccount().getProvider().getId().equals(providerId))
				return true;
		}

		return false;
	}

	//Controlla se l'utente corrente può inserire un nuovo utente di un dato provider
	@Override
	public boolean canProviderCreateUser(CurrentUser currentUser, Long providerId) {
		if(currentUser == null)
			return false;

		if(currentUser.hasRole(RoleEnum.PROVIDER_USER_CREATE)) {
			if(currentUser.getAccount().getProvider() != null && currentUser.getAccount().getProvider().getId().equals(providerId))
				return true;
		}

		return false;
	}


	@Override
	public boolean canShowUser(CurrentUser currentUser, Long userId) {
		if(canShowAllUser(currentUser))
			return true;

		if(currentUser.hasRole(RoleEnum.USER_SHOW)){
			return isUserOwner(currentUser.getAccount().getId(), userId);
		}

		return false;
	}

	@Override
	public boolean canEditUser(CurrentUser currentUser, Long userId) {
		if(currentUser == null)
			return false;

		if(currentUser.hasRole(RoleEnum.USER_EDIT_ALL))
			return true;

		if(currentUser.hasRole(RoleEnum.USER_EDIT)){
			return isUserOwner(currentUser.getAccount().getId(), userId);
		}

		return false;
	}

	@Override
	public boolean canCreateUser(CurrentUser currentUser) {
		if(currentUser == null)
			return false;

		if(currentUser.hasRole(RoleEnum.USER_CREATE))
			return true;

		return false;
	}

	private boolean isUserOwner(Long currentUserAccountId, Long userId){
		return userId.equals(currentUserAccountId);
	}

	/*	PIANO FORMATIVO	*/
	@Override
	public boolean canInsertPianoFormativo(CurrentUser currentUser, Long providerId) {
		if(!canEditProvider(currentUser, providerId))
			return false;

		if(canEditAllProvider(currentUser))
			return true;

		return providerService.canInsertPianoFormativo(providerId);
	}

	@Override
	public boolean canEditPianoFormativo(CurrentUser currentUser, Long pianoFormativoId) {
		PianoFormativo pianoFormativo = pianoFormativoService.getPianoFormativo(pianoFormativoId);

		if(!canEditProvider(currentUser, pianoFormativo.getProvider().getId()))
			return false;

		if(canEditAllProvider(currentUser))
			return true;

		return pianoFormativoService.isPianoModificabile(pianoFormativoId);
	}

	/*	FILE */
	@Override
	public boolean canShowFile(CurrentUser currentUser, Long fileId){
		//TODO
		return true;
	}

	@Override
	public boolean canPrendiInCaricaAccreditamento(CurrentUser currentUser, Long accreditamentoId) throws Exception{
		return accreditamentoService.canUserPrendiInCarica(accreditamentoId, currentUser);
	}

	@Override
	public boolean canValidateAccreditamento(CurrentUser currentUser, Long accreditamentoId, Boolean showRiepilogo) throws Exception {
		if (showRiepilogo != null && showRiepilogo == true) {
			return (accreditamentoService.canUserValutaDomandaShowRiepilogo(accreditamentoId, currentUser) || accreditamentoService.canUserValutaDomandaShow(accreditamentoId, currentUser));
		}
		else
			return (accreditamentoService.canUserValutaDomanda(accreditamentoId, currentUser) || accreditamentoService.canUserValutaDomandaShow(accreditamentoId, currentUser));
	}

	@Override
	public boolean canValidateAccreditamento(CurrentUser currentUser, Long accreditamentoId) throws Exception {
		return canValidateAccreditamento(currentUser, accreditamentoId, false);
	}

	@Override
	public boolean canEnableField(CurrentUser currentUser, Long accreditamentoId) throws Exception {
		return accreditamentoService.canUserEnableField(accreditamentoId, currentUser) || accreditamentoService.canUserAbilitaVariazioneDati(accreditamentoId, currentUser);
	}

	@Override
	public boolean canShowGruppo(CurrentUser currentUser, String gruppo) {
		if(gruppo == null || gruppo.isEmpty())
			return false;

		if(!currentUser.isProvider())
			return true;
		else
			return false;
	}

	@Override
	public boolean canSendIntegrazione(CurrentUser currentUser, Long accreditamentoId) throws Exception{
		return accreditamentoService.canUserInviaIntegrazione(accreditamentoId, currentUser) || accreditamentoService.canUserInviaVariazioneDati(accreditamentoId, currentUser);
	}

	@Override
	public boolean canUserPresaVisione(CurrentUser currentUser, Long accreditamentoId) throws Exception {
		return accreditamentoService.canUserPresaVisione(accreditamentoId, currentUser);
	}

	@Override
	public boolean canShowSeduta(CurrentUser currentUser) {
		if(currentUser.isSegreteria() || currentUser.isCommissioneEcm())
			return true;
		return false;
	}

	@Override
	public boolean canEditSeduta(CurrentUser currentUser) {
		if(currentUser.isSegreteria())
			return true;
		return false;
	}

	@Override
	public boolean canShowInScadenza(CurrentUser currentUser) {
		if(currentUser.isSegreteria())
			return true;
		return false;
	}

	//controlla se l'utente corrente può riassegnare i referee
	@Override
	public boolean canReassignCRECM(CurrentUser currentUser, Long accreditamentoId) throws Exception {
		Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);
		TaskInstanceDataModel task = workflowService.currentUserGetTaskForState(accreditamento);
		if(accreditamento.isAssegnamento() && currentUser.isSegreteria() && task != null)
			return true;
		return false;
	}

	@Override
	public boolean canShowAllEventi(CurrentUser currentUser) {
		if(currentUser.isSegreteria())
			return true;
		return false;
	}

	@Override
	public boolean canShowAllEventiProvider(CurrentUser currentUser, Long providerId) {
		if (isProviderUser(currentUser.getAccount().getId(), providerId) || currentUser.isSegreteria())
			return true;
		return false;
	}

	@Override
	public boolean canCreateEvento(CurrentUser currentUser, Long providerId) {
		if ((isProviderUser(currentUser.getAccount().getId(), providerId) && currentUser.isProvider()) || currentUser.isSegreteria())
			return true;
		return false;
	}

	@Override
	public boolean canDeleteEvento(CurrentUser currentUser, Long providerId) {
		if ((isProviderUser(currentUser.getAccount().getId(), providerId) && currentUser.isProvider()) || currentUser.isSegreteria())
			return true;
		return false;
	}

	@Override
	public boolean canEditEvento(CurrentUser currentUser, Long providerId) {
		if ((isProviderUser(currentUser.getAccount().getId(), providerId) && currentUser.isProvider()) || currentUser.isSegreteria())
			return true;
		return false;
	}

	@Override
	public boolean canSendComunicazioni(CurrentUser currentUser) {
		if (currentUser.isProvider() || currentUser.isSegreteria())
			return true;
		return false;
	}

	@Override
	public boolean canShowAnagrafeRegionale(CurrentUser currentUser) {
		if (currentUser.isSegreteria())
			return true;
		return false;
	}

	@Override
	public boolean canAllegaSponsorEvento(CurrentUser currentUser, Long eventoId) {
		if(currentUser.isSegreteria())
			return true;
		Evento evento = eventoService.getEvento(eventoId);
		if(currentUser.isProvider()
				&& currentUser.getAccount().getProvider().equals(evento.getProvider())
				&& evento.canDoUploadSponsor())
			return true;
		return false;
	}

	@Override
	public boolean canShowProtocollo(CurrentUser currentUser) {
		if(currentUser.isSegreteria())
			return true;
		return false;
	}

	@Override
	public boolean canEditVerbaleAccreditamento(CurrentUser currentUser, Long accreditamentoId) {
		if(currentUser.isSegreteria()){
			Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);
			if(accreditamento.isValutazioneSulCampo())
				return true;
		}
		return false;
	}
}
