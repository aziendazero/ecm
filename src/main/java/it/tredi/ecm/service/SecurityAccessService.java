package it.tredi.ecm.service;

import it.tredi.ecm.service.bean.CurrentUser;

public interface SecurityAccessService {
	public boolean canShowProvider(CurrentUser currentUser, Long providerId);
	public boolean canShowAllProvider(CurrentUser currentUser);
	public boolean canEditProvider(CurrentUser currentUser, Long providerId);
	
	public boolean canShowAccreditamento(CurrentUser currentUser, Long providerId);
	public boolean canShowAllAccreditamento(CurrentUser currentUser);
	public boolean canEditAccreditamento(CurrentUser currentUser, Long providerId);
	
	public boolean canShowUser(CurrentUser currentUser, Long userId);
	public boolean canShowAllUser(CurrentUser currentUser);
	public boolean canEditUser(CurrentUser currentUser, Long userId);
	public boolean canCreateUser(CurrentUser currentUser);
}