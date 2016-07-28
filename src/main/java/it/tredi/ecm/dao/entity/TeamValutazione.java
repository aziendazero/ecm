package it.tredi.ecm.dao.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TeamValutazione extends BaseEntity{
	@OneToOne
	private Account teamLeader;
	@OneToOne
	private Account componenteOsservatorioRegionale;
	@OneToMany
	private Set<Account> componentiSegreteria = new HashSet<Account>();
	@OneToOne
	private Persona responsabileInformatico; //TODO deve essere un account??? o una persona del provider?
}
