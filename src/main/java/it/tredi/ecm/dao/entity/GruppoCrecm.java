package it.tredi.ecm.dao.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class GruppoCrecm extends BaseEntityDefaultId{
	private String nome;
	@OneToMany
	private Set<Account> valutatori = new HashSet<Account>();
}
