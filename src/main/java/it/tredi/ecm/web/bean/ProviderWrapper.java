package it.tredi.ecm.web.bean;

import it.tredi.ecm.dao.entity.Provider;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderWrapper extends Wrapper{
	private Provider provider;
	private Long accreditamentoId;
}
