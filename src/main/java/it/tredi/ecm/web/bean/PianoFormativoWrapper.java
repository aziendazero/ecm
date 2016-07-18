package it.tredi.ecm.web.bean;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PianoFormativoWrapper {
	private long providerId;
	private Set<Integer> anniDisponibiliList = new HashSet<Integer>();
	private int annoPianoFormativo;
}
