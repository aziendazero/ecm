package it.tredi.ecm.exception;

public class AccreditamentoNotFoundException extends Exception{
	
	private static final long serialVersionUID = -4561304471522223805L;

	public AccreditamentoNotFoundException() {
	}
	
	public AccreditamentoNotFoundException(String message) {
		super(message);
	}
	
	public AccreditamentoNotFoundException(Throwable cause) {
		super(cause);
	}
	
	public AccreditamentoNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
