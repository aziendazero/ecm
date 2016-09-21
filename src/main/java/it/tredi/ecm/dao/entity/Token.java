package it.tredi.ecm.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Token {
	//@Column(unique=true)
	@Id
	@Column(length = 36)
	private String token;
}
