package it.tredi.ecm.service;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Seduta;
import it.tredi.ecm.dao.repository.SedutaRepository;

@Service
public class SedutaServiceImpl implements SedutaService {

	@Autowired SedutaRepository sedutaRepository;

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

}