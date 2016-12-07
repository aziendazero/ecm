package it.tredi.ecm.service;

import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.repository.DatiAccreditamentoRepository;

@Service
public class DatiAccreditamentoServiceImpl implements DatiAccreditamentoService {
	private static Logger LOGGER = LoggerFactory.getLogger(DatiAccreditamentoServiceImpl.class);

	@Autowired private DatiAccreditamentoRepository datiAccreditamentoRepository;
	@Autowired private AccreditamentoService accreditamentoService;

	@Override
	public DatiAccreditamento getDatiAccreditamento(Long id) {
		LOGGER.debug("Recupero dati accreditamento: " + id);
		return datiAccreditamentoRepository.findOne(id);
	}

	@Override
	@Transactional
	public void save(DatiAccreditamento datiAccreditamento, Long accreditamentoId) {
		LOGGER.debug("Salvataggio dati accreditamento.");
		if(datiAccreditamento.isNew()){
			datiAccreditamentoRepository.save(datiAccreditamento);
			Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);
			accreditamento.setDatiAccreditamento(datiAccreditamento);
			accreditamentoService.save(accreditamento);
		}else{
			datiAccreditamentoRepository.save(datiAccreditamento);
		}
	}

	@Override
	public Set<String> getFileTypeUploadedByDatiAccreditamentoId(Long id) {
		LOGGER.debug("Recupero i tipi di file presenti per datiAccreditamento: " + id);
		return datiAccreditamentoRepository.findAllFileTipoByDatiAccreditamentoId(id);
	}

}
