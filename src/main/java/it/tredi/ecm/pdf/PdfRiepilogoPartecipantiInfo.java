package it.tredi.ecm.pdf;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PdfRiepilogoPartecipantiInfo {
	Set<PdfPartecipanteInfo> partecipanti = new HashSet<PdfPartecipanteInfo>();
}
