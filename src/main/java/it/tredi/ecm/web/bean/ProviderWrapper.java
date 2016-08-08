package it.tredi.ecm.web.bean;

import java.util.HashMap;
import java.util.Map;

import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderWrapper extends Wrapper{
	private Provider provider;
	private Long accreditamentoId;
}
