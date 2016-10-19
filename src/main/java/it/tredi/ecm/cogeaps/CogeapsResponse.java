package it.tredi.ecm.cogeaps;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class CogeapsResponse {

	/*
	 * questi campi sono tutti valorizzati solamente nel caso in cui ci sia un problema di autenticazione o nella chiamata HTTP
	 */
	private int status;
	private String error;
	private String message;
	/**/
	
	//messaggio completo del risultato
	private String response;
	
	//http status code
	private int httpStatusCode;

}
