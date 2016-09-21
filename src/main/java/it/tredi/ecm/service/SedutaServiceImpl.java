package it.tredi.ecm.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Seduta;
import it.tredi.ecm.dao.entity.ValutazioneCommissione;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.repository.SedutaRepository;
import it.tredi.ecm.dao.repository.ValutazioneCommissioneRepository;
import it.tredi.ecm.service.bean.EcmProperties;

@Service
public class SedutaServiceImpl implements SedutaService {
	private static Logger LOGGER = LoggerFactory.getLogger(SedutaServiceImpl.class);

	@Autowired private SedutaRepository sedutaRepository;
	@Autowired private ValutazioneCommissioneRepository valutazioneCommissioneRepository;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private EcmProperties ecmProperties;

	@Override
	public Set<Seduta> getAllSedute() {
		return sedutaRepository.findAll();
	}

	@Override
	public Seduta getSedutaById(Long sedutaId) {
		return sedutaRepository.findOne(sedutaId);
	}

	@Override
	public void removeSedutaById(Long sedutaId) {
		sedutaRepository.delete(sedutaId);
	}

	//se la data non antecedente ad adesso + 30 minuti, posso editare / cancellare la seduta
	@Override
	public boolean canEditSeduta(Seduta seduta) {
		if(seduta.isNew() || seduta.getData().isAfter(LocalDate.now()) || (seduta.getData().isEqual(LocalDate.now()) && seduta.getOra().isAfter(LocalTime.now().plusMinutes(ecmProperties.getSedutaValidationMinutes()))))
			return true;
		else return false;
	}

	//se la data è passata e NON è già stata valutata la seduta può essere valutata
	@Override
	public boolean canBeEvaluated(Seduta seduta) {
		if(!seduta.isNew() &&
				(seduta.getData().isBefore(LocalDate.now()) || (seduta.getData().isEqual(LocalDate.now()) && seduta.getOra().isBefore(LocalTime.now()))) &&
				!seduta.isLocked())
			return true;
		else return false;
	}

	//controllo se la seduta può essere bloccata
	@Override
	public boolean canBeLocked(Seduta seduta) {
		if (!(seduta.getValutazioniCommissione() == null) && !seduta.getValutazioniCommissione().isEmpty() && !seduta.isLocked()) {
			boolean value = true;
			for (ValutazioneCommissione vc : seduta.getValutazioniCommissione()) {
				if (vc.getStato() == null || vc.getValutazioneCommissione() == null || vc.getValutazioneCommissione().isEmpty())
					value = false;
			}
			return value;
		}
		else return false;
	}

	@Override
	public boolean canBeRemoved(Long sedutaId) {
		Seduta seduta = sedutaRepository.findOne(sedutaId);
		return (seduta.getValutazioniCommissione() == null || seduta.getValutazioniCommissione().isEmpty());
	}

	@Override
	public Set<Accreditamento> getAccreditamentiInSeduta(Long sedutaId) {
		Seduta seduta = sedutaRepository.findOne(sedutaId);
		Set<Accreditamento> result = new HashSet<Accreditamento>();
		for (ValutazioneCommissione vc : seduta.getValutazioniCommissione()) {
			result.add(vc.getAccreditamento());
		}
		return result;
	}

	@Override
	public Set<Seduta> getAllSeduteAfter(LocalDate date, LocalTime time) {
		Set<Seduta> sedute = sedutaRepository.findAllByDataAndOraAceptable(date, time);
		return sedute;
	}

	@Override
	public void moveValutazioneCommissione(ValutazioneCommissione val, Seduta from, Seduta to) {
		//rimuove stato e commento sulla valutazione
		val.setStato(null);
		val.setValutazioneCommissione(null);
		//sposta la valutazione
		Set<ValutazioneCommissione> valutazioniFrom = from.getValutazioniCommissione();
		Set<ValutazioneCommissione> valutazioniTo = to.getValutazioniCommissione();
		valutazioniFrom.remove(val);
		valutazioniTo.add(val);
		from.setValutazioniCommissione(valutazioniFrom);
		to.setValutazioniCommissione(valutazioniTo);
		val.setSeduta(to);
		sedutaRepository.save(from);
		sedutaRepository.save(to);
		valutazioneCommissioneRepository.save(val);

		//TODO mandare avanti stato Bonita (val.accreditamento ritorna in INS_ODG)
	}



	@Override
	public void lockSeduta(Long sedutaId) throws Exception {
		Seduta seduta =  sedutaRepository.findOne(sedutaId);
		if(canBeLocked(seduta)) {
			seduta.setLocked(true);
			sedutaRepository.save(seduta);
			for (ValutazioneCommissione vc : seduta.getValutazioniCommissione()) {
				//TODO mandare avanti stato Bonita (a seconda dello stato settato nella valutazioneCommissione)
			}
		}
		else throw new Exception("Seduta non bloccabile");
	}

	@Override
	public Map<Long, Set<AccreditamentoStatoEnum>> prepareMappaStatiValutazione(Seduta seduta) {
		Map<Long, Set<AccreditamentoStatoEnum>> mappa = new HashMap<Long, Set<AccreditamentoStatoEnum>>();
		for (ValutazioneCommissione vc : seduta.getValutazioniCommissione()) {
			Set<AccreditamentoStatoEnum> value = new HashSet<AccreditamentoStatoEnum>();
			//TODO chiedere a Bonita gli stati dove può finire la domanda di accreditamento (sostituire parte fatta a manazza)
			value.add(AccreditamentoStatoEnum.ACCREDITATO);
			value.add(AccreditamentoStatoEnum.RICHIESTA_INTEGRAZIONE);
			value.add(AccreditamentoStatoEnum.DINIEGO);
			mappa.put(vc.getAccreditamento().getId(), value);
		}
		return mappa;
	}

	@Override
	public void addValutazioneCommissioneToSeduta(String motivazioneDaInserire, Long idAccreditamentoDaInserire, Seduta seduta) {
		ValutazioneCommissione valutazioneDaInserire = new ValutazioneCommissione();
		valutazioneDaInserire.setOggettoDiscussione(motivazioneDaInserire);
		valutazioneDaInserire.setAccreditamento(accreditamentoService.getAccreditamento(idAccreditamentoDaInserire));
		valutazioneDaInserire.setSeduta(seduta);
		Set<ValutazioneCommissione> setValutazioni = seduta.getValutazioniCommissione();
		setValutazioni.add(valutazioneDaInserire);
		seduta.setValutazioniCommissione(setValutazioni);
		valutazioneCommissioneRepository.save(valutazioneDaInserire);
		sedutaRepository.save(seduta);

		//TODO mandare avanti stato Bonita (valutazioneDaInserire.accreditamento si sposta di stato)
	}

	@Override
	public void removeValutazioneCommissioneFromSeduta(Long valutazioneCommissioneId) {
		ValutazioneCommissione valutazioneDaEliminare = valutazioneCommissioneRepository.findOne(valutazioneCommissioneId);

		//TODO mandare avanti stato Bonita (valutazioneDaEliminare.accreditamento si sposta di stato)

		valutazioneCommissioneRepository.delete(valutazioneDaEliminare);
	}

	//cerca nel DB la prossima Seduta (a partire da oggi)
	@Override
	public Seduta getNextSeduta() {
		LocalDate oggi = LocalDate.now();
		Seduta prossimaSedutaOggi = sedutaRepository.findFirstByDataAndOraAfterOrderByOraAsc(oggi, LocalTime.now());
		if (prossimaSedutaOggi != null)
			return prossimaSedutaOggi;
		else
			return sedutaRepository.findFirstByDataAfterOrderByDataAsc(oggi);
	}

}
