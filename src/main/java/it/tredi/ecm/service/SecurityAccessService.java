package it.tredi.ecm.service;

import it.tredi.ecm.service.bean.CurrentUser;

public interface SecurityAccessService {
	public boolean canShowProvider(CurrentUser currentUser, Long providerId);
	public boolean canShowAllProvider(CurrentUser currentUser);
	public boolean canEditProvider(CurrentUser currentUser, Long providerId);
	public boolean canEditAllProvider(CurrentUser currentUser);

	public boolean canShowAccreditamento(CurrentUser currentUser, Long providerId);
	public boolean canShowAllAccreditamento(CurrentUser currentUser);
	public boolean canEditAccreditamento(CurrentUser currentUser, Long providerId);

	public boolean canShowUser(CurrentUser currentUser, Long userId);
	public boolean canShowAllUser(CurrentUser currentUser);
	public boolean canEditUser(CurrentUser currentUser, Long userId);
	public boolean canCreateUser(CurrentUser currentUser);

	public boolean canInsertPianoFormativo(CurrentUser currentUser, Long providerId);
	public boolean canEditPianoFormativo(CurrentUser currentUser, Long pianoFormativoId);

	public boolean canShowFile(CurrentUser currentUser, Long fileId);

	public boolean canPrendiInCaricaAccreditamento(CurrentUser currentUser, Long accreditamentoId) throws Exception;
	public boolean canValidateAccreditamento(CurrentUser currentUser, Long accreditamentoId) throws Exception;

	public boolean canEnableField(CurrentUser currentUser, Long accreditamentoId) throws Exception;
	public boolean canSendIntegrazione(CurrentUser currentUser, Long accreditamentoId) throws Exception;
	public boolean canUserPresaVisione(CurrentUser currentUser, Long accreditamentoId) throws Exception;

	public boolean canShowGruppo(CurrentUser currentUser, String gruppo);

	public boolean canShowSeduta(CurrentUser currentUser);
	public boolean canEditSeduta(CurrentUser currentUser);
	public boolean canValidateAccreditamento(CurrentUser currentUser, Long accreditamentoId, Boolean showRiepilogo) throws Exception;

	public boolean canShowInScadenza(CurrentUser currentUser);

	public boolean canReassignCRECM(CurrentUser currentUser, Long accreditamentoId) throws Exception;

	public boolean canShowAllEventi(CurrentUser currentUser);
	public boolean canShowAllEventiProvider(CurrentUser currentUser, Long providerId);
	public boolean canCreateEvento(CurrentUser currentUser, Long providerId);
	public boolean canShowAllProviderUser(CurrentUser currentUser, Long providerId);
	boolean canProviderEditUser(CurrentUser currentUser, Long providerId, Long userId);
	boolean canProviderCreateUser(CurrentUser currentUser, Long providerId);

	public boolean canSendComunicazioni(CurrentUser currentUser);

	public boolean canShowAnagrafeRegionale(CurrentUser currentUser);

	public boolean canAllegaSponsorEvento(CurrentUser currentUser, Long eventoId);

	public boolean canShowProtocollo(CurrentUser currentUser);
}
