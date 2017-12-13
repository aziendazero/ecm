package it.tredi.ecm.dao.enumlist;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public enum IdentificativoPersonaRuoloEvento {
	A(1,"A", RuoloFSCEnum.RESPONSABILE_SCIENTIFICO_A, RuoloFSCEnum.ESPERTO_A, RuoloFSCEnum.COORDINATORE_A),
	B(2,"B", RuoloFSCEnum.RESPONSABILE_SCIENTIFICO_B, RuoloFSCEnum.ESPERTO_B, RuoloFSCEnum.COORDINATORE_B),
	C(3,"C", RuoloFSCEnum.RESPONSABILE_SCIENTIFICO_C, RuoloFSCEnum.ESPERTO_C, RuoloFSCEnum.COORDINATORE_C);
	
	private int id;
	private String nome;
	private RuoloFSCEnum ruoloFSCResponsabileSCientifico;
	private RuoloFSCEnum ruoloFSCEsperto;
	private RuoloFSCEnum ruoloFSCCoordinatore;

	private IdentificativoPersonaRuoloEvento(int id, String nome, RuoloFSCEnum ruoloFSCResponsabileSCientifico, RuoloFSCEnum ruoloFSCEsperto, RuoloFSCEnum ruoloFSCCoordinatore){
		this.id = id;
		this.nome = nome;
		this.ruoloFSCResponsabileSCientifico = ruoloFSCResponsabileSCientifico;
		this.ruoloFSCEsperto = ruoloFSCEsperto;
		this.ruoloFSCCoordinatore = ruoloFSCCoordinatore;
	}
	
	public static List<IdentificativoPersonaRuoloEvento> getOrderedValues() {
		List<IdentificativoPersonaRuoloEvento> toRet = new ArrayList<IdentificativoPersonaRuoloEvento>();
		toRet.add(A);
		toRet.add(B);
		toRet.add(C);
		return toRet;
	}
}
