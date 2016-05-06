package it.tredi.ecm.dao.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Account;

public interface AccountRepository extends CrudRepository<Account, Long> {
	Optional<Account> findOneByUsername(String username);
	Optional<Account> findOneByEmail(String email);
	Set<Account> findAll();
}
