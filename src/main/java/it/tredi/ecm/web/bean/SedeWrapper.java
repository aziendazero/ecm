package it.tredi.ecm.web.bean;

import java.util.List;

import it.tredi.ecm.dao.entity.Sede;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SedeWrapper {
	private Sede sede;
	private int accreditamentoId;
	
	private int idOffset;
	private List<Integer> editableId;
	private List<Integer> showId;
}
