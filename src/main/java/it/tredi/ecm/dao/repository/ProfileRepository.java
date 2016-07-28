package it.tredi.ecm.dao.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.enumlist.ProfileEnum;

public interface ProfileRepository extends CrudRepository<Profile, Long> {
	Optional<Profile> findOneByProfileEnum(ProfileEnum profileEnum);
	Set<Profile> findAll();
}
