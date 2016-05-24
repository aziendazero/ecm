package it.tredi.ecm.web.bean;

import java.util.Arrays;

import it.tredi.ecm.dao.entity.Provider;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProviderWrapper extends Wrapper {
	private Provider provider;
	private Long accreditamentoId;
	
	public ProviderWrapper(){
		setIdOffset(1);
		setIdEditabili(Arrays.asList(1,2,5,6,7));
	}
}
