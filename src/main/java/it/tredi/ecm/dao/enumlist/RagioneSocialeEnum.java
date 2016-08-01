package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum RagioneSocialeEnum {
	SRL (0, "S.R.L. (Società a responsabilità limitata)"),
	SAPA (1, "S.A.P.A. (Società in accomandita per azioni)"),
	SAS (2, "S.A.S.  (Società in accomandita semplice)"),
	SNC (3, "S.N.C. (Società in nome collettivo)"),
	SPA (4, "S.P.A. (Società per azioni)"),
	ALTRO (5, "Altro/Non presente");

	private int id;
	private String nome;

	private RagioneSocialeEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
