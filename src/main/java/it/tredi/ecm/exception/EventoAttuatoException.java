package it.tredi.ecm.exception;

public class EventoAttuatoException extends Exception{
	
	private static final long serialVersionUID = -4561304471522223805L;

	public EventoAttuatoException() {
	}
	
	public EventoAttuatoException(String message) {
		super(message);
	}
	
	public EventoAttuatoException(Throwable cause) {
		super(cause);
	}
	
	public EventoAttuatoException(String message, Throwable cause) {
		super(message, cause);
	}
}
