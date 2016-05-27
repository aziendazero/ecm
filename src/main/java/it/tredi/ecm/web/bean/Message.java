package it.tredi.ecm.web.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
	private String titolo;
	private String testo;
	private String tipo;
	
	public Message(String titolo, String testo, String tipo){
		this.titolo = titolo;
		this.testo = testo;
		this.tipo = tipo;
	}
}
