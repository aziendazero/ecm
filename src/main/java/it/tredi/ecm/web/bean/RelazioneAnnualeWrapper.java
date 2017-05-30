package it.tredi.ecm.web.bean;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.RelazioneAnnuale;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelazioneAnnualeWrapper{
	private long providerId;
	private RelazioneAnnuale relazioneAnnuale;
	private File relazioneFinale;
}
