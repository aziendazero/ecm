package it.tredi.ecm.dao.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class BonitaSemaphore {

	public BonitaSemaphore(){}
	public BonitaSemaphore(Long accreditamentoId){
		this.accreditamentoId = accreditamentoId;
	}

	@Id
	private Long accreditamentoId;

}
