package it.tredi.ecm.audit;

public enum IdentifierTypeEnum {
	ENTITY_ID,//l'oggetto e' una Entity lo isentifichiamo dal suo id
	VALUEOBJECT,//l'oggetto e' un ValulObject lo isentifichiamo dal suo indice se in una lista
	VALUEOBJECT_INDEX;//l'oggetto e' un ValulObject lo identifichiamo dal suo indice se in una lista
}
