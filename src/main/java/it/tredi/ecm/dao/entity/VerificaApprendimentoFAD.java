package it.tredi.ecm.dao.entity;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import it.tredi.ecm.dao.enumlist.VerificaApprendimentoFADEnum;
import it.tredi.ecm.dao.enumlist.VerificaApprendimentoInnerFADEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class VerificaApprendimentoFAD {
	@Enumerated(EnumType.STRING)
	private VerificaApprendimentoFADEnum verificaApprendimento;
	@Enumerated(EnumType.STRING)
	private VerificaApprendimentoInnerFADEnum verificaApprendimentoInner;
}
