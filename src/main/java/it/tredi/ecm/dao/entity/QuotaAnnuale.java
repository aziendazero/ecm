package it.tredi.ecm.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class QuotaAnnuale extends BaseEntity{
	private Boolean pagato = false;
	private Boolean pagInCorso = false;
	
	@Column(name = "anno_riferimento")
	private Integer annoRiferimento;
	
	@ManyToOne
	private Provider provider;
	
	@OneToOne(mappedBy="quotaAnnuale")
	private Pagamento pagamento;
}
