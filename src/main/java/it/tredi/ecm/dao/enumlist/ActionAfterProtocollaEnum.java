package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum ActionAfterProtocollaEnum {
	ESEGUI_TASK (1, "Esegui task"),
	SCADENZA_INSERIMENTO_DOMANDA_STANDARD (2, "Scadenza inserimento domanda standard"),
	MANCATO_PAGAMENTO_QUOTA (3, "Mancato pagamento della quota annuale"),
	BLOCCA_PER_RICHIESTA_PROVIDER (4, "Blocco del provider per esplicita richiesta di questi");

	private int id;
	private String nome;

	private ActionAfterProtocollaEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}

}