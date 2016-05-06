package it.tredi.ecm.service;

import java.util.Optional;
import java.util.Set;


import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.entity.Role;

public interface ProfileAndRoleService {
	Set<Profile> getAllProfile();
	Profile getProfile(Long id);
	Optional<Profile> getProfileByName(String name);
	void saveProfile(Profile profile);
	
	Set<Role> getAllRole();
}
