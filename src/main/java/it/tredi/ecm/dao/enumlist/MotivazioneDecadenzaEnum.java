package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum MotivazioneDecadenzaEnum {
	SCADENZA_INSERIMENTO_DOMANDA_STANDARD (1, "Scadenza termini di inserimento della Domanda Standard"),
	RICHIESTA_PROVIDER (2, "Richiesta del Provider"),
	MANCATO_PAGAMENTO_QUOTA_ANNUALE (3, "Mancato pagamento della quota annuale");

	private int id;
	private String nome;

	private MotivazioneDecadenzaEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
}
