package it.tredi.ecm.web.bean;

import java.util.HashSet;
import java.util.Set;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.FieldEditabile;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestWrapper {
	private Accreditamento accreditamento;
	private Set<FieldEditabile> fullLista = new HashSet<FieldEditabile>();
	private Set<IdFieldEnum> selected = new HashSet<IdFieldEnum>();
	
}
