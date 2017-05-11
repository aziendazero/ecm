package it.tredi.ecm.service;

public interface TokenService {
	public boolean checkTokenAndDelete(String token);

	public boolean checkReadyForBonita(Long accreditamentoId);

	public boolean checkReadyForBonita(Long accreditamentoId, Integer tentativo);

	public void createBonitaSemaphore(Long accreditamentoId);

	public void removeBonitaSemaphore(Long accreditamentoId);
}
