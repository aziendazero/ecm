package it.tredi.ecm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.enumlist.RoleEnum;
import it.tredi.ecm.service.bean.CurrentUser;

@Service
public class SecurityAccessServiceImpl implements SecurityAccessService {
	
	@Autowired private ProviderService providerService;
	@Autowired private AccreditamentoService accreditamentoService;

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

	@Override
	public boolean canShowPersona(CurrentUser currentUser, Long personaId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canEditPersona(CurrentUser currentUser, Long personaId) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isProviderOwner(Long currentUserAccountId, Long providerId){
		Long accountId = providerService.getAccountIdForProvider(providerId);
		return accountId.equals(currentUserAccountId);
	}
	
	private boolean isAccreditamentoOwner(Long currentUserAccountId, Long accreditamentoId){
		Long providerId = accreditamentoService.getProviderIdForAccreditamento(accreditamentoId);
		return isProviderOwner(currentUserAccountId, providerId);
	}
	
}
