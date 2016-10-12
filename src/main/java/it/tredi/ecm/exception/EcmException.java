package it.tredi.ecm.exception;

import lombok.Getter;

@Getter
public class EcmException extends Exception {
	
	private static final long serialVersionUID = 3149835349161512672L;
	private Exception originalException;
	private String messageTitle;
	private String messageDetail;
	
	public EcmException(String messageTitle, String messageDetail, Exception originalException) {
		super(originalException);
		this.messageTitle = messageTitle;
		this.messageDetail = messageDetail;
		this.originalException = originalException;
	}

}
