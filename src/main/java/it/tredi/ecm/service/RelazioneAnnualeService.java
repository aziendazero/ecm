package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.RelazioneAnnuale;

public interface RelazioneAnnualeService {
	public RelazioneAnnuale getRelazioneAnnuale(Long relazioneAnnualeId);
	public Set<RelazioneAnnuale> getAllRelazioneAnnuale();
	public Set<RelazioneAnnuale> getAllRelazioneAnnualeByProviderId(Long providerId);
	public RelazioneAnnuale getRelazioneAnnualeForProviderIdAndAnnoRiferimento(Long providerId, Integer annoRiferimento);
	public Set<Provider> getAllProviderNotRelazioneAnnualeRegistrata(Integer annoRiferimento);

	public Set<Provider> getAllProviderNotRelazioneAnnualeRegistrataAllaScadenza();
	public int countProviderNotRelazioneAnnualeRegistrataAllaScadenza();

	public RelazioneAnnuale createRelazioneAnnuale(Long providerId, Integer annoRiferimento);
	public void elaboraRelazioneAnnualeAndSave(RelazioneAnnuale relazioneAnnuale, boolean asBozza);
}
