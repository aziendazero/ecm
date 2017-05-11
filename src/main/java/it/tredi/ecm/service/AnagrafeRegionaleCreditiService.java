package it.tredi.ecm.service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import it.tredi.ecm.dao.entity.AnagrafeRegionaleCrediti;

public interface AnagrafeRegionaleCreditiService {
	public Set<Integer> getAnnoListForAnagrafeRegionaleCrediti();
	public Set<AnagrafeRegionaleCrediti> getAll(Integer annoRiferimento);
	public Set<AnagrafeRegionaleCrediti> getAllByCodiceFiscale(String codiceFiscale, Integer annoRiferimento);
	public BigDecimal getSumCreditiByCodiceFiscale(String codiceFiscale, Integer annoRiferimento);

	public Map<String,Integer> getRuoliAventeCreditiPerAnno(Long providerId, Integer annoRiferimento);
	public int getProfessioniAnagrafeAventeCrediti(Long providerId, Integer annoRiferimento);

//	public Set<Evento> getEventiAnagrafeAventeCrediti(Long providerId, Integer annoRiferimento);
	public Set<AnagrafeRegionaleCrediti> extractAnagrafeRegionaleCreditiPartecipantiFromXml(String fileName, byte []reportEventoXml) throws Exception;
}
