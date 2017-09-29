package it.tredi.ecm;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

@Service
public class TestGestioneTimerTest {
	
	public static void main(String[] args) throws Exception {
		inviaRichiestaIntegrazione(1234567, 1);
	}
	
	public static void inviaRichiestaIntegrazione(long accreditamentoId, long giorniTimer) throws Exception {
		long millisecondiInGiorno = 86400000;
		long millisecondiInMinuto = 60000;
		boolean conteggioGiorniAvanzatoAbilitato = true;
		boolean conteggioGiorniAvanzatoBeforeDayMode = true;
		
		
		
		
		//semaforo bonita
//		tokenService.createBonitaSemaphore(accreditamentoId);

//		Accreditamento accreditamento = getAccreditamento(accreditamentoId);
//		if(accreditamento.getWorkflowInCorso().getTipo() == TipoWorkflowEnum.ACCREDITAMENTO) {
//			accreditamento.setGiorniIntegrazione(giorniTimer);
//		} else {
//			accreditamento.getWorkflowInCorso().setGiorniIntegrazione(giorniTimer);
//		}
//		accreditamentoRepository.save(accreditamento);
//		//saveAndAudit(accreditamento);

		Long timerIntegrazioneRigetto = giorniTimer * millisecondiInGiorno;
		
		if (conteggioGiorniAvanzatoAbilitato && !conteggioGiorniAvanzatoBeforeDayMode) {
			timerIntegrazioneRigetto = millisecondsToAdd(giorniTimer);
			System.out.println("full time in ms: "+timerIntegrazioneRigetto);
		}
		else if (conteggioGiorniAvanzatoAbilitato && conteggioGiorniAvanzatoBeforeDayMode) {
			giorniTimer--;
			timerIntegrazioneRigetto = millisecondsToAdd(giorniTimer);
			System.out.println("full time in ms: "+timerIntegrazioneRigetto);
		}
		
		System.out.println("full time in ms: "+timerIntegrazioneRigetto);
//		if(ecmProperties.isDebugTestMode() && giorniTimer < 0) {
//			//Per efffettuare i test si da la possibilità di inserire il tempo in minuti
//			timerIntegrazioneRigetto = (-giorniTimer) * millisecondiInMinuto;
//		}
//		workflowService.eseguiTaskRichiestaIntegrazioneForCurrentUser(accreditamento, timerIntegrazioneRigetto);
//
//		//rilascio semaforo bonita
//		tokenService.removeBonitaSemaphore(accreditamentoId);
		
	}
	
	public static Long millisecondsToAdd(Long giorniTimer) {
		long millisecondiInGiorno = 86400000;
		long millisecondiInMinuto = 60000;
		//we get the current time in milliseconds
		LocalDateTime currentTime = LocalDateTime.now();
		Long currentHourInMilliseconds = (currentTime.getHour()*60)*millisecondiInMinuto;
		Long currentMinuteInMilliseconds = currentTime.getMinute()*millisecondiInMinuto;
		Long currentTimeInMillisecods = currentHourInMilliseconds + currentMinuteInMilliseconds;
		
		//we calculate the added time so that the timer in bonita stops at 23:59
		Long milliseconds2359 = millisecondiInGiorno - millisecondiInMinuto;
		Long addedTimeInMilliseconds = milliseconds2359 - currentTimeInMillisecods;
		
		return giorniTimer + (addedTimeInMilliseconds);
	}
}
