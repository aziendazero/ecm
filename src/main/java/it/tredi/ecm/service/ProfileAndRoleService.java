package it.tredi.ecm.service;

import java.util.Optional;
import java.util.Set;


import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.entity.Role;
import it.tredi.ecm.dao.enumlist.ProfileEnum;

public interface ProfileAndRoleService {
	Set<Profile> getAllProfile();
	Profile getProfile(Long id);
	Optional<Profile> getProfileByProfileEnum(ProfileEnum profileEnum);
	void saveProfile(Profile profile);

	Set<Role> getAllRole();
	Set<Profile> getAllUsableProfile();
}
