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
public class GruppoCrecm extends BaseEntity{
	private String nome;
	@OneToMany(mappedBy = "gruppoCrecm")
	private Set<Valutatore> valutatori = new HashSet<Valutatore>();
}
