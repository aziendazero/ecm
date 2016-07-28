package it.tredi.ecm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.enumlist.RoleEnum;
import it.tredi.ecm.service.bean.CurrentUser;

@Service
public class SecurityAccessServiceImpl implements SecurityAccessService {
	
	@Autowired private ProviderService providerService;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private PianoFormativoService pianoFormativoService;

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
			return isProviderOwner(currentUser.getAccount().getId(), providerId);
		}
		
		return false;
	}
	
	@Override
	public boolean canEditProvider(CurrentUser currentUser, Long providerId) {
		if(canEditAllProvider(currentUser))
			return true;
		
		if(currentUser.hasRole(RoleEnum.PROVIDER_EDIT)){
			return isProviderOwner(currentUser.getAccount().getId(), providerId);
		}
		
		return false;
	}
	
	private boolean isProviderOwner(Long currentUserAccountId, Long providerId){
		Long accountId = providerService.getAccountIdForProvider(providerId);
		return accountId.equals(currentUserAccountId);
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
		return isProviderOwner(currentUserAccountId, providerId);
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
	public boolean canEditPianoFormativo(CurrentUser currentUser, Long providerId, Long pianoFormativoId) {
		if(!canEditProvider(currentUser, providerId))
			return false;
		
		if(canEditAllProvider(currentUser))
			return true;
		
		return pianoFormativoService.isEditabile(pianoFormativoId);
	}
	
}
