package it.tredi.ecm.service;

import java.util.Optional;
import java.util.Set;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.enumlist.ProfileEnum;

public interface AccountService {
    public Optional<Account> getUserByUsername(String username);
    public Optional<Account> getUserByEmail(String email);
    public Account getUserById(Long id);
    public Set<Account> getUserByProfileEnum(ProfileEnum profileEnum);
    public Set<Account> getAllUsers();
    public Set<Account> findAllByProviderId(Long providerId);
    public void save(Account user) throws Exception;
    public void resetPassword(String email) throws Exception;
    public void changePassword(Long id, String password) throws Exception;
	public int countAllRefereeWithValutazioniNonDate();
	public Set<String> getEmailByProfileEnum(ProfileEnum profileEnum);
	public Long getProviderIdById(Long accountId);
}
