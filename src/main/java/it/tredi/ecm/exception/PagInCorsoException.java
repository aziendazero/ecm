package it.tredi.ecm.exception;

public class PagInCorsoException extends Exception{

	private static final long serialVersionUID = 4956100860922644753L;

	public PagInCorsoException() {
	}

	public PagInCorsoException(String message) {
		super(message);
	}

	public PagInCorsoException(Throwable cause) {
		super(cause);
	}

	public PagInCorsoException(String message, Throwable cause) {
		super(message, cause);
	}

}
