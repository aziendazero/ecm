package it.tredi.ecm.dao.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Profile;

public interface ProfileRepository extends CrudRepository<Profile, Long> {
	Optional<Profile> findOneByName(String name);
	Set<Profile> findAll();
}
