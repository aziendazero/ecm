package it.tredi.ecm.service;

import java.util.Optional;
import java.util.Set;

import it.tredi.ecm.dao.entity.Account;

public interface AccountService {
    Optional<Account> getUserByUsername(String username);
    Optional<Account> getUserByEmail(String email);
    Account getUserById(Long id);
    Set<Account> getAllUsers();
    void save(Account user);
}
