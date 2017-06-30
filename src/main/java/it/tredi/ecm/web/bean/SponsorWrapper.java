package it.tredi.ecm.web.bean;

import java.util.List;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Sponsor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SponsorWrapper {
	private List<Sponsor> sponsorList;
	private Long providerId;
	private String denominazioneProvider;
	private Long eventoId;
	private File sponsorFile;
}
