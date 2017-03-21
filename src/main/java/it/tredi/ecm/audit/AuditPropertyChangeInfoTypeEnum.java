package it.tredi.ecm.audit;

public enum AuditPropertyChangeInfoTypeEnum {
	VALUE,//la property in modifica è di tipo valore si deve mostrare la modifica usando i metodi getPreviousValue e getAfterValue
	FILE,//la property in modifica è un it.tredi.ecm.dao.entity.File getPreviousFileId e getAfterFileId restituiscono gli id, o null se non impostato, del file prima e dopo la modifica
	ENTITY,//la property in modifica è una entity si può mostrare l'audit dell'oggetto precedente e successivo per maggiori informazioni
	//TODO controllare se si verifica
	VALUEOBJECT,//la property in modifica è un ValueObject si può mostrare l'audit dell'oggetto precedente e successivo per maggiori informazioni

	MAP_VALUE,
	MAP_FILE,
	MAP_ENTITY,
	MAP_VALUEOBJECT,

	SET_VALUE,
	SET_FILE,
	SET_ENTITY,
	SET_VALUEOBJECT,

	LIST_VALUE,
	LIST_FILE,
	LIST_ENTITY,
	LIST_VALUEOBJECT,

	ARRAY_VALUE,
	ARRAY_FILE,
	ARRAY_ENTITY,
	ARRAY_VALUEOBJECT
	;
}
