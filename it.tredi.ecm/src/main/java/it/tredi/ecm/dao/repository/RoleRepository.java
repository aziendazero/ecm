package it.tredi.ecm.dao.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.tredi.ecm.dao.entity.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
	Set<Role> findAll();
}
