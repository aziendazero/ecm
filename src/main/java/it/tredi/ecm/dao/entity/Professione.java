package it.tredi.ecm.dao.entity;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Professione extends BaseEntity{
	private String nome;
	private List<String> discipline;
}
