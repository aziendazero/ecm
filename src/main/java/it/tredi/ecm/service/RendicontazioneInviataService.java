package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.RendicontazioneInviata;

public interface RendicontazioneInviataService {
	public void save(RendicontazioneInviata rendicontazioneInviata);
	public Set<RendicontazioneInviata> getAllInviiRendicontazionePendenti();
}
