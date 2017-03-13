package it.tredi.ecm.audit;

public enum AuditPropertyChangeInfoTypeEnum {
	VALUE,//la property in modifica è di tipo valore si deve mostrare la modifica usando i metodi getPreviousValue e getAfterValue
	FILE,//la property in modifica è un it.tredi.ecm.dao.entity.File getPreviousFileId e getAfterFileId restituiscono gli id, o null se non impostato, del file prima e dopo la modifica
	ENTITY,//la property in modifica è una entity si può mostrare l'audit dell'oggetto precedente e successivo per maggiori informazioni

	MAP_VALUE,
	MAP_FILE,
	MAP_ENTITY,
	MAP_VALUEOBJECT
	;
}
