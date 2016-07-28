package it.tredi.ecm.dao.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TeamValutazione extends BaseEntity{
	private Account teamLeader;
	private Account componenteOsservatorioRegionale;
	private Set<Account> componentiSegreteria = new HashSet<Account>();
	private Persona responsabileInformatico; //TODO deve essere un account??? o una persona del provider?
}
