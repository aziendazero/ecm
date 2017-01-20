package it.tredi.ecm.dao.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.enumlist.ProfileEnum;

public interface AccountRepository extends CrudRepository<Account, Long> {
	Optional<Account> findOneByUsername(String username);
	Optional<Account> findOneByEmail(String email);
	Set<Account> findAll();
	Set<Account> findAllByProfiles(Profile profile);
	@Query ("SELECT COUNT (a) FROM Account a WHERE a.valutazioniNonDate > 0")
	int countAllRefereeWithValutazioniNonDate();
	@Query ("SELECT a.domandeNonValutate FROM Account a WHERE a.id = :refereeId")
	Set<Accreditamento> getAllDomandeNonValutateByRefereeId(@Param("refereeId") Long refereeId);

	@Query("SELECT a.provider.id FROM Account a WHERE a.id = :accountId")
	public Optional<Long> getProviderIdById(@Param("accountId") Long accountId);

	public Set<Account> findAllByProviderId(Long providerId);
	Set<Account> findAllByProfilesContainingAndValutazioniNonDateLessThan(Profile profileReferee, int valutazioniNonDateLimit);
}
