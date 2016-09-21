package it.tredi.ecm.web.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseState {
	private boolean error;
	private String errorMessage;
	private String jsonObject;
	
	public ResponseState(boolean isError, String errorMessage) {
		this.error = isError;
		this.errorMessage = errorMessage;
	}
}
