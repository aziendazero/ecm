package it.tredi.ecm.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Seduta;
import it.tredi.ecm.dao.entity.ValutazioneCommissione;
import it.tredi.ecm.dao.repository.SedutaRepository;
import it.tredi.ecm.dao.repository.ValutazioneCommissioneRepository;

@Service
public class SedutaServiceImpl implements SedutaService {

	@Autowired SedutaRepository sedutaRepository;
	@Autowired ValutazioneCommissioneRepository valutazioneCommissioneRepository;

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

	//se la data non antecedente ad oggi posso editare / cancellare la seduta
	@Override
	public boolean canEditSeduta(Seduta seduta) {
		if(seduta.isNew() || !seduta.getData().isBefore(LocalDate.now()))
			return true;
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
	public Set<Seduta> getAllSeduteAfter(LocalDate date) {
		Set<Seduta> sedute = sedutaRepository.findAllByDataAfter(date);
		return sedute;
	}

	@Override
	public void moveValutazioneCommissione(ValutazioneCommissione val, Seduta from, Seduta to) {
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
	}

}
