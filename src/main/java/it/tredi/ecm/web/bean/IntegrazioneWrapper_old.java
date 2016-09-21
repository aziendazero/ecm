package it.tredi.ecm.web.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntegrazioneWrapper_old {
	private long accreditamentoId;
	private List<FieldIntegrazioneAccreditamento> fullList = new ArrayList<FieldIntegrazioneAccreditamento>();
	private Map<IdFieldEnum,FieldIntegrazioneAccreditamento> map = new HashMap<IdFieldEnum, FieldIntegrazioneAccreditamento>();
}
