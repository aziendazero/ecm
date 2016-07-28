package it.tredi.ecm.service;

import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.entity.Role;
import it.tredi.ecm.dao.enumlist.ProfileEnum;
import it.tredi.ecm.dao.repository.ProfileRepository;
import it.tredi.ecm.dao.repository.RoleRepository;

@Service
public class ProfileAndRoleServiceImpl implements ProfileAndRoleService{
	private static Logger LOGGER = LoggerFactory.getLogger(ProfileAndRoleServiceImpl.class);
	
	@Autowired
	private ProfileRepository profileRepository;
	@Autowired
	private RoleRepository roleRepository;
	
	@Override
	public Set<Profile> getAllProfile() {
		return profileRepository.findAll();
	}

	@Override
	public Profile getProfile(Long id) {
		return profileRepository.findOne(id);
	}
	
	@Override
	@Transactional
	public void saveProfile(Profile profile) {
		LOGGER.info("Saving profile");
		profileRepository.save(profile);
	}

	@Override
	public Set<Role> getAllRole() {
		return roleRepository.findAll();
	}

	@Override
	public Optional<Profile> getProfileByProfileEnum(ProfileEnum profileEnum) {
		return profileRepository.findOneByProfileEnum(profileEnum);
	}

}
