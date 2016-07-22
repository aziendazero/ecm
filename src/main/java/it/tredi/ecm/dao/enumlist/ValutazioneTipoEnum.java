package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum ValutazioneTipoEnum {
		SEGRETERIA_ECM(1,"segreteria ecm"),
		REFEREE(2, "referee"),
		COMMISSIONE_ECM(2, "commissione ecm"),
		TEAM_LEADER(4, "team leader");
		
		private int id;
		private String nome;

		private ValutazioneTipoEnum(int id, String nome){
			this.id = id;
			this.nome = nome;
		}
}
