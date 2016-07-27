package it.tredi.ecm.web.bean;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
