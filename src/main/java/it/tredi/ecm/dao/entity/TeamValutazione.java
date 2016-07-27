package it.tredi.ecm.dao.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamValutazione{
	private Account teamLeader;
	private Set<Account> membri = new HashSet<Account>();
	private Persona responsabileInformatico; 
}
