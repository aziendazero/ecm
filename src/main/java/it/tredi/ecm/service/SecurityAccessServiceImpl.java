package it.tredi.ecm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.enumlist.RoleEnum;
import it.tredi.ecm.service.bean.CurrentUser;

@Service
public class SecurityAccessServiceImpl implements SecurityAccessService {
	
	@Autowired private ProviderService providerService;
	@Autowired private AccreditamentoService accreditamentoService;

	/**		PROVIDER	**/
	@Override
	public boolean canShowAllProvider(CurrentUser currentUser) {
		if(currentUser == null)
			return false;
		
		if(currentUser.hasRole(RoleEnum.PROVIDER_SHOW_ALL.name()))
			return true;
		
		return false;
	}
	
	@Override
	public boolean canShowProvider(CurrentUser currentUser, Long providerId) {
		if(canShowAllProvider(currentUser))
			return true;

		if(currentUser.hasRole(RoleEnum.PROVIDER_SHOW.name())){
			return isProviderOwner(currentUser.getAccount().getId(), providerId);
		}
		
		return false;
	}
	
	@Override
	public boolean canEditProvider(CurrentUser currentUser, Long providerId) {
		if(currentUser == null)
			return false;
		
		if(currentUser.hasRole(RoleEnum.PROVIDER_EDIT_ALL.name()))
			return true;
		
		if(currentUser.hasRole(RoleEnum.PROVIDER_EDIT.name())){
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
		
		if(currentUser.hasRole(RoleEnum.ACCREDITAMENTO_SHOW_ALL.name()))
			return true;
		
		return false;
	}
	
	@Override
	public boolean canShowAccreditamento(CurrentUser currentUser, Long accreditamentoId) {
		if(canShowAllAccreditamento(currentUser))
			return true;
		
		if(currentUser.hasRole(RoleEnum.ACCREDITAMENTO_SHOW.name())){
			return isAccreditamentoOwner(currentUser.getAccount().getId(), accreditamentoId);
		}
		
		return false;
	}
	
	@Override
	public boolean canEditAccreditamento(CurrentUser currentUser, Long accreditamentoId) {
		if(currentUser == null)
			return false;
		
		if(currentUser.hasRole(RoleEnum.ACCREDITAMENTO_EDIT_ALL.name()))
			return true;
		
		if(currentUser.hasRole(RoleEnum.ACCREDITAMENTO_EDIT.name())){
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
		
		if(currentUser.hasRole(RoleEnum.USER_SHOW_ALL.name()))
			return true;
		
		return false;
	}
	
	@Override
	public boolean canShowUser(CurrentUser currentUser, Long userId) {
		if(canShowAllUser(currentUser))
			return true;
		
		if(currentUser.hasRole(RoleEnum.USER_SHOW.name())){
			return isUserOwner(currentUser.getAccount().getId(), userId);
		}
		
		return false;
	}
	
	@Override
	public boolean canEditUser(CurrentUser currentUser, Long userId) {
		if(currentUser == null)
			return false;
		
		if(currentUser.hasRole(RoleEnum.USER_EDIT_ALL.name()))
			return true;
		
		if(currentUser.hasRole(RoleEnum.USER_EDIT.name())){
			return isUserOwner(currentUser.getAccount().getId(), userId);
		}
		
		return false;
	}
	
	@Override
	public boolean canCreateUser(CurrentUser currentUser) {
		if(currentUser == null)
			return false;
		
		if(currentUser.hasRole(RoleEnum.USER_CREATE.name()))
			return true;
		
		return false;
	}
	
	private boolean isUserOwner(Long currentUserAccountId, Long userId){
		return userId.equals(currentUserAccountId);
	}
}