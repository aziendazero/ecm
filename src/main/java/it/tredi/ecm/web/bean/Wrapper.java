package it.tredi.ecm.web.bean;

import java.util.HashSet;
import java.util.Set;

import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Wrapper {
	private Set<IdFieldEnum> idEditabili = new HashSet<IdFieldEnum>();
}
