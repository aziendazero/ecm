package it.tredi.ecm.service;

import it.tredi.ecm.service.bean.CurrentUser;

public interface SecurityAccessService {
	boolean canShowProvider(CurrentUser currentUser, Long providerId);
	boolean canShowAllProvider(CurrentUser currentUser);
	boolean canEditProvider(CurrentUser currentUser, Long providerId);
	
	boolean canShowAccreditamento(CurrentUser currentUser, Long providerId);
	boolean canShowAllAccreditamento(CurrentUser currentUser);
	boolean canEditAccreditamento(CurrentUser currentUser, Long providerId);
}
