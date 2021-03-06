package it.tredi.ecm.web.bean;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.entity.Sede;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SedeWrapper extends Wrapper{
	private Sede sede;
	private Long accreditamentoId;
	private Long providerId;

	private Accreditamento accreditamento;

	//per gestione sostituzione full in integrazione
	private FieldIntegrazioneAccreditamento fullIntegrazione;

	public SedeWrapper(){}

	public SedeWrapper(Sede sede, Long accreditamentoId){
		this.sede = sede;
		this.accreditamentoId = accreditamentoId;
	}
}
