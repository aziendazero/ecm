package it.tredi.ecm.dao.enumlist;

import java.util.stream.Stream;

import it.tredi.ecm.dao.entity.Evento;
import lombok.Getter;

@Getter
public enum VerificaApprendimentoFSCEnum implements INomeEnum {

	QUESTIONARIO(1,"Questionario"),
	ESAME_ORALE(2,"Esame orale"),
	ESAME_PRATICO(3,"Esame pratico"),
	PROVA_SCRITTA(4,"Prova scritta"),
	RELAZIONE_FIRMATA(5,"Rapporto conclusivo basato su valutazione dell’apprendimento valutata dal responsabile scientifico"),
	RAPPORTO_CONCLUSIVO(6, "Rapporto conclusivo basato su valutazione dell’apprendimento valutata dal tutor"),
	
	// ERM015132
	RELAZIONE_FIRMATA_V1(50,"Relazione firmata dal responsabile o dal coordinatore del progetto"),
	RAPPORTO_CONCLUSIVO_V1(51, "Rapporto conclusivo di training individualizzato da parte del tutor");

	private int id;
	private String nome;

	private VerificaApprendimentoFSCEnum(int id, String nome){
		this.id = id;
		this.nome = nome;
	}
	
	// otiene la lista di valori che non siano deprecati.
	public static VerificaApprendimentoFSCEnum[] getValues(EventoVersioneEnum version /*Evento e*/){
		//EventoVersioneEnum version = e.getVersione();
		if(version != null && version == EventoVersioneEnum.UNO_PRIMA_2018) {
			// v1 
			return Stream.of(VerificaApprendimentoFSCEnum.values()).filter(c->c.getId() != 5 && c.getId() != 6).toArray(size->new VerificaApprendimentoFSCEnum[size]);
		}else {
			// v2
			return Stream.of(VerificaApprendimentoFSCEnum.values()).filter(c->c.getId() != 50 && c.getId() != 51).toArray(size->new VerificaApprendimentoFSCEnum[size]);
		}
	}
}

/* ERM015132 db patch

update ecmdb.eventofsc_verifica_apprendimento 
set verifica_apprendimento = 'RAPPORTO_CONCLUSIVO_V1'
where eventofsc_id in ( 
select distinct e.id from ecmdb.evento e, ecmdb.eventofsc_verifica_apprendimento eva 
where e.id = eva.eventofsc_id and eva.verifica_apprendimento='RAPPORTO_CONCLUSIVO' and e.versione = 1
) and verifica_apprendimento ='RAPPORTO_CONCLUSIVO'

update ecmdb.eventofsc_verifica_apprendimento 
set verifica_apprendimento = 'RELAZIONE_FIRMATA_V1'
where eventofsc_id in ( 
select e.id from ecmdb.evento e, ecmdb.eventofsc_verifica_apprendimento eva 
where e.id = eva.eventofsc_id and eva.verifica_apprendimento='RELAZIONE_FIRMATA' and e.versione = 1
) and verifica_apprendimento ='RELAZIONE_FIRMATA'

 */
