package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum EventoStatoEnum {
	BOZZA (1, "Attesa di validazione"),
	VALIDATO (2, "Accreditato"),
//	PAGATO (3, "Pagato"), //solo PROVIDER GRUPPO B -> non utilizzato per stato degli eventi
//	CONTRIBUTO_NON_PREVISTO (4, "Contributo non previsto"), //solo PROVIDER GRUPPO A -> non utilizzato per stato degli eventi
	RAPPORTATO (5, "Trasmissione di un report XML"),
	CANCELLATO (6, "Cancellato");
	
	private int id;
	private String nome;
	private WorkflowTipoEnum workflowTipoEnum; 

	private EventoStatoEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}

	/**
	 * BOZZA: 
	 * 	L'evento è completamentte modificabile...non vengono applicati i controlli di validazioni e obbligatorietà dei campi per il savataggio.
	 * 
	 * VALIDATO
	 * 	L'evento VALIDATO verrà visulizzato sul portale.
	 *  Per poter essere VALIDATO vengono applicati:
	 *  	+ tutti i controlli di validazione / obbligatorietà dei campi
	 *  	+ la data dell'evento deve essere >= Now() + 15;
	 *  
	 * **/
}
