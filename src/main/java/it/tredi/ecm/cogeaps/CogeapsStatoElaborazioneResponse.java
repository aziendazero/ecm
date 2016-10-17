package it.tredi.ecm.cogeaps;

import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class CogeapsStatoElaborazioneResponse extends CogeapsResponse {
	
	private int errCode;
	private String errMsg;
	private boolean elaborazioneCompletata;
	private String idCaricamento;
	private int codiceErroreBloccante;
	private String  messaggioErroreBloccante;

}
