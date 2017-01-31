package it.tredi.ecm.web.bean;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Provider;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderWrapper extends Wrapper{
	private Provider provider;
	private Long accreditamentoId;
	private Accreditamento accreditamento;

	public ProviderWrapper(){};

	public ProviderWrapper(Provider provider, Long accreditamentoId){
		this.provider = provider;
		this.accreditamentoId = accreditamentoId;
	}
}
